package de.mongo.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import lombok.*;
import org.bson.types.ObjectId;

@Entity("channel")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Channel {

    @Id
    private ObjectId id;
    private String name;
    @Reference
    private User user;

    @Builder
    public Channel(ObjectId id, String name, User user) {
        this.id = id;
        this.name = name;
        this.user = user;
    }
}

