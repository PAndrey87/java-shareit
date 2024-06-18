package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.model.BookingState;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto createBooking(@RequestHeader(HEADER_USER_ID) Long userId,
                                    @Valid @RequestBody BookingAddDto bookingAddDto) {
        return bookingService.create(userId, bookingAddDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveOrRejectBooking(@RequestHeader(HEADER_USER_ID) Long userId,
                                             @PathVariable Long bookingId,
                                             @RequestParam boolean approved) {
        return bookingService.approveOrReject(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(HEADER_USER_ID) Long userId,
                                     @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader(HEADER_USER_ID) Long userId,
                                            @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader(HEADER_USER_ID) Long userId,
                                             @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getOwnerBookings(userId, state);
    }
}
