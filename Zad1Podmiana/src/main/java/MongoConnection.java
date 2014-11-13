import com.mongodb.MongoClient;

import java.net.UnknownHostException;

public class MongoConnection {
    private MongoClient mongoClient;

    public MongoConnection(String host, int port){
        try {
            mongoClient = new MongoClient(host, port);
        }catch (UnknownHostException e){
            System.out.println("Host " + e + " is unknown");
        }
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }
}
