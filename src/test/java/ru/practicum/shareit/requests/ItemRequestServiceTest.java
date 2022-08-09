package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestWithAnswersDto;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestServiceTest {

    private final ItemRequestService requestService;
    private final ItemService itemService;

    private ItemRequestDto requestDto;
    private ItemRequestWithAnswersDto answersDto;

    @BeforeEach
    void setUp() {
    }

    @Test
    void addNewItemRequest() {
    }

    @Test
    void getOwnRequests() {
    }

    @Test
    void getAllRequests() {
    }

    @Test
    void getRequest() {
    }
}