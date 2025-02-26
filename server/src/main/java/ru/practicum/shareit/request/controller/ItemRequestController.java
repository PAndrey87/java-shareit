package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;


import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader(HEADER_USER_ID) Long userId,
                                             @RequestBody ItemRequestAddDto itemRequestAddDto) {
        log.info("[SERVER] Старт createItemRequest by userId: {} itemRequestAddDto: {} ", userId, itemRequestAddDto);
        return itemRequestService.create(userId, itemRequestAddDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllByOwnerRequest(@RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("[SERVER] Старт getAllByOwnerRequest by userId: {} ", userId);
        return itemRequestService.getAllByOwner(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(HEADER_USER_ID) Long userId,
                                               @RequestParam(defaultValue = "0")  Integer from,
                                               @RequestParam(defaultValue = "10")  Integer size) {
        return itemRequestService.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable Long requestId) {

        return itemRequestService.getItemRequest(requestId);
    }
}
