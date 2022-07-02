package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {

    /**
     * Добавление нового пользователя
     */
    User addUser(User user);

    /**
     * Обновление пользователя
     */
    User updateUser(User user);

    /**
     * Поиск пользователя
     */
    User getUser(long id);

    /**
     * Получение списка всех пользователей
     */
    List<User> getAllUsers();

    /**
    * Удаление пользователя
    */
    void deleteUser(long id);
}
