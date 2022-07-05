package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemControllerTest {

    private final UserController userController;

    private final ItemController itemController;

    @Autowired
    ItemControllerTest(UserController userController, ItemController itemController) {
        this.userController = userController;
        this.itemController = itemController;
    }

    private final UserDto user1 = UserDto.builder().name("test1").email("test1@test1.com").build();

    private final ItemDto item1 = ItemDto.builder().name("item1").description("item1").available(true).build();
    private final ItemDto item2 = ItemDto.builder().name("item2").description("item2").available(true).build();

    @Test
    void contextLoads() {
        assertNotNull(itemController);
    }

    @Test
    void testCreateAndFindCorrectItem() {
        final UserDto userDto = userController.addNewUser(user1);
        final ItemDto itemDto = itemController.addNewItem(userDto.getId(), item1);
        assertEquals(itemDto, itemController.findById(userDto.getId(), itemDto.getId()));
    }

    @Test
    void testCreateBlankName() {
        final UserDto userDto = userController.addNewUser(user1);
        assertThrows(ValidationException.class, () -> itemController.addNewItem(userDto.getId(), item1.toBuilder().name("").build()));
    }

    @Test
    void testCreateBlankDescription() {
        final UserDto userDto = userController.addNewUser(user1);
        assertThrows(ValidationException.class, () -> itemController.addNewItem(userDto.getId(), item1.toBuilder().description("").build()));
    }

    @Test
    void testFindItemsByOwnerId() {
        final UserDto userDto = userController.addNewUser(user1);
        final ItemDto itemDto = itemController.addNewItem(userDto.getId(), item1);
        final ItemDto itemDto2 = itemController.addNewItem(userDto.getId(), item2);
        assertEquals(List.of(itemDto, itemDto2), itemController.findAllByUserId(userDto.getId()));
    }

    @Test
    void testDeleteItemByOwner() {
        final UserDto userDto = userController.addNewUser(user1);
        final ItemDto itemDto = itemController.addNewItem(userDto.getId(), item1);
        final ItemDto itemDto2 = itemController.addNewItem(userDto.getId(), item2);
        itemController.deleteItem(userDto.getId(), itemDto2.getId());
        assertEquals(List.of(itemDto), itemController.findAllByUserId(userDto.getId()));
    }

    @Test
    void testSearchItemByDescription() {
        final UserDto userDto = userController.addNewUser(user1);
        final ItemDto itemDto = itemController.addNewItem(userDto.getId(), item1.toBuilder().name("roBOcoP").build());
        assertEquals(List.of(itemDto), itemController.findByDescription("oboc"));
    }
}
