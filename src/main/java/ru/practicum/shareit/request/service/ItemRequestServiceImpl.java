package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService{
    private final ItemRequestRepository repository;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    @Transactional
    public ItemRequestDto create (Long userId, ItemRequestAddDto itemRequestAddDto) {
        User requestor = UserMapper.toUser(userService.getById(userId));

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestAddDto);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

        return itemRequestMapper.toItemRequestDto(repository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getAllByOwner (Long userId) {
        List<ItemRequest> requests = repository.findAllByRequestorIdOrderByCreatedAsc(userId);

        return requests
                .stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(toList());
    }

    @Override
    public List<ItemRequestDto> getAll (Long userId, Integer from, Integer size) {
        List<ItemRequest> requests = repository.findByRequestorIdNot(userId, PageRequest.of(from, size));

        return requests
                .stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(toList());
    }

    @Override
    public ItemRequestDto getItemRequest (Long userId, Long requestId) {
        User user = UserMapper.toUser(userService.getById(userId));
        ItemRequest itemRequest = findItemRequestById(requestId);
        if (!user.getId().equals(itemRequest.getRequestor().getId())) {
            throw new NotFoundException("У вас нет доступа к запросу с ID " + requestId);
        }
        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

    public ItemRequest findItemRequestById (Long itemRequest) {
        return repository.findById(itemRequest)
                .orElseThrow(() -> new NotFoundException("Запрос с ID " + itemRequest + " не найден"));
    }

}
