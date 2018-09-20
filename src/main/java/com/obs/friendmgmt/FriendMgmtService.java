package com.obs.friendmgmt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendMgmtService {
    @Autowired
    private PersonRepo personRepo;

    public Person addFriendConnection(String email, List<String> friends) {

        // TODO Handle case insensitive
        // Create the person if the email does not exist.
        Person record = personRepo.findByEmail(email)
                .orElse(personRepo.insert(new Person().setEmail(email)));


        // Filter duplicate email address
        List<String> existingfriends = Optional.ofNullable(record.getFriends())
                .orElse(new ArrayList<String>(friends.size()));

        List<String> newFriends = friends.stream().filter(s -> !existingfriends.contains(s))
                .collect(Collectors.toList());
        if (!newFriends.isEmpty()) {
            existingfriends.addAll(newFriends);
        }
        record.setFriends(existingfriends);
        return personRepo.save(record);
    }

}
