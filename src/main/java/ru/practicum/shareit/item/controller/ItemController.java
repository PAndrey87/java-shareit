package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(@RequestHeader(HEADER_USER_ID) Long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER_USER_ID) Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(HEADER_USER_ID) Long userId,
                           @PathVariable Long itemId) {
        itemService.delete(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItem(@RequestHeader(HEADER_USER_ID) Long userId) {
        return itemService.getAllItemsByOwner(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@RequestHeader(HEADER_USER_ID) Long userId,
                           @PathVariable Long itemId) {
        return itemService.findItemById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader(HEADER_USER_ID) Long userId,
                                         @RequestParam String text) {
        return itemService.searchItemByNameOrDescription(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(HEADER_USER_ID) Long userId, @PathVariable Long itemId,
                              @Valid @RequestBody CommentDto commentDto) {
        return itemService.createComment(userId, itemId, commentDto);
    }
}