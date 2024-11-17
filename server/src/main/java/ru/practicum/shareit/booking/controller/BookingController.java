package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.model.BookingState;


import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto createBooking(@RequestHeader(HEADER_USER_ID) Long userId,
                                   @RequestBody BookingAddDto bookingAddDto) {
        log.info("[SERVER] Создание бронирования {}, userId={}", bookingAddDto, userId);
        return bookingService.create(userId, bookingAddDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveOrRejectBooking(@RequestHeader(HEADER_USER_ID) Long userId,
                                             @PathVariable Long bookingId,
                                             @RequestParam boolean approved) {
        log.info("[SERVER] Утверждение/отклонение бронирования booking {}, userId={}, approved={}", bookingId, userId, approved);
        return bookingService.approveOrReject(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(HEADER_USER_ID) Long userId,
                                     @PathVariable Long bookingId) {
        log.info("[SERVER] Получение бронирования {}, userId={}", bookingId, userId);
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader(HEADER_USER_ID) Long userId,
                                            @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("[SERVER] Получение бронирований state {}, userId={}", state, userId);
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader(HEADER_USER_ID) Long userId,
                                             @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("[SERVER] Получение бронирований владельца, userId={}, state={}", userId, state);
        return bookingService.getOwnerBookings(userId, state);
    }
}
