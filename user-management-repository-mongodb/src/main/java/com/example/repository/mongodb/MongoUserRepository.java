package com.example.repository.mongodb;

import com.example.common.model.User;
import com.example.common.repository.UserRepository;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Optional;

public class MongoUserRepository implements UserRepository {
    private final MongoCollection<Document> collection;

    public MongoUserRepository(MongoClient mongoClient, String databaseName) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        this.collection = database.getCollection("users");
    }

    @Override
    public void save(User user) {
        Document doc = new Document("username", user.getUsername())
                .append("email", user.getEmail())
                .append("password", user.getPassword())
                .append("displayName", user.getDisplayName())
                .append("phoneNumber", user.getPhoneNumber())
                .append("firstName", user.getFirstName())
                .append("lastName", user.getLastName())
                .append("dateOfBirth", user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null)
                .append("isDeleted", user.isDeleted());

        if (user.getId() != null) {
            collection.replaceOne(Filters.eq("_id", new ObjectId(user.getId())), doc);
        } else {
            collection.insertOne(doc);
            user.setId(doc.getObjectId("_id").toString());
        }
    }

    @Override
    public Optional<User> findById(String id) {
        Document doc = collection.find(Filters.eq("_id", new ObjectId(id))).first();
        return Optional.ofNullable(doc).map(this::mapToUser);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Document doc = collection.find(Filters.eq("email", email)).first();
        return Optional.ofNullable(doc).map(this::mapToUser);
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        Document doc = collection.find(Filters.eq("phoneNumber", phoneNumber)).first();
        return Optional.ofNullable(doc).map(this::mapToUser);
    }

    private User mapToUser(Document doc) {
        User user = new User();
        user.setId(doc.getObjectId("_id").toString());
        user.setUsername(doc.getString("username"));
        user.setEmail(doc.getString("email"));
        user.setPassword(doc.getString("password"));
        user.setDisplayName(doc.getString("displayName"));
        user.setPhoneNumber(doc.getString("phoneNumber"));
        user.setFirstName(doc.getString("firstName"));
        user.setLastName(doc.getString("lastName"));
        String dob = doc.getString("dateOfBirth");
        if (dob != null)
            user.setDateOfBirth(java.time.LocalDate.parse(dob));
        user.setDeleted(doc.getBoolean("isDeleted", false));
        return user;
    }
}
