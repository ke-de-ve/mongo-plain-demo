package de.mongo.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class User {

    private ObjectId _id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;

    public User() {
        this._id = ObjectId.get();
    }

    public User(ObjectId _id, String email, String password, String firstName, String lastName) {
        this._id = Objects.requireNonNullElseGet(_id, ObjectId::get);
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String toJson() {
        return String.format("{\"_id\":\"%s\", \"email\":\"%s\", \"password\":\"%s\", \"firstName\":\"%s\", \"lastName\":\"%s\"}",
                get_id(), getEmail(), getPassword(), getFirstName(), getLastName());
    }

}
