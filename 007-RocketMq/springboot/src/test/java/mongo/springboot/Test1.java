package mongo.springboot;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class Test1 {

	public static void main(String[] args) {

		//连接MongoDB的一个数据库，需要用户名、密码、数据库、服务器端的IP和端口号
		MongoClient mongoClient = getMongoClient();
		MongoDatabase mongoDatabase = mongoClient.getDatabase("project");

		 System.out.println("connect successfully!");
	}
	
	private static final String MONGODB_CONNECTION_STRING = "mongodb://root:root@172.17.11.35:27017/test1?authSource=admin";

    public static MongoClient getMongoClient() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(MONGODB_CONNECTION_STRING))
                .build();

        return MongoClients.create(settings);
	}
}
