package com.example.contactManager.controller;

import com.example.contactManager.model.Contact;
import com.example.contactManager.model.Phone;
import com.example.contactManager.service.ContactService;
import com.example.contactManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts")
public class ContactsController {

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

    @PostMapping
//    public ResponseEntity<Contact> addContact(@RequestHeader("Authorization") String token, @RequestBody Contact contact) {
//        Integer ownerId = getUserIdFromToken(token);
//        Contact savedContact = contactService.addContact(contact, ownerId);
//        return ResponseEntity.ok(savedContact);
//    }
    public ResponseEntity<Contact> addContact(@RequestHeader("Authorization") String token, @RequestBody Contact contact) {
        Long ownerId = contactService.getUserIdFromToken(token); // пполучаем ownerId
        Contact savedContact = contactService.addContact(contact, ownerId);
        return ResponseEntity.ok(savedContact);
    }

    @PutMapping("/{id}")
//    public ResponseEntity<Contact> updateContact(@RequestHeader("Authorization") String token, @PathVariable Integer id, @RequestBody Contact contact) {
//        Integer ownerId = getUserIdFromToken(token);
//        Contact updatedContact = contactService.updateContact(id, contact.getName(), contact.getEmail(), ownerId);
//        return updatedContact != null ? ResponseEntity.ok(updatedContact) : ResponseEntity.notFound().build();
//    }
    public ResponseEntity<Contact> updateContact(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody Contact contact) {
        Long userId = contactService.getUserIdFromToken(token); // Получаем userId
        Contact updatedContact = contactService.updateContact(id, contact.getName(), contact.getEmail(), userId);
        return ResponseEntity.ok(updatedContact);
    }


    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteContact(@RequestHeader("Authorization") String token, @PathVariable Integer id) {
//        Integer ownerId = getUserIdFromToken(token);
//        boolean deleted = contactService.deleteContact(id, ownerId);
//        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
//    }
    public ResponseEntity<Void> deleteContact(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        boolean isDeleted = contactService.deleteContact(id, token);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/phones")
//    public ResponseEntity<List<Phone>> getPhonesByContactId(@RequestHeader("Authorization") String token, @PathVariable Long id) {
//        Long ownerId = contactService.getUserIdFromToken(token);
//        List<Phone> phones = contactService.getPhonesByContactId(id, token);
//        return ResponseEntity.ok(phones);
//    }

    public ResponseEntity<List<Phone>> getPhonesByContactId(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        List<Phone> phones = contactService.getPhonesByContactId(id, token);
        return ResponseEntity.ok(phones);
    }

    @PostMapping("/{id}/phones")
//    public ResponseEntity<Phone> addPhoneToContact(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody Phone phone) {
//        Phone savedPhone = contactService.addPhoneToContact(id, phone, token);
//        return ResponseEntity.ok(savedPhone);
//    }
    public ResponseEntity<Phone> addPhoneToContact(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody Phone phone) {
        Phone savedPhone = contactService.addPhoneToContact(id, phone, token);
        return ResponseEntity.ok(savedPhone);
    }

    @DeleteMapping("/phones/{phoneId}")
//    public ResponseEntity<Void> deletePhone(@RequestHeader("Authorization") String token, @PathVariable Long phoneId) {
//        contactService.deletePhone(phoneId, token);
//        return ResponseEntity.noContent().build();
//    }
    public ResponseEntity<Void> deletePhone(@RequestHeader("Authorization") String token, @PathVariable Long phoneId) {
        contactService.deletePhone(phoneId, token);
        return ResponseEntity.noContent().build();
    }
@GetMapping("/{id}")
public ResponseEntity<Contact> getContactById(@RequestHeader("Authorization") String token, @PathVariable Long id) {
    // Передаем токен в сервис для получения владельца контакта...?
    Long ownerId = contactService.getUserIdFromToken(token);
    Contact contact = contactService.getContactById(id, ownerId);
    return contact != null ? ResponseEntity.ok(contact) : ResponseEntity.notFound().build();
}

//сейчас не используем
    private Integer getUserIdFromToken(String token) {
        return userService.findByToken(token)
                .map(user -> Math.toIntExact(user.getId()))
                .orElseThrow(() -> new RuntimeException("Invalid token"));
    }
    @GetMapping
//    public ResponseEntity<List<Contact>> getAllContacts(@RequestHeader("Authorization") String token) {
//        Integer ownerId = getUserIdFromToken(token);
//        List<Contact> contacts = contactService.getAllContacts(ownerId);
//        return ResponseEntity.ok(contacts);
//    }
    public ResponseEntity<List<Contact>> getAllContacts(@RequestHeader("Authorization") String token) {
        Long ownerId = contactService.getUserIdFromToken(token);
        List<Contact> contacts = contactService.getAllContacts(ownerId);
        return ResponseEntity.ok(contacts);
    }
}
