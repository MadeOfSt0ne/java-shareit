package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    /**
     * Получение списка пользователей
     */
    @Override
    public List<UserDto> getUsers() {
        List<User> users = userStorage.getAllUsers();
        return UserMapper.toUserDto(users);
    }

    /**
     * Добавление нового пользователя
     *
     * @param userDto dto пользователя
     */
    @Override
    public UserDto addNewUser(UserDto userDto) {
        User user = userStorage.addUser(UserMapper.toUser(userDto, 0));
        return UserMapper.toUserDto(user);
    }

    /**
     * Обновление пользователя
     *
     * @param userDto dto пользователя
     */
    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        User user = userStorage.updateUser(UserMapper.toUser(userDto, userId));
        return UserMapper.toUserDto(user);
    }

    /**
     * Поиск пользователя по id
     *
     * @param id id пользователя
     */
    @Override
    public UserDto findById(long id) {
        User user = userStorage.getUser(id);
        return UserMapper.toUserDto(user);
    }

    /**
     * Удаление пользователя по id
     *
     * @param id id пользователя
     */
    @Override
    public void deleteUser(long id) {
        userStorage.deleteUser(id);
    }
}
