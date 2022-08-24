package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.ItemRequestService;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceTest {

    private final UserService userService;

    private final ItemService itemService;

    private final BookingService bookingService;

    private final ItemRequestService itemRequestService;

    @Autowired
    ItemServiceTest(UserService userService, ItemService itemService, BookingService bookingService,
                    ItemRequestService itemRequestService) {
        this.userService = userService;
        this.itemService = itemService;
        this.bookingService = bookingService;
        this.itemRequestService = itemRequestService;
    }

    private UserDto user1;
    private ItemDto item1;
    private ItemDto item2;
    private NewBookingDto booking;

    @BeforeEach
    void setUp() {
        user1 = UserDto.builder().name("test1").email("test1@test1.com").build();
        item1 = ItemDto.builder().id(1L).name("item1").description("item1").available(true).build();
        item2 = ItemDto.builder().name("item2").description("item2").available(true).build();
        booking = NewBookingDto.builder()
                .id(1L)
                .itemId(item1.getId())
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build();
    }

    @Test
    void contextLoads() {
        assertNotNull(itemService);
    }

    @Test
    void testCreateAndFindCorrectItem() {
        final UserDto userDto = userService.addNewUser(user1);
        final ItemDto itemDto = itemService.addNewItem(userDto.getId(), item1);
        assertEquals(1, itemService.findById(userDto.getId(), itemDto.getId()).getId());
    }

    @Test
    void testCreateBlankName() {
        final UserDto userDto = userService.addNewUser(user1);
        assertThrows(ValidationException.class, () ->
                itemService.addNewItem(userDto.getId(), item1.toBuilder().name("").build()));
    }

    @Test
    void testCreateBlankDescription() {
        final UserDto userDto = userService.addNewUser(user1);
        assertThrows(ValidationException.class, () ->
                itemService.addNewItem(userDto.getId(), item1.toBuilder().description("").build()));
    }

    @Test
    void testFindItemsByOwnerId() {
        final UserDto userDto = userService.addNewUser(user1);
        final ItemDto itemDto = itemService.addNewItem(userDto.getId(), item1);
        final ItemDto itemDto2 = itemService.addNewItem(userDto.getId(), item2);
        assertEquals(2, itemService.getItems(userDto.getId(), 0, 10).size());
    }

    @Test
    void testDeleteItemByOwner() {
        final UserDto userDto = userService.addNewUser(user1);
        final ItemDto itemDto = itemService.addNewItem(userDto.getId(), item1);
        final ItemDto itemDto2 = itemService.addNewItem(userDto.getId(), item2);
        itemService.deleteItem(userDto.getId(), itemDto2.getId());
        assertEquals(1, itemService.getItems(userDto.getId(), 0, 10).size());
    }

    @Test
    void testSearchItemByDescription() {
        final UserDto userDto = userService.addNewUser(user1);
        final ItemDto itemDto = itemService.addNewItem(userDto.getId(), item1.toBuilder().name("roBOcoP").build());
        assertEquals(List.of(itemDto), itemService.searchByDescription("oboc", 0, 10));
        final ItemDto itemDto1 = itemService.addNewItem(userDto.getId(), item1.toBuilder().description("GaLaXy").build());
        assertEquals(List.of(itemDto1), itemService.searchByDescription("ALAx", 0, 10));
    }

    @Test
    void testAddComment() throws InterruptedException {
        final UserDto owner = userService.addNewUser(user1);
        final UserDto booker = userService.addNewUser(user1.toBuilder().name("booker").email("user2@gmail.com").build());
        final ItemDto itemDto = itemService.addNewItem(owner.getId(), item1);
        bookingService.addNewBooking(booker.getId(), booking);
        // Добавил 5 сек ожидания чтобы аренда завершилась
        TimeUnit.SECONDS.sleep(5);
        CommentDto commentDto = new CommentDto(1L, "comment", booker.getName(), LocalDateTime.of(2022, 8, 8, 8, 8, 8));
        itemService.addComment(booker.getId(), itemDto.getId(), commentDto);
        assertEquals(itemService.getComments(itemDto.getId()), List.of(commentDto));
    }

    @Test
    void testGetItemsForRequest() {
        final UserDto userDto = userService.addNewUser(user1);
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "request", 1L, LocalDateTime.now());
        final ItemRequestDto request = itemRequestService.addNewItemRequest(userDto.getId(), itemRequestDto);
        final ItemDto itemDto = itemService.addNewItem(userDto.getId(), item2.toBuilder().requestId(request.getId()).build());
        assertEquals(itemService.getItemsForRequest(1L), List.of(itemDto));
    }
}
