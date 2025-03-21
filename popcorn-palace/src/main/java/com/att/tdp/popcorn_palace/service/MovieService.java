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
        try{
            return movieRepository.save(movie);
        }catch(DataIntegrityViolationException error){
            throw new RuntimeException("A movie with this title already exists");
        }
    } 

    public Movie updateMovie(String title,Movie updatedMovie){
        Movie existingMovie=movieRepository.findByTitle(title).orElseThrow(()-> new RuntimeException("Movie not found"));
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

    

}
