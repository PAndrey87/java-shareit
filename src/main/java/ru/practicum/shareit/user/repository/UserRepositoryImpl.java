package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository {
    private Long userID = 0L;
    private Map<Long, User> users = new HashMap<>();

    private Long getId() {
        return ++userID;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(Long id) {
        return users.get(id);
    }

    @Override
    public User create(User user) {
        user.setId(getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(Long id, User user) {
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }
}
