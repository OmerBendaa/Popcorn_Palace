package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.exception.NotFoundException;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.ShowTime;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ShowtimeApiTest {

    @Mock
    private IShowTimeRepository showTimeRepository;

    @Mock
    private IMovieRepository movieRepository;

    @InjectMocks
    private ShowTimeService showTimeService;

    private Movie movie;
    private Movie secondMovie;
    private ShowTime showTime;

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
}