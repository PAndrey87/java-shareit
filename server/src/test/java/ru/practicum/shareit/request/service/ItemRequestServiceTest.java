package ru.practicum.shareit.request.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class ItemRequestServiceTest {

    public static final long ID = 1L;

    @Autowired
    private final EntityManager em;

    @Autowired
    private final ItemRequestService itemRequestService;

    @Autowired
    private final ItemRequestMapper itemRequestMapper;

    @Test
    void create() {
        ItemRequestAddDto requestDto = new ItemRequestAddDto("description");

        ItemRequestDto actualResult = itemRequestService.create(ID, requestDto);

        ItemRequest itemRequest = em.createQuery("SELECT ir FROM ItemRequest ir WHERE ir.id = 5", ItemRequest.class)
                .getSingleResult();
        ItemRequestDto expectedResult = itemRequestMapper.toItemRequestDto(itemRequest);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getAllByOwner() {
        List<ItemRequestDto> actualResult = itemRequestService.getAllByOwner(ID);

        List<ItemRequest> itemRequest = em.createQuery("SELECT ir FROM ItemRequest ir WHERE ir.requestor.id = " + ID, ItemRequest.class)
                .getResultList().stream().toList();
        List<ItemRequestDto> expectedResult = itemRequestMapper.toListOfItemRequestDto(itemRequest);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getAll() {
        List<ItemRequestDto> actualResult = itemRequestService.getAllByOwner(ID);

        List<ItemRequest> itemRequest = em.createQuery("SELECT ir FROM ItemRequest ir WHERE ir.requestor.id = " + ID, ItemRequest.class)
                .getResultList().stream().toList();
        List<ItemRequestDto> expectedResult = itemRequestMapper.toListOfItemRequestDto(itemRequest);

        assertEquals(expectedResult, actualResult);
    }

}