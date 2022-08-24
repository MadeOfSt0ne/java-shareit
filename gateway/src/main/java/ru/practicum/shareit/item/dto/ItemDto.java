package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    @NotBlank
    @NotEmpty
    @Size(max = 255)
    private String name;
    @NotBlank
    @NotEmpty
    @Size(max = 4000)
    private String description;
    @NotNull
    private Boolean available;
    @NotNull
    private Long owner;
    private Long requestId;
}
