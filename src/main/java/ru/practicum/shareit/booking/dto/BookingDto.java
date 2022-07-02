package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

@Data
public class BookingDto {
    private long id;
    private LocalDate start;
    private LocalDate end;
    private static Item item;
    private static User booker;
    private static Status status;
}
