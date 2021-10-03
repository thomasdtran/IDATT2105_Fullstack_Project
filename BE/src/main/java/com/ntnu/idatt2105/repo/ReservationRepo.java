package com.ntnu.idatt2105.repo;

import com.ntnu.idatt2105.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepo extends JpaRepository<Reservation, Long> {
    Reservation findReservationById(long id);

}
