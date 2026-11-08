package com.achek.hotelbooking.repository;

import com.achek.hotelbooking.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    // Custom query 1 (derived): hotels of a city
    List<Hotel> findByCityIgnoreCase(String city);
}
