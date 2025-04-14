package com.att.tdp.popcorn_palace.controller;

import org.springframework.web.bind.annotation.RestController;
import com.att.tdp.popcorn_palace.service.MovieService;

import lombok.extern.slf4j.Slf4j;

import com.att.tdp.popcorn_palace.model.Movie;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/movies")
public class MovieController {
    @Autowired
    private MovieService movieService;

    @GetMapping("/all")
    public ResponseEntity<List<Movie>> getAllMovies() {
        log.info("#getAllMovies");
        List<Movie> Movies = movieService.getAllMovies();
        return ResponseEntity.status(HttpStatus.OK).body(Movies);
    }

    @PostMapping
    public ResponseEntity<Movie> addMovie(@RequestBody Movie movie) {
        log.info("#addMovie: {}",movie);
        Movie addedMovie = movieService.addMovie(movie);
        log.info("#addMovie >> addedMovie: {}",addedMovie);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedMovie);
    }

    @PostMapping("/update/{movieTitle}")
    public ResponseEntity<Movie> updateMovie(@PathVariable String movieTitle, @RequestBody Movie updatedMovie) {
        log.info("#updateMovie >> By Title: {}",movieTitle);
        Movie createdMovie = movieService.updateMovie(movieTitle, updatedMovie);
        log.info("#updateMovie >> Updated Movie: {}",createdMovie);
        return ResponseEntity.status(HttpStatus.OK).body(createdMovie);
    }

    @DeleteMapping("/{movieTitle}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String movieTitle) {
        log.info("#deleteMovie >> By Title: {}",movieTitle);
        movieService.deleteMovieByTitle(movieTitle);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
