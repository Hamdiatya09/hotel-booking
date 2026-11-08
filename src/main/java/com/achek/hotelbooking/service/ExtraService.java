package com.achek.hotelbooking.service;

import com.achek.hotelbooking.model.Extra;
import com.achek.hotelbooking.repository.ExtraRepository;
import org.springframework.stereotype.Service;

@Service
public class ExtraService extends CrudService<Extra> {

    public ExtraService(ExtraRepository extraRepository) {
        super(extraRepository, "Extra");
    }

    @Override
    protected void copyFields(Extra s, Extra t) {
        t.setName(s.getName());
        t.setPrice(s.getPrice());
    }

    @Override
    protected void setId(Extra entity, Long id) {
        entity.setId(id);
    }
}
