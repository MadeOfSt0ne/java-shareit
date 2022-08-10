package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private long id;
    private Item item;
    private User booker;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;

    @Builder(toBuilder = true)
    @Data
    @AllArgsConstructor
    static class Item {
        private long id;
        private String name;
    }

    @Builder(toBuilder = true)
    @Data
    @AllArgsConstructor
    static class User {
        private long id;
    }
}
