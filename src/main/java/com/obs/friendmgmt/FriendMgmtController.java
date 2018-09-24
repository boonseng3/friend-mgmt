package com.obs.friendmgmt;

import com.obs.friendmgmt.dto.*;
import com.obs.friendmgmt.exception.ErrorMessageDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(description = "Friend Management API")
public class FriendMgmtController {

    @Autowired
    private FriendMgmtService friendMgmtService;

    @ApiOperation(value = "Create a friend connection between email addresses")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Friend connection created.", response = SuccessDto.class)})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/connections", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public SuccessDto createFriendConnection(@RequestBody FriendConnectionCreateRequestDto request) {

        try {
            friendMgmtService.addFriendConnection(request.getFriends());
            return new SuccessDto().setSuccess(true);
        } catch (Exception e) {
            log.error("Exception adding connection", e);
            return new SuccessDto();
        }
    }

    @ApiOperation(value = "Retrieve the friends list for an email address")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of friends.", response = FriendConnectionDto.class),
            @ApiResponse(code = 404, message = "Friend not found.", response = ErrorMessageDto.class)})
    @PutMapping(value = "/connections", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public FriendConnectionDto getFriendConnection(@RequestBody FriendConnectionRequestDto request) {
        return mapFriendConnectionDto(friendMgmtService.getFriendConnection(request.getEmail()), true);
    }

    @ApiOperation(value = "Retrieve the common friends list between two email addresses.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of common friends.", response = FriendConnectionDto.class)})
    @PutMapping(value = "/connections/common", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public FriendConnectionDto getCommonFriendConnection(@RequestBody CommonFriendConnectionRequestDto request) {
        return mapFriendConnectionDto(friendMgmtService.getCommonFriendConnection(request.getFriends()), true);
    }

    @ApiOperation(value = "Subscribe to updates from an email address.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Subscribed to this person for updates. This person do not need to be friend.", response = SuccessDto.class)})
    @PutMapping(value = "/subscribe", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public SuccessDto subscribeForUpdates(@RequestBody RequestorTargetRequestDto request) {
        friendMgmtService.subscribeForUpdates(request.getRequestor(), request.getTarget());
        return new SuccessDto().setSuccess(true);
    }

    @ApiOperation(value = "Block updates from an email address.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Email address blocked.", response = SuccessDto.class)})
    @PutMapping(value = "/block", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public SuccessDto block(@RequestBody RequestorTargetRequestDto request) {
        friendMgmtService.block(request.getRequestor(), request.getTarget());
        return new SuccessDto().setSuccess(true);
    }

    @ApiOperation(value = "Retrieve all email addresses that can receive updates from an email address")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of emails subscrbed to this email.", response = BroadcastMessageResponseDto.class)})
    @PutMapping(value = "/broadcast", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public BroadcastMessageResponseDto broadcast(@RequestBody BroadcastMessageRequestDto request) {
        return mapBroadcastMessageResponseDto(friendMgmtService.getBroadcastList(request.getSender()), true);
    }

}
