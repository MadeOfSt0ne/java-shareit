package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private static final String HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto addNewItem(@RequestHeader(HEADER) long userId, @RequestBody ItemDto itemDto) {
        log.info("User {} create item {}", userId, itemDto);
        return itemService.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER) long userId, @RequestBody ItemDto itemDto,
                              @PathVariable long itemId) {
        log.info("User {} update item {} with {}", userId, itemId, itemDto);
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader(HEADER) long userId, @PathVariable long itemId) {
        log.info("Get item id = {}", itemId);
        return itemService.findById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByDescription(@RequestParam(value = "text") String text) {
        log.info("Get item name/description = {}", text);
        return itemService.searchByDescription(text);
    }

    @GetMapping
    public List<ItemDto> findAllByUserId(@RequestHeader(HEADER) long userId) {
        log.info("Get items by user id = {}", userId);
        return itemService.getItems(userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(HEADER) long userId, @PathVariable long itemId) {
        log.info("Delete item id = {}", itemId);
        itemService.deleteItem(userId, itemId);
    }
}
