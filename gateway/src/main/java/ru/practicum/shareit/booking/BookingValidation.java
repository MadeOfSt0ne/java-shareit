package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;

public class BookingValidation {

    /**
     * Проверка времени начала и окончания бронирования
     */
    public void validate(NewBookingDto dto) {
        if (dto.getEnd().isBefore(dto.getStart()) || dto.getStart().isBefore(LocalDateTime.now()) ||
                dto.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Время начала позже времени окончания!");
        }
    }

    /**
     * Проверка значения approved
     */
    public void validate(Boolean approved) {
        if (approved == null) {
            throw new ValidationException("Approved не может быть пустым!");
        }
    }
}
