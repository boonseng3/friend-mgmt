package com.obs.friendmgmt;

import com.obs.friendmgmt.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static com.obs.friendmgmt.util.MappingUtil.mapBroadcastMessageResponseDto;
import static com.obs.friendmgmt.util.MappingUtil.mapFriendConnectionDto;

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
            friendMgmtService.addFriendConnection(obj.getFriends());
            return new SuccessDto().setSuccess(true);
        } catch (Exception e) {
            log.error("Exception adding connection", e);
            return new SuccessDto();
        }
    }

    @PutMapping(value = "/connections", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public FriendConnectionDto getFriendConnection(@RequestBody FriendConnectionRequestDto obj) {
        return mapFriendConnectionDto(friendMgmtService.getFriendConnection(obj.getEmail()), true);
    }

    @PutMapping(value = "/connections/common", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public FriendConnectionDto getCommonFriendConnection(@RequestBody CommonFriendConnectionRequestDto obj) {
        return mapFriendConnectionDto(friendMgmtService.getCommonFriendConnection(obj.getFriends()), true);
    }

    @PutMapping(value = "/subscribe", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public SuccessDto subscribeForUpdates(@RequestBody RequestorTargetRequestDto obj) {
        friendMgmtService.subscribeForUpdates(obj.getRequestor(), obj.getTarget());
        return new SuccessDto().setSuccess(true);
    }

    @PutMapping(value = "/block", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public SuccessDto block(@RequestBody RequestorTargetRequestDto obj) {
        friendMgmtService.block(obj.getRequestor(), obj.getTarget());
        return new SuccessDto().setSuccess(true);
    }

    @PutMapping(value = "/broadcast", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public BroadcastMessageResponseDto block(@RequestBody BroadcastMessageRequestDto obj) {
        return mapBroadcastMessageResponseDto(friendMgmtService.getBroadcastList(obj.getSender()), true);
    }

}
