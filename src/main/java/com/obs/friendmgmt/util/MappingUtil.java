package com.obs.friendmgmt.util;

import com.obs.friendmgmt.FriendConnectionDto;
import com.obs.friendmgmt.Person;

import java.util.List;

public class MappingUtil {

    public static FriendConnectionDto mapFriendConnectionDto(Person person, boolean success){
        return new FriendConnectionDto()
                .setSuccess(success)
                .setFriends(person.getFriends())
                .setCount(person.getFriends().size());
    }
    public static FriendConnectionDto mapFriendConnectionDto(List<String> emails, boolean success){
        return new FriendConnectionDto()
                .setSuccess(success)
                .setFriends(emails)
                .setCount(emails.size());
    }
}
