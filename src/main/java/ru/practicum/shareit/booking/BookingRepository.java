package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Получение списка бронирований пользователя с учетом статуса
     */
    List<Booking> findByBookerIdAndStatusOrderByStartDesc(long booker, Status status);

    /**
     * Получение списка бронирований пользователя
     */
    List<Booking> findByBookerIdOrderByStartDesc(long booker);

    /**
     * Получение списка будущих бронирований пользователя
     */
    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(long booker, LocalDateTime start);

    /**
     * Получение списка прошедших бронирований пользователя
     */
    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(long booker, LocalDateTime end);

    /**
     * Получений списка текущих бронирований пользователя
     */
    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long booker, LocalDateTime start, LocalDateTime end);

    /**
     * Получение списка бронирований предметов пользователя с учетом статуса
     */
    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(long owner, Status status);

    /**
     * Получение списка бронирований предметов пользователя
     */
    List<Booking> findByItemOwnerIdOrderByStartDesc(long owner);

    /**
     * Получение списка будущих бронирований предметов пользователя
     */
    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(long owner, LocalDateTime localDateTime);

    /**
     * Получение списка прошедших бронирований предметов пользователя
     */
    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(long owner, LocalDateTime localDateTime);

    /**
     * Получений списка текущих бронирований предметов пользователя
     */
    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(long owner, LocalDateTime start, LocalDateTime end);
}
