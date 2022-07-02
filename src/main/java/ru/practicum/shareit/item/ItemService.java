package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    /**
     * Получение списка предметов пользователя
     */
    List<ItemDto> getItems(long userId);

    /**
     * Добавление предмета
     */
    ItemDto addNewItem(long userId, ItemDto itemDto);

    /**
     * Редактирование предмета
     */
    ItemDto updateItem(long userId, ItemDto itemDto);

    /**
     * Поиск предмета по описанию
     */
    List<ItemDto> searchByDescription(String text);

    /**
     * Поиск предмета по описанию
     */
    ItemDto findById(long id);

    /**
     * Удаление предмета
     */
    void deleteItem(long userId, long itemId);
}
