package com.example.contactManager.controller;

import com.example.contactManager.model.Contact;
import com.example.contactManager.service.ContactService;
import com.example.contactManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContactById(@RequestHeader("Authorization") String token, @PathVariable Integer id) {
        Integer ownerId = userService.getOwnerIdFromToken(token);
        Contact contact = contactService.getContactById(id, ownerId);
        return contact != null ? ResponseEntity.ok(contact) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Contact>> getAllContacts(@RequestHeader("Authorization") String token) {
        Integer ownerId = userService.getOwnerIdFromToken(token);
        List<Contact> contacts = contactService.getAllContactsByOwnerId(ownerId);
        return ResponseEntity.ok(contacts);
    }

    @PostMapping
    public ResponseEntity<Contact> addContact(@RequestHeader("Authorization") String token, @RequestBody Contact contact) {
        Integer ownerId = userService.getOwnerIdFromToken(token); // Используем UserService для получения ownerId
        if (ownerId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        contact.setOwnerId(ownerId); // Устанавливаем ownerId в контакт
        Contact savedContact = contactService.addContact(contact);
        return ResponseEntity.ok(savedContact);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contact> updateContact(@RequestHeader("Authorization") String token, @PathVariable Integer id, @RequestBody Contact contact) {
        Integer ownerId = userService.getOwnerIdFromToken(token);
        if (ownerId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Проверяем, что contact не равен null и что все нужные поля заполнены
        if (contact == null || contact.getName() == null || contact.getEmail() == null) {
            return ResponseEntity.badRequest().build();
        }

        Contact updatedContact = contactService.updateContact(id, ownerId, contact.getName(), contact.getEmail());
        return updatedContact != null ? ResponseEntity.ok(updatedContact) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@RequestHeader("Authorization") String token, @PathVariable Integer id) {
        Integer ownerId = userService.getOwnerIdFromToken(token);
        if (ownerId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        boolean deleted = contactService.deleteContact(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    private void validateToken(String token) {
        if (token == null || !userService.findByToken(token).isPresent()) {
            throw new RuntimeException("Unauthorized access");
        }
    }
}
