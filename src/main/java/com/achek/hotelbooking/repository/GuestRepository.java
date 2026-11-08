package com.achek.hotelbooking.repository;

import com.achek.hotelbooking.dto.GuestActivity;
import com.achek.hotelbooking.model.Guest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GuestRepository extends JpaRepository<Guest, Long> {

    /**
     * Requête JPQL n°4 : clients classés par nombre de réservations non annulées.
     * GROUP BY + COUNT, résultat projeté dans l'interface GuestActivity (dto).
     * Le Pageable limite le nombre de résultats (top N).
     */
    @Query("""
            select g.id as guestId, g.firstName as firstName, g.lastName as lastName, count(r) as reservationCount
            from Reservation r join r.guest g
            where r.status <> com.achek.hotelbooking.model.ReservationStatus.CANCELLED
            group by g.id, g.firstName, g.lastName
            order by count(r) desc
            """)
    List<GuestActivity> findMostActiveGuests(Pageable pageable);
}