package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    /**
     * Получение списка предметов пользователя
     *
     * @param userId id пользователя
     */
    @Override
    public List<ItemDto> getItems(long userId) {
        List<Item> userItems = itemRepository.findByOwnerId(userId);
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
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, user));
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
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        if (hasAccess(itemId, userId)) {
            throw new ValidationException("Доступ запрещен!");
        }
        Item updated = itemRepository.save(ItemMapper.toItem(itemDto, user, itemId));
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
                .orElseThrow(() -> new ItemNotFoundException("Предмет не найден")).getOwner().getId();
    }

    /**
     * Поиск предмета по фрагменту в названии или описании
     *
     * @param text текст для поиска
     */
    @Override
    public List<ItemDto> searchByDescription(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> foundItems =
                itemRepository.searchItemByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCase(text, text);
        return ItemMapper.toItemDto(foundItems);
    }

    /**
     * Поиск предмета по id
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
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Предмет не найден"));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        if (commentDto.getText().isEmpty()) {
            throw new ValidationException("Пустой комментарий");
        }
        // нужна проверка что пользователь брал вещь в аренду. список букингов в стрим, findAny orElseThrow
        bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now()).stream()
                .filter(booking -> booking.getItem().getId() == itemId)
                .findAny()
                .orElseThrow(() -> new ValidationException("Доступ запрещен"));
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, item, user));
        return CommentMapper.toCommentDto(comment);
    }

    /**
     * Получение списка отзывов предмета
     *
     * @param itemId id предмета
     */
    @Override
    public List<CommentDto> getComments(long itemId) {
        List<Comment> comments = commentRepository.getAllByItemId(itemId);
        return CommentMapper.toCommentDto(comments);
    }
}
