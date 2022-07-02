package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final List<User> users = new ArrayList<>();

    /**
     * Добавление нового пользователя
     *
     * @param user пользователь
     */
    @Override
    public User addUser(User user) {
        if (checkEmail(user.getEmail())) {
            throw new ValidationException("Такой email уже существует: " + user.getEmail());
        }
        user.setId(getId());
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
        if (checkEmail(user.getEmail())) {
            throw new ValidationException("Такой email уже существует: " + user.getEmail());
        }
        User updated = getUser(user.getId());
        updated.setName(user.getName());
        updated.setEmail(user.getEmail());
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
     * Метод для генерации id
     */
    private long getId() {
        long lastId = users.stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }

    /**
     * Метод для проверки уникальности email
     */
    private boolean checkEmail(String email) {
        return users.stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
}
