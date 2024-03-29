package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        log.info("SERVER: User {} create new ItemRequest {}", userId, requestDto);
        return requestService.addNewItemRequest(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestWithAnswersDto> getOwnRequests(@RequestHeader(HEADER) long userId) {
        log.info("SERVER: User {} get own requests", userId);
        return requestService.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestWithAnswersDto> getAllRequests(@RequestHeader(HEADER) long userId,
                                       @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                       @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        log.info("SERVER: User {} get all requests with from = {} and size = {}", userId, from, size);
        return requestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithAnswersDto getRequest(@RequestHeader(HEADER) long userId, @PathVariable long requestId) {
        log.info("SERVER: User {} get request {}", userId, requestId);
        return requestService.getRequest(userId, requestId);
    }
}
