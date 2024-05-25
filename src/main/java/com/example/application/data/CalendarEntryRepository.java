package com.example.application.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface CalendarEntryRepository extends JpaRepository<CalendarEntry, Long> {

    CalendarEntry findByOriginalID(String originalID);

    @Override
    void deleteById(Long aLong);


    @Query("SELECT e FROM CalendarEntry e WHERE e.originalID = :originalID")
    List<CalendarEntry> search(@Param("originalID") String originalID);


    @Override
    default void deleteAll(Iterable<? extends CalendarEntry> entities) {

    }

}


