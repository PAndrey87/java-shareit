package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create (Long userId, @Valid ItemRequestAddDto itemRequestDto);

    List<ItemRequestDto> getAllByOwner (Long userId);

    List<ItemRequestDto> getAll (Long userId, Integer from, Integer size);

    ItemRequestDto getItemRequest (Long userId, Long requestId);
}
