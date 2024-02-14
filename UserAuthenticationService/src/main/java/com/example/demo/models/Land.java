package com.example.demo.models;

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
@Table
public class Land {

    @Id
    @SequenceGenerator(
            name = "land_sequence",
            sequenceName = "land_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "land_sequence"
    )
    private long landId;

    private String landName;

    private long area;

    private String email;

    private String location;


}
