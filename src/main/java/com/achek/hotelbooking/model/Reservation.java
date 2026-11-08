package com.achek.hotelbooking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Check-in date is required")
    private LocalDate checkIn;

    @NotNull(message = "Check-out date is required")
    private LocalDate checkOut;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    // Computed by the service (nights x base price + extras)
    private BigDecimal totalPrice;

    @ManyToOne(optional = false)
    @JoinColumn(name = "guest_id")
    @NotNull(message = "Guest is required")
    private Guest guest;

    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id")
    @NotNull(message = "Room is required")
    private Room room;

    // Many reservations <-> many extras
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "reservation_extra",
            joinColumns = @JoinColumn(name = "reservation_id"),
            inverseJoinColumns = @JoinColumn(name = "extra_id"))
    private Set<Extra> extras = new HashSet<>();

    @OneToMany(mappedBy = "reservation")
    @JsonIgnore
    private List<Payment> payments = new ArrayList<>();
}
