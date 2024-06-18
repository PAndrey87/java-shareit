package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;



@Component
public class BookingMapper {

    public BookingDto toBookingDto(Booking booking) {

        UserDto booker =  UserDto.builder()
                .id(booking.getBooker().getId())
                .name(booking.getBooker().getName())
                .email(booking.getBooker().getEmail())
                .build();

        ItemDto item = ItemDto.builder()
                .id(booking.getItem().getId())
                .name(booking.getItem().getName())
                .description(booking.getItem().getDescription())
                .available(booking.getItem().getAvailable())
                .build();

        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(booker)
                .item(item)
                .build();
    }

    public Booking toBooking(BookingAddDto bookingAddDto, User booker, Item item) {
        return Booking.builder()
               .id(bookingAddDto.getId())
               .start(bookingAddDto.getStart())
               .end(bookingAddDto.getEnd())
                .booker(booker)
               .item(item)
               .status(BookingStatus.WAITING)
               .build();
    }

    public static BookingInfoDto toBookingInfoDto(Booking booking) {
        return BookingInfoDto.builder()
               .id(booking.getId())
               .start(booking.getStart())
               .end(booking.getEnd())
               .bookerId(booking.getBooker().getId())
               .build();
    }
}
