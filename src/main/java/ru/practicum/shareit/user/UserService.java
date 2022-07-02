package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    /**
     * Получение списка пользователей
     */
    List<UserDto> getUsers();

    /**
     * Добавление нового пользователя
     */
    UserDto addNewUser(UserDto userDto);

    /**
     * Обновление пользователя
     */
    UserDto updateUser(UserDto userDto, long userId);

    /**
     * Поиск пользователя по id
     */
    UserDto findById(long id);

    /**
     * Удаление пользователя по id
     */
    void deleteUser(long id);
}
