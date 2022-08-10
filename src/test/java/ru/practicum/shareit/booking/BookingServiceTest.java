package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.dto.UpdateBookingDto;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceTest {

    private final BookingService bookingService;
    private final BookingRepository repository;
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    BookingServiceTest(BookingService bookingService, BookingRepository repository, UserService userService,
                       ItemService itemService) {
        this.bookingService = bookingService;
        this.repository = repository;
        this.userService = userService;
        this.itemService = itemService;
    }

    private NewBookingDto newBookingDto;
    private UserDto owner;
    private UserDto booker;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        newBookingDto = new NewBookingDto(1L, 1L, LocalDateTime.of(2022, 9, 9, 10, 10, 10),
                LocalDateTime.of(2022, 9, 9, 15, 15, 15));
        owner = userService.addNewUser(new UserDto(1L, "owner", "user@gmail.com"));
        booker = userService.addNewUser(new UserDto(2L, "booker", "booker@gmail.com"));
        itemDto = itemService.addNewItem(owner.getId(), new ItemDto(1L, "item", "description", true, 1L, null));
    }

    @Test
    void contextLoads() {
        assertNotNull(bookingService);
    }

    @Test
    void addNewBooking() {
        final BookingDto bookingDto1 = bookingService.addNewBooking(booker.getId(), newBookingDto);
        assertNotNull(repository.findById(newBookingDto.getId()).orElseThrow());
        assertEquals(repository.findById(newBookingDto.getId()).orElseThrow().getEnd(), newBookingDto.getEnd());
    }

    @Test
    void addNewBookingInvalid() {
        assertThrows(ValidationException.class, () -> bookingService.addNewBooking(
                booker.getId(), newBookingDto.toBuilder().start(LocalDateTime.now().minusDays(2)).build()));
        assertThrows(ValidationException.class, () -> bookingService.addNewBooking(
                booker.getId(), newBookingDto.toBuilder().start(LocalDateTime.now().plusDays(2))
                        .end(LocalDateTime.now().plusDays(1)).build()));
        assertThrows(UserNotFoundException.class, () -> bookingService.addNewBooking(10L, newBookingDto));
        assertThrows(ItemNotFoundException.class, () -> bookingService.addNewBooking(
                booker.getId(), newBookingDto.toBuilder().itemId(5L).build()));
        itemService.updateItem(owner.getId(), 1L, itemDto.toBuilder().available(false).build());
        assertThrows(ValidationException.class, () -> bookingService.addNewBooking(booker.getId(), newBookingDto));
    }

    @Test
    void updateBooking() {
        final BookingDto bookingDto1 = bookingService.addNewBooking(booker.getId(), newBookingDto);
        final UpdateBookingDto updateBookingDto1 = bookingService.updateBooking(owner.getId(), bookingDto1.getId(), true);
        assertEquals(Status.APPROVED, updateBookingDto1.getStatus());
    }

    @Test
    void updateBookingInvalid() {
        bookingService.addNewBooking(booker.getId(), newBookingDto);
        assertThrows(ValidationException.class, () -> bookingService.updateBooking(owner.getId(), newBookingDto.getId(),
                null));
        assertThrows(ItemNotFoundException.class, () -> bookingService.updateBooking(owner.getId(), 5L, true));
        assertThrows(UserNotFoundException.class, () -> bookingService.updateBooking(5L,
                newBookingDto.getId(), true));
    }

    @Test
    void findBooking() {
        final BookingDto bookingDto1 = bookingService.addNewBooking(booker.getId(), newBookingDto);
        final BookingDto bookingDto2 = bookingService.findBooking(owner.getId(), newBookingDto.getId());
        assertEquals(bookingDto1, bookingDto2);
    }

    @Test
    void findBookingInvalid() {
        assertThrows(UserNotFoundException.class, () -> bookingService.findBooking(5L, newBookingDto.getId()));
        assertThrows(ItemNotFoundException.class, () -> bookingService.findBooking(booker.getId(), 5L));
    }

    @Test
    void getAllFromUserByFutureState() {
        final BookingDto bookingDto1 = bookingService.addNewBooking(booker.getId(), newBookingDto);
        assertEquals(List.of(bookingDto1), bookingService.getAllFromUser(booker.getId(), "FUTURE", 0, 10));
    }

    @Test
    void getAllFromUserByCurrentState() {
        User user = new User(2L, "name", "email@email.com");
        Item item = new Item(1L, "na", "de", true, user, new ArrayList<>(), null);
        Booking booking = repository.save(new Booking(1L, LocalDateTime.now().minusDays(1).withNano(0),
                LocalDateTime.now().plusDays(1).withNano(0), item, user, Status.APPROVED));
        assertNotNull(bookingService.findBooking(user.getId(), 1L));
        assertEquals(1, bookingService.getAllFromUser(user.getId(), "CURRENT", 0, 10).size());
    }

    @Test
    void getAllFromUserByPastState() {
        User user = new User(2L, "name", "email@email.com");
        Item item = new Item(1L, "na", "de", true, user, new ArrayList<>(), null);
        Booking booking = repository.save(new Booking(1L, LocalDateTime.now().minusDays(1).withNano(0),
                LocalDateTime.now().minusHours(1).withNano(0), item, user, Status.APPROVED));
        assertEquals(booking.getStart(), repository.findById(1L).orElseThrow().getStart());
        assertNotNull(bookingService.findBooking(user.getId(), 1L));
        assertEquals(1, bookingService.getAllFromUser(user.getId(), "PAST", 0, 10).size());
    }

    @Test
    void getAllFromUserByWaitingState() {
        final BookingDto bookingDto1 = bookingService.addNewBooking(booker.getId(), newBookingDto);
        assertEquals(List.of(bookingDto1), bookingService.getAllFromUser(booker.getId(), "WAITING", 0, 10));
    }

    @Test
    void getAllFromUserByRejectedState() {
        final BookingDto bookingDto1 = bookingService.addNewBooking(booker.getId(), newBookingDto);
        final UpdateBookingDto updateBookingDto1 = bookingService.updateBooking(owner.getId(), bookingDto1.getId(), false);
        assertEquals(1, bookingService.getAllFromUser(booker.getId(), "REJECTED", 0, 10).size());
    }

    @Test
    void getAllFromUserByAllState() {
        User user = new User(2L, "name", "email@email.com");
        Item item = new Item(1L, "na", "de", true, user, new ArrayList<>(), null);
        Booking booking = repository.save(new Booking(1L, LocalDateTime.now().minusDays(1).withNano(0),
                LocalDateTime.now().minusHours(1).withNano(0), item, user, Status.APPROVED));
        Booking booking2 = repository.save(new Booking(2L, LocalDateTime.now().plusDays(1).withNano(0),
                LocalDateTime.now().plusDays(2).withNano(0), item, user, Status.REJECTED));
        assertEquals(2, bookingService.getAllFromUser(user.getId(), "ALL", 0, 10).size());
    }

    @Test
    void getAllFromUserInvalid() {
        assertThrows(UserNotFoundException.class, () -> bookingService.getAllFromUser(5L, "ALL", 0, 10));
        assertThrows(ValidationException.class, () -> bookingService.getAllFromUser(booker.getId(), "DONE", 0, 10));
    }

    @Test
    void getAllForItemsByCurrentState() {
        User user = new User(2L, "name", "email@email.com");
        Item item = new Item(1L, "na", "de", true, user, new ArrayList<>(), null);
        ItemDto itemDto1 = itemService.addNewItem(owner.getId(), itemDto);
        Booking booking = repository.save(new Booking(1L, LocalDateTime.now().minusDays(1).withNano(0),
                LocalDateTime.now().plusDays(1).withNano(0), item, user, Status.APPROVED));
        assertNotNull(bookingService.findBooking(2L, 1L));
        assertEquals(1, bookingService.getAllForItems(owner.getId(), "CURRENT", 0, 10).size());
    }

    @Test
    void getAllForItemsByPastState() {
        User user = new User(2L, "name", "email@email.com");
        Item item = new Item(1L, "na", "de", true, user, new ArrayList<>(), null);
        ItemDto itemDto1 = itemService.addNewItem(owner.getId(), itemDto);
        Booking booking = repository.save(new Booking(1L, LocalDateTime.now().minusDays(1).withNano(0),
                LocalDateTime.now().minusHours(1).withNano(0), item, user, Status.APPROVED));
        assertNotNull(bookingService.findBooking(2L, 1L));
        assertEquals(1, bookingService.getAllForItems(owner.getId(), "PAST", 0, 10).size());
    }

    @Test
    void getAllForItemsByFutureAndWaitingState() {
        User user = new User(2L, "name", "email@email.com");
        Item item = new Item(1L, "na", "de", true, user, new ArrayList<>(), null);
        ItemDto itemDto1 = itemService.addNewItem(owner.getId(), itemDto);
        final BookingDto bookingDto1 = bookingService.addNewBooking(booker.getId(), newBookingDto);
        assertEquals(List.of(bookingDto1), bookingService.getAllForItems(owner.getId(), "FUTURE", 0, 10));
        assertEquals(List.of(bookingDto1), bookingService.getAllForItems(owner.getId(), "WAITING", 0, 10));
        assertEquals(List.of(bookingDto1), bookingService.getAllForItems(owner.getId(), "ALL", 0, 10));
    }

    @Test
    void getAllForItemsByRejectedState() {
        final BookingDto bookingDto1 = bookingService.addNewBooking(booker.getId(), newBookingDto);
        final UpdateBookingDto updateBookingDto1 = bookingService.updateBooking(owner.getId(), bookingDto1.getId(), false);
        ItemDto itemDto1 = itemService.addNewItem(owner.getId(), itemDto);
        assertEquals(1, bookingService.getAllForItems(owner.getId(), "REJECTED", 0, 10).size());
    }

    @Test
    void getAllForItemsByAllState() {
        User user = new User(2L, "name", "email@email.com");
        Item item = new Item(1L, "na", "de", true, user, new ArrayList<>(), null);
        Booking booking = repository.save(new Booking(1L, LocalDateTime.now().minusDays(1).withNano(0),
                LocalDateTime.now().minusHours(1).withNano(0), item, user, Status.APPROVED));
        Booking booking2 = repository.save(new Booking(2L, LocalDateTime.now().plusDays(1).withNano(0),
                LocalDateTime.now().plusDays(2).withNano(0), item, user, Status.REJECTED));
        assertEquals(2, bookingService.getAllForItems(owner.getId(), "ALL", 0, 10).size());
    }

    @Test
    void getAllForItemsInvalid() {
        assertThrows(UserNotFoundException.class, () -> bookingService.getAllForItems(5L, "ALL", 0, 10));
        assertThrows(ValidationException.class, () -> bookingService.getAllForItems(booker.getId(), "ALL", 0, 10));
        assertThrows(ValidationException.class, () -> bookingService.getAllForItems(owner.getId(), "DONE", 0, 10));
    }
}