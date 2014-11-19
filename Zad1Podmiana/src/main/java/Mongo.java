import com.mongodb.*;
import org.apache.commons.lang3.math.NumberUtils;

import java.lang.reflect.Array;
import java.util.*;

public class Mongo {
    public static void main(String args[]){

        MongoConnection conn = new MongoConnection("localhost", 27017);
        MongoClient client = conn.getMongoClient();
        DB db = client.getDB("LabWT");
        DBCollection collection = db.getCollection("Train");
        DBCursor cursor = collection.find();

        Map<String, Integer> tagMap = new HashMap<String, Integer>();
        Long totalTagsCount = new Long(0);

        //Before
        Long timeStart = System.currentTimeMillis();

        cursor.snapshot();
            while (cursor.hasNext()) {
                DBObject o = cursor.next();
                String tags[];

                if ((o.get("Tags") instanceof String)) {
                    tags = o.get("Tags").toString().split(" ");
                    for (String t : tags) {
                        int count = tagMap.containsKey(t) ? tagMap.get(t) : 0;
                        tagMap.put(t, count + 1);
                    }
                    o.put("Tags", tags);
                    collection.save(o);
                } else if (NumberUtils.isNumber(o.get("Tags").toString())) {
                    String t = o.get("Tags").toString();
                    int count = tagMap.containsKey(t)?tagMap.get(t):0;
                    tagMap.put(t, count + 1);
                    o.put("Tags", t);
                    collection.save(o);
                } else {
                    if(o.get("Tags") instanceof Double){
                        Object obj = o.get("Tags");
                        String s = Double.toString((Double) obj);
                        int count = tagMap.containsKey(s) ? tagMap.get(s) : 0;
                        tagMap.put(s, count + 1);
                    }else {
                        BasicDBList l = (BasicDBList) o.get("Tags");
                        for (Object t : (Object[]) l.toArray()) {
                            int count = tagMap.containsKey(t) ? tagMap.get(t) : 0;
                            tagMap.put((String) t, count + 1);
                        }
                    }
                }
            }

        int all = 0;
        int unique = 0;

        int max = 0;
        String k = "";

        for (Map.Entry<String,Integer> e : tagMap.entrySet()) {
            if(e.getValue()==1) {
                unique++;
                all++;
            }
            else
                all+=e.getValue();
            if(e.getValue()> max ) {
                max = e.getValue();
                k = e.getKey();
            }
        }


        System.out.println("Liczba wszystkich rekordow " + collection.count());
        System.out.println("Liczba wszystkich tagow " + all);
        System.out.println("Liczba unikalnych tagow " + unique);
        System.out.println("Liczba roznych tagow " + tagMap.size());
        System.out.println("Najpopularniejszy tag to " + k + " " +max + " wystapien");
        System.out.println("Czas " + (System.currentTimeMillis()-timeStart) + " ms");
    }
}
