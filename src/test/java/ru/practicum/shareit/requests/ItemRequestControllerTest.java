package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestWithAnswersDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService service;

    @Autowired
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private static final String HEADER = "X-Sharer-User-Id";


    private ItemRequestDto requestDto;
    private ItemRequestWithAnswersDto answersDto;

    @BeforeEach
    void setUp() {
        mapper.findAndRegisterModules();

        requestDto = new ItemRequestDto(1L, "request", 1L,
                LocalDateTime.of(2022, 8, 8, 10, 10, 10));
        answersDto = new ItemRequestWithAnswersDto(2L, "answers",
                LocalDateTime.of(2022, 8, 8, 15, 15, 15), new ArrayList<>());
    }

    @Test
    void addNewItemRequest() throws Exception {
        when(service.addNewItemRequest(anyLong(), any()))
                .thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(requestDto.getCreated().toString())))
                .andExpect(jsonPath("$.requester", is(requestDto.getRequester()), Long.class));
    }

    @Test
    void getOwnRequests() throws Exception {
        when(service.getOwnRequests(anyLong()))
                .thenReturn(List.of(answersDto));

        mvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(answersDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(answersDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(answersDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(answersDto.getCreated().toString())))
                .andExpect(jsonPath("$[0].items", hasSize(0)));
    }

    @Test
    void getAllRequests() throws Exception {
        when(service.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(answersDto));

        mvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(answersDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(answersDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(answersDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(answersDto.getCreated().toString())))
                .andExpect(jsonPath("$[0].items", hasSize(0)));
    }

    @Test
    void getRequest() throws Exception {
        when(service.getRequest(anyLong(), anyLong()))
                .thenReturn(answersDto);

        mvc.perform(get("/requests/{requestId}", answersDto.getId())
                        .content(mapper.writeValueAsString(answersDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(answersDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(answersDto.getDescription())))
                .andExpect(jsonPath("$.created", is(answersDto.getCreated().toString())))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }
}