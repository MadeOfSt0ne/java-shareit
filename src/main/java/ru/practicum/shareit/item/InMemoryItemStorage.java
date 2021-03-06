package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, List<Item>> items = new HashMap<>();
    private static long id = 1;

    private static long getNextId() {
        return id++;
    }

    /**
     * Получение списка предметов пользователя
     *
     * @param userId id пользователя
     */
    @Override
    public List<Item> findByUserId(long userId) {
        return items.getOrDefault(userId, Collections.emptyList());
    }

    /**
     * Сохранение предмета
     *
     * @param item предмет
     */
    @Override
    public Item save(Item item) {
        item.setId(getNextId());
        items.compute(item.getOwner().getId(), (userId, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });
        return item;
    }

    /**
     * Обновление данных предмета
     *
     * @param item предмет
     */
    @Override
    public Item update(Item item, long itemId) {
        return findById(itemId);
    }

    /**
     * Поиск предмета по id
     *
     * @param itemId id предмета
     */
    @Override
    public Item findById(long itemId) {
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getId() == itemId)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * Поиск предмета по описанию
     *
     * @param text описание предмета
     */
    @Override
    public List<Item> findByDescription(String text) {
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Удаление предмета по id пользователя и id предмета
     *
     * @param userId id пользователя
     * @param itemId id предмета
     */
    @Override
    public void deleteItem(long userId, long itemId) {
        if (items.containsKey(userId)) {
            List<Item> userItems = items.get(userId);
            userItems.removeIf(item -> item.getId() == itemId);
        }
    }

}
