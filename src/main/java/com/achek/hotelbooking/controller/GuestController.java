package com.achek.hotelbooking.controller;

import com.achek.hotelbooking.dto.GuestActivity;
import com.achek.hotelbooking.model.Guest;
import com.achek.hotelbooking.service.GuestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guests")
public class GuestController extends CrudController<Guest> {

    private final GuestService guestService;

    public GuestController(GuestService guestService) {
        super(guestService);
        this.guestService = guestService;
    }

    @GetMapping("/top")
    public List<GuestActivity> topGuests(@RequestParam(defaultValue = "3") int limit) {
        return guestService.topGuests(limit);
    }
}
