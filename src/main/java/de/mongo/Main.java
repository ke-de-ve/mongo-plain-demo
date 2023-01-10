package de.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import de.mongo.model.User;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Main {
    public static final String MONGO_URI = "mongodb://localhost:27017/test";

    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoUtils.getMongoClientPojo(MONGO_URI) ) {

            // Create database
            mongoClient.getDatabase("chat");

            //List all databases
            System.out.printf("\ndatabases: %s\n\n", Arrays.deepToString(MongoUtils.getDatabases(mongoClient).toArray()));

            MongoCollection<Document> usersCollection = createUsers(mongoClient);

            //List all databases
            System.out.printf("\ndatabases: %s\n\n", Arrays.deepToString(MongoUtils.getDatabases(mongoClient).toArray()));

            System.out.println("\nRead all users ...");
            for (Document document : MongoUtils.getAllDocuments(usersCollection)) {
                System.out.println(document);
            }

            System.out.println("\nRead all users with last name Doe ...");
            for (Document document : MongoUtils.getAllDocuments(usersCollection, Filters.eq("lastName", "Doe"))) {
                System.out.println(document);
            }

            Document userDoe = MongoUtils.findByFieldFirst(usersCollection, "lastName", "Doe");
            System.out.printf("\nfind first user by last name (using document)\n%s\n", userDoe);

            System.out.printf("\nfind first user by last name (using Bson filter)\n%s\n",
                    MongoUtils.findByFieldFirst(usersCollection, Filters.eq("lastName", "Doe")));

            // Update user
            Bson filter = Filters.eq("_id", userDoe.get("_id"));
            Bson updateOperation = Updates.set("email", "new.email.value@yahoo.com");
            UpdateResult updateResult = usersCollection.updateOne(filter, updateOperation);
            System.out.printf("=> Updating the doc with \"_ids\":\"%s\"}. changing email.\n", userDoe.get("_id"));
            System.out.println(usersCollection.find(filter).first().toJson());
            System.out.printf("updateResult: %s\n", updateResult);

            // Delete all users
            DeleteResult deleteResult = usersCollection.deleteMany(Filters.empty());
            System.out.printf("\ndeleted %d documents", deleteResult.getDeletedCount());
        }
    }

    private static MongoCollection<Document> createUsers(MongoClient mongoClient) {
        // get database by name, if one doesn't exist yet Mongo will create at the time of first insert
        MongoDatabase sampleTrainingDB = mongoClient.getDatabase("chat");

        // get Collection (table in Relational by name,
        // if one doesn't exist yet Mongo will create at the time of first insert
        MongoCollection<Document> usersCollection = sampleTrainingDB.getCollection("users");

        insertOneUser(usersCollection, "John", "Doe", "john.doe@gmail.com");

        List<Document> users = new ArrayList<>();
        users.add(Document.parse(User.builder().firstName("Jane").lastName("Doe").email("jane.doe@gmail.com").password("QWErty1!").build().toJson()));
        users.add(Document.parse(User.builder().firstName("Mary").lastName("Lamb").email("mary.lamb@gmail.com").password("QWErty2@").build().toJson()));
        insertManyUsers(usersCollection, users);

        return usersCollection;
    }

    private static void insertManyUsers(MongoCollection<Document> usersCollection, List<Document> users) {
        InsertManyResult result = usersCollection.insertMany(users, new InsertManyOptions().ordered(false));
        System.out.printf("added %d users\n\tresult: %s\n\n", result.getInsertedIds().size(), result);
    }

    private static void insertOneUser(MongoCollection<Document> usersCollection, String firstName, String lastName, String email) {
        User user = User.builder().firstName(firstName).lastName(lastName).email(email).password(UUID.randomUUID().toString()).build();
        InsertOneResult result = usersCollection.insertOne(Document.parse(user.toJson()));
        System.out.printf("One user inserted \t %s\n\tresult: %s\n", user.toJson(), result);
    }
}