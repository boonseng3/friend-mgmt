package com.obs.friendmgmt;

import com.obs.friendmgmt.test.MongoRepoTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class PersonRepoIT extends MongoRepoTest {
    @Autowired
    PersonRepo personRepo;

    private Person expected1 = null;
    private Person expected2 = null;

    @Before
    public void before() {
        personRepo.deleteAll();

        expected1 = new Person().setEmail("person1@email.com");
        expected1.setId(personRepo.insert(expected1).getId());


        expected2 = new Person().setEmail("person2@email.com");
        expected2.setId(personRepo.insert(expected2).getId());
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
        .containsExactly(expected1, expected2);
    }

    @Test
    public void save() {
        Person result =  personRepo.save(expected1.setFriends(Arrays.asList("personAFriend1@email.com","personAFriend2@email.com")));
        assertThat(result).isEqualTo(expected1);


    }

    @Test
    public void findByEmail() {
        Person result = personRepo.findByEmail("person1@email.com").get();
        assertThat(result).isEqualTo(expected1);


        result = personRepo.findByEmail("PERSON1@email.com").get();
        assertThat(result).isEqualTo(expected1);


        result = personRepo.findByEmail("Person1@email.com").get();
        assertThat(result).isEqualTo(expected1);
    }

    @Test
    public void findByEmailNotFound() {
        Optional<Person> result = personRepo.findByEmail("1person1@email.com");
        assertThat(result).isEmpty();
    }

    @Test
    public void findByEmails() {
        List<Person> result = personRepo.findByEmail(Arrays.asList(expected1.getEmail(), expected2.getEmail(), UUID.randomUUID().toString()));
        assertThat(result)
                .containsExactly(expected1, expected2);
    }
}