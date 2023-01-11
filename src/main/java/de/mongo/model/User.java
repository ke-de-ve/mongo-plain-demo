package de.mongo.model;

import dev.morphia.annotations.*;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.Objects;

@Entity("user")
@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class User {

    @Id
    private ObjectId id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;

    public User() {
        this.id = ObjectId.get();
    }

    public User(ObjectId id, String email, String password, String firstName, String lastName) {
        this.id = Objects.requireNonNullElseGet(id, ObjectId::get);
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String toJson() {
        return String.format("{\"_id\":\"%s\", \"email\":\"%s\", \"password\":\"%s\", \"firstName\":\"%s\", \"lastName\":\"%s\"}",
                getId(), getEmail(), getPassword(), getFirstName(), getLastName());
    }

    public String getName() {
        return getFirstName() + " " + getLastName();
    }
}
