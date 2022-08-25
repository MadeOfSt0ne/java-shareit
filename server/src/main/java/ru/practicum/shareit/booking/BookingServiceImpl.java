package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private static final LocalDateTime NOW = LocalDateTime.now().withNano(0);

    /**
     * Добавление нового бронирования
     *
     * @param userId id пользователя
     * @param booking dto бронирования
     */
    @Override
    public BookingDto addNewBooking(long userId, NewBookingDto booking) {
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
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ItemNotFoundException("Бронирование не найдено"));
        if (booking.getStatus() == Status.APPROVED) {
            throw new ValidationException("Статус уже изменен");
        }
        User owner = itemRepository.findById(booking.getItem().getId()).orElseThrow().getOwner();
        if (userId != owner.getId()) {
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
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
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
    public List<BookingDto> getAllFromUser(long userId, String state, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Pageable pageable = PageRequest.of(from, size);
        Page<Booking> page;
        switch (state) {
            case ("CURRENT") :
                page = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, NOW, NOW, pageable);
                break;
            case ("FUTURE") :
                page = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, NOW, pageable);
                break;
            case ("PAST") :
                page = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, NOW, pageable);
                break;
            case ("WAITING") :
                page = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING, pageable);
                break;
            case ("REJECTED") :
                page = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, pageable);
                break;
            case ("ALL"):
                page = bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }
        return BookingMapper.toBookingDto(page.getContent());
    }

    /**
     * Получние списка бронирования для всех предметов пользователя
     *
     * @param userId id пользователя
     * @param state состояние бронирования
     */
    @Override
    public List<BookingDto> getAllForItems(long userId, String state, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Pageable pageable = PageRequest.of(from, size);
        if (itemRepository.findByOwnerId(userId, pageable).getContent().size() == 0) {
            throw new ValidationException("Предметы не найдены");
        }
        Page<Booking> bookings;
        switch (state) {
            case ("CURRENT") :
                bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, NOW, NOW, pageable);
                break;
            case ("FUTURE") :
                bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, NOW, pageable);
                break;
            case ("PAST") :
                bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, NOW, pageable);
                break;
            case ("WAITING") :
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING, pageable);
                break;
            case ("REJECTED") :
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, pageable);
                break;
            case ("ALL") :
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId, pageable);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }
        return BookingMapper.toBookingDto(bookings.getContent());
    }
}
