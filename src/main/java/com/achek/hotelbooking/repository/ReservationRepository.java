package com.achek.hotelbooking.repository;

import com.achek.hotelbooking.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByGuestId(Long guestId);

    /** Requête dérivée n°5 : réservations dont la date d'arrivée est comprise dans une période. */
    List<Reservation> findByCheckInBetween(LocalDate from, LocalDate to);

    /**
     * Règle métier : une chambre ne peut pas être réservée deux fois sur des dates qui se chevauchent.
     * Renvoie true s'il existe une autre réservation non annulée sur la même chambre et la même période.
     * excludeId permet d'ignorer la réservation en cours de modification.
     */
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