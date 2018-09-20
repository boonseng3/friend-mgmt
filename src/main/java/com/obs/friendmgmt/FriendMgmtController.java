package com.obs.friendmgmt;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/friends")
public class FriendMgmtController {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/connections", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public SuccessDto createFriendConnection(@RequestBody FriendConnectionDto obj) {
        return new SuccessDto();
    }
}
