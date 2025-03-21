package com.att.tdp.popcorn_palace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.repository.IMovieRepository;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {
    @Autowired
    private IMovieRepository movieRepository;

    public List<Movie> getAllMovies(){
        return movieRepository.findAll();
    }

    public Movie addMovie(Movie movie){
        validateMovie(movie);
        try{ 
            return movieRepository.save(movie);
        }catch(DataIntegrityViolationException error){
            throw new RuntimeException("A movie with this title already exists");
        }
    } 

    public Movie updateMovie(String title,Movie updatedMovie){
        validateMovie(updatedMovie);
        Movie existingMovie=movieRepository.findByTitle(title).orElseThrow(()-> new RuntimeException("Movie with this title does not exist"));
        Optional <Movie> movieWithNewTitle=movieRepository.findByTitle(updatedMovie.getTitle());
        if(movieWithNewTitle.isPresent()&& !movieWithNewTitle.get().getId().equals(existingMovie.getId())){
            throw new RuntimeException("The updated title is already taken");
        }
        existingMovie.setTitle(updatedMovie.getTitle());
        existingMovie.setGenre(updatedMovie.getGenre());
        existingMovie.setDuration(updatedMovie.getDuration());
        existingMovie.setRating(updatedMovie.getRating());
        existingMovie.setReleaseYear(updatedMovie.getReleaseYear());

        return movieRepository.save(existingMovie);
    }

    public void deleteMovieByTitle(String title){
        Movie movie=movieRepository.findByTitle(title).orElseThrow(()->new RuntimeException("There is no movie with the given title'"+title+"'"));
        movieRepository.delete(movie);
    }

    private void validateMovie(Movie movie){
        if(movie.getTitle()==null||movie.getTitle().trim().isEmpty()){
            throw new IllegalArgumentException("Title is required and can't be empty");
        }
        if(movie.getGenre()==null||movie.getGenre().trim().isEmpty()){
            throw new IllegalArgumentException("Genre is required and can't be empty");
        }
        if(movie.getDuration()<=0){
            throw new IllegalArgumentException("Duration must be greater than 0");
        }
        if(movie.getRating()<0||movie.getRating()>10){
            throw new IllegalArgumentException("Rating must be between 0 and 10");
        }
        if(movie.getReleaseYear()<1900||movie.getReleaseYear()>2022){
            throw new IllegalArgumentException("Release year must be between 1900 and 2022");
        }
    }

    

}
