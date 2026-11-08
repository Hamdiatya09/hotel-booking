package com.achek.hotelbooking.controller;

import com.achek.hotelbooking.model.Room;
import com.achek.hotelbooking.service.RoomService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController extends CrudController<Room> {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        super(roomService);
        this.roomService = roomService;
    }

    @GetMapping("/available")
    public List<Room> available(@RequestParam Long hotelId,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        return roomService.findAvailable(hotelId, checkIn, checkOut);
    }

    @GetMapping("/search")
    public List<Room> search(@RequestParam String city, @RequestParam(defaultValue = "1") int capacity) {
        return roomService.search(city, capacity);
    }
}
