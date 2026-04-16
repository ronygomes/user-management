package me.ronygomes.userManagement.repository.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import me.ronygomes.userManagement.common.model.User;
import me.ronygomes.userManagement.common.repository.UserRepository;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.Optional;

public class MongoUserRepository implements UserRepository {

    private static final String USERS_COLLECTION_NAME = "users";

    private final MongoCollection<Document> collection;

    public MongoUserRepository(MongoClient mongoClient, String databaseName) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        this.collection = database.getCollection(USERS_COLLECTION_NAME);
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
            user.setDateOfBirth(LocalDate.parse(dob));
        user.setDeleted(doc.getBoolean("isDeleted", false));
        return user;
    }
}
