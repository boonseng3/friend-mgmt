package com.obs.friendmgmt;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class FriendMgmtServiceTest {

    @InjectMocks
    private FriendMgmtService friendMgmtService;
    @Mock
    private PersonRepo personRepo;


    @Test
    public void addConnectionBetweenExistingPerson() {
        Person record = new Person().setId(new ObjectId()).setEmail("person1@email.com");
        Person updatedRecord = new Person().setId(record.getId()).setEmail("person1@email.com").setFriends(new ArrayList<>(Arrays.asList("person2@email.com")));

        Person friendRecord1 = new Person().setId(new ObjectId()).setEmail("person2@email.com");
        Person updatedfriendRecord1 = new Person().setId(friendRecord1.getId()).setEmail("person2@email.com").setFriends(new ArrayList<>(Arrays.asList("person1@email.com")));

        Mockito.when(personRepo.findByEmail(record.getEmail()))
                .thenReturn(Optional.of(record));
        Mockito.when(personRepo.save(updatedRecord))
                .thenReturn(updatedRecord);

         Mockito.when(personRepo.findByEmail("person2@email.com"))
                .thenReturn(Optional.of(friendRecord1));

        friendMgmtService.addFriendConnection(Arrays.asList("person1@email.com", "person2@email.com"));

        Mockito.verify(personRepo, times(1)).save(updatedRecord);
        Mockito.verify(personRepo, times(1)).save(updatedfriendRecord1);
    }

    @Test
    public void AddFriendConnectionButPerson1BlockPerson2() {
        Person record = new Person().setId(new ObjectId()).setEmail("person1@email.com").setBlocked(Arrays.asList("person2@email.com"));
        Person updatedRecord = new Person().setId(record.getId()).setEmail("person1@email.com");

        Person friendRecord1 = new Person().setId(new ObjectId()).setEmail("person2@email.com");
        Person updatedfriendRecord1 = new Person().setId(friendRecord1.getId()).setEmail("person2@email.com");

        Mockito.when(personRepo.findByEmail(record.getEmail()))
                .thenReturn(Optional.of(record));

        Mockito.when(personRepo.save(updatedRecord))
                .thenReturn(updatedRecord);

        Mockito.when(personRepo.findByEmail("person2@email.com"))
                .thenReturn(Optional.of(friendRecord1));

        friendMgmtService.addFriendConnection(Arrays.asList("person1@email.com", "person2@email.com"));

        Mockito.verify(personRepo, times(0)).save(any());
    }

    @Test
    public void AddFriendConnectionButPerson1BlockedByPerson2() {
        Person record = new Person().setId(new ObjectId()).setEmail("person1@email.com");
        Person updatedRecord = new Person().setId(record.getId()).setEmail("person1@email.com");

        Person friendRecord1 = new Person().setId(new ObjectId()).setEmail("person2@email.com").setBlocked(Arrays.asList("person1@email.com"));

        Mockito.when(personRepo.findByEmail(record.getEmail()))
                .thenReturn(Optional.of(record));

        Mockito.when(personRepo.save(updatedRecord))
                .thenReturn(updatedRecord);

        Mockito.when(personRepo.findByEmail("person2@email.com"))
                .thenReturn(Optional.of(friendRecord1));

        friendMgmtService.addFriendConnection(Arrays.asList("person1@email.com", "person2@email.com"));

        Mockito.verify(personRepo, times(0)).save(any());
    }

    /**
     * Verify that existing friend connection will not result in save.
     */
    @Test
    public void addDuplicateConnection2ExistingUserWithConnections() {
        Person record = new Person().setEmail("person1@email.com").setId(new ObjectId()).setFriends(new ArrayList<>(Arrays.asList("person1Friend1@email.com")));
        Person friendRecord1 = new Person().setId(new ObjectId()).setEmail("person1Friend1@email.com").setFriends(new ArrayList<>(Arrays.asList("person1@email.com")));


        Mockito.when(personRepo.findByEmail(record.getEmail()))
                .thenReturn(Optional.of(record));

        // friend 1 exist
        Mockito.when(personRepo.findByEmail("person1Friend1@email.com"))
                .thenReturn(Optional.of(friendRecord1));


        friendMgmtService.addFriendConnection(Arrays.asList("person1@email.com", "person1Friend1@email.com"));

        Mockito.verify(personRepo, times(0)).save(any());
    }

    @Test
    public void getExistingUserConnections() {
        Person record = new Person().setId(new ObjectId()).setEmail("person1@email.com").setFriends(new ArrayList<>(Arrays.asList("person1Friend1@email.com", "person1Friend2@email.com")));

        Mockito.when(personRepo.findByEmail(record.getEmail()))
                .thenReturn(Optional.of(record));

        Person result = friendMgmtService.getFriendConnection(record.getEmail());

        assertThat(result).hasFieldOrPropertyWithValue("email", "person1@email.com");
        assertThat(result.getFriends())
                .containsExactly("person1Friend1@email.com", "person1Friend2@email.com");

    }

    @Test
    public void getCommongFriendConnection() {
        Mockito.when(personRepo.findByEmail(Arrays.asList("person1@email.com", "person2@email.com")))
                .thenReturn(Arrays.asList(new Person().setEmail("person1@email.com").setFriends(Arrays.asList("friendA@email.com", "friendB@email.com", "friendC@email.com")),
                        new Person().setEmail("person2@email.com").setFriends(Arrays.asList("friendB@email.com", "friendC@email.com", "friendD@email.com"))));

        List<String> result = friendMgmtService.getCommonFriendConnection(Arrays.asList("person1@email.com", "person2@email.com"));
        assertThat(result).containsExactly("friendB@email.com", "friendC@email.com");
    }

    @Test
    public void subscribeForupdates() {
        Person record = new Person().setEmail("person1@email.com").setId(new ObjectId());
        Person updatedRecord = new Person().setId(record.getId()).setEmail("person1@email.com").setSubscribed(new ArrayList<>(Arrays.asList("person2@email.com")));

        Person record2 = new Person().setEmail("person2@email.com").setId(new ObjectId());
        Person updatedRecord2 = new Person().setId(record2.getId()).setEmail("person2@email.com").setSubscribers(new ArrayList<>(Arrays.asList("person1@email.com")));

        Mockito.when(personRepo.findByEmail(record.getEmail()))
                .thenReturn(Optional.of(record));

        Mockito.when(personRepo.findByEmail(record2.getEmail()))
                .thenReturn(Optional.of(record2));


        friendMgmtService.subscribeForUpdates("person1@email.com", "person2@email.com");
        Mockito.verify(personRepo, times(1)).save(updatedRecord);
        Mockito.verify(personRepo, times(1)).save(updatedRecord2);
    }


    /**
     * Verify the target is added to requestor block list, target is removed from requestor subscribed list and requestor is removed from targed subscriber list
     */
    @Test
    public void block() {
        Person person1Record = new Person().setEmail("person1@email.com").setId(new ObjectId()).setSubscribed(new ArrayList<>(Arrays.asList("person2@email.com")));
        Person person1UpdatedBlockRecord = new Person().setEmail("person1@email.com").setId(person1Record.getId()).setSubscribed(new ArrayList<>(Arrays.asList("person2@email.com")))
                .setBlocked(new ArrayList<>(Arrays.asList("person2@email.com")));
        Person person2Record = new Person().setEmail("person2@email.com").setId(new ObjectId()).setSubscribers(new ArrayList<>(Arrays.asList("person1@email.com")));

        Mockito.when(personRepo.findByEmail(person1Record.getEmail()))
                .thenReturn(Optional.of(person1Record))
                .thenReturn(Optional.of(person1UpdatedBlockRecord));

        Mockito.when(personRepo.findByEmail(person2Record.getEmail()))
                .thenReturn(Optional.of(person2Record));


        friendMgmtService.block("person1@email.com", "person2@email.com");

        ArgumentCaptor<Person> argument = ArgumentCaptor.forClass(Person.class);

        Person expectedSavePerson1BlockRecord = new Person().setEmail("person1@email.com").setId(person1Record.getId()).setSubscribed(new ArrayList<>(Arrays.asList("person2@email.com")))
                .setBlocked(new ArrayList<>(Arrays.asList("person2@email.com")));
        Person expectedSavePerson1SubscribedRecord = new Person().setId(person1Record.getId()).setEmail("person1@email.com").setSubscribed(new ArrayList()).setBlocked(new ArrayList<>(Arrays.asList("person2@email.com")));
        Person expectedSavedPerson2SubscriberRecord = new Person().setId(person2Record.getId()).setEmail("person2@email.com").setSubscribers(new ArrayList());

        Mockito.verify(personRepo, times(3)).save(argument.capture());
        assertThat(argument.getAllValues()).containsExactly(expectedSavePerson1BlockRecord,
                expectedSavePerson1SubscribedRecord,
                expectedSavedPerson2SubscriberRecord);
    }
}