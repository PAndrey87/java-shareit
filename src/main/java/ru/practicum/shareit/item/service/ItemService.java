package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long id, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    void delete(Long userId, Long itemId);

    List<ItemDto> getAllItemsByOwner(Long id);

    ItemDto findItemById(Long id);

    List<ItemDto> searchItemByNameOrDescription(String text);
}
