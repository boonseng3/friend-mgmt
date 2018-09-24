package com.obs.friendmgmt;

import com.obs.friendmgmt.exception.RecordNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FriendMgmtService {
    @Autowired
    private PersonRepo personRepo;

    /**
     * Add friend connections to each person in the connections list.
     *
     * @param connections list of emails to add friend connection
     */
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

                                // need to check block list, to prevent new friend connection to the person if blocked.
                                Optional.ofNullable(record.getBlocked()).ifPresent(blocks -> {
                                    newFriends.removeAll(blocks);
                                });

                                // need to check if the target also block the requestor hence prevent friend connection
                               List<String> updatedNewFriends =  newFriends.stream()
                                        .filter(target -> !Optional.ofNullable(personRepo.findByEmail(target)
                                                .orElseGet(() -> personRepo.insert(new Person().setEmail(target)))
                                        .getBlocked()).orElse(new ArrayList<>()).contains(currentEmail))
                                .collect(Collectors.toList());

                                if(!updatedNewFriends.isEmpty()){
                                    currentFriends.addAll(updatedNewFriends);
                                    personRepo.save(record.setFriends(currentFriends));
                                }

                            });
                });

    }

    /**
     * Return a list if there are any new items in the newList, otherwise empty.
     *
     * @param sourceList exisitng list
     * @param newList    new list to filter for duplicates
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

    /**
     * Return common friends among the list of person.
     *
     * @param emails list of person email
     * @return list of common friends
     */
    public List<String> getCommonFriendConnection(List<String> emails) {
        return personRepo.findByEmail(emails)
                .stream().map(Person::getFriends)
                .reduce((strings, strings2) -> strings.stream().filter(s -> strings2.contains(s)).collect(Collectors.toList())).orElse(Collections.EMPTY_LIST);
    }

    /**
     * Update the requestor as subscribed to the target and target as having requestor as subscribers.
     *
     * @param requestor
     * @param target
     */
    public void subscribeForUpdates(String requestor, String target) {

        // Update subscribed relationship
        Person requestorRecord = personRepo.findByEmail(requestor)
                .orElseGet(() -> personRepo.insert(new Person().setEmail(requestor)));

        mergeFilterDuplicate(Optional.ofNullable(requestorRecord.getSubscribed()).orElse(new ArrayList<>(1)), Arrays.asList(target))
                .ifPresent(newSubscribedSources -> {
                    List<String> currentSubscribedSources = Optional.ofNullable(requestorRecord.getSubscribed()).orElse(new ArrayList<>(1));
                    currentSubscribedSources.addAll(newSubscribedSources);
                    personRepo.save(requestorRecord.setSubscribed(currentSubscribedSources));
                });

        // Update the subscribers relationship
        Person targetRecord = personRepo.findByEmail(target)
                .orElseGet(() -> personRepo.insert(new Person().setEmail(target)));

        mergeFilterDuplicate(Optional.ofNullable(targetRecord.getSubscribers()).orElse(new ArrayList<>(1)), Arrays.asList(requestor))
                .ifPresent(newSubscribers -> {
                    List<String> currentSubscribers = Optional.ofNullable(targetRecord.getSubscribers()).orElse(new ArrayList<>(1));
                    currentSubscribers.addAll(newSubscribers);
                    personRepo.save(targetRecord.setSubscribers(currentSubscribers));
                });
    }

    /**
     * Update the requestor block list and remove the subscribe/subscriber relationship with target.
     * @param requestor
     * @param target
     */
    public void block(String requestor, String target) {
        // add to blocked list
        Person requestorRecord = personRepo.findByEmail(requestor)
                .orElseGet(() -> personRepo.insert(new Person().setEmail(requestor)));

        mergeFilterDuplicate(Optional.ofNullable(requestorRecord.getBlocked()).orElse(new ArrayList<>(1)), Arrays.asList(target))
                .ifPresent(newBlocked -> {
                    List<String> currentBlocked = Optional.ofNullable(requestorRecord.getBlocked()).orElse(new ArrayList<>(1));
                    currentBlocked.addAll(newBlocked);
                    personRepo.save(requestorRecord.setBlocked(currentBlocked));
                });

        // remove subscribed/subscriber relationship
        Person subscribeRecord = personRepo.findByEmail(requestor).get();
        Optional.ofNullable(subscribeRecord.getSubscribed()).filter(strings -> strings.remove(target))
                .ifPresent(strings -> personRepo.save(subscribeRecord));
        Person subscriberRecord = personRepo.findByEmail(target).get();
        Optional.ofNullable(subscriberRecord.getSubscribers()).filter(strings -> strings.remove(requestor))
                .ifPresent(strings -> personRepo.save(subscriberRecord));

    }

    /**
     * Return the list of subscribers to this person.
     * @param email
     * @return
     */
    public List<String> getBroadcastList(String email) {
        return personRepo.findByEmail(email)
                .orElseGet(() -> personRepo.insert(new Person().setEmail(email))).getSubscribers();

    }
}
