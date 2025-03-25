package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.model.Booking;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.ShowTime;
import com.att.tdp.popcorn_palace.repository.IBookingRepository;
import com.att.tdp.popcorn_palace.repository.IMovieRepository;
import com.att.tdp.popcorn_palace.repository.IShowTimeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;
import org.mockito.junit.jupiter.MockitoSettings;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingApiTest {

    @Mock
    private IBookingRepository bookingRepository;

    @Mock
    private IShowTimeRepository showTimeRepository;

    @Mock
    private IMovieRepository movieRepository;

    @InjectMocks
    private BookingService bookingService;

    private Movie movie;
    private ShowTime showTime;
    private Booking booking;

    @BeforeEach
    void setUp() {
        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Avengers");
        movie.setGenre("Action");
        movie.setDuration(120);

        showTime = new ShowTime();
        showTime.setId(1L);
        showTime.setMovieId(1L);
        showTime.setTheater("Theater 1");
        showTime.setStartTime(Instant.now());
        showTime.setEndTime(Instant.now().plusSeconds(3*60*60));
        showTime.setPrice(10.0);

        booking = new Booking();
        booking.setShowtimeId(1L);
        booking.setSeatNumber(3);
        booking.setUserId(UUID.randomUUID().toString());

        when(movieRepository.save(movie)).thenReturn(movie);
        when(showTimeRepository.save(showTime)).thenReturn(showTime);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(showTimeRepository.existsById(showTime.getId())).thenReturn(true);
        when(bookingRepository.findByShowtimeIdAndSeatNumber(showTime.getId(), booking.getSeatNumber())).thenReturn(Optional.of(booking));
        movieRepository.save(movie);
        showTimeRepository.save(showTime);
        bookingRepository.save(booking);
    }

    @Test
    void getAllBookingsTest() {
        Booking booking = new Booking();
        booking.setUserId(UUID.randomUUID().toString());
        booking.setShowtimeId(1L);
        booking.setSeatNumber(5);
        List<Booking> bookings = Arrays.asList(booking);
        when(bookingRepository.findAll()).thenReturn(bookings);
        List<Booking> result = bookingService.getAllBookings();
        assertEquals(1, result.size());
        assertEquals(bookings, result);
    }

    @Test
    void createBookingWithNonExistingShowtimeIdTest() {
        Booking newBooking = new Booking();
        newBooking.setShowtimeId(999L);
        newBooking.setSeatNumber(5);
        newBooking.setUserId(UUID.randomUUID().toString());

        when(showTimeRepository.existsById(newBooking.getShowtimeId())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(newBooking);
        });

        assertEquals("There is no showtime with the given showtimeId: 999", exception.getMessage());
    }

    @Test
    void createBookingWithNoShowTimeIdTest() {
        Booking newBooking = new Booking();
        newBooking.setSeatNumber(5);
        newBooking.setUserId(UUID.randomUUID().toString());

        when(showTimeRepository.existsById(newBooking.getShowtimeId())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(newBooking);
        });

        assertEquals("Showtime ID is required and must be a valid number greater than 0", exception.getMessage());
    }
    
    @Test
    void createBookingWithNegativeShowTimeIdTest() {
        Booking newBooking = new Booking();
        newBooking.setShowtimeId(-5L);
        newBooking.setSeatNumber(5);
        newBooking.setUserId(UUID.randomUUID().toString());

        when(showTimeRepository.existsById(newBooking.getShowtimeId())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(newBooking);
        });

        assertEquals("Showtime ID is required and must be a valid number greater than 0", exception.getMessage());
    }
    
    @Test
    void createBookingWithAlreadySelectedSeatTest() {
        Booking newBooking = new Booking();
        newBooking.setShowtimeId(1L);
        newBooking.setSeatNumber(3);
        newBooking.setUserId(UUID.randomUUID().toString());

        when(bookingRepository.findByShowtimeIdAndSeatNumber(newBooking.getShowtimeId(), newBooking.getSeatNumber())).thenReturn(Optional.of(booking));

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            bookingService.createBooking(newBooking);
        });
        assertEquals("The selected seat is already taken for this showtime", exception.getMessage());
    }

    @Test
    void createBookingWithNoSeatNumberTest() {
        Booking newBooking = new Booking();
        newBooking.setShowtimeId(1L);
        newBooking.setUserId(UUID.randomUUID().toString());

        when(showTimeRepository.existsById(newBooking.getShowtimeId())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(newBooking);
        });

        assertEquals("Seat number is required and must be greater than 0", exception.getMessage());
    }

    @Test
    void createBookingWithNegativeSeatNumberTest() {
        Booking newBooking = new Booking();
        newBooking.setShowtimeId(1L);
        newBooking.setSeatNumber(-5);
        newBooking.setUserId(UUID.randomUUID().toString());

        when(showTimeRepository.existsById(newBooking.getShowtimeId())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(newBooking);
        });

        assertEquals("Seat number is required and must be greater than 0", exception.getMessage());
    }

    @Test
    void createBookingWithNoUserIdTest() {
        Booking newBooking = new Booking();
        newBooking.setShowtimeId(1L);
        newBooking.setSeatNumber(5);

        when(showTimeRepository.existsById(newBooking.getShowtimeId())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(newBooking);
        });

        assertEquals("User ID is required and can't be empty", exception.getMessage());
    }

    @Test
    void createBookingWithEmptyUserIdTest() {
        Booking newBooking = new Booking();
        newBooking.setShowtimeId(1L);
        newBooking.setSeatNumber(5);
        newBooking.setUserId("");

        when(showTimeRepository.existsById(newBooking.getShowtimeId())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(newBooking);
        });

        assertEquals("User ID is required and can't be empty", exception.getMessage());
    }
    
    @Test
    void createBookingSuccessfullyTest() {
        Booking newBooking = new Booking();
        newBooking.setShowtimeId(showTime.getId());
        newBooking.setSeatNumber(5);
        newBooking.setUserId(UUID.randomUUID().toString());

        when(showTimeRepository.existsById(newBooking.getShowtimeId())).thenReturn(true);
        when(bookingRepository.save(newBooking)).thenReturn(newBooking);

        Booking createdBooking = bookingService.createBooking(newBooking);
        assertNotEquals(null, createdBooking);
        assertEquals(newBooking.getShowtimeId(), createdBooking.getShowtimeId());
        assertEquals(newBooking.getSeatNumber(), createdBooking.getSeatNumber());
        assertEquals(newBooking.getUserId(), createdBooking.getUserId());
    }
}