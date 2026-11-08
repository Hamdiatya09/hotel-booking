package com.achek.hotelbooking.service;

import com.achek.hotelbooking.model.RoomType;
import com.achek.hotelbooking.repository.RoomTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class RoomTypeService extends CrudService<RoomType> {

    public RoomTypeService(RoomTypeRepository roomTypeRepository) {
        super(roomTypeRepository, "RoomType");
    }

    @Override
    protected void copyFields(RoomType s, RoomType t) {
        t.setName(s.getName());
        t.setDescription(s.getDescription());
        t.setBasePrice(s.getBasePrice());
        t.setCapacity(s.getCapacity());
    }

    @Override
    protected void setId(RoomType entity, Long id) {
        entity.setId(id);
    }
}
