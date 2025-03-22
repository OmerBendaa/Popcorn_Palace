package com.att.tdp.popcorn_palace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.att.tdp.popcorn_palace.exception.NotFoundException;
import com.att.tdp.popcorn_palace.model.ShowTime;
import com.att.tdp.popcorn_palace.repository.IMovieRepository;
import com.att.tdp.popcorn_palace.repository.IShowTimeRepository;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
public class ShowTimeService {
    @Autowired
    private IShowTimeRepository showTimeRepository;
    @Autowired
    private IMovieRepository movieRepository;

    public Optional<ShowTime> getShowTimeById(Long showtimeId){
        try{ 
            return showTimeRepository.findById(showtimeId);
        }catch(NotFoundException error){
            throw new NotFoundException("There is no showtime with the given id");
        }
       
    }

    public ShowTime addShowTime(ShowTime showTime){
            validateShowTime(showTime);
            List<ShowTime> overlappingShowTimes = showTimeRepository.findOverlappingShowTimes(
                    showTime.getTheater(), showTime.getStartTime(), showTime.getEndTime());
            if (!overlappingShowTimes.isEmpty()) {
                throw new DataIntegrityViolationException("The showTime you are trying to add overlaps with an existing showTime");
            }
            try{
                return showTimeRepository.save(showTime);
            }catch(DataIntegrityViolationException error){
                throw new DataIntegrityViolationException("A showtime with this id already exists");
            }
    } 

    public ShowTime updateShowTime(Long showTimeId, ShowTime updatedShowTime) {
        ShowTime existingShowTime = showTimeRepository.findById(showTimeId)
                .orElseThrow(() -> new NotFoundException("There is no showTime with the given Id '" + showTimeId + "'"));

        if (updatedShowTime.getMovieId() != null) {
            if (updatedShowTime.getMovieId() <= 0) {
                throw new IllegalArgumentException("movieId must be a valid id");
            }
            if (!movieRepository.existsById(updatedShowTime.getMovieId())) {
                throw new NotFoundException("A movie with the given id does not exist");
            }
            existingShowTime.setMovieId(updatedShowTime.getMovieId());
        }

        if (updatedShowTime.getTheater() != null && !updatedShowTime.getTheater().trim().isEmpty()) {
            existingShowTime.setTheater(updatedShowTime.getTheater());
        }

        if (updatedShowTime.getStartTime() != null&& !updatedShowTime.getStartTime().toString().trim().isEmpty()) {
            validateInstantFormat(updatedShowTime.getStartTime());
            existingShowTime.setStartTime(updatedShowTime.getStartTime());
        }

        if (updatedShowTime.getEndTime() != null&& !updatedShowTime.getEndTime().toString().trim().isEmpty()) {
            validateInstantFormat(updatedShowTime.getEndTime());
            existingShowTime.setEndTime(updatedShowTime.getEndTime());
        }

        if (updatedShowTime.getPrice() != null && updatedShowTime.getPrice() > 0) {
            existingShowTime.setPrice(updatedShowTime.getPrice());
        }

        List<ShowTime> overlappingShowTimes = showTimeRepository.findOverlappingShowTimes(
                existingShowTime.getTheater(), existingShowTime.getStartTime(), existingShowTime.getEndTime());
        if (!overlappingShowTimes.isEmpty() && overlappingShowTimes.stream().anyMatch(s -> !s.getId().equals(showTimeId))) {
            throw new DataIntegrityViolationException("The updated showTime overlaps with an existing showTime");
        }

        return showTimeRepository.save(existingShowTime);
    }

    public void deleteShowTimeById(Long showTimeId){
        ShowTime showTime=showTimeRepository.findById(showTimeId).orElseThrow(()->new NotFoundException("There is no showTime with the given id '"+showTimeId+"'"));
        showTimeRepository.delete(showTime);
    }

    private void validateShowTime(ShowTime showTime) {
        if (showTime.getMovieId() == null || showTime.getMovieId() <= 0) {
            throw new IllegalArgumentException("movieId is required and must be a valid id");
        }
        if (showTime.getTheater() == null || showTime.getTheater().trim().isEmpty()) {
            throw new IllegalArgumentException("Theater is required and can't be empty");
        }
        if (showTime.getStartTime() == null|| showTime.getStartTime().toString().trim().isEmpty()) {
            throw new IllegalArgumentException("startTime is required and can't be null");
        }
        validateInstantFormat(showTime.getStartTime());
        if (showTime.getEndTime() == null||showTime.getEndTime().toString().trim().isEmpty()) {
            throw new IllegalArgumentException("endTime is required and can't be null");
        }
        validateInstantFormat(showTime.getEndTime());
        if (showTime.getStartTime().isAfter(showTime.getEndTime())) {
            throw new IllegalArgumentException("startTime must be earlier than endTime");
        }
        if (showTime.getPrice() == null || showTime.getPrice() <= 0) {
            throw new IllegalArgumentException("price is required and must be greater than 0");
        }
        
    }

    private void validateInstantFormat(Instant instant) {
        try {
            Instant.parse(instant.toString());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format for given: " + instant);
        }
    }

}
