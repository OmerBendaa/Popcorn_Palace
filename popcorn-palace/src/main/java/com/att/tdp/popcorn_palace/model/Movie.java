package com.att.tdp.popcorn_palace.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import jakarta.persistence.GenerationType;

@Data
@Entity

public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false,unique=true)
    private String title;
    private String genre;
    private Integer duration;
    private Double rating;
    private Integer releaseYear;
}
