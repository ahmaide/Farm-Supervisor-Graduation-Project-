package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Pest {

    @Id
    @SequenceGenerator(
            name = "pest_sequence",
            sequenceName = "pest_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "pst_sequence"
    )
    private long pestId;

    private long cropId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "EEE MMM dd HH:mm:ss yyyy", locale = "en")
    private Date infectedDate;

    private String pestType;

    private int typeId;

    private String typeName;
}
