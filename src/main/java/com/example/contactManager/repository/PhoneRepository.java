package com.example.contactManager.repository;

import com.example.contactManager.model.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhoneRepository extends JpaRepository<Phone, Long>  {
    List<Phone> findAllByContactId(Long contactId);
}
