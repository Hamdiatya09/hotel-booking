package com.achek.hotelbooking.service;

import com.achek.hotelbooking.exception.BusinessException;
import com.achek.hotelbooking.exception.ConflictException;
import com.achek.hotelbooking.model.*;
import com.achek.hotelbooking.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock ReservationRepository reservationRepository;
    @Mock GuestRepository guestRepository;
    @Mock RoomRepository roomRepository;
    @Mock ExtraRepository extraRepository;
    @InjectMocks ReservationService service;

    private Guest guest;
    private Room room;
    private Extra breakfast;

    @BeforeEach
    void setUp() {
        guest = new Guest();
        guest.setId(1L);

        RoomType type = new RoomType();
        type.setBasePrice(new BigDecimal("100.00"));
        room = new Room();
        room.setId(1L);
        room.setNumber("101");
        room.setStatus(RoomStatus.ACTIVE);
        room.setRoomType(type);

        breakfast = new Extra();
        breakfast.setId(1L);
        breakfast.setPrice(new BigDecimal("20.00"));
    }

    private Reservation request(LocalDate in, LocalDate out) {
        Reservation r = new Reservation();
        r.setCheckIn(in);
        r.setCheckOut(out);
        r.setGuest(guest);
        r.setRoom(room);
        r.setExtras(Set.of(breakfast));
        return r;
    }

    @Test
    void create_computesTotalPrice_nightsTimesPricePlusExtras() {
        LocalDate in = LocalDate.now().plusDays(1);
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(extraRepository.findById(1L)).thenReturn(Optional.of(breakfast));
        when(reservationRepository.existsOverlap(eq(1L), any(), any(), anyLong())).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));

        Reservation saved = service.create(request(in, in.plusDays(3)));

        assertEquals(0, new BigDecimal("320.00").compareTo(saved.getTotalPrice())); // 3 x 100 + 20
        assertEquals(ReservationStatus.PENDING, saved.getStatus());
    }

    @Test
    void create_whenRoomAlreadyBooked_throwsConflict() {
        LocalDate in = LocalDate.now().plusDays(1);
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(reservationRepository.existsOverlap(eq(1L), any(), any(), anyLong())).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.create(request(in, in.plusDays(2))));
    }

    @Test
    void create_whenCheckOutBeforeCheckIn_throwsBusinessException() {
        LocalDate in = LocalDate.now().plusDays(5);
        assertThrows(BusinessException.class, () -> service.create(request(in, in.minusDays(1))));
    }
}
