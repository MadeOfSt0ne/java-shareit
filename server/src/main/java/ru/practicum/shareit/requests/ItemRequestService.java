package ru.practicum.shareit.requests;

import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestWithAnswersDto;

import java.util.List;

public interface ItemRequestService {

    /**
     * Добавление нового запроса
     */
    ItemRequestDto addNewItemRequest(long userId, ItemRequestDto itemRequestDto);

    /**
     * Получение списка своих запросов с ответами на них
     */
    List<ItemRequestWithAnswersDto> getOwnRequests(long userId);

    /**
     * Получение списка запросов, созданных другими пользователями
     */
    List<ItemRequestWithAnswersDto> getAllRequests(long userId, int from, int size);

    /**
     * Получение данных о конкретном запросе с ответами на него
     */
    ItemRequestWithAnswersDto getRequest(long userId, long requestId);
}
