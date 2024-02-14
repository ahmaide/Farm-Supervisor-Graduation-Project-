package com.example.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Specialization {

    @Id
    @SequenceGenerator(
            name = "specialization_sequence",
            sequenceName = "specialization_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "specialization_sequence"
    )
    private long specId;

    private String email;

    private String type;
}
