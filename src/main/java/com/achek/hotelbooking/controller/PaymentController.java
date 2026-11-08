package com.achek.hotelbooking.controller;

import com.achek.hotelbooking.dto.HotelRevenue;
import com.achek.hotelbooking.model.Payment;
import com.achek.hotelbooking.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController extends CrudController<Payment> {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        super(paymentService);
        this.paymentService = paymentService;
    }

    @GetMapping("/reservation/{reservationId}")
    public List<Payment> byReservation(@PathVariable Long reservationId) {
        return paymentService.findByReservation(reservationId);
    }

    @GetMapping("/stats/revenue-by-hotel")
    public List<HotelRevenue> revenueByHotel() {
        return paymentService.revenueByHotel();
    }
}
