import com.mongodb.*;

import java.util.Arrays;
import java.util.List;

public class Mongo {
    public static DBObject groupFields;
    public static DBObject group;
    public static DBObject sort;
    public static DBObject match;
    public static DBObject limit;

    public static AggregationOutput dataPlanLanguage(DBCollection collection){
        groupFields = new BasicDBObject("_id", "$Language");
        groupFields.put("total", new BasicDBObject("$sum", 1 ));
        group = new BasicDBObject("$group", groupFields);

        List<DBObject> pipeline = Arrays.asList(group);
        AggregationOutput output = collection.aggregate(pipeline);

        for(DBObject result : output.results()){
            System.out.println(result);
        }

        return output;
    }
    public static AggregationOutput nonBasicPackages(DBCollection collection){
        groupFields = new BasicDBObject("_id","$package_name");
        groupFields.put("total", new BasicDBObject("$sum", 1 ));
        group = new BasicDBObject("$group", groupFields);
        sort = new BasicDBObject("$sort", new BasicDBObject("total", -1));
        match = new BasicDBObject("$match", new BasicDBObject("Language", "English"));
        DBObject matchs = new BasicDBObject("$match", new BasicDBObject("package_id", new BasicDBObject("$ne", 0)));
        limit = new BasicDBObject("$limit", 10);

        List<DBObject> pipeline = Arrays.asList(match, matchs, group, sort, limit);
        AggregationOutput output = collection.aggregate(pipeline);
        for(DBObject result : output.results()){
            System.out.println(result);
        }
        return output;
    }
    public static AggregationOutput englishCatDescription(DBCollection collection){
        groupFields = new BasicDBObject("_id","$CategoryDescription");
        groupFields.put("total", new BasicDBObject("$sum", 1 ));
        group = new BasicDBObject("$group", groupFields);
        sort = new BasicDBObject("$sort", new BasicDBObject("total", -1));
        match = new BasicDBObject("$match", new BasicDBObject("Language", "English"));
        limit = new BasicDBObject("$limit", 10);

        List<DBObject> pipeline = Arrays.asList(match, group, sort, limit);
        AggregationOutput output = collection.aggregate(pipeline);
        for(DBObject result : output.results()){
            System.out.println(result);
        }
        return output;
    }

    public static AggregationOutput spanishCatDescription(DBCollection collection){
        groupFields = new BasicDBObject("_id","$CategoryDescription");
        groupFields.put("total", new BasicDBObject("$sum", 1 ));
        group = new BasicDBObject("$group", groupFields);
        sort = new BasicDBObject("$sort", new BasicDBObject("total", -1));
        match = new BasicDBObject("$match", new BasicDBObject("Language", "Spanish"));
        limit = new BasicDBObject("$limit", 10);

        List<DBObject> pipeline = Arrays.asList(match, group, sort, limit);
        AggregationOutput output = collection.aggregate(pipeline);
        for(DBObject result : output.results()){
            System.out.println(result);
        }
        return output;
    }
    public static AggregationOutput categoryCode(DBCollection collection){
        groupFields = new BasicDBObject("_id","$CategoryCode");
        groupFields.put("total", new BasicDBObject("$sum", 1 ));
        group = new BasicDBObject("$group", groupFields);
        sort = new BasicDBObject("$sort", new BasicDBObject("total", -1));
        limit = new BasicDBObject("$limit", 10);

        List<DBObject> pipeline = Arrays.asList(group, sort, limit);
        AggregationOutput output = collection.aggregate(pipeline);
        for(DBObject result : output.results()){
            System.out.println(result);
        }
        return output;
    }

    public static void main(String args[]){

        MongoConnection conn = new MongoConnection("localhost", 27017);
        MongoClient client = conn.getMongoClient();
        DB db = client.getDB("Hospital");
        DBCollection collection = db.getCollection("plan");

        dataPlanLanguage(collection);
        englishCatDescription(collection);
        spanishCatDescription(collection);
        categoryCode(collection);
        nonBasicPackages(collection);

    }
}
