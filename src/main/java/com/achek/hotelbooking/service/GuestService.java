package com.achek.hotelbooking.service;

import com.achek.hotelbooking.dto.GuestActivity;
import com.achek.hotelbooking.exception.BusinessException;
import com.achek.hotelbooking.model.Guest;
import com.achek.hotelbooking.repository.GuestRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuestService extends CrudService<Guest> {

    private final GuestRepository guestRepository;

    public GuestService(GuestRepository guestRepository) {
        super(guestRepository, "Guest");
        this.guestRepository = guestRepository;
    }

    public List<GuestActivity> topGuests(int limit) {
        if (limit < 1 || limit > 50) {
            throw new BusinessException("limit must be between 1 and 50");
        }
        return guestRepository.findMostActiveGuests(PageRequest.of(0, limit));
    }

    @Override
    protected void copyFields(Guest s, Guest t) {
        t.setFirstName(s.getFirstName());
        t.setLastName(s.getLastName());
        t.setEmail(s.getEmail());
        t.setPhone(s.getPhone());
    }

    @Override
    protected void setId(Guest entity, Long id) {
        entity.setId(id);
    }
}
