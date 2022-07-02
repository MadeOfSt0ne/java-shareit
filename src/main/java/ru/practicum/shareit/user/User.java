package ru.practicum.shareit.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class User {
    private long id;
    @NotBlank
    @NotNull
    private String name;
    @Email
    private String email;
}
