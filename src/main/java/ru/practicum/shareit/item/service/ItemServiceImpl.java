package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        UserDto owner = userService.getById(ownerId);
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(UserMapper.toUser(owner));
        itemRepository.save(item);

        return itemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        userService.getById(userId);
        Item updatedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));
        if (!updatedItem.getOwner().getId().equals(userId)) {
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
        return itemMapper.toItemDto(itemRepository.save(updatedItem));
    }

    @Override
    @Transactional
    public void delete(Long userId, Long itemId) {
        userService.getById(userId);
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(Long userId) {
        List<Item> items = itemRepository.findAllByOwner_Id(userId);

        return items.stream()
                .map(item -> itemMapper.toItemDto(item, getLastBooking(item), getNextBooking(item),getAllCommentsByItemId(item.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto findItemById(Long itemId, Long userId) {
        Item item  = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));
        BookingInfoDto last = getLastBooking(item);
        BookingInfoDto next = getNextBooking(item);

        if (item.getOwner().getId().equals(userId)) {
            return itemMapper.toItemDto(item, getLastBooking(item), getNextBooking(item),getAllCommentsByItemId(item.getId()));
        } else {
            ItemDto itemDto = itemMapper.toItemDto(item);
            itemDto.setComments(getAllCommentsByItemId(itemId));
            return itemDto;
        }
    }

    @Override
    public List<ItemDto> searchItemByNameOrDescription(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + itemId + " не найден"));
        Item item  = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));

        if (bookingRepository.findAllByBooker_IdAndItem_IdAndStatusEqualsAndEndIsBefore(userId, itemId, BookingStatus.APPROVED, LocalDateTime.now()).isEmpty()) {
            throw new BadRequestException("Вы не можете оставить комментарий, так как у вас есть активная бронь");
        }
        Comment comment = commentMapper.toComment(commentDto, author, item);
        comment.setCreated(LocalDateTime.now());

        commentRepository.save(comment);
        return commentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getAllCommentsByItemId(Long itemId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        return comments
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private BookingInfoDto getLastBooking(Item item) {
        return bookingRepository
                .findTopByItem_IdAndStartBeforeOrderByEndDesc(item.getId(), LocalDateTime.now())
                .map(BookingMapper::toBookingInfoDto)
                .orElse(null);
    }

    private BookingInfoDto getNextBooking(Item item) {
        return bookingRepository
                .findTopByItem_IdAndStartAfterAndStatusNotOrderByStartAsc(item.getId(), LocalDateTime.now(), BookingStatus.REJECTED)
                .map(BookingMapper::toBookingInfoDto)
                .orElse(null);
    }
}