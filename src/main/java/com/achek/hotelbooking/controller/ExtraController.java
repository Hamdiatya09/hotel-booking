package com.achek.hotelbooking.controller;

import com.achek.hotelbooking.model.Extra;
import com.achek.hotelbooking.service.ExtraService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/extras")
public class ExtraController extends CrudController<Extra> {

    public ExtraController(ExtraService service) {
        super(service);
    }
}
