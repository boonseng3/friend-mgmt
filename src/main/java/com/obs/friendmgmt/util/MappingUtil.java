package com.obs.friendmgmt.util;

import com.obs.friendmgmt.Person;
import com.obs.friendmgmt.dto.BroadcastMessageResponseDto;
import com.obs.friendmgmt.dto.FriendConnectionDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MappingUtil {

    public static FriendConnectionDto mapFriendConnectionDto(Person person, boolean success) {
        return new FriendConnectionDto()
                .setSuccess(success)
                .setFriends(Optional.ofNullable(person.getFriends()).orElseGet(() -> new ArrayList<>()))
                .setCount(Optional.ofNullable(person.getFriends()).orElseGet(() -> new ArrayList<>()).size());
    }

    public static FriendConnectionDto mapFriendConnectionDto(List<String> emails, boolean success) {
        return new FriendConnectionDto()
                .setSuccess(success)
                .setFriends(emails)
                .setCount(emails.size());
    }

    public static BroadcastMessageResponseDto mapBroadcastMessageResponseDto(List<String> emails, boolean success) {
        return new BroadcastMessageResponseDto()
                .setSuccess(success)
                .setRecipients(emails);
    }
}
