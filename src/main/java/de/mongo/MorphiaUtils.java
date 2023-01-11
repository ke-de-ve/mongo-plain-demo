package de.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.result.UpdateResult;
import de.mongo.model.Employee;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.Mapper;
import dev.morphia.query.Query;

import static de.mongo.MongoUtils.getMongoClient;
import static dev.morphia.query.filters.Filters.gt;
import static dev.morphia.query.filters.Filters.lte;
import static dev.morphia.query.updates.UpdateOperators.inc;
import static org.testng.Assert.assertEquals;

public class MorphiaUtils {


    public static void example(MongoClient client) {
        final Datastore datastore = Morphia.createDatastore(client, "morphia_example");
        // tell Morphia where to find your classes
        // can be called multiple times with different packages or classes

        Mapper mapper = datastore.getMapper();
        mapper.mapPackage("de.mongo");
        datastore.ensureIndexes();

        datastore.insert(Employee.builder().name("JohnDoe").age(32).salary(12345.67).build());
    }

    public static void demo() {
        final Datastore datastore = Morphia.createDatastore(getMongoClient(Main.MONGO_URI), "morphia_example");

        // tell morphia where to find your classes
        // can be called multiple times with different packages or classes
        datastore.getMapper().mapPackage("dev.morphia.example");

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
