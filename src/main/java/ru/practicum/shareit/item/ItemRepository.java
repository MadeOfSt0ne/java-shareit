package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Получение списка предметов пользователя
     *
     * @param userId id пользователя
     */
    Page<Item> findByOwnerId(long userId, Pageable pageable);

    /**
     * Поиск предмета по фрагменту в названии и описании. Available = true
     *
     * @param name текст для поиска
     * @param description текст для поиска
     */
    Page<Item> searchAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
            String name, String description, Pageable pageable);

    /**
     * Поиск предметов, подходящих под запрос
     *
     * @param requestId id запроса
     */
    List<Item> searchAllByRequestId(long requestId);
}

