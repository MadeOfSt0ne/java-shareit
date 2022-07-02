package ru.practicum.shareit.item;

import java.util.List;

public interface ItemRepository {

    /**
     * Получение списка предметов пользователя
     */
    List<Item> findByUserId(long userId);

    /**
     * Сохранение предмета
     */
    Item save(Item item);

    /**
     * Обновление данных предмета
     */
    Item update(Item item);

    /**
     * Поиск предмета по id
     */
    Item findById(long itemId);

    /**
     * Поиск предмета по описанию
     */
    List<Item> findByDescription(String text);

    /**
     * Удаление предмета по id пользователя и id предмета
     */
    void deleteItem(long userId, long itemId);
}
