package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.Collections;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@Import(ItemServiceImpl.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateItem() throws Exception {
        long userId = 1L;
        ItemDto requestDto = ItemDto.builder().build();

        ItemDto responseDto = ItemDto.builder()
                .id(1L)
                .build();

        when(itemService.create(userId, requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));

        verify(itemService, times(1)).create(userId, requestDto);
    }

    @Test
    void testUpdateItem() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        ItemDto requestDto = ItemDto.builder().build();

        ItemDto responseDto = ItemDto.builder()
                .id(1L)
                .build();

        when(itemService.update(userId, itemId, requestDto)).thenReturn(responseDto);

        mockMvc.perform(patch("/items/" + itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));

        verify(itemService, times(1)).update(userId, itemId, requestDto);
    }

    @Test
    void testDeleteItem() throws Exception {
        long userId = 1L;
        long itemId = 1L;

        mockMvc.perform(delete("/items/" + itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemService, times(1)).delete(userId, itemId);
    }

    @Test
    void testGetItem() throws Exception {
        long userId = 1L;

        when(itemService.getAllItemsByOwner(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemService, times(1)).getAllItemsByOwner(userId);
    }

    @Test
    void testFindItemById() throws Exception {
        long userId = 1L;
        long itemId = 1L;

        ItemDto responseDto = ItemDto.builder()
                .id(itemId)
                .build();

        when(itemService.findItemById(userId, itemId)).thenReturn(responseDto);

        mockMvc.perform(get("/items/" + itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));

        verify(itemService, times(1)).findItemById(userId, itemId);
    }

    @Test
    void testSearchItem() throws Exception {
        long userId = 1L;
        String text = "text";

        when(itemService.searchItemByNameOrDescription(text)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemService, times(1)).searchItemByNameOrDescription(text);
    }

    @Test
    void testCreateComment() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        CommentDto requestDto = CommentDto.builder().build();

        CommentDto responseDto = CommentDto.builder()
                .id(1L)
                .build();

        when(itemService.createComment(userId, itemId, requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/items/" + itemId + "/comment")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));

        verify(itemService, times(1)).createComment(userId, itemId, requestDto);
    }

}