package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Получение страницы бронирований пользователя с учетом статуса
     */
    Page<Booking> findByBookerIdAndStatusOrderByStartDesc(long booker, Status status, Pageable pageable);

    /**
     * Получение страницы бронирований пользователя
     */
    Page<Booking> findByBookerIdOrderByStartDesc(long booker, Pageable pageable);

    /**
     * Получение страницы будущих бронирований пользователя
     */
    Page<Booking> findByBookerIdAndStartAfterOrderByStartDesc(long booker, LocalDateTime start, Pageable pageable);

    /**
     * Получение страницы прошедших бронирований пользователя
     */
    Page<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(long booker, LocalDateTime end, Pageable pageable);

    /**
     * Получение списка прошедших бронирований пользователя для проверки, что он брал вещь в аренду
     */
    List<Booking> findByBookerIdAndEndBefore(long booker, LocalDateTime end);

    /**
     * Получений страницы текущих бронирований пользователя
     */
    Page<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            long booker, LocalDateTime start, LocalDateTime end, Pageable pageable);

    /**
     * Получение страницы бронирований предметов пользователя с учетом статуса
     */
    Page<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(long owner, Status status, Pageable pageable);

    /**
     * Получение страницы бронирований предметов пользователя
     */
    Page<Booking> findByItemOwnerIdOrderByStartDesc(long owner, Pageable pageable);

    /**
     * Получение страницы будущих бронирований предметов пользователя
     */
    Page<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(long owner, LocalDateTime localDateTime, Pageable pageable);

    /**
     * Получение страницы прошедших бронирований предметов пользователя
     */
    Page<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(long owner, LocalDateTime localDateTime, Pageable pageable);

    /**
     * Получений страницы текущих бронирований предметов пользователя
     */
    Page<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            long owner, LocalDateTime start, LocalDateTime end, Pageable pageable);

    /**
     * Получние последнего бронирования
     */
    Booking getFirstByItemIdOrderByEndDesc(long itemId);

    /**
     * Получение следующего бронирования
     */
    Booking getFirstByItemIdOrderByStartAsc(long itemId);

}


