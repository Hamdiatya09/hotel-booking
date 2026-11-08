package com.achek.hotelbooking.service;

import com.achek.hotelbooking.exception.BusinessException;
import com.achek.hotelbooking.model.Room;
import com.achek.hotelbooking.model.RoomStatus;
import com.achek.hotelbooking.repository.HotelRepository;
import com.achek.hotelbooking.repository.RoomRepository;
import com.achek.hotelbooking.repository.RoomTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.achek.hotelbooking.service.RepositoryUtils.require;

@Service
public class RoomService extends CrudService<Room> {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RoomTypeRepository roomTypeRepository;

    public RoomService(RoomRepository roomRepository, HotelRepository hotelRepository,
                       RoomTypeRepository roomTypeRepository) {
        super(roomRepository, "Room");
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    @Override
    @Transactional
    public Room create(Room room) {
        resolveReferences(room);
        if (room.getStatus() == null) {
            room.setStatus(RoomStatus.ACTIVE);
        }
        return super.create(room);
    }

    @Override
    @Transactional
    public Room update(Long id, Room source) {
        resolveReferences(source);
        return super.update(id, source);
    }

    public List<Room> findAvailable(Long hotelId, LocalDate checkIn, LocalDate checkOut) {
        if (!checkOut.isAfter(checkIn)) {
            throw new BusinessException("Check-out date must be after check-in date");
        }
        require(hotelRepository, hotelId, "Hotel");
        return roomRepository.findAvailableRooms(hotelId, checkIn, checkOut);
    }

    public List<Room> search(String city, int capacity) {
        return roomRepository.findByHotelCityIgnoreCaseAndRoomTypeCapacityGreaterThanEqual(city, capacity);
    }

    private void resolveReferences(Room room) {
        room.setHotel(require(hotelRepository, room.getHotel().getId(), "Hotel"));
        room.setRoomType(require(roomTypeRepository, room.getRoomType().getId(), "RoomType"));
    }

    @Override
    protected void copyFields(Room s, Room t) {
        t.setNumber(s.getNumber());
        t.setFloor(s.getFloor());
        if (s.getStatus() != null) {
            t.setStatus(s.getStatus());
        }
        t.setHotel(s.getHotel());
        t.setRoomType(s.getRoomType());
    }

    @Override
    protected void setId(Room entity, Long id) {
        entity.setId(id);
    }
}
