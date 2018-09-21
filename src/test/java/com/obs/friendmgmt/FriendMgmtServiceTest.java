package com.obs.friendmgmt;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import static org.mockito.Mockito.times;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class FriendMgmtServiceTest {

    @InjectMocks
    private FriendMgmtService friendMgmtService;
    @Mock
    private PersonRepo personRepo;

    @Test
    public void addNewConnection2ExistingUserWithoutConnections() {
        Person record = new Person().setEmail("person1@email.com").setId(new ObjectId());
        Person updatedRecord =  new Person().setId(record.getId()).setEmail("person1@email.com").setFriends( new ArrayList<>(Arrays.asList("person1Friend1@email.com", "person1Friend2@email.com")));

        Person friendRecord1 = new Person().setEmail("person1Friend1@email.com");
        Person updatedfriendRecord1 =  new Person().setEmail("person1Friend1@email.com").setFriends( new ArrayList<>(Arrays.asList("person1@email.com", "person1Friend2@email.com")));

        Person friendRecord2 = new Person().setEmail("person1Friend2@email.com").setId(new ObjectId());
        Person updatedfriendRecord2 =  new Person().setId(friendRecord2.getId()).setEmail("person1Friend2@email.com").setFriends( new ArrayList<>(Arrays.asList("person1@email.com","person1Friend1@email.com")));

        Mockito.when(personRepo.findByEmail(record.getEmail()))
                .thenReturn(Optional.of(record));

        Mockito.when(personRepo.save(updatedRecord))
                .thenReturn(updatedRecord);


        // friend 1 does not exist
        Mockito.when(personRepo.findByEmail("person1Friend1@email.com"))
                .thenReturn(Optional.empty());
        Mockito.when(personRepo.insert(friendRecord1))
                .thenReturn(friendRecord1);


        // friend 2 exist
        Mockito.when(personRepo.findByEmail("person1Friend2@email.com"))
                .thenReturn(Optional.of(friendRecord2));

        friendMgmtService.addFriendConnection(Arrays.asList("person1@email.com", "person1Friend1@email.com", "person1Friend2@email.com"));

        Mockito. verify(personRepo, times(1)).save(updatedRecord);
        Mockito. verify(personRepo, times(1)).save(updatedfriendRecord1);
        Mockito. verify(personRepo, times(1)).save(updatedfriendRecord2);
    }


    @Test
    public void addNewConnection2NonExistingUserWithoutConnections() {
        Person record =  new Person().setEmail("person1@email.com");
        Person updatedRecord =  new Person().setEmail("person1@email.com").setFriends( new ArrayList<>(Arrays.asList("person1Friend1@email.com", "person1Friend2@email.com")));

        Person friendRecord1 = new Person().setEmail("person1Friend1@email.com");
        Person updatedfriendRecord1 =  new Person().setEmail("person1Friend1@email.com").setFriends( new ArrayList<>(Arrays.asList("person1@email.com", "person1Friend2@email.com")));

        Person friendRecord2 = new Person().setEmail("person1Friend2@email.com").setId(new ObjectId());
        Person updatedfriendRecord2 =  new Person().setId(friendRecord2.getId()).setEmail("person1Friend2@email.com").setFriends( new ArrayList<>(Arrays.asList("person1@email.com","person1Friend1@email.com")));

        Mockito.when(personRepo.findByEmail(record.getEmail()))
                .thenReturn(Optional.empty());
        Mockito.when(personRepo.insert(record))
                .thenReturn(record);
        Mockito.when(personRepo.save(updatedRecord))
                .thenReturn(updatedRecord);

        // friend 1 does not exist
        Mockito.when(personRepo.findByEmail("person1Friend1@email.com"))
                .thenReturn(Optional.empty());
        Mockito.when(personRepo.insert(friendRecord1))
                .thenReturn(friendRecord1);


        // friend 2 exist
        Mockito.when(personRepo.findByEmail("person1Friend2@email.com"))
                .thenReturn(Optional.of(friendRecord2));

        friendMgmtService.addFriendConnection(Arrays.asList("person1@email.com", "person1Friend1@email.com", "person1Friend2@email.com"));

        Mockito. verify(personRepo, times(1)).save(updatedRecord);
        Mockito. verify(personRepo, times(1)).save(updatedfriendRecord1);
        Mockito. verify(personRepo, times(1)).save(updatedfriendRecord2);
    }

    @Test
    public void addDuplicateConnection2ExistingUserWithConnections() {
        Person record = new Person().setEmail("person1@email.com").setId(new ObjectId()).setFriends( new ArrayList<>(Arrays.asList("person1Friend1@email.com")));
        Person updatedRecord =  new Person().setId(record.getId()).setEmail("person1@email.com").setFriends( new ArrayList<>(Arrays.asList("person1Friend1@email.com", "person1Friend2@email.com")));

        Person friendRecord1 = new Person().setEmail("person1Friend1@email.com");
        Person updatedfriendRecord1 =  new Person().setEmail("person1Friend1@email.com").setFriends( new ArrayList<>(Arrays.asList("person1@email.com", "person1Friend2@email.com")));

        Person friendRecord2 = new Person().setEmail("person1Friend2@email.com").setId(new ObjectId());
        Person updatedfriendRecord2 =  new Person().setId(friendRecord2.getId()).setEmail("person1Friend2@email.com").setFriends( new ArrayList<>(Arrays.asList("person1@email.com","person1Friend1@email.com")));

        Mockito.when(personRepo.findByEmail(record.getEmail()))
                .thenReturn(Optional.of(record));

        Mockito.when(personRepo.save(updatedRecord))
                .thenReturn(updatedRecord);


        // friend 1 does not exist
        Mockito.when(personRepo.findByEmail("person1Friend1@email.com"))
                .thenReturn(Optional.empty());
        Mockito.when(personRepo.insert(friendRecord1))
                .thenReturn(friendRecord1);


        // friend 2 exist
        Mockito.when(personRepo.findByEmail("person1Friend2@email.com"))
                .thenReturn(Optional.of(friendRecord2));

        friendMgmtService.addFriendConnection(Arrays.asList("person1@email.com", "person1Friend1@email.com", "person1Friend2@email.com"));

        Mockito. verify(personRepo, times(1)).save(updatedRecord);
        Mockito. verify(personRepo, times(1)).save(updatedfriendRecord1);
        Mockito. verify(personRepo, times(1)).save(updatedfriendRecord2);
    }

    @Test
    public void getExistingUserConnections() {
        Person record =  new Person().setId(new ObjectId()).setEmail("person1@email.com").setFriends( new ArrayList<>(Arrays.asList("person1Friend1@email.com", "person1Friend2@email.com")));

        Mockito.when(personRepo.findByEmail(record.getEmail()))
                .thenReturn(Optional.of(record));

        Person result = friendMgmtService.getFriendConnection(record.getEmail());

        assertThat(result).hasFieldOrPropertyWithValue("email", "person1@email.com");
        assertThat(result.getFriends())
                .containsExactly("person1Friend1@email.com", "person1Friend2@email.com");

    }

    @Test
    public void getCommongFriendConnection() {
        Mockito.when(personRepo.findByEmail(Arrays.asList("person1@email.com","person2@email.com")))
                .thenReturn(Arrays.asList(new Person().setEmail("person1@email.com").setFriends(Arrays.asList("friendA@email.com","friendB@email.com","friendC@email.com")),
                        new Person().setEmail("person2@email.com").setFriends(Arrays.asList("friendB@email.com","friendC@email.com","friendD@email.com"))));

        List<String> result = friendMgmtService.getCommongFriendConnection(Arrays.asList("person1@email.com","person2@email.com"));
        assertThat(result).containsExactly("friendB@email.com","friendC@email.com");
    }
}