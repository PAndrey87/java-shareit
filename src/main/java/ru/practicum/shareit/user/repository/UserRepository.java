package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    List<User> findAll();

    User findById(long id);

    User create(User user);

    User update(long id, User user);

    void delete(long id);
}