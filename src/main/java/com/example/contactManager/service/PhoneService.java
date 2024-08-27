package com.example.contactManager.service;

import com.example.contactManager.model.Contact;
import com.example.contactManager.model.Phone;
import com.example.contactManager.repository.ContactRepository;
import com.example.contactManager.repository.PhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhoneService {
    @Autowired
    private PhoneRepository phoneRepository;

    @Autowired
    private ContactRepository contactRepository;

    public Phone addPhone(Integer ownerId, Integer contactId, String telephone) {
        Contact contact = contactRepository.findById(contactId)
                .filter(c -> c.getOwnerId().equals(ownerId))
                .orElseThrow(() -> new RuntimeException("Contact not found or does not belong to user"));

        Phone phone = Phone.builder()
                .contact(contact)
                .telephone(telephone)
                .build();

        return phoneRepository.save(phone);
    }

    public void deletePhone(Integer id) {
        phoneRepository.deleteById(id);
    }

    public Phone getPhoneById(Integer id) {
        return phoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Телефон не найден"));
    }
}
