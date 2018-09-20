package com.obs.friendmgmt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.obs.friendmgmt.test.ControllerTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class FriendMgmtControllerTest extends ControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private FriendMgmtController controller;
    @Mock
    private FriendMgmtService friendMgmtService;

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

        Mockito.when(friendMgmtService.addFriendConnection("andy@example.com", new ArrayList(Arrays.asList("john@example.com"))))
                .thenReturn(new Person().setEmail("andy@example.com").setFriends(new ArrayList(Arrays.asList("john@example.com"))));
        Mockito.when(friendMgmtService.addFriendConnection("john@example.com", new ArrayList(Arrays.asList("andy@example.com"))))
                .thenReturn(new Person().setEmail("john@example.com").setFriends(new ArrayList(Arrays.asList("andy  @example.com"))));

        mvc.perform(MockMvcRequestBuilders
                .post("/friends/connections").content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                .andReturn();

    }
}