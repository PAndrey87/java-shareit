package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@Import(BookingServiceImpl.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createBookingReturnsBookingDtoAndStatusOk() throws Exception {
        long userId = 1L;
        BookingAddDto requestDto = BookingAddDto.builder()
                .id(1L)
                .start(LocalDateTime.MIN)
                .end(LocalDateTime.MAX)
                .itemId(1L)
                .build();

        BookingDto responseDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.MIN)
                .end(LocalDateTime.MAX)
                .item(ItemDto.builder().build())
                .booker(UserDto.builder().build())
                .status(BookingStatus.WAITING)
                .build();

        when(bookingService.create(userId, requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));

        verify(bookingService, times(1)).create(userId, requestDto);
    }

    @Test
    void  approveOrRejectReturnsBookingDtoAndStatusO() throws Exception {
        String approved = "true";
        long bookingId = 1L;
        long userId = 1L;
        BookingDto responseDto = BookingDto.builder().build();

        when(bookingService.approveOrReject(userId, bookingId, Boolean.parseBoolean(approved))).thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", approved))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));

        verify(bookingService, times(1)).approveOrReject(userId, bookingId, Boolean.parseBoolean(approved));
    }

    @Test
    void getByIdReturnsBookingDtoAndStatusOk() throws Exception {
        long bookingId = 1L;
        long userId = 1L;
        BookingDto responseDto = BookingDto.builder().build();

        when(bookingService.getById(userId, bookingId)).thenReturn(responseDto);

        mockMvc.perform(get("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));

        verify(bookingService, times(1)).getById(userId, bookingId);
    }

    @Test
    void getUserBookingsReturnsEmptyList() throws Exception {
        String state = "PAST";
        long userId = 1L;

        when(bookingService.getUserBookings(userId, BookingState.PAST)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(bookingService, times(1)).getUserBookings(userId, BookingState.PAST);
    }

    @Test
    void getOwnerBookingsReturnsEmptyList() throws Exception {
        String state = "ALL";
        long userId = 1L;

        when(bookingService.getOwnerBookings(userId, BookingState.ALL)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(bookingService, times(1)).getOwnerBookings(userId, BookingState.ALL);
    }

}