package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceTest {

	private final UserService userService;

	@Autowired
	UserServiceTest(UserService userService) {
		this.userService = userService;
	}

	private UserDto user1;
	private UserDto user2;
	private UserDto user3;

	@BeforeEach
	void setUp() {
		user1 = UserDto.builder().name("test1").email("test1@test.com").build();
		user2 = UserDto.builder().name("test2").email("test2@test.com").build();
		user3 = UserDto.builder().name("test3").email("test3@test.com").build();
	}

	@Test
	void contextLoads() {
		assertNotNull(userService);
	}

	@Test
	void testCreateAndFindCorrectUser() {
		final UserDto userDto = userService.addNewUser(user1);
		assertEquals(userDto, userService.findById(userDto.getId()));
	}

	@Test
	void testUpdateUserCorrect() {
		final UserDto userDto = userService.addNewUser(user1);
        userService.updateUser(user2, userDto.getId());
		assertEquals(user2.getName(), userService.findById(userDto.getId()).getName());
		assertEquals(user2.getEmail(), userService.findById(userDto.getId()).getEmail());
	}

	@Test
	void testGetAllUsers() {
		final UserDto userDto1 = userService.addNewUser(user1);
		final UserDto userDto2 = userService.addNewUser(user2);
		assertEquals(List.of(userDto1, userDto2), userService.getUsers());
	}

	@Test
	void testDeleteUser() {
		final UserDto user = userService.addNewUser(user1);
		final UserDto user1 = userService.addNewUser(user2);
		userService.deleteUser(user1.getId());
		assertEquals(List.of(user), userService.getUsers());
	}
}
