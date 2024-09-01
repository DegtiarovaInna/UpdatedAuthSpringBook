package com.example.contactManager.service;
import com.example.contactManager.model.User;
import com.example.contactManager.model.Contact;
import com.example.contactManager.model.Phone;
import com.example.contactManager.repository.ContactRepository;
import com.example.contactManager.repository.PhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private PhoneRepository phoneRepository;
    @Autowired
    private UserService userService; // зависимость для проверки роли

//    public Contact getContactById(Integer id, Integer ownerId) {
//        return contactRepository.findByIdAndOwnerId(id, ownerId).orElse(null);
//    }
public Contact getContactById(Long id, Long userId) {
    // Находим контакт по ID
    Contact contact = contactRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Contact not found"));

    // Проверка, является ли пользователь администратором или владельцем контакта
    if (userService.isAdmin(userId) || contact.getOwnerId().equals(userId)) {
        return contact;
    } else {
        throw new RuntimeException("Unauthorized action");
    }
}
    public List<Contact> getAllContacts(Long ownerId) {
        return contactRepository.findAllByOwnerId(ownerId);
    }



    public Contact addContact(Contact contact, Long ownerId) {
        // Проверка существования контакта
        Optional<Contact> existingContact = contactRepository.findByEmailAndNameAndOwnerId(
                contact.getEmail(), contact.getName(), ownerId);

        if (existingContact.isPresent()) {
            // Tсли контакт уже существует
            throw new RuntimeException("Такой пользователь уже есть");
        }
        // Установка владельца
        contact.setOwnerId(ownerId);
        // Соххранение
        return contactRepository.save(contact);
    } //чаn GPT сказал: всё норм, но предложил добавить GlobalExceptionHandler класс
    //@RestControllerAdvice
    //public class GlobalExceptionHandler {
    //
    //    @ExceptionHandler(RuntimeException.class)
    //    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
    //        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    //    }
    //}
    //пока не добавляю, не разобралась. Но вроде в реале так делают, когда много контроллеров, чтоб не дублировать код.
    // У нас не много, можем обрабатывать исключения прямо в методах контроллера.



    public Contact updateContact(Long id, String name, String email, Long userId) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        // Разрешить изменение, если пользователь администратор
        if (userService.isAdmin(userId) || contact.getOwnerId().equals(userId)) {
            contact.setName(name);
            contact.setEmail(email);
            return contactRepository.save(contact);
        } else {
            throw new RuntimeException("Unauthorized action");
        }
    }

    public boolean deleteContact(Long id, String token) {
        Long userId = getUserIdFromToken(token); // Получаем ID пользователя из токена
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        if (userService.isAdmin(userId) || contact.getOwnerId().equals(userId)) {
            contactRepository.delete(contact);
            return true;
        } else {
            throw new RuntimeException("Unauthorized action");
        }
    }
//    public boolean deleteContact(Integer id, Integer ownerId) {
//        Contact contact = contactRepository.findByIdAndOwnerId(id, ownerId)
//                .orElseThrow(() -> new RuntimeException("Contact not found or not owned by the user"));
//        contactRepository.delete(contact);
//        return true;
//    }

public List<Phone> getPhonesByContactId(Long contactId, String token) {
    Long ownerId = getUserIdFromToken(token);
    Contact contact = contactRepository.findById(contactId)
            .orElseThrow(() -> new RuntimeException("Contact not found or not owned by the user"));

    // Разрешить получение телефонов, если пользователь администратор или владелец контакта
    if (userService.isAdmin(ownerId) || contact.getOwnerId().equals(ownerId)) {
        return phoneRepository.findAllByContactId(contactId);
    } else {
        throw new RuntimeException("Unauthorized action");
    }
}
//    public List<Phone> getPhonesByContactId(Long contactId, String token) {
//        Long ownerId = getUserIdFromToken(token);
//        Contact contact = contactRepository.findByIdAndOwnerId(contactId, ownerId)
//                .orElseThrow(() -> new RuntimeException("Contact not found or not owned by the user"));
//        return phoneRepository.findAllByContactId(contactId);
//    }
//    public List<Phone> getPhonesByContactId(Integer contactId, Integer ownerId) {
//        Contact contact = contactRepository.findByIdAndOwnerId(contactId, ownerId)
//                .orElseThrow(() -> new RuntimeException("Contact not found or not owned by the user"));
//        return phoneRepository.findAllByContactId(contactId);
//    }
public Phone addPhoneToContact(Long contactId, Phone phone, String token) {
    Long ownerId = getUserIdFromToken(token);
    Contact contact = contactRepository.findById(contactId)
            .orElseThrow(() -> new RuntimeException("Contact not found or not owned by the user"));

    // Проверка, является ли пользователь администратором
    if (userService.isAdmin(ownerId)) {
        phone.setContact(contact);
        return phoneRepository.save(phone);
    }

    if (!contact.getOwnerId().equals(ownerId)) {
        throw new RuntimeException("Unauthorized action");
    }

    phone.setContact(contact);
    return phoneRepository.save(phone);
}
//public Phone addPhoneToContact(Long contactId, Phone phone, String token) {
//    Long ownerId = getUserIdFromToken(token);
//    Contact contact = contactRepository.findByIdAndOwnerId(contactId, ownerId)
//            .orElseThrow(() -> new RuntimeException("Contact not found or not owned by the user"));
//    phone.setContact(contact);
//    return phoneRepository.save(phone);
//}
//    public Phone addPhoneToContact(Integer contactId, Phone phone, Integer ownerId) {
//        Contact contact = contactRepository.findByIdAndOwnerId(contactId, ownerId)
//                .orElseThrow(() -> new RuntimeException("Contact not found or not owned by the user"));
//        phone.setContact(contact);
//        return phoneRepository.save(phone);
//    }
public void deletePhone(Long phoneId, String token) {
    Long ownerId = getUserIdFromToken(token);
    Phone phone = phoneRepository.findById(phoneId)
            .orElseThrow(() -> new RuntimeException("Phone not found"));
    Contact contact = phone.getContact();

    if (userService.isAdmin(ownerId) || contact.getOwnerId().equals(ownerId)) {
        phoneRepository.delete(phone);
    } else {
        throw new RuntimeException("Unauthorized action");
    }
}
//public void deletePhone(Long phoneId, String token) {
//    Long ownerId = getUserIdFromToken(token);
//    Phone phone = phoneRepository.findById(phoneId)
//            .orElseThrow(() -> new RuntimeException("Phone not found"));
//    Contact contact = phone.getContact();
//
//    if (userService.isAdmin(ownerId) || contact.getOwnerId().equals(ownerId)) {
//        phoneRepository.delete(phone);
//    } else {
//        throw new RuntimeException("Unauthorized action");
//    }
//}


//    public void deletePhone(Integer phoneId, Integer ownerId) {
//        Phone phone = phoneRepository.findById(phoneId)
//                .orElseThrow(() -> new RuntimeException("Phone not found"));
//        Contact contact = phone.getContact();
//
//        // выводим в консоль информацию о найденном телефоне и контакте, ищу ошибки
//        System.out.println("Phone ID: " + phoneId);
//        System.out.println("Contact ID: " + contact.getId());
//        System.out.println("Contact Owner ID: " + contact.getOwnerId());
//        System.out.println("User Owner ID: " + ownerId);
//
//        if (!contact.getOwnerId().equals(ownerId)) {
//            throw new RuntimeException("Unauthorized action");
//        }
//
//        try {
//            phoneRepository.delete(phone);
//            System.out.println("Phone deleted successfully");
//        } catch (Exception e) {
//            System.out.println("Error during phone deletion: " + e.getMessage());
//            throw e;
//        }
//    }

    //  для получения ID пользователя из токена
    public Long getUserIdFromToken(String token) {
        return userService.findByToken(token)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
    }
}
