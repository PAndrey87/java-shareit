package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker_Id(Long userId, Sort sortByStartDesc);

    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfter(Long userId, LocalDateTime now, LocalDateTime now1, Sort sortByStartDesc);

    List<Booking> findAllByBooker_IdAndEndBefore(Long userId, LocalDateTime now, Sort sortByStartDesc);

    List<Booking> findAllByBooker_IdAndStartAfter(Long userId, LocalDateTime now, Sort sortByStartDesc);

    List<Booking> findAllByBooker_IdAndStatus(Long userId, BookingStatus bookingStatus, Sort sortByStartDesc);

    List<Booking> findAllByItem_Owner_Id(Long ownerId, Sort sortByStartDesc);

    List<Booking> findAllByItem_Owner_IdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime now, LocalDateTime now1, Sort sortByStartDesc);

    List<Booking> findAllByItem_Owner_IdAndEndBefore(Long ownerId, LocalDateTime now, Sort sortByStartDesc);

    List<Booking> findAllByItem_Owner_IdAndStartAfter(Long ownerId, LocalDateTime now, Sort sortByStartDesc);

    List<Booking> findAllByItem_Owner_IdAndStatus(Long ownerId, BookingStatus bookingStatus, Sort sortByStartDesc);

    List<Booking> findAllByBooker_IdAndItem_IdAndStatusEqualsAndEndIsBefore(Long bookerId, Long itemId, BookingStatus status, LocalDateTime now);

    Optional<Booking> findTopByItem_IdAndStartBeforeOrderByEndDesc(Long itemId, LocalDateTime endTime);

    Optional<Booking> findTopByItem_IdAndStartAfterAndStatusNotOrderByStartAsc(Long itemId, LocalDateTime startTime, BookingStatus rejectedStatus);
}
