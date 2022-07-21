package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

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
        userRepository.findById(userId);   // проверка что пользователь существует
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
        if (hasAccess(itemId, userId)) {
            throw new ValidationException("Доступ запрещен!");
        }
        Item updated = itemRepository.save(ItemMapper.toItem(itemDto, userId), itemId);
        return ItemMapper.toItemDto(updated);
    }

    /**
     * Проверка прав доступа к предмету
     *
     * @param userId id пользователя
     * @param itemId id предмета
     */
    private boolean hasAccess(long itemId, long userId) {
        return userId != itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмет не найден")).getOwner();
    }

    /**
     * Поиск предмета по описанию
     *
     * @param text текст для поиска
     */
    @Override
    public List<ItemDto> searchByDescription(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> foundItems = itemRepository.findByDescription(text);
        return ItemMapper.toItemDto(foundItems);
    }

    /**
     * Поиск предмета по описанию
     *
     * @param itemId id предмета
     */
    @Override
    public ItemDto findById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Предмет не найден"));
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
        if (hasAccess(itemId, userId)) {
            throw new ValidationException("Доступ запрещен!");
        }
        itemRepository.deleteById(itemId);
    }

    /**
     * Добавление комментария к предмету
     *
     * @param userId id пользователя
     * @param itemId id предмета
     * @param commentDto dto комментария
     */
    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        // нужна проверка что пользователь брал вещь в аренду. список букингов в стрим, findAny orElseThrow
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, itemId, userId));
        return CommentMapper.toCommentDto(comment);
    }
}
