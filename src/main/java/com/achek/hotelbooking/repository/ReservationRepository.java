package com.achek.hotelbooking.repository;

import com.achek.hotelbooking.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByGuestId(Long guestId);

    // Custom query 5 (derived): reservations starting inside a period
    List<Reservation> findByCheckInBetween(LocalDate from, LocalDate to);

    // Used by the business rule "a room cannot be double-booked"
    @Query("""
            select case when count(r) > 0 then true else false end from Reservation r
            where r.room.id = :roomId
              and r.id <> :excludeId
              and r.status <> com.achek.hotelbooking.model.ReservationStatus.CANCELLED
              and r.checkIn < :checkOut
              and r.checkOut > :checkIn
            """)
    boolean existsOverlap(@Param("roomId") Long roomId,
                          @Param("checkIn") LocalDate checkIn,
                          @Param("checkOut") LocalDate checkOut,
                          @Param("excludeId") Long excludeId);
}
