package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private Long itemID = 0L;
    private Map<Long, Item> items = new HashMap<>();

    private Long getId() {
        return ++itemID;
    }

    @Override
    public Item create(Long ownerId, Item item) {
        item.setId(getId());
        item.setOwner(ownerId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void delete(Long id) {
        items.remove(id);
    }

    @Override
    public List<Item> getAllItemsByOwner(Long id) {
        return items.values().stream().filter(item -> item.getOwner().equals(id)).collect(Collectors.toList());
    }

    @Override
    public Item findItemById(Long id) {
        return items.get(id);
    }

    @Override
    public List<Item> searchItemByNameOrDescription(String text) {
        if (text == null || text.length() == 0) {
            return new ArrayList<>();
        }
        return items.values().stream().filter(item -> item.getName().toLowerCase().contains(text.toLowerCase().toLowerCase()) || item.getDescription().toLowerCase().contains(text.toLowerCase().toLowerCase())).filter(Item::getAvailable).collect(Collectors.toList());
    }
}
