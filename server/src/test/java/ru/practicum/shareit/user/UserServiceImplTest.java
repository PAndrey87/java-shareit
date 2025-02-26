package ru.practicum.shareit.user;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        // Arrange
        User user1 = User.builder()
                .id(1L)
                .name("Test User 1")
                .email("test1@example.com")
                .build();
        User user2 = User.builder()
                .id(2L)
                .name("Test User 2")
                .email("test2@example.com")
                .build();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        when(userMapper.toUserDto(user1)).thenReturn(UserDto.builder()
                .id(user1.getId())
                .name(user1.getName())
                .email(user1.getEmail())
                .build());
        when(userMapper.toUserDto(user2)).thenReturn(UserDto.builder()
                .id(user2.getId())
                .name(user2.getName())
                .email(user2.getEmail())
                .build());

        List<UserDto> users = userService.getAllUsers();

        assertEquals(2, users.size());
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(2)).toUserDto(any(User.class));
    }

    @Test
    void getById_ShouldReturnUserDto_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("Test User")
                .email("test@example.com")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build());

        // Act
        UserDto userDto = userService.getById(userId);

        // Assert
        assertNotNull(userDto);
        assertEquals(userId, userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).toUserDto(user);
    }

    @Test
    void getById_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        // Arrange
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.getById(userId));
        verify(userRepository, times(1)).findById(userId);
    }



    @Test
    void create_ShouldCreateUserAndReturnUserDto() {
        // Arrange
        UserDto userDto = UserDto.builder()
                .name("New User")
                .email("newuser@example.com")
                .build();
        User user = User.builder()
                .name("New User")
                .email("newuser@example.com")
                .build();
        when(userMapper.toUser(userDto)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user); // Используем any()
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        // Act
        UserDto createdUserDto = userService.create(userDto);

        // Assert
        assertNotNull(createdUserDto);
        assertEquals(userDto.getName(), createdUserDto.getName());
        assertEquals(userDto.getEmail(), createdUserDto.getEmail());
        verify(userMapper, times(1)).toUser(userDto);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toUserDto(user);
    }

    @Test
    void update_ShouldUpdateUserAndReturnUserDto_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        UserDto userDto = UserDto.builder()
                .name("Updated User")
                .email("updated@example.com")
                .build();
        User user = User.builder()
                .name("Updated User")
                .email("updated@example.com")
                .build();
        User existingUser = User.builder()
                .id(userId)
                .name("Test User")
                .email("test@example.com")
                .build();
        when(userMapper.toUser(userDto)).thenReturn(user);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        // Act
        UserDto updatedUserDto = userService.update(userId, userDto);

        // Assert
        assertNotNull(updatedUserDto);
        assertEquals(userDto.getName(), updatedUserDto.getName());
        assertEquals(userDto.getEmail(), updatedUserDto.getEmail());
        verify(userMapper, times(1)).toUser(userDto);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toUserDto(user);
    }

    @Test
    void update_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        // Arrange
        Long userId = 99L;
        UserDto userDto = UserDto.builder()
                .name("Updated User")
                .email("updated@example.com")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.update(userId, userDto));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void delete_ShouldDeleteUser_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act
        userService.delete(userId);

        // Assert
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void delete_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        // Arrange
        Long userId = 99L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.delete(userId));
        verify(userRepository, times(1)).existsById(userId);
    }






}
