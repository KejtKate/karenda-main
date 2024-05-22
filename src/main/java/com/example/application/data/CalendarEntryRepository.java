package com.example.application.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CalendarEntryRepository extends JpaRepository<CalendarEntry, Long> {

    CalendarEntry findByOriginalID(String originalID);

    @Override
    void deleteById(Long aLong);


    List<CalendarEntry> findAllByUserId(User userId); //??


    List<CalendarEntry> findAllByUserUsername(User username); //??

}


