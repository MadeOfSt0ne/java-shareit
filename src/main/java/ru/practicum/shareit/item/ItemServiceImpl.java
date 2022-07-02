package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    /**
     * Получение списка предметов пользователя
     *
     * @param userId id пользователя
     */
    @Override
    public List<ItemDto> getItems(long userId) {
        List<Item> userItems = itemRepository.findByUserId(userId);
        return ItemMapper.toItemDto(userItems);
    }

    /**
     * Добавление предмета
     *
     * @param userId id пользователя
     * @param itemDto dto предмета
     */
    @Override
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, userId));
        return ItemMapper.toItemDto(item);
    }

    /**
     * Редактирование предмета
     *
     * @param userId id пользователя
     * @param itemDto dto предмета
     */
    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto) {
        Item item = itemRepository.update(ItemMapper.toItem(itemDto, userId));
        return ItemMapper.toItemDto(item);
    }

    /**
     * Поиск предмета по описанию
     *
     * @param text текст для поиска
     */
    @Override
    public List<ItemDto> searchByDescription(String text) {
        List<Item> userItems = itemRepository.findByDescription(text);
        return ItemMapper.toItemDto(userItems);
    }

    /**
     * Поиск предмета по описанию
     *
     * @param id id предмета
     */
    @Override
    public ItemDto findById(long id) {
        Item item = itemRepository.findById(id);
        return ItemMapper.toItemDto(item);
    }

    /**
     * Удаление предмета
     *
     * @param userId id пользователя
     * @param itemId id предмета
     */
    @Override
    public void deleteItem(long userId, long itemId) {
        itemRepository.deleteItem(userId, itemId);
    }
}
