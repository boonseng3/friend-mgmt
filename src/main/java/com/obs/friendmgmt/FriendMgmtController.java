package com.obs.friendmgmt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/friends")
@Slf4j
public class FriendMgmtController {

    @Autowired
    private FriendMgmtService friendMgmtService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/connections", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public SuccessDto createFriendConnection(@RequestBody FriendConnectionDto obj) {

        try {
            obj.getFriends().stream()
                    .forEach(email -> {
                        friendMgmtService.addFriendConnection(email, obj.getFriends()
                                .stream().filter(s -> !s.equalsIgnoreCase(email)).collect(Collectors.toList())
                        );
                    });
            return new SuccessDto().setSuccess(true);
        } catch (Exception e) {
            log.error("Exception adding connection", e);
            return new SuccessDto();
        }
    }
}
