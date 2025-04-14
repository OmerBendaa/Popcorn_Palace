package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.model.Booking;
import com.att.tdp.popcorn_palace.service.BookingService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        log.info("#getAllBookings");
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getAllBookings());
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createBooking(@RequestBody Booking booking) {
        log.info("#createBooking: {}", booking);
        Booking savedBooking = bookingService.createBooking(booking);
        log.info("#createBooking: Created Booking: {}", savedBooking);
        Map<String, String> response = new HashMap<>();
        response.put("bookingId", savedBooking.getId().toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}