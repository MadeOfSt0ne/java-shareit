package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final static String HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto addNewItem(@RequestHeader(HEADER) long userId, @RequestBody ItemDto itemDto) {
        return itemService.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER) long userId, @RequestBody ItemDto itemDto,
                              @PathVariable long itemId) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader(HEADER) long userId, @PathVariable long itemId) {
        return itemService.findById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByDescription(@RequestParam(value = "text") String text) {
        return itemService.searchByDescription(text);
    }

    @GetMapping
    public List<ItemDto> findAllByUserId(@RequestHeader(HEADER) long userId) {
        return itemService.getItems(userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(HEADER) long userId, @PathVariable long itemId) {
        itemService.deleteItem(userId, itemId);
    }
}
