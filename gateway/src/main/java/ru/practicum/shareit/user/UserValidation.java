package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

public class UserValidation {

    /**
     * Проверка пользователя
     */
    public void validate(UserDto dto) {
        if (dto.getEmail() == null || !dto.getEmail().contains("@")) {
            throw new ValidationException("Невалидный email");
        }
    }
}
