package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.LastNextBookingDto;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<ItemOwnerDto> getItems(long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from, size, Sort.by("id").ascending());
        Page<Item> userItems = itemRepository.findByOwnerId(userId, pageable);
        List<ItemOwnerDto> result = new ArrayList<>();
        for (Item item : userItems) {
            result.add(findById(userId, item.getId()));
        }
        return result;
    }

    /**
     * Добавление предмета
     *
     * @param userId id пользователя
     * @param itemDto dto предмета
     */
    @Override
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        if (itemDto.getName().isEmpty() || itemDto.getDescription() == null || itemDto.getDescription().isEmpty()
                || itemDto.getAvailable() == null) {
            throw new ValidationException("Неверные данные!");
        }
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
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        checkAccess(itemId, userId);
        Item updated = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Предмет не найден"));
        if (itemDto.getName() != null) {
            updated.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updated.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updated.setAvailable(itemDto.getAvailable());
        }
        itemRepository.save(updated);
        return ItemMapper.toItemDto(updated);
    }

    /**
     * Проверка прав доступа к предмету
     *
     * @param userId id пользователя
     * @param itemId id предмета
     */
    @SneakyThrows
    private void checkAccess(long itemId, long userId) {
        if (userId != itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмет не найден")).getOwner().getId()) {
            throw new AccessDeniedException("Доступ запрещен!");
        }
    }

    /**
     * Поиск предмета по фрагменту в названии или описании
     *
     * @param text текст для поиска
     */
    @Override
    public List<ItemDto> searchByDescription(String text, int from, int size) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        Pageable pageable = PageRequest.of(from, size, Sort.by("id").ascending());
        Page<Item> foundItems =
                itemRepository.searchAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
                        text, text, pageable);
        return foundItems.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    /**
     * Поиск предмета по id
     *
     * @param itemId id предмета
     */
    @Override
    public ItemOwnerDto findById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Предмет не найден"));
        Booking l = bookingRepository.getFirstByItemIdOrderByEndDesc(itemId);
        Booking n = bookingRepository.getFirstByItemIdOrderByStartAsc(itemId);
        LastNextBookingDto last = null;
        if (l != null) {
            last = BookingMapper.toLastNextBookingDto(l);
        }
        LastNextBookingDto next = null;
        if (n != null) {
            next = BookingMapper.toLastNextBookingDto(n);
        }
        List<CommentDto> comments = getComments(itemId);
        if (userId == item.getOwner().getId()) {
            return ItemMapper.toItemOwnerDto(item, comments, next, last);
        }
        return ItemMapper.toItemOwnerDto(item, comments, null, null);
    }

    /**
     * Удаление предмета
     *
     * @param userId id пользователя
     * @param itemId id предмета
     */
    @Override
    public void deleteItem(long userId, long itemId) {
        checkAccess(itemId, userId);
        itemRepository.deleteById(itemId);
    }

    /**
     * Добавление комментария к предмету
     *
     * @param userId id пользователя
     * @param itemId id предмета
     * @param commentDto dto комментария
     */
    @SneakyThrows
    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Предмет не найден"));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        if (commentDto.getText().isEmpty()) {
            throw new ValidationException("Пустой комментарий");
        }
        // проверка что пользователь брал вещь в аренду
        bookingRepository.findByBookerIdAndEndBefore(userId, LocalDateTime.now()).stream()
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

    /**
     * Получение списка предметов, подходящих под запрос
     *
     * @param requestId id запроса
     */
    @Override
    public List<ItemDto> getItemsForRequest(long requestId) {
        List<Item> items = itemRepository.searchAllByRequestId(requestId);
        return ItemMapper.toItemDto(items);
    }
}
