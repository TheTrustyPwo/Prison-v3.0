package net.evilkingdom.prison.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;

public final class Mongo implements Closeable {
    private MongoClient client;

    public void connect(@NotNull final String connectionString) {
        this.client = MongoClients.create(connectionString);
    }

    public MongoClient getClient() {
        return client;
    }

    @Override
    public void close() throws IOException {
        this.client.close();
    }
}
