package com.achek.hotelbooking.config;

import com.achek.hotelbooking.model.*;
import com.achek.hotelbooking.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/** Inserts a small data set at first start so the API can be tested immediately. */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seed(HotelRepository hotels, RoomTypeRepository types, RoomRepository rooms,
                           GuestRepository guests, ExtraRepository extras) {
        return args -> {
            if (hotels.count() > 0) {
                return;
            }
            Hotel tunis = hotels.save(new Hotel(null, "Hotel Medina", "Tunis", "12 Rue de la Kasbah", 4, null));
            Hotel sousse = hotels.save(new Hotel(null, "Sousse Beach", "Sousse", "Boulevard du 14 Janvier", 5, null));

            RoomType single = types.save(new RoomType(null, "Single", "1 bed, city view", new BigDecimal("90.00"), 1, null));
            RoomType dbl = types.save(new RoomType(null, "Double", "Queen bed", new BigDecimal("140.00"), 2, null));
            RoomType suite = types.save(new RoomType(null, "Suite", "Living room + sea view", new BigDecimal("260.00"), 4, null));

            rooms.save(new Room(null, "101", 1, RoomStatus.ACTIVE, tunis, single, null));
            rooms.save(new Room(null, "102", 1, RoomStatus.ACTIVE, tunis, dbl, null));
            rooms.save(new Room(null, "201", 2, RoomStatus.ACTIVE, tunis, suite, null));
            rooms.save(new Room(null, "101", 1, RoomStatus.ACTIVE, sousse, dbl, null));
            rooms.save(new Room(null, "301", 3, RoomStatus.MAINTENANCE, sousse, suite, null));

            guests.save(new Guest(null, "Sami", "Ben Ali", "sami.benali@example.com", "+21620111222", null));
            guests.save(new Guest(null, "Leila", "Trabelsi", "leila.trabelsi@example.com", "+21650333444", null));

            extras.save(new Extra(null, "Breakfast", new BigDecimal("15.00"), null));
            extras.save(new Extra(null, "Airport transfer", new BigDecimal("35.00"), null));
            extras.save(new Extra(null, "Spa access", new BigDecimal("50.00"), null));
        };
    }
}
