package com.obs.friendmgmt;

import lombok.*;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Document(collection = "person")
public class Person {
    @Id
    protected ObjectId id;
    @Indexed(name = "person_email_inx", unique = true, background = true)
    protected String email;
    @Indexed(name = "person_friends_inx", background = true)
    protected List<String> friends;
    // list of emails that subscribe to this person for update
    @Indexed(name = "person_subscribers_inx", background = true)
    protected List<String> subscribers;
    // list of emails that this person subscribed for update
    @Indexed(name = "person_subscribed_inx", background = true)
    protected List<String> subscribed;
}
