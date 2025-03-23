package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.model.Booking;
import com.att.tdp.popcorn_palace.repository.IBookingRepository;
import com.att.tdp.popcorn_palace.repository.IShowTimeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingService {
    @Autowired
    private IBookingRepository bookingRepository;

    @Autowired
    private IShowTimeRepository showTimeRepository;
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    
    public Booking createBooking(Booking booking) {
        validateBooking(booking);
        Optional<Booking> existingBooking = bookingRepository.findByShowtimeIdAndSeatNumber(booking.getShowtimeId(), booking.getSeatNumber());
        if (existingBooking.isPresent()) {
            throw new DataIntegrityViolationException("The selected seat is already taken for this showtime");
        }
        return bookingRepository.save(booking);
    }
    private void validateBooking(Booking booking) {
        if (booking.getShowtimeId() == null || booking.getShowtimeId() <= 0) {  
            throw new IllegalArgumentException("Showtime ID is required and must be a valid number greater than 0");
        }
        if (!showTimeRepository.existsById(booking.getShowtimeId())) {
            throw new IllegalArgumentException("There is no showtime with the given showtimeId: " + booking.getShowtimeId());
        }
        if (booking.getSeatNumber() == null || booking.getSeatNumber() <= 0) {
            throw new IllegalArgumentException("Seat number is required and must be greater than 0");
        }
        if (booking.getUserId() == null || booking.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required and can't be empty");
        }
        try {
            UUID.fromString(booking.getUserId());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("User ID must be a valid UUID");
        }
    }

}