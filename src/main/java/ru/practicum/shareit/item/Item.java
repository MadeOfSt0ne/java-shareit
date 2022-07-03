package ru.practicum.shareit.item;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Item {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private long owner;
    private long request;
}
