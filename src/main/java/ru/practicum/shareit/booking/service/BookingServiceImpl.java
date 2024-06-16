package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedSateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    private final Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");

    @Override
    @Transactional
    public BookingDto create(Long userId, BookingAddDto bookingAddDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + "не найден"));
        Item item = itemRepository.findById(bookingAddDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id " + bookingAddDto.getItemId() + " не найдена"));
        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь с id " + bookingAddDto.getItemId() + " не доступна для бронирования");
        }
        if (bookingAddDto.getEnd().isBefore(bookingAddDto.getStart()) || bookingAddDto.getStart().equals(bookingAddDto.getEnd())) {
            throw new BadRequestException("EndDate is before startDate");
        }

        if (bookingAddDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("StartDate is before CurrentDate");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Вы не можете бронировать свою вещь");
        }

        Booking booking = bookingMapper.toBooking(bookingAddDto, booker, item);
        bookingRepository.save(booking);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto approveOrReject(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Аренда с id " + bookingId + " не найдена"));
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Подтвердить или отклонить аренду может только владелец вещи");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BadRequestException("Статус уже был проставлен");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookingRepository.save(booking);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Аренда с id " + bookingId + " не найдена"));
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Посмотреть аренду может только автор аренды или владелец вещи");
        }
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long bookerId, BookingState state) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User " + bookerId + "не найден"));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBooker_Id(bookerId, sortByStartDesc);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfter(bookerId, now, now, sortByStartDesc);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBooker_IdAndEndBefore(bookerId, now, sortByStartDesc);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBooker_IdAndStartAfter(bookerId, now, sortByStartDesc);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBooker_IdAndStatus(bookerId, BookingStatus.WAITING, sortByStartDesc);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBooker_IdAndStatus(bookerId, BookingStatus.REJECTED, sortByStartDesc);
                break;
            default:
                throw new UnsupportedSateException("Unknown state: " + state);
        }

        return bookings.stream().map(bookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, BookingState state) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User " + ownerId + "не найден"));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItem_Owner_Id(ownerId, sortByStartDesc);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStartBeforeAndEndAfter(ownerId, now, now, sortByStartDesc);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItem_Owner_IdAndEndBefore(ownerId, now, sortByStartDesc);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStartAfter(ownerId, now, sortByStartDesc);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStatus(ownerId, BookingStatus.WAITING, sortByStartDesc);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItem_Owner_IdAndStatus(ownerId, BookingStatus.REJECTED, sortByStartDesc);
                break;
            default:
                throw new UnsupportedSateException("Unknown state: " + state);
        }

        return bookings.stream().map(bookingMapper::toBookingDto).collect(Collectors.toList());
    }
}
