package com.att.tdp.popcorn_palace.repository;
import com.att.tdp.popcorn_palace.model.ShowTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
@Repository
public interface IShowTimeRepository extends JpaRepository<ShowTime,Long> {
    
    
    @Query("SELECT s FROM ShowTime s WHERE s.theater = :theater AND " +
           "((:startTime BETWEEN s.startTime AND s.endTime) OR " +
           "(:endTime BETWEEN s.startTime AND s.endTime) OR " +
           "(s.startTime BETWEEN :startTime AND :endTime) OR " +
           "(s.endTime BETWEEN :startTime AND :endTime))")
    List<ShowTime> findOverlappingShowTimes(@Param("theater") String theater,
                                            @Param("startTime") Instant startTime,
                                            @Param("endTime") Instant endTime);
    List<ShowTime> findByMovieId(Long movieId);
}
