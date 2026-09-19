package org.example.repository;

import jakarta.persistence.LockModeType;
import org.example.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
