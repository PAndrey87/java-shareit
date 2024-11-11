package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private Long id;
    private String name;
    @Email
    @NotBlank
    private String email;
}
