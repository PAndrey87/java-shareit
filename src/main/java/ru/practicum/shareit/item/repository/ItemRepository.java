package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item create(Long ownerId, Item item);

    Item update(Item item);

    void delete(Long id);

    List<Item> getAllItemsByOwner(Long id);

    Item findItemById(Long id);

    List<Item> searchItemByNameOrDescription(String text);
}
