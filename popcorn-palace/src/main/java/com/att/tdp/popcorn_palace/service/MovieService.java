package com.att.tdp.popcorn_palace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.att.tdp.popcorn_palace.exception.NotFoundException;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.ShowTime;
import com.att.tdp.popcorn_palace.repository.IMovieRepository;
import com.att.tdp.popcorn_palace.repository.IShowTimeRepository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
@Service
public class MovieService {

    @Autowired
    private IMovieRepository movieRepository;
    @Autowired
    private IShowTimeRepository showTimeRepository;
    @Autowired
    private ShowTimeService showTimeService;

    public List<Movie> getAllMovies(){
        return movieRepository.findAll();
    }

    public Movie addMovie(Movie movie){
        validateMovie(movie);
        try{ 
            return movieRepository.save(movie);
        }catch(DataIntegrityViolationException error){
            throw new DataIntegrityViolationException("A movie with this title already exists");
        }
    } 

    public Movie updateMovie(String title,Movie updatedMovie){
        Movie existingMovie=movieRepository.findByTitle(title).orElseThrow(()-> new NotFoundException("There is no movie with the given title '"+title+"'"));
        Integer existingMovieDuration=existingMovie.getDuration();
        if(updatedMovie.getTitle()!=null && updatedMovie.getTitle().trim().isEmpty()){
            throw new IllegalArgumentException("Title can't be empty");
        }
        if (updatedMovie.getTitle() != null && !updatedMovie.getTitle().trim().isEmpty()) {
            Optional<Movie> movieWithNewTitle = movieRepository.findByTitle(updatedMovie.getTitle());
            if (movieWithNewTitle.isPresent() && !movieWithNewTitle.get().getId().equals(existingMovie.getId())) {
                throw new RuntimeException("The updated title is already taken");
            }
            existingMovie.setTitle(updatedMovie.getTitle());
        }
        if (updatedMovie.getGenre() != null && !updatedMovie.getGenre().trim().isEmpty()) {
            if (updatedMovie.getGenre().matches(".*\\d.*")) {
                throw new IllegalArgumentException("Genre can't contain numbers");
            }
            existingMovie.setGenre(updatedMovie.getGenre());
        }
    
        if (updatedMovie.getDuration() != null && updatedMovie.getDuration() > 0) {
            existingMovie.setDuration(updatedMovie.getDuration());
        }
    
        if (updatedMovie.getRating() != null && updatedMovie.getRating() >= 0) {
            existingMovie.setRating(updatedMovie.getRating());
        }
    
        if (updatedMovie.getReleaseYear() != null && updatedMovie.getReleaseYear() > 0) {
            existingMovie.setReleaseYear(updatedMovie.getReleaseYear());
        }
        if(existingMovieDuration<existingMovie.getDuration()){
            List<ShowTime> relatedShowTimes = showTimeRepository.findByMovieId(existingMovie.getId());
            for(ShowTime showTime:relatedShowTimes){
                 Duration showTimeDuration = Duration.between(showTime.getStartTime(), showTime.getEndTime());
            if (showTimeDuration.toMinutes() < existingMovie.getDuration()) {
                throw new IllegalArgumentException("The duration of showtime: "+showTime.getId()+" is too short for the updated movie's duration and needs to be extended");
            }
            }
        }
    
        return movieRepository.save(existingMovie);
    }

    public void deleteMovieByTitle(String title){
        Movie movie=movieRepository.findByTitle(title).orElseThrow(()->new NotFoundException("There is no movie with the given title '"+title+"'"));
        List<ShowTime> relatedShowTimes = showTimeRepository.findByMovieId(movie.getId());
        for(ShowTime showTime:relatedShowTimes){
            showTimeService.deleteShowTimeById(showTime.getId());
        }
        movieRepository.delete(movie);
    } 
    
    private void validateMovie(Movie movie){
        if(movie.getTitle()==null||movie.getTitle().trim().isEmpty()){
            throw new IllegalArgumentException("Title is required and can't be empty");
        }
        if(movie.getGenre()==null||movie.getGenre().trim().isEmpty()){
            throw new IllegalArgumentException("Genre is required and can't be empty");
        }
        if(movie.getGenre().matches(".*\\d.*")){
            throw new IllegalArgumentException("Genre can't contain numbers");
        }
        if(movie.getDuration()==null||movie.getDuration()<=0){
            throw new IllegalArgumentException("Duration must be greater than 0");
        }
        if(movie.getRating()<0){
            throw new IllegalArgumentException("Rating must be greater than 0");
        }
        if(movie.getReleaseYear()==null||movie.getReleaseYear()<0){
            throw new IllegalArgumentException("Release year is required and must be greater than 0");
        }
    }
}
