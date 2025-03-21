package com.att.tdp.popcorn_palace.controller;
import org.springframework.web.bind.annotation.RestController;
import com.att.tdp.popcorn_palace.service.MovieService;
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

@RestController
@RequestMapping("/movies")
public class MovieController {
    @Autowired
    private MovieService movieService;

    @GetMapping("/all")
    public ResponseEntity<List<Movie>> getAllMovies(){
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @PostMapping
    public ResponseEntity<?> addMovie(@RequestBody Movie movie){
        try{
            return new ResponseEntity<>(movieService.addMovie(movie),HttpStatus.CREATED);
        }catch(RuntimeException error){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.getMessage());
        }
    }

    @PostMapping("/update/{movieTitle}")
    public ResponseEntity<?> updateMovie(@PathVariable String title,@RequestBody Movie updatedMovie){
        try{
            return ResponseEntity.ok(movieService.updateMovie(title, updatedMovie));
        }catch(RuntimeException error){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.getMessage());
        }
    }

    @DeleteMapping("/{movieTitle}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String title){
        try{
            movieService.deleteMovieByTitle(title);
            return ResponseEntity.noContent().build();
        }catch(RuntimeException error){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}

