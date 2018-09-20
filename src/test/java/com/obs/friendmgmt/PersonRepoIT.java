package com.obs.friendmgmt;

import com.obs.friendmgmt.test.MongoRepoTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class PersonRepoIT extends MongoRepoTest {
    @Autowired
    PersonRepo personRepo;

    private Person expected1 = null;

    @Before
    public void before() {
        personRepo.deleteAll();

        expected1 = new Person().setEmail("person1@email.com");
        expected1.setId(personRepo.insert(expected1).getId());
    }

    @Test
    public void insert() {
        Person result = personRepo.insert(new Person()
                .setEmail("personA@email.com")
                .setFriends(Arrays.asList("personAFriend1@email.com","personAFriend2@email.com")));

        assertThat(result).hasFieldOrPropertyWithValue("email", "personA@email.com");
        assertThat(result.getId()).isNotNull();
        assertThat(result.getFriends()).containsExactly("personAFriend1@email.com","personAFriend2@email.com");
    }

    @Test
    public void deleteAll() {
        personRepo.deleteAll();
        assertThat(personRepo.findAll()).isEmpty();
    }

    @Test
    public void findAll() {
        assertThat(personRepo.findAll())
        .containsExactly(expected1);
    }

    @Test
    public void save() {
        personRepo.save(expected1.setFriends(Arrays.asList("personAFriend1@email.com","personAFriend2@email.com")));
        Person result = personRepo.findByEmail("person1@email.com").get();
        assertThat(result).isEqualTo(expected1);


    } @Test
    public void findByEmail() {
        Person result = personRepo.findByEmail("person1@email.com").get();
        assertThat(result).isEqualTo(expected1);


    }
}