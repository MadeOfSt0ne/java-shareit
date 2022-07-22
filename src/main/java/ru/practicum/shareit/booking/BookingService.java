package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    /**
     * Добавление нового бронирования
     */
    BookingDto addNewBooking(long userId, BookingDto bookingDto);

    /**
     * Обновление бронирования
     */
    BookingDto updateBooking(long userId, long bookingId, Boolean approved);

    /**
     * Поиск бронирования
     */
    BookingDto findBooking(long userId, long bookingId);

    /**
     * Получение списка всех бронирований пользователя
     */
    List<BookingDto> getAllFromUser(long userId, String state);

    /**
     * Получние списка бронирования для всех предметов пользователя
     */
    List<BookingDto> getAllForItems(long userId, String state);
}
