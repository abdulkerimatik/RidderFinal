package tr.edu.metu.ii.sm.tsp.mongo;

import com.mongodb.*;
import tr.edu.metu.ii.sm.tsp.domain.ArticleItem;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by omer.dogan on 28/05/2015.
 */
public class MongoConnector {
    String host;
    int port;
    String databaseName;

    DB database;

    public MongoConnector() throws UnknownHostException {
        this("localhost",27017,"Ridder");
    }

    public MongoConnector(String host, int port, String databaseName) throws UnknownHostException {
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;

        Mongo mongo = new Mongo(this.host, this.port);
        database = mongo.getDB(this.databaseName);
    }

    public MongoConnector(String host, int port) throws UnknownHostException {
        this(host,port,"Ridder");
    }

    public ArrayList<String> getAllCategories()
    {
        ArrayList<String> categories= new ArrayList<String>();

        DBCollection categoryCollection = database.getCollection("Category");
        DBCursor cursor = categoryCollection.find();

        List<DBObject> categoryObjects = cursor.toArray();

        for(DBObject dbObject:categoryObjects)
        {
            categories.add(dbObject.get("Name").toString());
        }

        return categories;
    }

    public boolean addEntry(ArticleItem item, String feedSource, String category)
    {
        DBCollection entryCollection = database.getCollection("Entry");

        DBObject uniqueQuery=new BasicDBObject();
        uniqueQuery.put("ItemId",item.getItemId());
        long entryCountWithId = entryCollection.count(uniqueQuery);

        if (entryCountWithId==0)
        {
            DBObject entryObject = new BasicDBObject();
            entryObject.put("ItemId",item.getItemId());
            entryObject.put("Title",item.getTitle());
            entryObject.put("EntryUrl",item.getItemUrl());
            entryObject.put("ImageUrl",item.getImageUrl());
            entryObject.put("Summary",item.getContent());
            entryObject.put("Source",feedSource);
            entryObject.put("Category",category);

            entryCollection.insert(entryObject);

            return true;
        }

        return false;
    }
}
