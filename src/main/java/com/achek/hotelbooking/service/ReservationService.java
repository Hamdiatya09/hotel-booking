package com.achek.hotelbooking.service;

import com.achek.hotelbooking.exception.BusinessException;
import com.achek.hotelbooking.exception.ConflictException;
import com.achek.hotelbooking.model.*;
import com.achek.hotelbooking.repository.ExtraRepository;
import com.achek.hotelbooking.repository.GuestRepository;
import com.achek.hotelbooking.repository.ReservationRepository;
import com.achek.hotelbooking.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.achek.hotelbooking.service.RepositoryUtils.require;

@Service
public class ReservationService extends CrudService<Reservation> {

    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;
    private final ExtraRepository extraRepository;

    public ReservationService(ReservationRepository reservationRepository, GuestRepository guestRepository,
                              RoomRepository roomRepository, ExtraRepository extraRepository) {
        super(reservationRepository, "Reservation");
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
        this.roomRepository = roomRepository;
        this.extraRepository = extraRepository;
    }

    @Override
    @Transactional
    public Reservation create(Reservation reservation) {
        if (reservation.getCheckIn().isBefore(LocalDate.now())) {
            throw new BusinessException("Check-in date cannot be in the past");
        }
        reservation.setId(null);
        reservation.setStatus(ReservationStatus.PENDING);
        prepare(reservation, -1L);
        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public Reservation update(Long id, Reservation source) {
        Reservation existing = findById(id);
        if (existing.getStatus() == ReservationStatus.CANCELLED
                || existing.getStatus() == ReservationStatus.CHECKED_OUT) {
            throw new BusinessException("A cancelled or finished reservation cannot be modified");
        }
        copyFields(source, existing);
        prepare(existing, id);
        return reservationRepository.save(existing);
    }

    @Transactional
    public Reservation cancel(Long id) {
        Reservation reservation = findById(id);
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BusinessException("Reservation is already cancelled");
        }
        if (reservation.getStatus() == ReservationStatus.CHECKED_IN
                || reservation.getStatus() == ReservationStatus.CHECKED_OUT) {
            throw new BusinessException("A reservation already started cannot be cancelled");
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        return reservationRepository.save(reservation);
    }

    public List<Reservation> findByGuest(Long guestId) {
        require(guestRepository, guestId, "Guest");
        return reservationRepository.findByGuestId(guestId);
    }

    public List<Reservation> findByPeriod(LocalDate from, LocalDate to) {
        if (to.isBefore(from)) {
            throw new BusinessException("'to' date must not be before 'from' date");
        }
        return reservationRepository.findByCheckInBetween(from, to);
    }

    /**
     * Validates dates, resolves references, checks availability and computes the total price:
     * nights x room base price + price of each extra.
     */
    private void prepare(Reservation r, Long excludeId) {
        if (!r.getCheckOut().isAfter(r.getCheckIn())) {
            throw new BusinessException("Check-out date must be after check-in date");
        }
        Guest guest = require(guestRepository, r.getGuest().getId(), "Guest");
        Room room = require(roomRepository, r.getRoom().getId(), "Room");

        if (room.getStatus() == RoomStatus.MAINTENANCE) {
            throw new BusinessException("Room " + room.getNumber() + " is under maintenance");
        }
        if (reservationRepository.existsOverlap(room.getId(), r.getCheckIn(), r.getCheckOut(), excludeId)) {
            throw new ConflictException("Room " + room.getNumber() + " is already booked for these dates");
        }

        Set<Extra> extras = new HashSet<>();
        if (r.getExtras() != null) {
            for (Extra e : r.getExtras()) {
                extras.add(require(extraRepository, e.getId(), "Extra"));
            }
        }

        long nights = ChronoUnit.DAYS.between(r.getCheckIn(), r.getCheckOut());
        BigDecimal total = room.getRoomType().getBasePrice().multiply(BigDecimal.valueOf(nights));
        for (Extra e : extras) {
            total = total.add(e.getPrice());
        }

        r.setGuest(guest);
        r.setRoom(room);
        r.setExtras(extras);
        r.setTotalPrice(total);
    }

    @Override
    protected void copyFields(Reservation s, Reservation t) {
        t.setCheckIn(s.getCheckIn());
        t.setCheckOut(s.getCheckOut());
        t.setGuest(s.getGuest());
        t.setRoom(s.getRoom());
        t.setExtras(s.getExtras() == null ? new HashSet<>() : new HashSet<>(s.getExtras()));
        if (s.getStatus() != null) {
            t.setStatus(s.getStatus());
        }
    }

    @Override
    protected void setId(Reservation entity, Long id) {
        entity.setId(id);
    }
}
