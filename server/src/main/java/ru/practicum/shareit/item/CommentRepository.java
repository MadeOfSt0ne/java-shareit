package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Получение списка отзывов предмета
     *
     * @param itemId id предмета
     */
    List<Comment> getAllByItemId(long itemId);
}
