package com.example.contactManager.repository;

import com.example.contactManager.model.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class JdbsContactRepository implements ContactRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Contact save(Contact contact) { //Меняю, т к возвращает ИД при запросе null т е не обновляет объект Contact после вставки в базу данных
        if (contact.getId() == null) {
            // Insert
            String sql = "INSERT INTO contact (name, email, owner_id) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, contact.getName(), contact.getEmail(), contact.getOwnerId());

            // Получаем последний вставленный ID
            Integer generatedId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
            if (generatedId != null) {
                contact.setId(generatedId);
            } else {
                throw new RuntimeException("Failed to retrieve the generated ID for the contact");
            }  } else {
                String sql = "UPDATE contact SET name = ?, email = ?, owner_id = ? WHERE id = ?";
                jdbcTemplate.update(sql, contact.getName(), contact.getEmail(), contact.getOwnerId(), contact.getId());
            }
            return contact;
    }

    @Override
    public Optional<Contact> findById(Integer id) {
        String sql = "SELECT * FROM contact WHERE id = ?";
        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                return Optional.of(Contact.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .email(rs.getString("email"))
                        .ownerId(rs.getInt("owner_id"))
                        .build());
            } else {
                return Optional.empty();
            }
        }, id);
    }

    @Override
    public List<Contact> findAll() {
        String sql = "SELECT * FROM contact";
        return jdbcTemplate.query(sql, (rs, rowNum) -> Contact.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .build());
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM contact WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }


}
