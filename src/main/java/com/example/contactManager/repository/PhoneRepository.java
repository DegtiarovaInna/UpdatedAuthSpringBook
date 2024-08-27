package com.example.contactManager.repository;

import com.example.contactManager.model.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneRepository extends JpaRepository<Phone, Integer>  {
}
