package ru.practicum.shareit.booking.controller;

import org.springframework.http.ResponseEntity;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;




@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;
	private static final String HEADER_USER_ID = "X-Sharer-User-Id";

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader(HEADER_USER_ID) long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("[GATEWAY] Получение бронирований state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader(HEADER_USER_ID) long userId,
			@RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("[GATEWAY] Создание бронирования {}, userId={}", requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader(HEADER_USER_ID) long userId,
			@PathVariable Long bookingId) {
		log.info("[GATEWAY] Получение бронирования {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveOrRejectBooking(@RequestHeader(HEADER_USER_ID) long userId,
											 @PathVariable Long bookingId,
											 @RequestParam boolean approved) {
		log.info("[GATEWAY] Утверждение/отклонение бронирования booking {}, userId={}, approved={}", bookingId, userId, approved);
		return bookingClient.approveOrReject(userId, bookingId, approved);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getOwnerBookings(@RequestHeader(HEADER_USER_ID) long userId,
											 @RequestParam(defaultValue = "ALL") String stateParam) {
		BookingState state = BookingState.from(stateParam).orElse(BookingState.ALL);
		log.info("[GATEWAY] Получение бронирований владельца, userId={}, state={}", userId, state);
		return bookingClient.getOwnerBookings(userId, state);
	}
}
