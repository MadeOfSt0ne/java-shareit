package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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
     * @param itemRequestDto запрос
     */
    @Override
    public ItemRequestDto addNewItemRequest(long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow();
        ItemRequest request = requestRepository.save(ItemRequestMapper.toItemRequest(user, itemRequestDto));
        return ItemRequestMapper.toItemRequestDto(request);
    }

    /**
     * Получение списка своих запросов с ответами на них
     *
     * @param userId id пользователя
     */
    @Override
    public List<ItemRequestWithAnswersDto> getOwnRequests(long userId) {
        userRepository.findById(userId).orElseThrow();
        List<ItemRequest> requests = requestRepository.findAllByRequesterId(userId);
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
    public List<ItemRequestWithAnswersDto> getAllRequests(long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from, size, Sort.by("created").descending());
        Page<ItemRequest> requests = requestRepository.findAll(pageable);
        List<ItemRequestWithAnswersDto> result = new ArrayList<>();
        for (ItemRequest request : requests.getContent()) {
            if (request.getRequester().getId() != userId) {
                result.add(ItemRequestMapper.toItemRequestDto(request, itemService.getItemsForRequest(request.getId())));
            }
        }
        return result;
    }

    /**
     * Получение данных о конкретном запросе с ответами на него
     *
     * @param userId id пользовтеля
     * @param requestId id запроса
     */
    @Override
    public ItemRequestWithAnswersDto getRequest(long userId, long requestId) {
        userRepository.findById(userId).orElseThrow();
        ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow();
        List<ItemDto> items = itemService.getItemsForRequest(requestId);
        return ItemRequestMapper.toItemRequestDto(itemRequest, items);
    }
}
