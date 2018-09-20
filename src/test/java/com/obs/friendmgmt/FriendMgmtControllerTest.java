package com.obs.friendmgmt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.obs.friendmgmt.test.ControllerTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class FriendMgmtControllerTest extends ControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private FriendMgmtController controller;
    private MockMvc mvc;


    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    public void createFriendConnection() throws Exception {
        ObjectNode request = objectMapper.createObjectNode();
        request.withArray("friends")
                .add("andy@example.com")
                .add("john@example.com");

        ObjectNode expected = objectMapper.createObjectNode()
                .put("success", true);

        mvc.perform(MockMvcRequestBuilders
                .post("/friends/connections").content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                .andReturn();

    }
}