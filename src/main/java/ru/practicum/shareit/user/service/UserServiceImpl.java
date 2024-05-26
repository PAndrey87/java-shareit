package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        if (userRepository.findById(id) == null) {
            throw new NotFoundException("User not found");
        }
        return userMapper.toUserDto(userRepository.findById(id));
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        checkEmailDuplicate(user);
        return userMapper.toUserDto(userRepository.create(user));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User user = userMapper.toUser(userDto);
        User updatedUser = userRepository.findById(id);

        if (user.getEmail() != null && !user.getEmail().isBlank() && !user.getEmail().equals(updatedUser.getEmail())) {
            checkEmailDuplicate(user);
            updatedUser.setEmail(user.getEmail());
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            updatedUser.setName(user.getName());
        }

        return userMapper.toUserDto(userRepository.update(id, updatedUser));
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }

    private void checkEmailDuplicate(User user) {
        for (User checkUser : userRepository.findAll()) {
            if (checkUser.getEmail().equals(user.getEmail())) {
                throw new ConflictException("Email already exists");
            }
        }
    }
}
