package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserStorage userStorage;

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
        User user = userStorage.getUser(userId);
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, userId));
        return ItemMapper.toItemDto(item);
    }

    /**
     * Редактирование предмета
     *
     * @param userId id пользователя
     * @param itemId id предмета
     * @param itemDto dto предмета
     */
    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        if (userId != itemRepository.findById(itemId).getOwner()) {
            throw new UserNotFoundException("Доступ запрещен!");
        }
        Item item = itemRepository.update(ItemMapper.toItem(itemDto, userId), itemId);
        return ItemMapper.toItemDto(item);
    }

    /**
     * Поиск предмета по описанию
     *
     * @param text текст для поиска
     */
    @Override
    public List<ItemDto> searchByDescription(long userId, String text) {
        List<Item> foundItems = itemRepository.findByDescription(text);
        return ItemMapper.toItemDto(foundItems);
    }

    /**
     * Поиск предмета по описанию
     *
     * @param id id предмета
     */
    @Override
    public ItemDto findById(long userId, long id) {
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
