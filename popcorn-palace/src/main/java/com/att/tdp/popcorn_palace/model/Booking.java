package com.att.tdp.popcorn_palace.model;
import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Booking {
    
    @Id
    private UUID id;

    private Long showtimeId;
    private Integer seatNumber;
    private String userId;

    public Booking() {
        this.id = UUID.randomUUID();
    }
}
