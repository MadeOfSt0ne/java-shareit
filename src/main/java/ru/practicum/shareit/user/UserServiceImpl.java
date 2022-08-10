package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    /**
     * Получение списка пользователей
     */
    @Override
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return UserMapper.toUserDto(users);
    }

    /**
     * Добавление нового пользователя
     *
     * @param userDto dto пользователя
     */
    @Override
    public UserDto addNewUser(UserDto userDto) {
        if (userDto.getEmail() == null || !userDto.getEmail().contains("@")) {
            throw new ValidationException("Некорректный email!");
        }
        if(userRepository.findByEmailContaining(userDto.getEmail()) != null) {
            throw new AlreadyExistsException("Почта уже используется " + userDto.getEmail());
        }
        User user = userRepository.save(UserMapper.toUser(userDto, 0));
        return UserMapper.toUserDto(user);
    }

    /**
     * Обновление пользователя
     *
     * @param userDto dto пользователя
     */
    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        User updated = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        if (userDto.getName() != null) {
            updated.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            updated.setEmail(userDto.getEmail());
        }
        userRepository.save(updated);
        return UserMapper.toUserDto(updated);
    }

    /**
     * Поиск пользователя по id
     *
     * @param id id пользователя
     */
    @Override
    public UserDto findById(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        return UserMapper.toUserDto(user);
    }

    /**
     * Удаление пользователя по id
     *
     * @param id id пользователя
     */
    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }
}
