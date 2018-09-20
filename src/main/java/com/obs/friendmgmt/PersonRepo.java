package com.obs.friendmgmt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PersonRepo {
    @Autowired
    MongoTemplate mongoTemplate;
    @Value("${application.mongo.collection.person.name}")
    private String collectionName;


    public <T> T insert(T object) {
        mongoTemplate.insert(object, collectionName);
        return object;
    }

    public void deleteAll() {
        // use this remove query instead of dropCollection to preserve the index
        mongoTemplate.remove(new Query(), Person.class);
    }

    public List<Person> findAll() {
        return mongoTemplate.findAll(Person.class, collectionName);
    }

    public void save(Person obj) {
        mongoTemplate.save(obj, collectionName);
    }

    public Optional<Person> findByEmail(String email) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        return Optional.ofNullable(mongoTemplate.findOne(query, Person.class, collectionName));
    }
}
