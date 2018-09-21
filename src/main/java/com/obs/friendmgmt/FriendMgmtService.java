package com.obs.friendmgmt;

import com.obs.friendmgmt.exception.RecordNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendMgmtService {
    @Autowired
    private PersonRepo personRepo;

    /**
     * Add friend connections to each person in the connections list.
     * @param connections list of emails to add friend connection
     */
    @Transactional
    public void addFriendConnection(List<String> connections) {
        connections.stream()
                .forEach(currentEmail -> {
                    // filter out the current email from the whole list to get just the friends
                    List<String> friends = connections.stream().filter(s -> !s.equalsIgnoreCase(currentEmail)).collect(Collectors.toList());

                    // Create the person if the email does not exist.
                    Person record = personRepo.findByEmail(currentEmail)
                            .orElseGet(() -> personRepo.insert(new Person().setEmail(currentEmail)));

                    mergeFilterDuplicate(Optional.ofNullable(record.getFriends()).orElse(new ArrayList<>(friends.size())), friends)
                            .ifPresent(newFriends -> {
                                List<String> currentFriends = Optional.ofNullable(record.getFriends()).orElse(new ArrayList<>(friends.size()));
                                currentFriends.addAll(newFriends);
                                personRepo.save(record.setFriends(currentFriends));
                            });
                });

    }

    /**
     * Return a list if there are any new items in the newList, otherwise empty.
     *
     * @param sourceList exisitng list
     * @param newList new list to filter for duplicates
     * @return filtered list
     */
    private Optional<List<String>> mergeFilterDuplicate(@NotNull List<String> sourceList, @NotNull List<String> newList) {
        List<String> merged = newList.stream().filter(s -> !sourceList.contains(s)).collect(Collectors.toList());
        return merged.isEmpty() ? Optional.empty() : Optional.of(merged);

    }

    public Person getFriendConnection(String email) {
        // Create the person if the email does not exist.
        return personRepo.findByEmail(email).orElseThrow(() -> new RecordNotFoundException("Email not found"));

    }

}
