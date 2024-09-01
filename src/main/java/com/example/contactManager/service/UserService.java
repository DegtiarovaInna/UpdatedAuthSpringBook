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

    public User register(RegisterRequestDto request) {
        //Проверка на то существует ли уже пользователь с таким email
        Optional <User> existingUser= userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw  new RuntimeException("User with this email already exists");
        }
        //Если пользователь с таким email не найден, то создаем нового
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(request.getPassword());
        user.setRole("ROLE_USER");

        //сохраняем пользователя
        return  userRepository.save(user);
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
    public void updateUserRole(Long userId, String role ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!role.equals("ROLE_USER") && !role.equals("ROLE_ADMIN")) {
            throw new RuntimeException("Invalid role");
        }
        user.setRole(role);
        userRepository.save(user);
    }
//    public boolean isAdmin(String token) {
//        return findByToken(token)
//                .map(user -> user.getRole().equals("ROLE_ADMIN"))
//                .orElse(false);
//    }
public boolean isAdmin(Long userId) {
    return userRepository.findById(userId)
            .map(user -> user.getRole().equals("ROLE_ADMIN"))
            .orElse(false);
}
    public Long getUserIdFromToken(String token) {
        return findByToken(token)
                .map(user -> user.getId()) // Возвращаем Long напрямую
                .orElseThrow(() -> new RuntimeException("Invalid token"));
    }
}
