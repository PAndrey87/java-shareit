package ru.practicum.shareit.booking.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class BookingServiceTest {

    private static final LocalDateTime dateTimeBefore = LocalDateTime.parse("2000-01-01T12:00:00.000", DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    private static final LocalDateTime dateTimeAfter = LocalDateTime.parse("3000-01-01T12:00:00.000", DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    private static final long USER_ID = 1L;

    private static final long ID_NOT_EXISTING = 99L;

    @Autowired
    EntityManager em;

    @Autowired
    private final BookingService bookingService;

    @Autowired
    private BookingMapper bookingMapper;

    @Test
    void createBooking_UserNotFound() {
        assertThrows(NotFoundException.class, () -> bookingService.create(ID_NOT_EXISTING, BookingAddDto.builder().build()));
    }

    @Test
    void createBooking_ItemNotFound() {
        BookingAddDto dto = BookingAddDto.builder()
                .itemId(ID_NOT_EXISTING)
                .build();

        assertThrows(NotFoundException.class, () -> bookingService.create(USER_ID, dto));
    }

    @Test
    void createBooking_Unavailable() {
        BookingAddDto dto = BookingAddDto.builder()
                .itemId(4L)
                .build();

        assertThrows(BadRequestException.class, () -> bookingService.create(USER_ID, dto));
    }

    @Test
    void createBooking_EndDateBeforeStartDate() {
        BookingAddDto dto = BookingAddDto.builder()
                .itemId(2L)
                .start(dateTimeAfter)
                .end(dateTimeBefore)
                .build();

        assertThrows(BadRequestException.class, () -> bookingService.create(USER_ID, dto));
    }

    @Test
    void createBooking_StartDateBeforeCurrentDate() {
        BookingAddDto dto = BookingAddDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now())
                .end(dateTimeAfter)
                .build();

        assertThrows(BadRequestException.class, () -> bookingService.create(USER_ID, dto));
    }

    @Test
    void createBooking_CantBookOwnItem() {
        BookingAddDto dto = BookingAddDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(dateTimeAfter)
                .build();

        assertThrows(NotFoundException.class, () -> bookingService.create(USER_ID, dto));
    }

    @Test
    void createBooking_Success() {
        LocalDateTime now = LocalDateTime.now();

        BookingAddDto dto = BookingAddDto.builder()
                .id(1L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .itemId(2L)
                .build();

        ItemDto item = ItemDto.builder()
                .id(2L)
                .name("name2")
                .description("description2")
                .available(true)
                .requestId(null)
                .lastBooking(null)
                .nextBooking(null)
                .comments(null)
                .build();

        UserDto booker = UserDto.builder()
                .id(1L)
                .name("name1")
                .email("email1")
                .build();

        BookingDto booking = BookingDto.builder()
                .id(1L)
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        BookingDto actualResult = bookingService.create(USER_ID, dto);

        assertEquals(booking, actualResult);
    }

    @Test
    void approveOrReject_NotFound() {
        assertThrows(NotFoundException.class, () -> bookingService.approveOrReject(USER_ID, ID_NOT_EXISTING, true));
    }

    @Test
    void approveOrReject_ForbiddenException() {
        assertThrows(ForbiddenException.class, () -> bookingService.approveOrReject(2L, 1L, true));
    }

    @Test
    void approveOrReject_BadRequestException() {
        assertThrows(BadRequestException.class, () -> bookingService.approveOrReject(1L, 2L, true));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void approveOrReject_Approve(boolean approve) {
        long bookingId = 1L;
        BookingDto actualResult = bookingService.approveOrReject(USER_ID, bookingId, approve);

        Booking booking = em.createQuery("select b from Booking b where b.id = " + bookingId, Booking.class)
                .getSingleResult();
        BookingDto expectedResult = bookingMapper.toBookingDto(booking);

        assertEquals(approve ? BookingStatus.APPROVED : BookingStatus.REJECTED, actualResult.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getById_NotFound() {
        assertThrows(NotFoundException.class, () -> bookingService.getById(ID_NOT_EXISTING, 1L));
    }

    @Test
    void getById_NotAuthor() {
        assertThrows(NotFoundException.class, () -> bookingService.getById(2L, 1L));
    }

    @Test
    void getById_Success() {
        long bookingId = 1L;
        Booking booking = em.createQuery("Select b from Booking b where b.id = " + bookingId, Booking.class)
                .getSingleResult();
        BookingDto expectedResult = bookingMapper.toBookingDto(booking);

        BookingDto actualResult = bookingService.getById(bookingId, bookingId);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getUserBookings_NotFoundException() {
        assertThrows(NotFoundException.class, () -> bookingService.getUserBookings(99L, BookingState.ALL));
    }

    @ParameterizedTest
    @EnumSource(BookingState.class)
    void getUserBookings(BookingState bookingState) {
        if (bookingState == BookingState.UNSUPPORTED_STATUS) {
            assertThrows(UnsupportedStateException.class, () -> bookingService.getUserBookings(USER_ID, bookingState));
        } else {
            List<BookingDto> bookings = bookingService.getUserBookings(USER_ID, bookingState);
            assertEquals(getTestBookings(bookingState), bookings);
        }
    }

    @Test
    void getOwnerBookings_NotFoundException() {
        assertThrows(NotFoundException.class, () -> bookingService.getOwnerBookings(99L, BookingState.ALL));
    }

    @ParameterizedTest
    @EnumSource(BookingState.class)
    void getOwnerBookings(BookingState bookingState) {
        if (bookingState == BookingState.UNSUPPORTED_STATUS) {
            assertThrows(UnsupportedStateException.class, () -> bookingService.getOwnerBookings(USER_ID, bookingState));
        } else {
            List<BookingDto> bookings = bookingService.getOwnerBookings(USER_ID, bookingState);
            assertEquals(getTestBookings(bookingState), bookings);
        }
    }

    private static List<BookingDto> getTestBookings(BookingState bookingState) {
        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("name1")
                .description("description1")
                .available(true)
                .requestId(null)
                .lastBooking(null)
                .nextBooking(null)
                .comments(null)
                .build();

        UserDto booker = UserDto.builder()
                .id(1L)
                .name("name1")
                .email("email1")
                .build();

        BookingDto booking1 = BookingDto.builder()
                .id(1L)
                .start(dateTimeBefore)
                .end(dateTimeAfter)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        BookingDto booking2 = BookingDto.builder()
                .id(2L)
                .start(dateTimeAfter)
                .end(dateTimeBefore)
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        BookingDto booking3 = BookingDto.builder()
                .id(3L)
                .start(dateTimeBefore)
                .end(dateTimeBefore)
                .item(item)
                .booker(booker)
                .status(BookingStatus.REJECTED)
                .build();

        BookingDto booking4 = BookingDto.builder()
                .id(4L)
                .start(dateTimeBefore)
                .end(dateTimeAfter)
                .item(item)
                .booker(booker)
                .status(BookingStatus.CANCELLED)
                .build();

        return switch (bookingState) {
            case CURRENT -> List.of(booking1, booking4);
            case PAST -> List.of(booking2, booking3);
            case FUTURE -> List.of(booking2);
            case WAITING -> List.of(booking1);
            case REJECTED -> List.of(booking3);
            default -> List.of(booking2, booking1, booking3, booking4);
        };
    }

}