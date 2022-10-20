package ru.practicum.shareit.request;

import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

public class RequestValidation {

    /**
     * Проверка запроса
     */
    public void validate(ItemRequestDto dto) {
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new ValidationException("Пустое описание");
        }
    }
}
