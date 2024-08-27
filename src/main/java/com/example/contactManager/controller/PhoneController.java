package com.example.contactManager.controller;

import com.example.contactManager.model.Contact;
import com.example.contactManager.model.Phone;
import com.example.contactManager.model.User;
import com.example.contactManager.service.ContactService;
import com.example.contactManager.service.PhoneService;
import com.example.contactManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/contacts")
public class PhoneController {

    @Autowired
    private PhoneService phoneService;

    @Autowired
    private ContactService contactService;
    @Autowired
    private UserService userService;


    @PostMapping("/{contactId}/phones")
    public ResponseEntity<Phone> addPhone(@RequestHeader("Authorization") String token,
                                          @PathVariable Integer contactId,
                                          @RequestBody Phone phone) {
        Integer ownerId = userService.getOwnerIdFromToken(token); // Используем UserService
        if (ownerId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // Добавляем телефон для контакта с указанным contactId
        Phone savedPhone = phoneService.addPhone(ownerId, contactId, phone.getTelephone());
        return ResponseEntity.ok(savedPhone);
    }


    @DeleteMapping("/{contactId}/phones/{phoneId}")
    public ResponseEntity<Void> deletePhone(@RequestHeader("Authorization") String token,
                                            @PathVariable Integer contactId,
                                            @PathVariable Integer phoneId) {
        Integer ownerId = userService.getOwnerIdFromToken(token);
        if (ownerId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Phone phone = phoneService.getPhoneById(phoneId);
        if (phone != null && contactService.getContactById(contactId, ownerId) != null) {
            phoneService.deletePhone(phoneId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }


    
}
