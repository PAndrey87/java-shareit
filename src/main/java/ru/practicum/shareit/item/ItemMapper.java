package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .request(item.getRequest())
                .available(item.getAvailable())
                .build();
    }

    public Item toItem(ItemDto itemDto) {
        return Item.builder()
               .id(itemDto.getId())
               .name(itemDto.getName())
               .description(itemDto.getDescription())
               .request(itemDto.getRequest())
               .available(itemDto.getAvailable())
               .build();
    }


}
