package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
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

    private final static LocalDateTime NOW = LocalDateTime.now();

    /**
     * Добавление нового бронирования
     *
     * @param userId id пользователя
     * @param bookingDto dto бронирования
     */
    @Override
    public BookingDto addNewBooking(long userId, BookingDto bookingDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        itemRepository.findById(bookingDto.getItem().getId()).orElseThrow(() -> new ItemNotFoundException("Предмет не найден"));
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getEnd().isBefore(LocalDateTime.now())
            || bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Некорректное время окончания бронирования");
        }
        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingDto, user, Status.WAITING));
        return BookingMapper.toBookingDto(booking);
    }

    /**
     * Обновление бронирования
     *
     * @param userId id пользователя
     * @param bookingId id бронирования
     * @param approved подтверждено бронирование или отклонено
     */
    @Override
    public BookingDto updateBooking(long userId, long bookingId, Boolean approved) {
        if (approved == null) {
            throw new ValidationException("Approved не может быть пустым");
        }
        Booking booking = bookingRepository.findById(bookingId).
                orElseThrow(() -> new ItemNotFoundException("Бронирование не найдено"));
        if (userId != itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new ItemNotFoundException("Предмет не найден")).getOwner().getId()) {
            throw new ValidationException("Доступ запрещен");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    /**
     * Поиск бронирования по id
     *
     * @param userId id пользователя
     * @param bookingId id бронирования
     */
    @Override
    public BookingDto findBooking(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ItemNotFoundException("Бронирование не найдено"));
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
