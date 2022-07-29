package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;

    private final ItemService itemService;

    private final UserRepository userRepository;

    /**
     * Добавление нового запроса
     *
     * @param userId id пользователя
     * @param itemRequest запрос
     */
    @Override
    public ItemRequestDto addNewItemRequest(long userId, ItemRequestDto itemRequest) {
        if (itemRequest.getDescription().isEmpty()) {
            throw new ValidationException("Пустое описание");
        }
        User user = userRepository.findById(userId).orElseThrow();
        ItemRequest request = requestRepository.save(ItemRequestMapper.toItemRequest(user, itemRequest));
        return ItemRequestMapper.toItemRequestDto(request);
    }

    /**
     * Получение списка своих запросов с ответами на них
     *
     * @param userId id пользователя
     */
    @Override
    public List<ItemRequestWithAnswersDto> getOwnRequests(long userId) {
        List<ItemRequest> requests = new ArrayList<>(requestRepository.findAllByRequesterId(userId));
        List<ItemRequestWithAnswersDto> items = new ArrayList<>();
        for (ItemRequest request : requests) {
            items.add(ItemRequestMapper.toItemRequestDto(request, itemService.getItemsForRequest(request.getId())));
        }
        return items;
    }

    /**
     * Получение списка запросов, созданных другими пользователями
     *
     * @param userId id пользователя
     * @param from индекс первого элемента
     * @param size количество элементов для отображения
     */
    @Override
    public Page<ItemRequestDto> getAllRequests(long userId, int from, int size) {
        return null;
    }

    /**
     * Получение данных о конкретном запросе с ответами на него
     *
     * @param userId id пользовтеля
     * @param requestId id запроса
     */
    @Override
    public ItemRequestWithAnswersDto getRequest(long userId, long requestId) {
        User user = userRepository.findById(userId).orElseThrow();
        ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow();
        List<ItemDto> items = itemService.getItemsForRequest(requestId);
        return ItemRequestMapper.toItemRequestDto(itemRequest, items);
    }
}
