## Zadanie 2

___

Baza jaką pobrałem to 2014 Drug and Health Plan Data z http://www.medicare.gov/download/downloaddb.asp

	MongoDB 2.6.5

Polecenie import:

![import](https://github.com/KLamkiewicz/NoSql/blob/master/Images/importhospital.png)

Import czas:

![czas](https://github.com/KLamkiewicz/NoSql/blob/master/Images/hospimporttime.png)

	MongoDB 2.8.0-rc0
	
Polecenie:

![importrc](https://github.com/KLamkiewicz/NoSql/blob/master/Images/MongoRC/hospital/importcommand.png)

Import czas:

![czasrc](https://github.com/KLamkiewicz/NoSql/blob/master/Images/MongoRC/hospital/importtime.png)


<br/>
<br/>  
Rozwiązania w języku Javascript - pełne rozwiązanie przy użyciu NodeJS dostępne jest [tutaj](https://github.com/KLamkiewicz/NoSql/tree/master/Zad2AggJS).
<br/>
<br/>  
Plany według języka

```javascript
col.aggregate([
	{$group: {
		_id: "$Language",
		total: { $sum: 1 }
	}

	}
], function(err, result){
	console.log("Data plan language");
	console.log(result);
});
```

![pie](https://github.com/KLamkiewicz/NoSql/blob/master/Images/wykresy/dataplanlanguage.png)
<br/>
<br/>  
Pakiety zdrowotne w języku angielskim z pominięciem pakietu podstawowego

```javascript
col.aggregate([
	{ $match: { $and: [{package_id : {$ne : 0}}, {Language : "English"}]}},
	{ $group: {
			_id: "$package_name",
			total: { $sum: 1 }
	}},
	{ $sort : { total : -1}},
], function(err, result){
	console.log("Non basic packages in English");
	console.log(result);
});
```

![plans](https://github.com/KLamkiewicz/NoSql/blob/master/Images/wykresy/englishpacketswithoutbasic.png)
<br/>
<br/>  
Kategorie w języku angielskim - top10

```javascript
col.aggregate([
	{ $match: { Language: "English"}},
	{ $group: {
			_id: "$CategoryDescription",
			total: { $sum: 1 }
	}},
	{ $sort : { total : -1}},
	{$limit : 10}
], function(err, result){
	console.log("English category descriptions");
	console.log(result);
});
```

![katen](https://github.com/KLamkiewicz/NoSql/blob/master/Images/wykresy/englishdescriptions.png)
<br/>
<br/>  
Kategorie w języku hiszpańskim - top10

```javascript
col.aggregate([
	{ $match: { Language: "Spanish"}},
	{ $group: {
			_id: "$CategoryDescription",
			total: { $sum: 1 }
	}},
	{ $sort : { total : -1}},
	{$limit : 10}
], function(err, result){
	console.log("Spanish category descriptions");
	console.log(result);
});
```

![katsp](https://github.com/KLamkiewicz/NoSql/blob/master/Images/wykresy/spanishdescriptions.png)

 
<br/>
<br/>     
        
Kody kategorii - top10 
```javascript
col.aggregate([
	{ $group: {
			_id: "$CategoryCode",
			total: { $sum: 1 },
	}},
	{ $sort : { total : -1}},
	 {$limit : 10}
], function(err, result){
	console.log("Category code");
	console.log(result);
});
```

![katcode](https://github.com/KLamkiewicz/NoSql/blob/master/Images/wykresy/categorycodetop.png)

	MongoDB 2.6.5
	
![aggjs]()
	
	MongoDB 2.8.0-rc0
	
![aggjsrc](https://github.com/KLamkiewicz/NoSql/blob/master/Images/MongoRC/hospital/aggregatejs.png)

<br/>
<br/>

Rozwiązanie przy użyciu języka Java, cały projekt dostępny jest [tutaj](https://github.com/KLamkiewicz/NoSql/tree/master/Zad2AggJava).

```java

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

```




