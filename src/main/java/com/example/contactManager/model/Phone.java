package com.example.contactManager.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String telephone;

    @ManyToOne
    @JoinColumn(name = "contact_id")
    @JsonBackReference // Это аннотация предотвращает бесконечную рекурсию
    private Contact contact;
}
