package com.example.contactManager.repository;

import com.example.contactManager.model.Contact;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;


@Repository
public class InMemoryContactRepository implements ContactRepository {
    private final List<Contact> contacts = new ArrayList<>();
    private final AtomicInteger counter = new AtomicInteger(); //для генерации уникальных идентификаторов в памяти.

    @Override
    public Contact save(Contact contact) {
        System.out.println("Используется InMemoryContactRepository");
        if (contact.getId() == null) {
            contact.setId(counter.incrementAndGet());
            contacts.add(contact);
        } else {
            int index = findIndexById(contact.getId());
            if (index >= 0) {
                contacts.set(index, contact);
            } else {
                throw new RuntimeException("Contact not found");
            }
        }
        return contact;
    }

    @Override
    public Optional<Contact> findById(Integer id) {
        return contacts.stream()
                .filter(contact -> contact.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Contact> findAll() {
        return new ArrayList<>(contacts);
    }

    @Override
    public void deleteById(Integer id) {
        contacts.removeIf(contact -> contact.getId().equals(id));
    }

    private int findIndexById(Integer id) {
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }
}
