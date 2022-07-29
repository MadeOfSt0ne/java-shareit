package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {

	private final UserController userController;

	@Autowired
	UserControllerTest(UserController userController) {
		this.userController = userController;
	}

	private final UserDto user1 = UserDto.builder().name("test1").email("test1@test.com").build();
	private final UserDto user2 = UserDto.builder().name("test2").email("test2@test.com").build();
	private final UserDto user3 = UserDto.builder().name("test3").email("test3@test.com").build();


	@Test
	void contextLoads() {
		assertNotNull(userController);
	}

	@Test
	void testCreateAndFindCorrectUser() {
		final UserDto userDto = userController.addNewUser(user1);
		assertEquals(userDto, userController.findById(userDto.getId()));
	}

	@Test
	void testCreateUserWithoutEmail() {
		assertThrows(ValidationException.class, () -> userController.addNewUser(user1.toBuilder().email("").build()));
	}

	@Test
	void testCreateUserWithExistingEmail() {
		final UserDto userDto = userController.addNewUser(user1);
		assertThrows(DataIntegrityViolationException.class,
				() -> userController.addNewUser(user2.toBuilder().email("test1@test.com").build()));
	}

	@Test
	void testUpdateUserCorrect() {
		final UserDto userDto = userController.addNewUser(user1);
        userController.updateUser(user2, userDto.getId());
		assertEquals(user2.getName(), userController.findById(userDto.getId()).getName());
		assertEquals(user2.getEmail(), userController.findById(userDto.getId()).getEmail());
	}

	@Test
	void testUpdateUserExistingEmail() {
		final UserDto userDto1 = userController.addNewUser(user1);
		final UserDto userDto2 = userController.addNewUser(user2);
		assertThrows(DataIntegrityViolationException.class,
				() -> userController.updateUser(user3.toBuilder().email("test1@test.com").build(), userDto2.getId()));
	}

	@Test
	void testGetAllUsers() {
		final UserDto userDto1 = userController.addNewUser(user1);
		final UserDto userDto2 = userController.addNewUser(user2);
		assertEquals(List.of(userDto1, userDto2), userController.getAllUsers());
	}

	@Test
	void testDeleteUser() {
		final UserDto user = userController.addNewUser(user1);
		final UserDto user1 = userController.addNewUser(user2);
		userController.deleteUser(user1.getId());
		assertEquals(List.of(user), userController.getAllUsers());
	}
}
