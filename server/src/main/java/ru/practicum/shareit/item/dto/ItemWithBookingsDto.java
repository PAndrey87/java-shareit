package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemWithBookingsDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest request;
    private LocalDateTime lastBookingDate;
    private LocalDateTime nextBookingDate;
}
