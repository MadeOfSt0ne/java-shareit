package ru.practicum.shareit.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestServiceTest {

    private final ItemRequestService requestService;
    private final ItemRequestRepository repository;
    private final UserService userService;

    @Autowired
    ItemRequestServiceTest(ItemRequestService requestService, ItemRequestRepository repository, UserService userService) {
        this.requestService = requestService;
        this.repository = repository;
        this.userService = userService;
    }
    private ItemRequestDto requestDto;
    private ItemRequestWithAnswersDto answersDto;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        requestDto = new ItemRequestDto(1L, "request", 1L,
                LocalDateTime.of(2022, 8, 8, 10, 10, 10));
        answersDto = new ItemRequestWithAnswersDto(1L, "request",
                LocalDateTime.of(2022, 8, 8, 10, 10, 10), new ArrayList<>());
        userDto = userService.addNewUser(new UserDto(1L, "user name", "user@gmail.com"));
    }

    @Test
    void contextLoads() {
        assertNotNull(requestService);
    }

    @Test
    void addNewItemRequest() {
        final ItemRequestDto request = requestService.addNewItemRequest(userDto.getId(), requestDto);
        assertNotNull(repository.findById(requestDto.getId()).orElseThrow());
        assertEquals(repository.findById(requestDto.getId()).orElseThrow().getDescription(), requestDto.getDescription());
    }

    @Test
    void addNewItemRequestWithNotExistingUser() {
        assertThrows(NoSuchElementException.class, () -> requestService.addNewItemRequest(3L, requestDto));
    }

    @Test
    void addNewItemRequestWithBlankDescription() {
        final ItemRequestDto requestDto1 = new ItemRequestDto(1L, null, 1L, LocalDateTime.now());
        assertThrows(ValidationException.class, () -> requestService.addNewItemRequest(1L, requestDto1));
    }

    @Test
    void getOwnRequestsWithNotExistingUser() {
        assertThrows(NoSuchElementException.class, () -> requestService.getOwnRequests(3L));
    }

    @Test
    void getOwnRequests() {
        final ItemRequestDto request = requestService.addNewItemRequest(userDto.getId(), requestDto);
        assertEquals(1, requestService.getOwnRequests(userDto.getId()).size());
        assertEquals(answersDto, requestService.getOwnRequests(userDto.getId()).get(0));

    }

    @Test
    void getAllRequests() {
        final ItemRequestDto request = requestService.addNewItemRequest(userDto.getId(), requestDto);
        assertEquals(1, requestService.getAllRequests(2L, 0, 10).size());
        assertEquals(answersDto, requestService.getAllRequests(2L, 0, 10).get(0));
    }

    @Test
    void getRequest() {
        final ItemRequestDto request = requestService.addNewItemRequest(userDto.getId(), requestDto);
        assertEquals(answersDto, requestService.getRequest(userDto.getId(), requestDto.getId()));
    }

    @Test
    void getRequestWithNotExistingId() {
        assertThrows(NoSuchElementException.class, () -> requestService.getRequest(1L, 3L));
    }
}