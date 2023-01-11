package de.mongo.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import lombok.*;
import org.bson.types.ObjectId;

@Entity("post")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Post {
    @Id
    private ObjectId id;
    private String text;
    @Reference
    private Channel channel;
    @Reference
    private User user;

    @Builder
    public Post(ObjectId id, String text, Channel channel, User user) {
        this.id = id;
        this.text = text;
        this.channel = channel;
        this.user = user;
    }
}