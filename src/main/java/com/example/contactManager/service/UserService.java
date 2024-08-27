package com.example.contactManager.service;

import com.example.contactManager.dto.LoginRequestDto;
import com.example.contactManager.dto.LoginResponseDto;
import com.example.contactManager.dto.RegisterRequestDto;
import com.example.contactManager.model.User;
import com.example.contactManager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;



    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User register(RegisterRequestDto request) {
        // Проверяем, существует ли уже пользователь с таким email
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        // Если пользователь с таким email не найден, создаем нового
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // Пароль должен быть захеширован
        user.setName(request.getName());

        // Сохраняем нового пользователя в базе данных
        return userRepository.save(user);
    }


    public LoginResponseDto login(LoginRequestDto request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isPresent() && userOptional.get().getPassword().equals(request.getPassword())) {
            String token = generateToken(userOptional.get());
            return new LoginResponseDto(request.getEmail(), token);
        }
        throw new RuntimeException("Invalid Login or Password");
    }

    private String generateToken(User user) {
        return "001{" + user.getEmail() + "|" + user.getPassword() + "}";
    }

    public Optional<User> findByToken(String token) {
        if (token.startsWith("001{") && token.endsWith("}")) {
            String[] parts = token.substring(4, token.length() - 1).split("\\|");
            if (parts.length == 2) {
                return userRepository.findByEmail(parts[0]);
            }
        }
        return Optional.empty();
    }
    public Integer getOwnerIdFromToken(String token) {
        if (token.startsWith("001{") && token.endsWith("}")) {
            String tokenContent = token.substring(4, token.length() - 1);
            String[] parts = tokenContent.split("\\|");
            if (parts.length == 2) {
                String email = parts[0];
                Optional<User> userOptional = userRepository.findByEmail(email);
                if (userOptional.isPresent()) {
                    return userOptional.get().getId().intValue();
                }
            }
        }
        return null;
    }
}
