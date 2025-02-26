package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemWithBookingsDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
    private LocalDateTime lastBookingDate;
    private LocalDateTime nextBookingDate;
}
