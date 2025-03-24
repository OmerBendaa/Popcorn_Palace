package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.model.Booking;
import com.att.tdp.popcorn_palace.repository.IBookingRepository;
import com.att.tdp.popcorn_palace.service.BookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private IBookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void getAllBookingsTest() {
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        List<Booking> bookings = Arrays.asList(booking1, booking2);
        when(bookingRepository.findAll()).thenReturn(bookings);
        List<Booking> result = bookingService.getAllBookings();
        assertEquals(2, result.size());
        assertEquals(bookings, result);
    }

    // @Test
    // void createBookingTest() {
    //     Booking newBooking = new Booking();
    //     Booking savedBooking = new Booking();
        
    //     when(bookingRepository.save(newBooking)).thenReturn(savedBooking);

    //     Booking result = bookingService.createBooking(newBooking);
    //     assertEquals(savedBooking.getId(), result.getId());
    // }
}