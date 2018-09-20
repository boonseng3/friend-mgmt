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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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

        Mockito.when(personRepo.findByEmail(record.getEmail()))
                .thenReturn(Optional.of(record));

        Mockito.when(personRepo.save(updatedRecord))
                .thenReturn(updatedRecord);


        Person result = friendMgmtService.addFriendConnection("person1@email.com", Arrays.asList("person1Friend1@email.com", "person1Friend2@email.com"));
        assertThat(result).hasFieldOrPropertyWithValue("email", "person1@email.com");
        assertThat(result.getFriends())
                .containsExactly("person1Friend1@email.com", "person1Friend2@email.com");
    }


    @Test
    public void addNewConnection2NonExistingUserWithoutConnections() {
        Person record =  new Person().setEmail("person1@email.com");
        Person updatedRecord =  new Person().setEmail("person1@email.com").setFriends( new ArrayList<>(Arrays.asList("person1Friend1@email.com", "person1Friend2@email.com")));

        Mockito.when(personRepo.findByEmail(record.getEmail()))
                .thenReturn(Optional.empty());

        Mockito.when(personRepo.insert(record))
                .thenReturn(record);

        Mockito.when(personRepo.save(updatedRecord))
                .thenReturn(updatedRecord);


        Person result = friendMgmtService.addFriendConnection("person1@email.com", Arrays.asList("person1Friend1@email.com", "person1Friend2@email.com"));
        assertThat(result).hasFieldOrPropertyWithValue("email", "person1@email.com");
        assertThat(result.getFriends())
                .containsExactly("person1Friend1@email.com", "person1Friend2@email.com");
    }
    @Test
    public void addDuplicateConnection2NonExistingUserWithConnections() {
        Person record = new Person().setEmail("person1@email.com").setId(new ObjectId()).setFriends( new ArrayList<>(Arrays.asList("person1Friend1@email.com")));
        Person updatedRecord =  new Person().setId(record.getId()).setEmail("person1@email.com").setFriends( new ArrayList<>(Arrays.asList("person1Friend1@email.com", "person1Friend2@email.com")));

        Mockito.when(personRepo.findByEmail(record.getEmail()))
                .thenReturn(Optional.of(record));

        Mockito.when(personRepo.save(updatedRecord))
                .thenReturn(updatedRecord);


        Person result = friendMgmtService.addFriendConnection("person1@email.com", Arrays.asList("person1Friend1@email.com", "person1Friend2@email.com"));
        assertThat(result).hasFieldOrPropertyWithValue("email", "person1@email.com");
        assertThat(result.getFriends())
                .containsExactly("person1Friend1@email.com", "person1Friend2@email.com");
    }
}