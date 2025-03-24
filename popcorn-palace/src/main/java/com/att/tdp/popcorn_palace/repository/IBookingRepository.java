package com.att.tdp.popcorn_palace.repository;
import com.att.tdp.popcorn_palace.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IBookingRepository extends JpaRepository<Booking, UUID> {
    Optional<Booking> findByShowtimeIdAndSeatNumber(Long showtimeId, Integer seatNumber);
    List<Booking > findByShowtimeId(Long showtimeId);
}