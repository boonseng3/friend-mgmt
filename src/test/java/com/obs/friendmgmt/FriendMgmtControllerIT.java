package com.obs.friendmgmt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.obs.friendmgmt.exception.ErrorMessageDto;
import com.obs.friendmgmt.test.ControllerIT;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        personRepo.insert(new Person().setEmail("peter@example.com"));
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

    @Test
    public void getFriendConnection() throws Exception {
        ObjectNode request = objectMapper.createObjectNode();
        request.withArray("friends")
                .add("andy@example.com")
                .add("john@example.com")
                .add("peter@example.com");

        ResponseEntity<SuccessDto> createResponseEntity = restTemplate.postForEntity("/friends/connections", request, SuccessDto.class);
        assertThat(createResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponseEntity.getBody()).hasFieldOrPropertyWithValue("success", true);


        Map<String, Object> getRequest = new HashMap<>();
        getRequest.put("email", "andy@example.com");
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity(getRequest, requestHeaders);

        ResponseEntity<FriendConnectionDto> getResponseEntity = restTemplate.exchange("/friends/connections", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<FriendConnectionDto>() {
        });
        assertThat(getResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponseEntity.getBody()).hasFieldOrPropertyWithValue("success", true)
                .hasFieldOrPropertyWithValue("count", 2);
        assertThat(getResponseEntity.getBody().getFriends()).containsExactly("john@example.com", "peter@example.com");


        getRequest = new HashMap<>();
        getRequest.put("email", "john@example.com");
        requestHeaders = new HttpHeaders();
        requestEntity = new HttpEntity(getRequest, requestHeaders);
        getResponseEntity = restTemplate.exchange("/friends/connections", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<FriendConnectionDto>() {
        });
        assertThat(getResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponseEntity.getBody()).hasFieldOrPropertyWithValue("success", true)
                .hasFieldOrPropertyWithValue("count", 2);
        assertThat(getResponseEntity.getBody().getFriends()).containsExactly("andy@example.com", "peter@example.com");


        getRequest = new HashMap<>();
        getRequest.put("email", "peter@example.com");
        requestHeaders = new HttpHeaders();
        requestEntity = new HttpEntity(getRequest, requestHeaders);
        getResponseEntity = restTemplate.exchange("/friends/connections", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<FriendConnectionDto>() {
        });
        assertThat(getResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponseEntity.getBody()).hasFieldOrPropertyWithValue("success", true)
                .hasFieldOrPropertyWithValue("count", 2);
        assertThat(getResponseEntity.getBody().getFriends()).containsExactly("andy@example.com", "john@example.com");
    }

    @Test
    public void getFriendConnectionNotFound() throws Exception {
        Map<String, Object> getRequest = new HashMap<>();
        getRequest.put("email", UUID.randomUUID().toString()+"@example.com");
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity(getRequest, requestHeaders);

        ResponseEntity<ErrorMessageDto> getResponseEntity = restTemplate.exchange("/friends/connections", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<ErrorMessageDto>() {
        });
        assertThat(getResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        System.out.println("getResponseEntity.getBody() = " + getResponseEntity.getBody());
        assertThat(getResponseEntity.getBody()).hasFieldOrPropertyWithValue("message", "Email not found");


    }
}