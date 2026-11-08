package com.achek.hotelbooking.repository;

import com.achek.hotelbooking.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByHotelId(Long hotelId);

    /**
     * Requête dérivée n°2 : chambres d'une ville pouvant accueillir au moins N personnes.
     * Spring génère le SQL à partir du nom : jointure Hotel (city) + RoomType (capacity >= ?).
     */
    List<Room> findByHotelCityIgnoreCaseAndRoomTypeCapacityGreaterThanEqual(String city, Integer capacity);

    /**
     * Requête JPQL n°3 : chambres libres d'un hôtel sur toute la période [checkIn, checkOut[.
     * NOT EXISTS exclut les chambres ayant une réservation non annulée qui chevauche la période.
     * Deux périodes se chevauchent si : début1 < fin2 ET fin1 > début2.
     * Les chambres en maintenance sont exclues (status = ACTIVE).
     */
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