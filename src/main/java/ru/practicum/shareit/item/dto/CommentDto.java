package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @NotBlank
    @NotNull
    private String text;
    private String authorName;
    private LocalDateTime created;
}
