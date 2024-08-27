package com.example.contactManager.service;

import com.example.contactManager.model.Contact;
import com.example.contactManager.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;
    public List<Contact> getAllContactsByOwnerId(Integer ownerId) {
        return contactRepository.findAll().stream()
                .filter(contact -> contact.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    public Contact getContactById(Integer id, Integer ownerId) {
        return contactRepository.findById(id)
                .filter(c -> c.getOwnerId().equals(ownerId))
                .orElse(null);
    }

    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    public Contact addContact(Contact contact) {

        System.out.println("Сохраняется к: " + contact);
        return contactRepository.save(contact);
    }

    public Contact updateContact(Integer id, Integer ownerId, String name, String email) {
        Contact contact = contactRepository.findById(id)
                .filter(c -> c.getOwnerId().equals(ownerId))
                .orElseThrow(() -> new RuntimeException("Contact not found or does not belong to user"));

        contact.setName(name);
        contact.setEmail(email);
        return contactRepository.save(contact);
    }

    public boolean deleteContact(Integer id) {
        contactRepository.deleteById(id);
        return true;
    }
}
