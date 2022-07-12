package ru.practicum.shareit.requests.dto;

import ru.practicum.shareit.requests.ItemRequest;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester(),
                itemRequest.getCreated()
        );
    }

    public static ItemRequest toItemRequest(long userId, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequester(userId);
        itemRequest.setCreated(itemRequestDto.getCreated());
        return itemRequest;
    }
}
