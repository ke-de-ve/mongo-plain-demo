package de.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoUtils {

    public static MongoClient getMongoClientPojo(String mongoDbUri) {
        ConnectionString connectionString = new ConnectionString(mongoDbUri);

        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();

        return MongoClients.create(clientSettings);
    }

    public static MongoDatabase createDatabase(MongoClient mongoClient, String dbName) {
        return mongoClient.getDatabase(dbName);
    }


    public static List<String> getDatabases(MongoClient mongoClient) {
        List<String> names = new ArrayList<>();
        mongoClient.listDatabaseNames().forEach(names::add);

        return names;
    }

    public static List<Document> getAllDocuments(MongoCollection<Document> collection) {
        return collection.find().into(new ArrayList<>());
    }

    public static List<Document> getAllDocuments(MongoCollection<Document> collection, Bson filter) {
        return collection.find(filter).into(new ArrayList<>());
    }

    public static Document findByFieldFirst(MongoCollection<Document> collection, String field, String value) {
        return collection.find(new Document(field, value)).first();
    }

    public static Document findByFieldFirst(MongoCollection<Document> collection, Bson filter) {
        return collection.find(filter).first();
    }
}
