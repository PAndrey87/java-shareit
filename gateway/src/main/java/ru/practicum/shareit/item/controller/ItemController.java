package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import jakarta.validation.Valid;

/**
 * TODO Sprint add-controllers.
 */

@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(HEADER_USER_ID) Long userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        log.info("Creating item {}, userId={}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(HEADER_USER_ID) Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("Updating item {}, userId={}, itemId={}", itemDto, userId, itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(HEADER_USER_ID) Long userId,
                           @PathVariable Long itemId) {
        log.info("Deleting item, userId={}, itemId={}", userId, itemId);
        itemClient.deleteItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItem(@RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Getting all items for userId={}", userId);
        return itemClient.getAllItemsByOwner(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@RequestHeader(HEADER_USER_ID) Long userId,
                           @PathVariable Long itemId) {
        log.info("Getting item by id {}, userId={}", itemId, userId);
        return itemClient.findItemById(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(HEADER_USER_ID) Long userId,
                                         @RequestParam String text) {
        log.info("Searching items, userId={}, text={}", userId, text);
        return itemClient.searchItemByNameOrDescription(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(HEADER_USER_ID) Long userId, @PathVariable Long itemId,
                              @Valid @RequestBody CommentDto commentDto) {
        log.info("Creating comment {}, userId={}, itemId={}", commentDto, userId, itemId);
        return itemClient.createCommentItem(userId, itemId, commentDto);
    }
}