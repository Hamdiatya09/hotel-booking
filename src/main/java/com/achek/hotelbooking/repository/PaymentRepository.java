package com.achek.hotelbooking.repository;

import com.achek.hotelbooking.dto.HotelRevenue;
import com.achek.hotelbooking.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByReservationId(Long reservationId);

    // Total already paid for one reservation (null when there is no payment yet)
    @Query("select sum(p.amount) from Payment p where p.reservation.id = :reservationId")
    BigDecimal sumByReservation(@Param("reservationId") Long reservationId);

    // Custom query 6 (JPQL + joins + aggregate): revenue per hotel
    @Query("""
            select h.name as hotelName, sum(p.amount) as revenue
            from Payment p join p.reservation res join res.room rm join rm.hotel h
            group by h.name
            order by sum(p.amount) desc
            """)
    List<HotelRevenue> revenueByHotel();
}
