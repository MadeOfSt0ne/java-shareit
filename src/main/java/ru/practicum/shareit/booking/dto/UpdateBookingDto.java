package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookingDto {
    private long id;
    private Status status;
    private User booker;
    private Item item;

    @Data
    @AllArgsConstructor
    static class Item {
        private long id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class User {
        private long id;
    }
}
