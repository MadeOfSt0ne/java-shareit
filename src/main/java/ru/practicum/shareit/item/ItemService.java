package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
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
    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    /**
     * Поиск предмета по описанию
     */
    List<ItemDto> searchByDescription(String text);

    /**
     * Поиск предмета по id
     */
    ItemDto findById(long userId, long itemId);

    /**
     * Удаление предмета
     */
    void deleteItem(long userId, long itemId);

    /**
     * Добавление комментария к предмету
     */
    CommentDto addComment(long userId, long itemId, CommentDto commentDto);

    /**
     * Получение списка отзывов предмета
     */
    List<CommentDto> getComments(long itemId);
}
