package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        userService.getById(ownerId);
        Item item = itemRepository.create(ownerId, itemMapper.toItem(itemDto));
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        userService.getById(userId);
        Item updatedItem = itemRepository.findItemById(itemId);
        if (!updatedItem.getOwner().equals(userId)) {
            throw new NotFoundException("У пользователя с ID " + userId + " нет вещи с ID " + itemId);
        }
        if (itemDto.getName() != null && !itemDto.getName().equals(updatedItem.getName())) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().equals(updatedItem.getDescription())) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null && !itemDto.getAvailable().equals(updatedItem.getAvailable())) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }

//        return itemMapper.toItemDto(itemRepository.update(itemMapper.toItem(itemDto)));
        return itemMapper.toItemDto(itemRepository.update(updatedItem));
    }

    @Override
    public void delete(Long userId, Long itemId) {
        userService.getById(userId);
        itemRepository.delete(itemId);
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(Long id) {
        userService.getById(id);
        return itemRepository.getAllItemsByOwner(id).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto findItemById(Long id) {
        return itemMapper.toItemDto(itemRepository.findItemById(id));
    }

    @Override
    public List<ItemDto> searchItemByNameOrDescription(String text) {
        return itemRepository.searchItemByNameOrDescription(text).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }
}
