package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.ValidationException;

public class ItemValidation {

    /**
     * Проверка значений получаемого предмета
     */
    public void validate(ItemDto dto) {
        if (dto.getName().isEmpty() || dto.getDescription() == null || dto.getDescription().isEmpty()
                || dto.getAvailable() == null) {
            throw new ValidationException("GATEWAY: Невалидные данные");
        }
    }

    /**
     * Проверка комментария
     */
    public void validate(CommentDto dto) {
        if (dto.getText().isBlank()) {
            throw new ValidationException("Empty comment");
        }
    }
}
