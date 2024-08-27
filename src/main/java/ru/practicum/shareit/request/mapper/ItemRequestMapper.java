package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemRequestMapper {
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    public ItemRequestMapper (ItemMapper itemMapper, UserMapper userMapper) {
        this.itemMapper = itemMapper;
        this.userMapper = userMapper;
    }

    public static ItemRequest toItemRequest (ItemRequestAddDto itemRequestAddDto) {
        return ItemRequest.builder()
                .description(itemRequestAddDto.getDescription())
                .build();
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        List<ItemDto> itemsDto = itemRequest
                .getItems()
                .stream()
                .map(item -> itemMapper.toItemDto(item))
               .collect(Collectors.toList());

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(userMapper.toUserDto(itemRequest.getRequestor()))
                .created(itemRequest.getCreated())
                .items(itemsDto)
                .build();
    }

    public List<ItemRequestDto> toListOfItemRequestDto(List<ItemRequest> list) {
        return list.stream()
                .map(this::toItemRequestDto)
                .collect(Collectors.toList());
    }
}

