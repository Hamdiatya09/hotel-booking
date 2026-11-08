package com.achek.hotelbooking.controller;

import com.achek.hotelbooking.model.Reservation;
import com.achek.hotelbooking.service.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController extends CrudController<Reservation> {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        super(reservationService);
        this.reservationService = reservationService;
    }

    @GetMapping("/guest/{guestId}")
    public List<Reservation> byGuest(@PathVariable Long guestId) {
        return reservationService.findByGuest(guestId);
    }

    @GetMapping("/period")
    public List<Reservation> byPeriod(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return reservationService.findByPeriod(from, to);
    }

    @PatchMapping("/{id}/cancel")
    public Reservation cancel(@PathVariable Long id) {
        return reservationService.cancel(id);
    }
}
