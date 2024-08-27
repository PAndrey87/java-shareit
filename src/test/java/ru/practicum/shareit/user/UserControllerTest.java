package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("TestUser")
            .email("test@mail.net")
            .build();

    // Тест для POST /users
    @Test
    public void testCreateUser() throws Exception {
        when(userService.create(userDto)).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
    }

    // Тест для GET /users/{userId}
    @Test
    public void testGetUserById() throws Exception {
        when(userService.getById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
    }

    // Тест для GET /users
    @Test
    public void testGetAllUsers() throws Exception {
        List<UserDto> users = List.of(userDto);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));
    }

    // Тест для PATCH /users/{userId}
    @Test
    public void testUpdateUser() throws Exception {
        when(userService.update(1L, userDto)).thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
    }

    // Тест для DELETE /users/{userId}
    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(1L);
    }

    // Тест для GET /users/{userId} - пользователь не найден
    @Test
    public void testGetUserById_NotFound() throws Exception {
        when(userService.getById(99L)).thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound())

         .andExpect(content().string("User with id " + 99 + " not found"));
    }

    ////////////////////////////////////////////////////////////
    // Тест для POST /users - невалидный email
    @Test
    public void testCreateUser_InvalidEmail() throws Exception {
        UserDto invalidUserDto = UserDto.builder()
                .name("TestUser")
                .email("invalid-email")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(status().isBadRequest());
        // Можно добавить проверку сообщения об ошибке, если нужно
    }

    // Тест для POST /users - пустое имя
    @Test
    public void testCreateUser_EmptyName() throws Exception {
        UserDto invalidUserDto = UserDto.builder()
                .name("")
                .email("test@mail.net")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(status().isBadRequest());
        // Можно добавить проверку сообщения об ошибке, если нужно
    }

    // Тест для PATCH /users/{userId} - пользователь не найден
    @Test
    public void testUpdateUser_NotFound() throws Exception {
        when(userService.update(99L, userDto)).thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(patch("/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound());
        // Можно добавить проверку сообщения об ошибке, если нужно
    }

    // Тест для DELETE /users/{userId} - пользователь не найден
    @Test
    public void testDeleteUser_NotFound() throws Exception {
        doThrow(new NotFoundException("User not found")).when(userService).delete(99L);

        mockMvc.perform(delete("/users/99"))
                .andExpect(status().isNotFound());
        // Можно добавить проверку сообщения об ошибке, если нужно
    }
}
