package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final List<User> users = new ArrayList<>();
    private static long id = 1;
    private static long getNextId() {
        return id++;
    }

    /**
     * Добавление нового пользователя
     *
     * @param user пользователь
     */
    @Override
    public User addUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный email!");
        }
        if (checkEmail(user.getEmail())) {
            throw new AlreadyExistsException("Такой email уже существует: " + user.getEmail());
        }
        user.setId(getNextId());
        users.add(user);
        return user;
    }

    /**
     * Обновление пользователя
     *
     * @param user пользователь
     */
    @Override
    public User updateUser(User user) {
        User updated = getUser(user.getId());
        if (checkEmail(user.getEmail()) && !user.getEmail().equals(updated.getEmail())) {
            throw new AlreadyExistsException("Такой email уже существует: " + user.getEmail());
        }
        if (user.getName() != null) {
            updated.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updated.setEmail(user.getEmail());
        }
        return updated;
    }

    /**
     * Поиск пользователя
     *
     * @param id id пользователя
     */
    @Override
    public User getUser(long id) {
        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * Получение списка всех пользователей
     */
    @Override
    public List<User> getAllUsers() {
        return users;
    }

    /**
     * Удаление пользователя
     *
     * @param id id пользователя
     */
    @Override
    public void deleteUser(long id) {
        users.removeIf(user -> user.getId() == id);
    }

    /**
     * Метод для проверки уникальности email
     */
    private boolean checkEmail(String email) {
        return users.stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
}
