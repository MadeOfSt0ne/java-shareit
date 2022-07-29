package ru.practicum.shareit.requests;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestWithAnswersDto;

import java.util.List;

public interface ItemRequestService {

    /**
     * Добавление нового запроса
     */
    ItemRequestDto addNewItemRequest(long userId, ItemRequestDto itemRequest);

    /**
     * Получение списка своих запросов с ответами на них
     */
    List<ItemRequestWithAnswersDto> getOwnRequests(long userId);

    /**
     * Получение списка запросов, созданных другими пользователями
     */
    Page<ItemRequestDto> getAllRequests(long userId, int from, int size);

    /**
     * Получение данных о конкретном запросе с ответами на него
     */
    ItemRequestWithAnswersDto getRequest(long userId, long requestId);
}
