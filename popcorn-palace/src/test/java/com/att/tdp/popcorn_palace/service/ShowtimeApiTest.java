package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.exception.NotFoundException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ShowtimeApiTest {

    @Mock
    private IShowTimeRepository showTimeRepository;

    @Mock
    private IMovieRepository movieRepository;

    @Mock
    private IBookingRepository bookingRepository;

    @InjectMocks
    private ShowTimeService showTimeService;

    private Movie movie;
    private Movie secondMovie;
    private ShowTime showTime;
    private ShowTime secondShowTime;

    @BeforeEach
    void setUp() {
        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Avengers");
        movie.setGenre("Action");
        movie.setDuration(120);

        secondMovie = new Movie();
        secondMovie.setId(2L);
        secondMovie.setTitle("Endgame");
        secondMovie.setGenre("Action");
        secondMovie.setDuration(180);

        showTime = new ShowTime();
        showTime.setId(1L);
        showTime.setMovieId(1L);
        showTime.setTheater("Theater 1");
        showTime.setStartTime(Instant.now());
        showTime.setEndTime(Instant.now().plusSeconds(2 * 60 * 60));
        showTime.setPrice(10.0);

        secondShowTime = new ShowTime();
        secondShowTime.setId(2L);
        secondShowTime.setMovieId(2L);
        secondShowTime.setTheater("Theater 2");
        secondShowTime.setStartTime(Instant.now());
        secondShowTime.setEndTime(Instant.now().plusSeconds(3 * 60 * 60));
        secondShowTime.setPrice(10.0);

        when(movieRepository.save(movie)).thenReturn(movie);
        when(movieRepository.save(secondMovie)).thenReturn(secondMovie);
        when(showTimeRepository.save(showTime)).thenReturn(showTime);
        when(showTimeRepository.existsById(showTime.getId())).thenReturn(true);
        when(movieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));
        when(movieRepository.findById(secondMovie.getId())).thenReturn(Optional.of(secondMovie));
        movieRepository.save(movie);
        movieRepository.save(secondMovie);
        showTimeRepository.save(showTime);
    }

    @Test
    void getAllShowTimesTest() {
        ShowTime showTime = new ShowTime();
        showTime.setId(1L);
        showTime.setMovieId(1L);
        showTime.setTheater("Theater 1");
        showTime.setStartTime(Instant.now());
        showTime.setEndTime(Instant.now().plusSeconds(2 * 60 * 60));
        showTime.setPrice(10.0);
        showTimeRepository.save(showTime);
        List<ShowTime> showTimes = Arrays.asList(showTime);
        when(showTimeRepository.findAll()).thenReturn(showTimes);
        List<ShowTime> result = showTimeService.getAllShowTimes();
        assertEquals(1, result.size());
        assertEquals(showTimes, result);
    }

    @Test
    void getShowTimeByIdSuccessTest() {
        ShowTime showTime = new ShowTime();
        showTime.setId(1L);
        when(showTimeRepository.findById(showTime.getId())).thenReturn(Optional.of(showTime));
        ShowTime result = showTimeService.getShowTimeById(showTime.getId());
        assertEquals(showTime.getId(), result.getId());
    }

    @Test
    void getShowTimeByIdFail() {
        Long nonExistentId = 5L;
        when(showTimeRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            showTimeService.getShowTimeById(nonExistentId);
        });
        assertEquals("There is no showtime with the given id: 5", exception.getMessage());
    }

    @Test
    void addShowTimeSuccessfullyTest() {
        ShowTime showTime = new ShowTime();
        showTime.setMovieId(1L);
        showTime.setTheater("Theater 1");
        showTime.setStartTime(Instant.now());
        showTime.setEndTime(Instant.now().plusSeconds(2 * 60 * 60));
        showTime.setPrice(10.0);
        when(movieRepository.findById(showTime.getMovieId())).thenReturn(Optional.of(movie));
        when(showTimeRepository.save(showTime)).thenReturn(showTime);
        ShowTime result = showTimeService.addShowTime(showTime);
        assertEquals(showTime, result);
    }

    @Test
    void addShowtimeWithoutMovieIdTest() {
        ShowTime showTime = new ShowTime();
        showTime.setTheater("Theater 1");
        showTime.setStartTime(Instant.now());
        showTime.setEndTime(Instant.now().plusSeconds(2 * 60 * 60));
        showTime.setPrice(10.0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            showTimeService.addShowTime(showTime);
        });
        assertEquals("movieId is required and must be a valid id", exception.getMessage());
    }

    @Test
    void addShowTimeNegativeMovieIdTest() {
        ShowTime showTime = new ShowTime();
        showTime.setMovieId(-1L);
        showTime.setTheater("Theater 1");
        showTime.setStartTime(Instant.now());
        showTime.setEndTime(Instant.now().plusSeconds(2 * 60 * 60));
        showTime.setPrice(10.0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            showTimeService.addShowTime(showTime);
        });
        assertEquals("movieId is required and must be a valid id", exception.getMessage());
    }

    @Test
    void addShowTimeFailNonExistMovieTest() {
        ShowTime showTime = new ShowTime();
        showTime.setMovieId(10L);
        showTime.setTheater("Theater 1");
        showTime.setStartTime(Instant.now());
        showTime.setEndTime(Instant.now().plusSeconds(2 * 60 * 60));
        showTime.setPrice(10.0);
        when(movieRepository.findById(showTime.getMovieId())).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            showTimeService.addShowTime(showTime);
        });
        assertEquals("Movie with the given id does not exist", exception.getMessage());
    }

    @Test
    void addShowTimeFailTheaterNullTest() {
        ShowTime showTime = new ShowTime();
        showTime.setMovieId(1L);
        showTime.setStartTime(Instant.now());
        showTime.setEndTime(Instant.now().plusSeconds(2 * 60 * 60));
        showTime.setPrice(10.0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            showTimeService.addShowTime(showTime);
        });
        assertEquals("Theater is required and can't be empty", exception.getMessage());
    }

    @Test
    void addShowTimeFailTheaterEmptyTest() {
        ShowTime showTime = new ShowTime();
        showTime.setMovieId(1L);
        showTime.setTheater("");
        showTime.setStartTime(Instant.now());
        showTime.setEndTime(Instant.now().plusSeconds(2 * 60 * 60));
        showTime.setPrice(10.0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            showTimeService.addShowTime(showTime);
        });
        assertEquals("Theater is required and can't be empty", exception.getMessage());
    }

    @Test
    void addShowTimeFailStartTimeNullTest() {
        ShowTime showTime = new ShowTime();
        showTime.setMovieId(1L);
        showTime.setTheater("Theater 1");
        showTime.setEndTime(Instant.now().plusSeconds(2 * 60 * 60));
        showTime.setPrice(10.0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            showTimeService.addShowTime(showTime);
        });
        assertEquals("startTime is required and can't be null", exception.getMessage());
    }

    @Test
    void addShowTimeFailEndTimeNullTest() {
        ShowTime showTime = new ShowTime();
        showTime.setMovieId(1L);
        showTime.setTheater("Theater 1");
        showTime.setPrice(10.0);
        showTime.setStartTime(Instant.now());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            showTimeService.addShowTime(showTime);
        });
        assertEquals("endTime is required and can't be null", exception.getMessage());
    }

    @Test
    void addShowTimeFailEndTimeBeforeStartTimeTest() {
        ShowTime showTime = new ShowTime();
        showTime.setMovieId(1L);
        showTime.setTheater("Theater 1");
        showTime.setPrice(10.0);
        showTime.setStartTime(Instant.now().plusSeconds(2 * 60 * 60));
        showTime.setEndTime(Instant.now());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            showTimeService.addShowTime(showTime);
        });
        assertEquals("startTime must be earlier than endTime", exception.getMessage());
    }

    @Test
    void addShowTimeFailPriceNullTest() {
        ShowTime showTime = new ShowTime();
        showTime.setMovieId(1L);
        showTime.setTheater("Theater 1");
        showTime.setStartTime(Instant.now());
        showTime.setEndTime(Instant.now().plusSeconds(2 * 60 * 60));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            showTimeService.addShowTime(showTime);
        });
        assertEquals("price is required and must be greater than 0", exception.getMessage());
    }

    @Test
    void addShowTimeFailPriceNegativeTest() {
        ShowTime showTime = new ShowTime();
        showTime.setMovieId(1L);
        showTime.setTheater("Theater 1");
        showTime.setPrice(-5.0);
        showTime.setStartTime(Instant.now());
        showTime.setEndTime(Instant.now().plusSeconds(2 * 60 * 60));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            showTimeService.addShowTime(showTime);
        });
        assertEquals("price is required and must be greater than 0", exception.getMessage());
    }

    @Test
    void addShowTimeFailShowtimeShorterThanMovieDurationTest() {
        ShowTime showTime = new ShowTime();
        showTime.setMovieId(1L);
        showTime.setTheater("Theater 1");
        showTime.setPrice(10.0);
        showTime.setStartTime(Instant.now());
        showTime.setEndTime(Instant.now().plusSeconds(60 * 60));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            showTimeService.addShowTime(showTime);
        });
        assertEquals("The duration of a showtime must be equal or greater than the movie's duration",
                exception.getMessage());
    }

    @Test
    void addOverlapsingShowtimeTest() {
        {
            ShowTime showTime = new ShowTime();
            showTime.setId(1L);
            showTime.setMovieId(1L);
            showTime.setTheater("Theater 1");
            showTime.setStartTime(Instant.now());
            showTime.setEndTime(Instant.now().plusSeconds(2 * 60 * 60));
            showTime.setPrice(10.0);
            when(showTimeRepository.findOverlappingShowTimes(showTime.getTheater(), showTime.getStartTime(),
                    showTime.getEndTime())).thenReturn(Arrays.asList(showTime));
            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
                showTimeService.addShowTime(showTime);
            });
            assertEquals("The showTime you are trying to add overlaps with an existing showTime",
                    exception.getMessage());
        }
    }

    @Test
    void updateSuccessTest() {
        ShowTime updatedShowTime = new ShowTime();
        updatedShowTime.setId(1L);
        updatedShowTime.setMovieId(2L);
        updatedShowTime.setTheater("Theater 1");
        updatedShowTime.setStartTime(showTime.getStartTime());
        updatedShowTime.setEndTime(showTime.getStartTime().plusSeconds(3 * 60 * 60));
        updatedShowTime.setPrice(10.0);

        when(showTimeRepository.findById(updatedShowTime.getId())).thenReturn(Optional.of(showTime));
        when(movieRepository.findById(updatedShowTime.getMovieId())).thenReturn(Optional.of(secondMovie));
        when(showTimeRepository.save(updatedShowTime)).thenReturn(updatedShowTime);
        showTimeService.updateShowTime(updatedShowTime.getId(), updatedShowTime);

        when(showTimeRepository.findById(updatedShowTime.getId())).thenReturn(Optional.of(updatedShowTime));
        ShowTime result = showTimeRepository.findById(updatedShowTime.getId()).get();
        assertEquals(updatedShowTime.getMovieId(), result.getMovieId());
        assertEquals(updatedShowTime.getEndTime(), result.getEndTime());
        assertEquals(updatedShowTime.getTheater(), result.getTheater());
        assertEquals(updatedShowTime.getStartTime(), result.getStartTime());
        assertEquals(updatedShowTime.getPrice(), result.getPrice());

    }

    @Test
    void updateWithNotExistingShowtimeTest() {
        ShowTime updatedShowTime = new ShowTime();
        updatedShowTime.setId(10L);
        updatedShowTime.setMovieId(1L);
        updatedShowTime.setTheater("Theater 1");
        updatedShowTime.setStartTime(Instant.now());
        updatedShowTime.setEndTime(Instant.now().plusSeconds(2 * 60 * 60));
        updatedShowTime.setPrice(10.0);
        when(showTimeRepository.findById(updatedShowTime.getId())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            showTimeService.updateShowTime(updatedShowTime.getId(), updatedShowTime);
        });
        assertEquals("There is no showTime with the given Id '10'", exception.getMessage());
    }

    @Test
    void updateFailNegativeMovieIdTest() {
        ShowTime updatedShowTime = new ShowTime();
        updatedShowTime.setId(10L);
        updatedShowTime.setMovieId(-1L);
        updatedShowTime.setTheater("Theater 1");
        updatedShowTime.setStartTime(Instant.now());
        updatedShowTime.setEndTime(Instant.now().plusSeconds(2 * 60 * 60));
        updatedShowTime.setPrice(10.0);

        when(showTimeRepository.findById(updatedShowTime.getId())).thenReturn(Optional.of(showTime));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            showTimeService.updateShowTime(updatedShowTime.getId(), updatedShowTime);
        });
        assertEquals("movieId must be a valid id - greater than 0", exception.getMessage());
    }

    @Test
    void updateFailNonExistingMovieTest() {
        ShowTime updatedShowTime = new ShowTime();
        updatedShowTime.setId(1L);
        updatedShowTime.setMovieId(90L);
        updatedShowTime.setTheater("Theater 1");
        updatedShowTime.setStartTime(Instant.now());
        updatedShowTime.setEndTime(Instant.now().plusSeconds(2 * 60 * 60));
        updatedShowTime.setPrice(10.0);

        when(showTimeRepository.findById(updatedShowTime.getId())).thenReturn(Optional.of(showTime));
        when(movieRepository.findById(updatedShowTime.getMovieId())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            showTimeService.updateShowTime(updatedShowTime.getId(), updatedShowTime);
        });
        assertEquals("There is no movie with the given id '90'", exception.getMessage());
    }

    @Test
    void partialUpdateSuccessTest() {
        ShowTime partialUpdateShowTime = new ShowTime();
        partialUpdateShowTime.setId(1L);
        partialUpdateShowTime.setTheater("Theater 5");
        partialUpdateShowTime.setPrice(15.0);

        when(showTimeRepository.findById(partialUpdateShowTime.getId())).thenReturn(Optional.of(showTime));
        when(movieRepository.findById(showTime.getMovieId())).thenReturn(Optional.of(movie));
        when(showTimeRepository.save(showTime)).thenReturn(showTime);

        showTimeService.updateShowTime(partialUpdateShowTime.getId(), partialUpdateShowTime);

        showTime.setTheater(partialUpdateShowTime.getTheater());
        showTime.setPrice(partialUpdateShowTime.getPrice());
        when(showTimeRepository.findById(partialUpdateShowTime.getId())).thenReturn(Optional.of(showTime));
        ShowTime result = showTimeRepository.findById(partialUpdateShowTime.getId()).get();

        assertEquals(partialUpdateShowTime.getTheater(), result.getTheater());
        assertEquals(partialUpdateShowTime.getPrice(), result.getPrice());
        assertEquals(showTime.getMovieId(), result.getMovieId());
        assertEquals(showTime.getStartTime(), result.getStartTime());
        assertEquals(showTime.getEndTime(), result.getEndTime());
    }

    @Test
    void updateFailStartTimeAfterEndTimeTest() {
        ShowTime updatedShowTime = new ShowTime();
        updatedShowTime.setId(1L);
        updatedShowTime.setMovieId(1L);
        updatedShowTime.setTheater("Theater 1");
        updatedShowTime.setStartTime(Instant.now().plusSeconds(2 * 60 * 60)); // Set startTime after endTime
        updatedShowTime.setEndTime(Instant.now());
        updatedShowTime.setPrice(10.0);

        when(showTimeRepository.findById(updatedShowTime.getId())).thenReturn(Optional.of(showTime));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            showTimeService.updateShowTime(updatedShowTime.getId(), updatedShowTime);
        });

        assertEquals("startTime must be earlier than endTime", exception.getMessage());
    }

    @Test
    void updateFailScreeningTimeTooShortTest() {
        ShowTime updatedShowTime = new ShowTime();
        updatedShowTime.setId(1L);
        updatedShowTime.setMovieId(2L);
        updatedShowTime.setTheater("Theater 1");
        updatedShowTime.setStartTime(Instant.now());
        updatedShowTime.setEndTime(Instant.now().plusSeconds(2 * 60 * 60));
        updatedShowTime.setPrice(10.0);

        when(showTimeRepository.findById(updatedShowTime.getId())).thenReturn(Optional.of(showTime));
        when(movieRepository.findById(updatedShowTime.getMovieId())).thenReturn(Optional.of(secondMovie));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            showTimeService.updateShowTime(updatedShowTime.getId(), updatedShowTime);
        });
        assertEquals("The duration of a showtime must be equal or greater than the movie's duration",
                exception.getMessage());
    }

    @Test
    void updateFailShowtimeOverlapsesTest() {
        // Update secondShowTime to have the same theater and overlapping time with the
        // first showTime
        secondShowTime.setTheater("Theater 1");
        secondShowTime.setStartTime(showTime.getStartTime().plusSeconds(30 * 60)); // Overlapping time
        secondShowTime.setEndTime(showTime.getEndTime().plusSeconds(30 * 60 * 60 * 60)); // Overlapping time

        when(showTimeRepository.findById(secondShowTime.getId())).thenReturn(Optional.of(secondShowTime));
        when(movieRepository.findById(secondShowTime.getMovieId())).thenReturn(Optional.of(secondMovie));
        when(showTimeRepository.findOverlappingShowTimes(secondShowTime.getTheater(), secondShowTime.getStartTime(),
                secondShowTime.getEndTime())).thenReturn(Arrays.asList(showTime));

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            showTimeService.updateShowTime(secondShowTime.getId(), secondShowTime);
        });

        assertEquals("The updated showTime overlaps with an existing showTime", exception.getMessage());
    }

    @Test
    void deleteShowTimeTest() {
        when(showTimeRepository.findById(showTime.getId())).thenReturn(Optional.of(showTime));
        when(showTimeRepository.existsById(showTime.getId())).thenReturn(true);

        showTimeService.deleteShowTimeById(showTime.getId());

        verify(showTimeRepository, times(1)).delete(showTime);
        when(showTimeRepository.findById(showTime.getId())).thenReturn(Optional.empty());
        when(showTimeRepository.existsById(showTime.getId())).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            showTimeService.deleteShowTimeById(showTime.getId());
        });

        assertEquals("There is no showTime with the given id '1'", exception.getMessage());
    }
}