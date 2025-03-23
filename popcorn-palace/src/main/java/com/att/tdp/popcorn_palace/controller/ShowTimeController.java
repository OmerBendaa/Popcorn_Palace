package com.att.tdp.popcorn_palace.controller;
import org.springframework.web.bind.annotation.RestController;
import com.att.tdp.popcorn_palace.service.ShowTimeService;
import com.att.tdp.popcorn_palace.model.ShowTime;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/showtimes")
public class ShowTimeController {
    @Autowired
    private ShowTimeService showTimeService;

    @GetMapping("/all")
    public ResponseEntity<List<ShowTime>> getAllShowTimes(){
        return ResponseEntity.ok(showTimeService.getAllShowTimes());
    }
    @GetMapping("/{showtimeId}")
    public ResponseEntity<Optional<ShowTime>> getShowTimeById(@PathVariable Long showtimeId){
        return ResponseEntity.ok(showTimeService.getShowTimeById(showtimeId));
    }

    @PostMapping
    public ResponseEntity<?> addShowTime(@RequestBody ShowTime showTime){
            return new ResponseEntity<>(showTimeService.addShowTime(showTime),HttpStatus.CREATED);
    }

    @PostMapping("/update/{showtimeId}")
    public ResponseEntity<?> updateShowTime(@PathVariable Long showtimeId,@RequestBody ShowTime updatedShowTime){
            return ResponseEntity.ok(showTimeService.updateShowTime(showtimeId, updatedShowTime));

    }

    @DeleteMapping("/{showtimeId}")
    public ResponseEntity<Void> deleteShowTimeById(@PathVariable Long showtimeId){
            showTimeService.deleteShowTimeById(showtimeId);
            return ResponseEntity.noContent().build();
    }
}

