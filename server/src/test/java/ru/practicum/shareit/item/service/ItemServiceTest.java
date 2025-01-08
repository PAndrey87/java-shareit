package ru.practicum.shareit.item.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class ItemServiceTest {

    private static final LocalDateTime dateTimeBefore = LocalDateTime.parse("2000-01-01T12:00:00.000", DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    private static final LocalDateTime dateTimeAfter = LocalDateTime.parse("3000-01-01T12:00:00.000", DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    private static final long ID = 1L;

    private static final long NON_EXISTING_ID = 99L;

    @Autowired
    private final EntityManager em;

    @Autowired
    private final ItemService itemService;

    @Autowired
    private final ItemMapper itemMapper;

    private static ItemDto itemDto;

    @BeforeAll
    static void beforeAll() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("name1")
                .description("description1")
                .available(true)
                .requestId(1L)
                .lastBooking(BookingInfoDto.builder()
                        .id(1L)
                        .bookerId(1L)
                        .start(dateTimeBefore)
                        .end(dateTimeAfter)
                        .build())
                .nextBooking(BookingInfoDto.builder()
                        .id(2L)
                        .bookerId(1L)
                        .start(dateTimeAfter)
                        .end(dateTimeBefore)
                        .build())
                .comments(List.of(CommentDto.builder()
                        .id(1L)
                        .text("text1")
                        .authorName("name1")
                        .created(dateTimeBefore)
                        .build()))
                .build();
    }

    @Test
    void create_RequestNotFound() {
        ItemDto requestDto = ItemDto.builder()
                .requestId(NON_EXISTING_ID)
                .build();

        assertThrows(NotFoundException.class, () -> itemService.create(ID, requestDto));
    }

    @Test
    void create_Success() {
        long id = 1L;
        ItemDto requestDto = ItemDto.builder()
                .id(id)
                .name("name")
                .description("description")
                .available(true)
                .build();

        ItemDto actualResult = itemService.create(ID, requestDto);

        Item item = em.createQuery("SELECT i FROM Item i WHERE i.id = " + id, Item.class).getSingleResult();
        ItemDto expectedResult = itemMapper.toItemDto(item);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void update_NotFound() {
        ItemDto requestDto = ItemDto.builder()
                .build();

        assertThrows(NotFoundException.class, () -> itemService.update(ID, NON_EXISTING_ID, requestDto));
    }

    @Test
    void update_UserDontHaveItem() {
        ItemDto requestDto = ItemDto.builder()
                .build();

        assertThrows(NotFoundException.class, () -> itemService.update(2L, ID, requestDto));
    }

    @Test
    void update_SuccessWithChanges() {
        ItemDto requestDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(false)
                .build();

        ItemDto actualResult = itemService.update(ID, ID, requestDto);

        Item item = em.createQuery("SELECT i FROM Item i WHERE i.id = " + ID, Item.class).getSingleResult();
        ItemDto expectedResult = itemMapper.toItemDto(item);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void update_SuccessNoChanges() {
        ItemDto requestDto = ItemDto.builder()
                .name("name1")
                .description("description1")
                .available(true)
                .build();

        ItemDto actualResult = itemService.update(ID, ID, requestDto);

        Item item = em.createQuery("SELECT i FROM Item i WHERE i.id = " + ID, Item.class).getSingleResult();
        ItemDto expectedResult = itemMapper.toItemDto(item);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void update_SuccessNulls() {
        ItemDto requestDto = ItemDto.builder()
                .build();

        ItemDto actualResult = itemService.update(ID, ID, requestDto);

        Item item = em.createQuery("SELECT i FROM Item i WHERE i.id = " + ID, Item.class).getSingleResult();
        ItemDto expectedResult = itemMapper.toItemDto(item);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void delete_NotFound() {
        assertThrows(NotFoundException.class, () -> itemService.delete(NON_EXISTING_ID, ID));
    }

    @Test
    void delete_Success() {
        Long countBefore = em.createQuery("SELECT COUNT(i) FROM Item i", Long.class).getSingleResult();

        itemService.delete(ID, ID);

        Long countAfter = em.createQuery("SELECT COUNT(i) FROM Item i", Long.class).getSingleResult();

        assertEquals(countBefore - 1, countAfter);
    }

    @Test
    void getAllItemsByOwner() {
        List<ItemDto> actualResult = itemService.getAllItemsByOwner(ID);

        assertEquals(List.of(itemDto), actualResult);
    }

    @Test
    void findItemById_NotFound() {
        assertThrows(NotFoundException.class, () -> itemService.findItemById(NON_EXISTING_ID, ID));
    }

    @Test
    void findItemById_Owner() {
        ItemDto actualResult = itemService.findItemById(ID, ID);

        assertEquals(itemDto, actualResult);
    }

    @Test
    void findItemById_NotOwner() {
        ItemDto expectedResult = ItemDto.builder()
                .id(2L)
                .name("name2")
                .description("description2")
                .available(true)
                .requestId(2L)
                .comments(List.of(CommentDto.builder()
                        .id(2L)
                        .text("text2")
                        .authorName("name2")
                        .created(dateTimeBefore)
                        .build()))
                .build();

        ItemDto actualResult = itemService.findItemById(2L, ID);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void searchItemByNameOrDescription_Empty() {
        List<ItemDto> actualResult = itemService.searchItemByNameOrDescription("");

        assertEquals(Collections.emptyList(), actualResult);
    }

    @Test
    void searchItemByNameOrDescription() {
        List<ItemDto> expectedResult = List.of(
                ItemDto.builder()
                        .id(1L)
                        .name("name1")
                        .description("description1")
                        .available(true)
                        .requestId(1L)
                        .comments(Collections.emptyList())
                        .build(),

                ItemDto.builder()
                        .id(2L)
                        .name("name2")
                        .description("description2")
                        .available(true)
                        .requestId(2L)
                        .comments(Collections.emptyList())
                        .build(),

                ItemDto.builder()
                        .id(3L)
                        .name("name3")
                        .description("description3")
                        .available(true)
                        .requestId(3L)
                        .comments(Collections.emptyList())
                        .build()
        );

        List<ItemDto> actualResult = itemService.searchItemByNameOrDescription("description");

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void createComment_UserNotFound() {
        CommentDto requestDto = CommentDto.builder()
                .build();

        assertThrows(NotFoundException.class, () -> itemService.createComment(NON_EXISTING_ID, ID, requestDto));
    }

    @Test
    void createComment_ItemNotFound() {
        CommentDto requestDto = CommentDto.builder()
                .build();

        assertThrows(NotFoundException.class, () -> itemService.createComment(ID, NON_EXISTING_ID, requestDto));
    }

    @Test
    void createComment_BadRequestException() {
        CommentDto requestDto = CommentDto.builder()
                .build();

        assertThrows(BadRequestException.class, () -> itemService.createComment(2L, 2L, requestDto));
    }

    @Test
    void createComment_Empty() {
        CommentDto requestDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .build();

        CommentDto actualResult = itemService.createComment(ID, ID, requestDto);

        Comment comment = em.createQuery("SELECT c FROM Comment c WHERE c.id = " + ID, Comment.class).getSingleResult();
        CommentDto expectedResult = CommentMapper.toCommentDto(comment);

        assertEquals(expectedResult, actualResult);
    }

}