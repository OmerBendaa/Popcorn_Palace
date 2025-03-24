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
import java.util.List;

@RestController
@RequestMapping("/showtimes")
public class ShowTimeController {
    @Autowired
    private ShowTimeService showTimeService;

    @GetMapping("/all")
    public ResponseEntity<List<ShowTime>> getAllShowTimes(){
        List<ShowTime> showTimes = showTimeService.getAllShowTimes();
        return ResponseEntity.status(HttpStatus.OK).body(showTimes);
    }
    @GetMapping("/{showtimeId}")
    public ResponseEntity<ShowTime> getShowTimeById(@PathVariable Long showtimeId){
        ShowTime showTime = showTimeService.getShowTimeById(showtimeId);
        return ResponseEntity.status(HttpStatus.OK).body(showTime);
    }

    @PostMapping
    public ResponseEntity<ShowTime> addShowTime(@RequestBody ShowTime showTime){
        ShowTime addedShowTime = showTimeService.addShowTime(showTime);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedShowTime);
    }

    @PostMapping("/update/{showtimeId}")
    public ResponseEntity<Void> updateShowTime(@PathVariable Long showtimeId, @RequestBody ShowTime updatedShowTime){
        showTimeService.updateShowTime(showtimeId, updatedShowTime);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{showtimeId}")
    public ResponseEntity<Void> deleteShowTimeById(@PathVariable Long showtimeId){
            showTimeService.deleteShowTimeById(showtimeId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

