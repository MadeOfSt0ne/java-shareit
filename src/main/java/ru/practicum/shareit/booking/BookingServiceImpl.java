package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.dto.UpdateBookingDto;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private static final LocalDateTime NOW = LocalDateTime.now();

    /**
     * Добавление нового бронирования
     *
     * @param userId id пользователя
     * @param booking dto бронирования
     */
    @Override
    public BookingDto addNewBooking(long userId, NewBookingDto booking) {
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().isBefore(LocalDateTime.now())
            || booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Некорректное время окончания бронирования");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Предмет не найден"));
        if (!item.getAvailable()) {
            throw new ValidationException("Предмет не доступен");
        }
        if (userId == item.getOwner().getId()) {
            throw new UserNotFoundException("Owner");
        }
        Booking newBooking = bookingRepository.save(BookingMapper.toBooking(booking, user, item, Status.WAITING));
        return BookingMapper.toBookingDto(newBooking);
    }

    /**
     * Обновление бронирования
     *
     * @param userId    id пользователя
     * @param bookingId id бронирования
     * @param approved  подтверждено бронирование или отклонено
     */
    @Override
    public UpdateBookingDto updateBooking(long userId, long bookingId, Boolean approved) {
        if (approved == null) {
            throw new ValidationException("Approved не может быть пустым");
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ItemNotFoundException("Бронирование не найдено"));
        if (booking.getStatus() == Status.APPROVED) {
            throw new ValidationException("Статус уже изменен");
        }
        if (userId != itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new ItemNotFoundException("Предмет не найден")).getOwner().getId()) {
            throw new UserNotFoundException("Доступ запрещен");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        Booking updated = bookingRepository.save(booking);
        return BookingMapper.toUpdateBookingDto(updated);
    }

    /**
     * Поиск бронирования по id
     *
     * @param userId id пользователя
     * @param bookingId id бронирования
     */
    @Override
    public BookingDto findBooking(long userId, long bookingId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ItemNotFoundException("Бронирование не найдено"));
        if (userId != booking.getBooker().getId() && userId != booking.getItem().getOwner().getId()) {
            throw new UserNotFoundException("Бронирование не найдено");
        }
        return BookingMapper.toBookingDto(booking);
    }

    /**
     * Получение списка всех бронирований пользователя. В репозитории есть описание для каждого метода =)
     *
     * @param userId id пользователя
     * @param state состояние бронирования
     */
    @Override
    public List<BookingDto> getAllFromUser(long userId, String state) {
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ("CURRENT"):
                bookings.addAll(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, NOW, NOW));
                break;
            case ("FUTURE") :
                bookings.addAll(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, NOW));
                break;
            case ("PAST") :
                bookings.addAll(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, NOW));
                break;
            case ("WAITING") :
                bookings.addAll(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING));
                break;
            case ("REJECTED") :
                bookings.addAll(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED));
                break;
            default:
                bookings.addAll(bookingRepository.findByBookerIdOrderByStartDesc(userId));
                break;
        }
        return BookingMapper.toBookingDto(bookings);
    }

    /**
     * Получние списка бронирования для всех предметов пользователя
     *
     * @param userId id пользователя
     * @param state состояние бронирования
     */
    @Override
    public List<BookingDto> getAllForItems(long userId, String state) {
        if (itemRepository.findByOwnerId(userId).size() == 0) {
            throw new ValidationException("Предметы не найдены");
        }
        List<Booking> list = new ArrayList<>();
        switch (state) {
            case ("CURRENT"):
                list.addAll(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, NOW, NOW));
                break;
            case ("FUTURE") :
                list.addAll(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, NOW));
                break;
            case ("PAST") :
                list.addAll(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, NOW));
                break;
            case ("WAITING") :
                list.addAll(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING));
                break;
            case ("REJECTED") :
                list.addAll(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED));
                break;
            default:
                list.addAll(bookingRepository.findByItemOwnerIdOrderByStartDesc(userId));
                break;
        }
        return BookingMapper.toBookingDto(list);
    }
}
