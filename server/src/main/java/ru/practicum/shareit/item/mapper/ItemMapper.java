package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .available(item.getAvailable())
                .comments(new ArrayList<>())
                .build();
    }

    public ItemDto toItemDto(Item item, BookingInfoDto lastBooking, BookingInfoDto nextBooking, List<CommentDto> comments) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }

    public Item toItem(ItemDto itemDto, ItemRequest request) {
        return Item.builder()
               .id(itemDto.getId())
               .name(itemDto.getName())
               .description(itemDto.getDescription())
//               .request(itemDto.getRequestId())
               .request(request)
               .available(itemDto.getAvailable())
               .build();
    }
}
