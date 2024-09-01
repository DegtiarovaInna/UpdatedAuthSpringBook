package com.example.contactManager.repository;

import com.example.contactManager.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    Optional<Contact> findByIdAndOwnerId(Long id, Long ownerId);

    List<Contact> findAllByOwnerId(Long ownerId);
    // Метод для поиска контакта по email и name для того, чтоб убрать возможность добавления одинаковых контактов юзеру
    Optional<Contact> findByEmailAndNameAndOwnerId(String email, String name, Long ownerId);

}
