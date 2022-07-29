package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestWithAnswersDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private static final String HEADER = "X-Sharer-User-Id";

    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto addNewItemRequest(@RequestHeader(HEADER) long userId, @RequestBody ItemRequestDto requestDto) {
        log.info("User {} create new ItemRequest {}", userId, requestDto);
        return requestService.addNewItemRequest(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestWithAnswersDto> getOwnRequests(@RequestHeader(HEADER) long userId) {
        log.info("User {} get own requests", userId);
        return requestService.getOwnRequests(userId);
    }

    @GetMapping
    public Page<ItemRequestDto> getAllRequests(@RequestHeader(HEADER) long userId,
                                               @RequestParam(value = "from", defaultValue = "0") int from,
                                               @RequestParam(value = "size", defaultValue = "20") int size) {
        log.info("User {} get all requests", userId);
        return requestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithAnswersDto getRequest(@RequestHeader(HEADER) long userId, @PathVariable long requestId) {
        log.info("User {} get request {}", userId, requestId);
        return requestService.getRequest(userId, requestId);
    }
}
