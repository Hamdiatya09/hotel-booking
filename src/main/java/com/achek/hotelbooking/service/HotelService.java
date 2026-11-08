package com.achek.hotelbooking.service;

import com.achek.hotelbooking.model.Hotel;
import com.achek.hotelbooking.repository.HotelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService extends CrudService<Hotel> {

    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        super(hotelRepository, "Hotel");
        this.hotelRepository = hotelRepository;
    }

    public List<Hotel> findByCity(String city) {
        return hotelRepository.findByCityIgnoreCase(city);
    }

    @Override
    protected void copyFields(Hotel s, Hotel t) {
        t.setName(s.getName());
        t.setCity(s.getCity());
        t.setAddress(s.getAddress());
        t.setStars(s.getStars());
    }

    @Override
    protected void setId(Hotel entity, Long id) {
        entity.setId(id);
    }
}
