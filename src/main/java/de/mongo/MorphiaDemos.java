package de.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.result.UpdateResult;
import de.mongo.model.Channel;
import de.mongo.model.Employee;
import de.mongo.model.Post;
import de.mongo.model.User;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.Mapper;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;

import static de.mongo.MongoUtils.getMongoClient;
import static dev.morphia.query.filters.Filters.*;
import static dev.morphia.query.updates.UpdateOperators.inc;
import static org.testng.Assert.assertEquals;

public class MorphiaDemos {


    public static void example(MongoClient client) {
        final Datastore datastore = Morphia.createDatastore(client, "morphia_example");
        // tell Morphia where to find your classes
        // can be called multiple times with different packages or classes

        Mapper mapper = datastore.getMapper();
        mapper.mapPackage("de.mongo");
        datastore.ensureIndexes();

        datastore.insert(Employee.builder().name("JohnDoe").age(32).salary(12345.67).build());
    }

    public static void demoChatUsingMorphia() {
        final Datastore datastore = Morphia.createDatastore(getMongoClient(Main.MONGO_URI), "chat");

        // tell morphia where to find your classes
        // can be called multiple times with different packages or classes
        datastore.getMapper().mapPackage("de.mongo");

        datastore.getDatabase().drop();
        datastore.ensureIndexes();

        // Add Users
        final User john = new User(null, "john.doe@gmail.com", "pass", "John", "Doe");
        datastore.save(john);

        final User jane = new User(null, "jane.doe@gmail.com", "passJane", "Jane", "Doe");
        datastore.save(jane);

        System.out.println("users with last name Doe: ");
        datastore.find(User.class)
                .filter(Filters.eq("lastName", "Doe"))
                .stream()
                .forEach(System.out::println);

        // Add Channel
        final Channel channelJohn = new Channel(null, "John's channel", john);
        datastore.save(channelJohn);

        System.out.println("channels: ");
        datastore.find(Channel.class)
                .stream().forEach(System.out::println);


        // add posts between John and Jane
        datastore.save(new Post(null, "hi there", channelJohn, john));
        datastore.save(new Post(null, "hi John", channelJohn, jane));
        datastore.save(new Post(null, "How are you?", channelJohn, john));
        datastore.save(new Post(null, "Doing good, you?", channelJohn, jane));
        System.out.println("posts: ");
        datastore.find(Post.class)
                .stream().forEach(post-> System.out.println("from " + post.getUser().getName() + ": " + post.getText()));

        System.out.println("\n=============================================================================\n");
    }

    public static void demoMorphia() {
        final Datastore datastore = Morphia.createDatastore(getMongoClient(Main.MONGO_URI), "morphia_example");

        // tell morphia where to find your classes
        // can be called multiple times with different packages or classes
        datastore.getMapper().mapPackage("de.mongo");

        // create the Datastore connecting to the database running on the default port on the local host
        datastore.getDatabase().drop();
        datastore.ensureIndexes();

        final Employee elmer = new Employee("Elmer Fudd", 50000.0);
        datastore.save(elmer);

        final Employee daffy = new Employee("Daffy Duck", 40000.0);
        datastore.save(daffy);

        final Employee pepe = new Employee("Pepé Le Pew", 25000.0);
        datastore.save(pepe);

        elmer.getDirectReports().add(daffy);
        elmer.getDirectReports().add(pepe);

        datastore.save(elmer);

        Query<Employee> query = datastore.find(Employee.class);
        final long employees = query.count();

        assertEquals(employees, 3);

        query.stream().distinct().forEach(System.out::println);


        long underpaid = datastore.find(Employee.class)
                .filter(lte("salary", 30000))
                .count();
        assertEquals(underpaid, 1);

        final Query<Employee> underPaidQuery = datastore.find(Employee.class)
                .filter(lte("salary", 30000));
        final UpdateResult results = underPaidQuery.update(inc("salary", 10000))
                .execute();

        assertEquals(results.getModifiedCount(), 1);

        datastore.find(Employee.class)
                .filter(gt("salary", 100000))
                .findAndDelete();
    }

}
