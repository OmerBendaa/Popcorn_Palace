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
class MovieApiTest {

    @Mock
    private IShowTimeRepository showTimeRepository;

    @Mock
    private IMovieRepository movieRepository;

    @Mock
    private IBookingRepository bookingRepository;

    @InjectMocks
    private MovieService movieService;

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
        showTime.setMovieId(2L);
        showTime.setTheater("Theater 1");
        showTime.setStartTime(Instant.now());
        showTime.setEndTime(Instant.now().plusSeconds(3 * 60 * 60));
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
        when(showTimeRepository.save(secondShowTime)).thenReturn(secondShowTime);
        when(showTimeRepository.existsById(showTime.getId())).thenReturn(true);
        when(movieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));
        when(movieRepository.findById(secondMovie.getId())).thenReturn(Optional.of(secondMovie));
        movieRepository.save(movie);
        movieRepository.save(secondMovie);
        showTimeRepository.save(showTime);
        showTimeRepository.save(secondShowTime);
    }

    @Test
    void testGetAllMoviesTest() {
        List<Movie> movies = Arrays.asList(movie, secondMovie);
        when(movieRepository.findAll()).thenReturn(movies);
        assertEquals(movies, movieService.getAllMovies());
    }

    @Test
    void addMovieSuccessfullyTest() {
        Movie newMovie = new Movie();
        newMovie.setId(3L);
        newMovie.setTitle("Spider-Man");
        newMovie.setGenre("Action");
        newMovie.setDuration(130);
        newMovie.setRating(10.0);
        newMovie.setReleaseYear(2021);

        when(movieRepository.save(newMovie)).thenReturn(newMovie);

        Movie savedMovie = movieService.addMovie(newMovie);

        verify(movieRepository, times(1)).save(newMovie);

        assertEquals(newMovie, savedMovie);
    }

    @Test
    void addMovieFailTitleAlreadyExistsTest() {
        Movie newMovie = new Movie();
        newMovie.setId(3L);
        newMovie.setTitle("Avengers");
        newMovie.setGenre("Action");
        newMovie.setDuration(130);
        newMovie.setRating(10.0);
        newMovie.setReleaseYear(2021);

        when(movieRepository.save(newMovie))
                .thenThrow(new DataIntegrityViolationException("A movie with this title already exists"));

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            movieService.addMovie(newMovie);
        });

        assertEquals("A movie with this title already exists", exception.getMessage());
    }

    @Test
    void addMovieFailTitleNullTest() {
        Movie newMovie = new Movie();
        newMovie.setId(3L);
        newMovie.setGenre("Action");
        newMovie.setDuration(130);
        newMovie.setRating(10.0);
        newMovie.setReleaseYear(2021);

        when(movieRepository.save(newMovie))
                .thenThrow(new DataIntegrityViolationException("A movie with this title already exists"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            movieService.addMovie(newMovie);
        });

        assertEquals("Title is required and can't be empty", exception.getMessage());
    }

    @Test
    void addMovieFailTitleIsEmptyTest() {
        Movie newMovie = new Movie();
        newMovie.setId(3L);
        newMovie.setTitle("");
        newMovie.setGenre("Action");
        newMovie.setDuration(130);
        newMovie.setRating(10.0);
        newMovie.setReleaseYear(2021);

        when(movieRepository.save(newMovie))
                .thenThrow(new DataIntegrityViolationException("A movie with this title already exists"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            movieService.addMovie(newMovie);
        });

        assertEquals("Title is required and can't be empty", exception.getMessage());
    }

    @Test
    void addMovieFailGenreIsNullTest() {
        Movie newMovie = new Movie();
        newMovie.setId(3L);
        newMovie.setTitle("Spider-Man");
        newMovie.setDuration(130);
        newMovie.setRating(10.0);
        newMovie.setReleaseYear(2021);

        when(movieRepository.save(newMovie))
                .thenThrow(new DataIntegrityViolationException("A movie with this title already exists"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            movieService.addMovie(newMovie);
        });

        assertEquals("Genre is required and can't be empty", exception.getMessage());
    }

    @Test
    void addMovieFailGenreIsEmptyTest() {
        Movie newMovie = new Movie();
        newMovie.setId(3L);
        newMovie.setTitle("Spider-Man");
        newMovie.setGenre("");
        newMovie.setDuration(130);
        newMovie.setRating(10.0);
        newMovie.setReleaseYear(2021);

        when(movieRepository.save(newMovie))
                .thenThrow(new DataIntegrityViolationException("A movie with this title already exists"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            movieService.addMovie(newMovie);
        });

        assertEquals("Genre is required and can't be empty", exception.getMessage());
    }

    @Test
    void addMovieFailGenreContainsNumbersTest() {
        Movie newMovie = new Movie();
        newMovie.setId(3L);
        newMovie.setTitle("Spider-Man");
        newMovie.setGenre("Ac3ti4on");
        newMovie.setDuration(130);
        newMovie.setRating(10.0);
        newMovie.setReleaseYear(2021);

        when(movieRepository.save(newMovie))
                .thenThrow(new DataIntegrityViolationException("A movie with this title already exists"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            movieService.addMovie(newMovie);
        });

        assertEquals("Genre can't contain numbers", exception.getMessage());
    }

    @Test
    void addMovieFailDurationIsNullTest() {
        Movie newMovie = new Movie();
        newMovie.setId(3L);
        newMovie.setTitle("Spider-Man");
        newMovie.setGenre("Action");
        newMovie.setRating(10.0);
        newMovie.setReleaseYear(2021);

        when(movieRepository.save(newMovie))
                .thenThrow(new DataIntegrityViolationException("A movie with this title already exists"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            movieService.addMovie(newMovie);
        });

        assertEquals("Duration must be greater than 0", exception.getMessage());
    }

    @Test
    void addMovieFailNegativeDurationTest() {
        Movie newMovie = new Movie();
        newMovie.setId(3L);
        newMovie.setTitle("Spider-Man");
        newMovie.setGenre("Action");
        newMovie.setDuration(-20);
        newMovie.setRating(10.0);
        newMovie.setReleaseYear(2021);

        when(movieRepository.save(newMovie))
                .thenThrow(new DataIntegrityViolationException("A movie with this title already exists"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            movieService.addMovie(newMovie);
        });

        assertEquals("Duration must be greater than 0", exception.getMessage());
    }

    @Test
    void addMovieFailNegativeRatingTest() {
        Movie newMovie = new Movie();
        newMovie.setId(3L);
        newMovie.setTitle("Spider-Man");
        newMovie.setGenre("Action");
        newMovie.setDuration(130);
        newMovie.setRating(-5.0);
        newMovie.setReleaseYear(2021);

        when(movieRepository.save(newMovie))
                .thenThrow(new DataIntegrityViolationException("A movie with this title already exists"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            movieService.addMovie(newMovie);
        });

        assertEquals("Rating must be greater than 0", exception.getMessage());
    }

    @Test
    void addMovieFailReleaseYearIsNullTest() {
        Movie newMovie = new Movie();
        newMovie.setId(3L);
        newMovie.setTitle("Spider-Man");
        newMovie.setGenre("Action");
        newMovie.setDuration(130);
        newMovie.setRating(10.0);

        when(movieRepository.save(newMovie))
                .thenThrow(new DataIntegrityViolationException("A movie with this title already exists"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            movieService.addMovie(newMovie);
        });

        assertEquals("Release year is required and must be greater than 0", exception.getMessage());
    }

    @Test
    void addMovieFailNegativeReleaseYearTest() {
        Movie newMovie = new Movie();
        newMovie.setId(3L);
        newMovie.setTitle("Spider-Man");
        newMovie.setGenre("Action");
        newMovie.setDuration(130);
        newMovie.setRating(10.0);
        newMovie.setReleaseYear(-5);

        when(movieRepository.save(newMovie))
                .thenThrow(new DataIntegrityViolationException("A movie with this title already exists"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            movieService.addMovie(newMovie);
        });

        assertEquals("Release year is required and must be greater than 0", exception.getMessage());
    }

    @Test
    void updateMovieSuccessfullTest() {
        // Create a new Movie object with updated details
        Movie updatedMovie = new Movie();
        updatedMovie.setId(2L); // Set the correct ID
        updatedMovie.setTitle("Spider-Man");
        updatedMovie.setGenre("Action");
        updatedMovie.setDuration(60);
        updatedMovie.setRating(10.0);
        updatedMovie.setReleaseYear(2021);

        when(movieRepository.findByTitle("Endgame")).thenReturn(Optional.of(secondMovie));
        when(movieRepository.save(updatedMovie)).thenReturn(updatedMovie);

        Movie result = movieService.updateMovie("Endgame", updatedMovie);
        assertEquals(updatedMovie, result);
    }

    @Test
    void updateFailMovieNonExistingMovieTest() {
        Movie updatedMovie = new Movie();
        updatedMovie.setId(2L);
        updatedMovie.setTitle("Spider-Man");
        updatedMovie.setGenre("Action");
        updatedMovie.setDuration(60);
        updatedMovie.setRating(10.0);
        updatedMovie.setReleaseYear(2021);

        when(movieRepository.findByTitle("Deadpool")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            movieService.updateMovie("Deadpool", updatedMovie);
        });

        assertEquals("There is no movie with the given title 'Deadpool'", exception.getMessage());
    }

    @Test
    void updateFailMovieTitleEmptyTest() {
        Movie updatedMovie = new Movie();
        updatedMovie.setId(2L);
        updatedMovie.setTitle("");
        updatedMovie.setGenre("Action");
        updatedMovie.setDuration(60);
        updatedMovie.setRating(10.0);
        updatedMovie.setReleaseYear(2021);

        when(movieRepository.findByTitle("Endgame")).thenReturn(Optional.of(secondMovie));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            movieService.updateMovie("Endgame", updatedMovie);
        });

        assertEquals("Title can't be empty", exception.getMessage());
    }

    @Test
    void updateFailTitleAlreadyTakenTest() {
        Movie updatedMovie = new Movie();
        updatedMovie.setId(2L);
        updatedMovie.setTitle("Avengers");
        updatedMovie.setGenre("Action");
        updatedMovie.setDuration(60);
        updatedMovie.setRating(10.0);
        updatedMovie.setReleaseYear(2021);

        when(movieRepository.findByTitle("Endgame")).thenReturn(Optional.of(secondMovie));
        when(movieRepository.findByTitle("Avengers")).thenReturn(Optional.of(movie));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            movieService.updateMovie("Endgame", updatedMovie);
        });

        assertEquals("The updated title is already taken", exception.getMessage());
    }

    @Test
    void updateFailShowTimeNotLongEnoughTest() {
        Movie updatedMovie = new Movie();
        updatedMovie.setId(2L); 
        updatedMovie.setTitle("Endgame");
        updatedMovie.setGenre("Action");
        updatedMovie.setDuration(300); 
        updatedMovie.setRating(10.0);
        updatedMovie.setReleaseYear(2021);

       
        when(movieRepository.findByTitle("Endgame")).thenReturn(Optional.of(secondMovie));
        when(showTimeRepository.findByMovieId(2L)).thenReturn(Arrays.asList(showTime));

        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            movieService.updateMovie("Endgame", updatedMovie);
        });

        assertEquals("The duration of showtime: 1 is too short for the updated movie's duration and needs to be extended", exception.getMessage());
    }
    @Test
    void deleteMovieTest() {
        // Mock the repository methods to return the existing movie when queried by title
        when(movieRepository.findByTitle(movie.getTitle())).thenReturn(Optional.of(movie));
        when(movieRepository.existsById(movie.getId())).thenReturn(true);
    
        // Call the deleteMovieByTitle method to delete the movie
        movieService.deleteMovieByTitle(movie.getTitle());
    
        verify(movieRepository, times(1)).delete(movie);
    
        when(movieRepository.findByTitle(movie.getTitle())).thenReturn(Optional.empty());
        when(movieRepository.existsById(movie.getId())).thenReturn(false);
    
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            movieService.deleteMovieByTitle(movie.getTitle());
        });
    
        assertEquals("There is no movie with the given title 'Avengers'", exception.getMessage());
    }
}