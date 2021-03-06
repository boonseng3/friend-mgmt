package com.obs.friendmgmt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.obs.friendmgmt.dto.BroadcastMessageResponseDto;
import com.obs.friendmgmt.dto.FriendConnectionDto;
import com.obs.friendmgmt.dto.SuccessDto;
import com.obs.friendmgmt.exception.ErrorMessageDto;
import com.obs.friendmgmt.test.ControllerIT;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.Arrays;
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

        // setup some data
        ObjectNode request = objectMapper.createObjectNode();
        request.withArray("friends")
                .add("person1@example.com")
                .add("person2@example.com")
                .add("person3@example.com")
                .add("person4@example.com");

        ResponseEntity<SuccessDto> responseEntity = restTemplate.postForEntity("/friends/connections", request, SuccessDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(personRepo.findByEmail("person1@example.com").get().getFriends()).containsExactly("person2@example.com", "person3@example.com", "person4@example.com");
        assertThat(personRepo.findByEmail("person2@example.com").get().getFriends()).containsExactly("person1@example.com", "person3@example.com", "person4@example.com");
        assertThat(personRepo.findByEmail("person3@example.com").get().getFriends()).containsExactly("person1@example.com", "person2@example.com", "person4@example.com");
        assertThat(personRepo.findByEmail("person4@example.com").get().getFriends()).containsExactly("person1@example.com", "person2@example.com", "person3@example.com");

        request = objectMapper.createObjectNode();
        request.withArray("friends")
                .add("person2@example.com")
                .add("person5@example.com");
        responseEntity = restTemplate.postForEntity("/friends/connections", request, SuccessDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(personRepo.findByEmail("person2@example.com").get().getFriends()).containsExactly("person1@example.com", "person3@example.com", "person4@example.com", "person5@example.com");
        assertThat(personRepo.findByEmail("person5@example.com").get().getFriends()).containsExactly("person2@example.com");

        request = objectMapper.createObjectNode();
        request.withArray("friends")
                .add("person3@example.com")
                .add("person5@example.com");
        responseEntity = restTemplate.postForEntity("/friends/connections", request, SuccessDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(personRepo.findByEmail("person3@example.com").get().getFriends()).containsExactly("person1@example.com", "person2@example.com", "person4@example.com", "person5@example.com");
        assertThat(personRepo.findByEmail("person5@example.com").get().getFriends()).containsExactly("person2@example.com", "person3@example.com");
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
        assertThat(personRepo.findByEmail("andy@example.com").get().getFriends()).containsExactly("john@example.com");
        assertThat(personRepo.findByEmail("john@example.com").get().getFriends()).containsExactly("andy@example.com");
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
        getRequest.put("email", UUID.randomUUID().toString() + "@example.com");
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity(getRequest, requestHeaders);

        ResponseEntity<ErrorMessageDto> getResponseEntity = restTemplate.exchange("/friends/connections", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<ErrorMessageDto>() {
        });
        assertThat(getResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getResponseEntity.getBody()).hasFieldOrPropertyWithValue("message", "Email not found");
    }


    @Test
    public void getCommonFriendConnection() throws Exception {
        Map<String, Object> getRequest = new HashMap<>();
        getRequest.put("friends", Arrays.asList("person1@example.com", "person2@example.com"));
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity(getRequest, requestHeaders);

        ResponseEntity<FriendConnectionDto> getResponseEntity = restTemplate.exchange("/friends/connections/common", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<FriendConnectionDto>() {
        });
        assertThat(getResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponseEntity.getBody()).hasFieldOrPropertyWithValue("success", true)
                .hasFieldOrPropertyWithValue("count", 2);
        assertThat(getResponseEntity.getBody().getFriends()).containsExactly("person3@example.com", "person4@example.com");

        getRequest = new HashMap<>();
        getRequest.put("friends", Arrays.asList("person2@example.com", "person3@example.com"));
        requestHeaders = new HttpHeaders();
        requestEntity = new HttpEntity(getRequest, requestHeaders);

        getResponseEntity = restTemplate.exchange("/friends/connections/common", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<FriendConnectionDto>() {
        });
        assertThat(getResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponseEntity.getBody()).hasFieldOrPropertyWithValue("success", true)
                .hasFieldOrPropertyWithValue("count", 3);
        assertThat(getResponseEntity.getBody().getFriends()).containsExactly("person1@example.com", "person4@example.com", "person5@example.com");
    }

    @Test
    public void subscribeForUpdates() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("requestor", "person1@example.com");
        request.put("target", "person2@example.com");
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity(request, requestHeaders);

        ResponseEntity<SuccessDto> getResponseEntity = restTemplate.exchange("/friends/subscribe", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<SuccessDto>() {
        });
        assertThat(getResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponseEntity.getBody()).hasFieldOrPropertyWithValue("success", true);
        assertThat(personRepo.findByEmail("person1@example.com").get().getSubscribed()).containsExactly("person2@example.com");
        assertThat(personRepo.findByEmail("person2@example.com").get().getSubscribers()).containsExactly("person1@example.com");
    }

    @Test
    public void block() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("requestor", "person3@example.com");
        request.put("target", "person5@example.com");
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity(request, requestHeaders);

        ResponseEntity<SuccessDto> getResponseEntity = restTemplate.exchange("/friends/subscribe", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<SuccessDto>() {
        });
        assertThat(getResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponseEntity.getBody()).hasFieldOrPropertyWithValue("success", true);
        assertThat(personRepo.findByEmail("person3@example.com").get().getSubscribed()).containsExactly("person5@example.com");
        assertThat(personRepo.findByEmail("person5@example.com").get().getSubscribers()).containsExactly("person3@example.com");

        getResponseEntity = restTemplate.exchange("/friends/block", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<SuccessDto>() {
        });
        assertThat(getResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponseEntity.getBody()).hasFieldOrPropertyWithValue("success", true);
        assertThat(personRepo.findByEmail("person3@example.com").get().getSubscribed()).doesNotContain("person5@example.com");
        assertThat(personRepo.findByEmail("person5@example.com").get().getSubscribers()).doesNotContain("person3@example.com");
    }

    @Test
    public void blockAdd() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("requestor", "person1@example.com");
        request.put("target", "person5@example.com");
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity(request, requestHeaders);

        ResponseEntity<SuccessDto> getResponseEntity = restTemplate.exchange("/friends/block", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<SuccessDto>() {
        });
        assertThat(getResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponseEntity.getBody()).hasFieldOrPropertyWithValue("success", true);
        assertThat(personRepo.findByEmail("person1@example.com").get().getBlocked()).containsExactly("person5@example.com");
        assertThat(personRepo.findByEmail("person1@example.com").get().getFriends()).doesNotContain("person5@example.com");
        assertThat(personRepo.findByEmail("person5@example.com").get().getBlocked()).isNull();
        assertThat(personRepo.findByEmail("person5@example.com").get().getFriends()).doesNotContain("person1@example.com");

        // create friend connection
        ObjectNode requestFriend = objectMapper.createObjectNode();
        requestFriend.withArray("friends")
                .add("person1@example.com")
                .add("person5@example.com");

        requestEntity = new HttpEntity(requestFriend, requestHeaders);

        ResponseEntity<SuccessDto> responseEntity = restTemplate.exchange("/friends/connections", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<SuccessDto>() {
        });
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).hasFieldOrPropertyWithValue("success", true);
        assertThat(personRepo.findByEmail("person1@example.com").get().getBlocked()).containsExactly("person5@example.com");
        assertThat(personRepo.findByEmail("person1@example.com").get().getFriends()).doesNotContain("person5@example.com");
        assertThat(personRepo.findByEmail("person5@example.com").get().getBlocked()).isNull();
        assertThat(personRepo.findByEmail("person5@example.com").get().getFriends()).doesNotContain("person1@example.com");

        requestFriend = objectMapper.createObjectNode();
        requestFriend.withArray("friends")
                .add("person5@example.com")
                .add("person1@example.com");

        requestEntity = new HttpEntity(requestFriend, requestHeaders);

        responseEntity = restTemplate.exchange("/friends/connections", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<SuccessDto>() {
        });
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).hasFieldOrPropertyWithValue("success", true);
        assertThat(personRepo.findByEmail("person1@example.com").get().getBlocked()).containsExactly("person5@example.com");
        assertThat(personRepo.findByEmail("person1@example.com").get().getFriends()).doesNotContain("person5@example.com");
        assertThat(personRepo.findByEmail("person5@example.com").get().getBlocked()).isNull();
        assertThat(personRepo.findByEmail("person5@example.com").get().getFriends()).doesNotContain("person1@example.com");
    }

    @Test
    public void broadcast() throws Exception {

        // person3 subscribe to person 5
        Map<String, Object> request = new HashMap<>();
        request.put("requestor", "person3@example.com");
        request.put("target", "person5@example.com");
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity(request, requestHeaders);

        ResponseEntity<SuccessDto> getResponseEntity = restTemplate.exchange("/friends/subscribe", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<SuccessDto>() {
        });
        assertThat(getResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponseEntity.getBody()).hasFieldOrPropertyWithValue("success", true);
        assertThat(personRepo.findByEmail("person3@example.com").get().getSubscribed()).containsExactly("person5@example.com");
        assertThat(personRepo.findByEmail("person5@example.com").get().getSubscribers()).containsExactly("person3@example.com");

        Map<String, Object> broadcastRequest = new HashMap<>();
        broadcastRequest.put("sender", "person5@example.com");
        broadcastRequest.put("text", "test");
        requestEntity = new HttpEntity(broadcastRequest, requestHeaders);

        // verify person 5 subscribers contains only person 3
        ResponseEntity<BroadcastMessageResponseDto> broadcastResponseEntity = restTemplate.exchange("/friends/broadcast", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<BroadcastMessageResponseDto>() {
        });
        assertThat(broadcastResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(broadcastResponseEntity.getBody()).hasFieldOrPropertyWithValue("success", true);
        assertThat(broadcastResponseEntity.getBody().getRecipients()).containsExactly("person3@example.com");

        // person 4 subscribe to person 5
        request = new HashMap<>();
        request.put("requestor", "person4@example.com");
        request.put("target", "person5@example.com");
        requestHeaders = new HttpHeaders();
        requestEntity = new HttpEntity(request, requestHeaders);

       getResponseEntity = restTemplate.exchange("/friends/subscribe", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<SuccessDto>() {
        });
        assertThat(getResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponseEntity.getBody()).hasFieldOrPropertyWithValue("success", true);
        assertThat(personRepo.findByEmail("person4@example.com").get().getSubscribed()).containsExactly("person5@example.com");
        assertThat(personRepo.findByEmail("person5@example.com").get().getSubscribers()).containsExactly("person3@example.com","person4@example.com");

        // verify person 5 subscribers contains only person 3, person 4
        broadcastRequest = new HashMap<>();
        broadcastRequest.put("sender", "person5@example.com");
        broadcastRequest.put("text", "test");
        requestEntity = new HttpEntity(broadcastRequest, requestHeaders);

        broadcastResponseEntity = restTemplate.exchange("/friends/broadcast", HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<BroadcastMessageResponseDto>() {
        });
        assertThat(broadcastResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(broadcastResponseEntity.getBody()).hasFieldOrPropertyWithValue("success", true);
        assertThat(broadcastResponseEntity.getBody().getRecipients()).containsExactly("person3@example.com","person4@example.com");
    }
}