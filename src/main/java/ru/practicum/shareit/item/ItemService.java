package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;

import java.util.List;

public interface ItemService {

    /**
     * Получение списка предметов пользователя
     */
    List<ItemOwnerDto> getItems(long userId, int from, int size);

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
    List<ItemDto> searchByDescription(String text, int from, int size);

    /**
     * Поиск предмета по id
     */
    ItemOwnerDto findById(long userId, long itemId);

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

    /**
     * Получение списка предметов, подходящих под запрос
     */
    List<ItemDto> getItemsForRequest(long requestId);
}
