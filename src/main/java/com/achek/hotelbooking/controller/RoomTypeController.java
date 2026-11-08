package com.achek.hotelbooking.controller;

import com.achek.hotelbooking.model.RoomType;
import com.achek.hotelbooking.service.RoomTypeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room-types")
public class RoomTypeController extends CrudController<RoomType> {

    public RoomTypeController(RoomTypeService service) {
        super(service);
    }
}
