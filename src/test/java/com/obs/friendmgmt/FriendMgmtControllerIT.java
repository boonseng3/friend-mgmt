package com.obs.friendmgmt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.obs.friendmgmt.test.ControllerIT;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class FriendMgmtControllerIT extends ControllerIT {
    @Autowired // pre- configured to resolve relative paths to http://localhost:${local.server.port}
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FriendMgmtService friendMgmtService;
    @Autowired
    private PersonRepo personRepo;

    @Before
    public void before() {
        personRepo.deleteAll();
    }

    @Test
    public void createFriendConnection() throws Exception {
        ObjectNode request = objectMapper.createObjectNode();
        request.withArray("friends")
                .add("andy@example.com")
                .add("john@example.com");

        ResponseEntity<SuccessDto> responseEntity = restTemplate.postForEntity("/friends/connections", request, SuccessDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).hasFieldOrPropertyWithValue("success", true);

    }
}