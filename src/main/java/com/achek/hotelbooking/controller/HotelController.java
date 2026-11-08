package com.achek.hotelbooking.controller;

import com.achek.hotelbooking.model.Hotel;
import com.achek.hotelbooking.service.HotelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController extends CrudController<Hotel> {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        super(hotelService);
        this.hotelService = hotelService;
    }

    @GetMapping("/city/{city}")
    public List<Hotel> findByCity(@PathVariable String city) {
        return hotelService.findByCity(city);
    }
}
