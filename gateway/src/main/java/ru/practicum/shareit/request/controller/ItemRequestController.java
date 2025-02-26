package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(HEADER_USER_ID) Long userId,
                                                    @Valid @RequestBody ItemRequestAddDto itemRequestAddDto) {
        log.info("[GATEWAY] Старт createItemRequest by userId: {} itemRequestAddDto: {} ", userId, itemRequestAddDto);
        return itemRequestClient.createItemRequest(userId, itemRequestAddDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByOwnerRequest(@RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("[GATEWAY] Старт getAllByOwnerRequest by userId: {}  ", userId);
        return itemRequestClient.getItemRequestAllByOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(HEADER_USER_ID) Long userId,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(defaultValue = "10") @Positive Integer size) {
        return itemRequestClient.getAllItemRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable Long requestId) {

        return itemRequestClient.getItemRequest(requestId);
    }
}
