package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.ValidationException;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private static final String HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(HEADER) long userId, @RequestBody ItemDto itemDto) {
        if (itemDto.getName().isEmpty() || itemDto.getDescription() == null || itemDto.getDescription().isEmpty()
                || itemDto.getAvailable() == null) {
            throw new ValidationException("GATEWAY: Невалидные данные");
        }
        log.info("GATEWAY: User {} create item {}", userId, itemDto);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(HEADER) long userId, @RequestBody ItemDto itemDto,
                                 @PathVariable long itemId) {
        log.info("GATEWAY: User {} update item {} with {}", userId, itemId, itemDto);
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@RequestHeader(HEADER) long userId, @PathVariable long itemId) {
        log.info("GATEWAY: Get item id = {}", itemId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByDescription(@RequestParam(value = "text") String text,
                                  @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                  @Positive @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        log.info("GATEWAY: Get item name/description = {}", text);
        return itemClient.findByDescription(text, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader(HEADER) long userId,
                                  @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                  @Positive @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        log.info("GATEWAY: Get items by user id = {}", userId);
        return itemClient.findAllByUserId(userId, from, size);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(HEADER) long userId, @PathVariable long itemId) {
        log.info("GATEWAY: Delete item id = {}", itemId);
        itemClient.delete(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(HEADER) long userId, @PathVariable long itemId,
                                 @RequestBody CommentDto commentDto) {
        log.info("GATEWAY: User {} adds comment {} to item {}", userId, commentDto, itemId);
        if (commentDto.getText().isBlank()) {
            throw new ValidationException("Empty comment");
        }
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
