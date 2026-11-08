package com.achek.hotelbooking.repository;

import com.achek.hotelbooking.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByHotelId(Long hotelId);

    // Custom query 2 (derived): rooms in a city that can host at least N people
    List<Room> findByHotelCityIgnoreCaseAndRoomTypeCapacityGreaterThanEqual(String city, Integer capacity);

    // Custom query 3 (JPQL): rooms of a hotel free for the whole period [checkIn, checkOut[
    @Query("""
            select r from Room r
            where r.hotel.id = :hotelId
              and r.status = com.achek.hotelbooking.model.RoomStatus.ACTIVE
              and not exists (
                    select res.id from Reservation res
                    where res.room = r
                      and res.status <> com.achek.hotelbooking.model.ReservationStatus.CANCELLED
                      and res.checkIn < :checkOut
                      and res.checkOut > :checkIn)
            """)
    List<Room> findAvailableRooms(@Param("hotelId") Long hotelId,
                                  @Param("checkIn") LocalDate checkIn,
                                  @Param("checkOut") LocalDate checkOut);
}
