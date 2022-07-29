package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Получение списка предметов пользователя
     *
     * @param userId id пользователя
     */
    List<Item> findByOwnerId(long userId);

    /**
     * Поиск предмета по фрагменту в названии и описании. Available = true
     *
     * @param name текст для поиска
     * @param description текст для поиска
     */
    List<Item> searchAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(String name, String description);

    /**
     * Поиск предметов, подходящих под запрос
     *
     * @param requestId id запроса
     */
    List<Item> searchAllByRequestId(long requestId);
}

