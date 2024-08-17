package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingAddDto bookingAddDto);

    BookingDto approveOrReject(Long userId, Long bookingId, boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getUserBookings(Long userId, BookingState state);

    List<BookingDto> getOwnerBookings(Long userId, BookingState state);
}
