##Komputer 

	* Dysk twardy WD5000 500GB 7200RPM
	* Procesor Pentium Dual-Core E5400 @ 2.70Ghz
	* Ram 2GB DDR2
	* System Windows 7 64bit

***

# Zadanie 1

  * [a)](#a)
  * [b)](#b)
  * [c)](#c)
  * [d)](#d)
  
*** 

### <a id="a"></a>a)

Pierwszym krokiem było poprawienie pliku CSV przy użyciu 2unix.sh:
![2unix](https://github.com/KLamkiewicz/NoSql/blob/master/Images/2shell.png)

</br>

	Następnie zimportowałem przerobiony plik do mongodb - MongoDB 2.6.5:
	
![dbCount](https://github.com/KLamkiewicz/NoSql/blob/master/Images/import.png)  

	MongoDB 2.8.0-rc0
	
![dbCountrc](https://github.com/KLamkiewicz/NoSql/blob/master/Images/MongoRC/importtime.png) 

	MongoDB 2.8.0-rc0 z użyciem Wired Tiger

Start serwera z użyciem Wired Tiger:

![start](https://github.com/KLamkiewicz/NoSql/blob/master/Images/MongoRC/wiredtiger/start.png)

Import:

![impwt](https://github.com/KLamkiewicz/NoSql/blob/master/Images/MongoRC/wiredtiger/import.png)

Niestety błąd, otrzymuje informację o niewstarczającej ilości przestrzeni, mimo, że takową posiadam, a następni serwer ulega zamknięciu:

![timewt](https://github.com/KLamkiewicz/NoSql/blob/master/Images/MongoRC/wiredtiger/errorimport.png)
	
	MongoDB 2.8.0-rc1 z użyciem Wired Tiger
	
Sprawdzenie wersji:

![ver](https://github.com/KLamkiewicz/NoSql/blob/master/Images/MongoRC/wiredtiger/rc1version.png)

Start serwera:

![startrc1](https://github.com/KLamkiewicz/NoSql/blob/master/Images/MongoRC/wiredtiger/rc1start.png)

Import:

![imp](https://github.com/KLamkiewicz/NoSql/blob/master/Images/MongoRC/wiredtiger/rc1import.png)

Pamięć:

![mem](https://github.com/KLamkiewicz/NoSql/blob/master/Images/MongoRC/wiredtiger/rc1memory.png)

CPU:

![cprc1](https://github.com/KLamkiewicz/NoSql/blob/master/Images/MongoRC/wiredtiger/rc1cpu.png)

Czas:

![time](https://github.com/KLamkiewicz/NoSql/blob/master/Images/MongoRC/wiredtiger/rc1importtime.png)


####Postgres


Utworzenie tabeli w postgresie: 

![post](https://github.com/KLamkiewicz/NoSql/blob/master/Images/postgres/createpostgres.png)

<br/>

Monitor zasobów podczas importu:

![imz](https://github.com/KLamkiewicz/NoSql/blob/master/Images/postgres/postgresimportmonitor.png)

<br/>

Zaimportowana baza:

![import](https://github.com/KLamkiewicz/NoSql/blob/master/Images/postgres/postgresimport.png)

<br/>

***

### <a id="b"></a>b)

Zliczenie zimportowanych rekordów.

![dbCount](https://github.com/KLamkiewicz/NoSql/blob/master/Images/count.png)

***

### <a id="c"></a>c)

[Program](https://github.com/KLamkiewicz/NoSql/blob/master/Zad1Podmiana/src/main/java/Mongo.java) zamieniający tagi na tablice tagów w języku Java.

Opis: 

Program przechodzi kolejne rekordy w naszej kolekcji w poszukiwani pól "tags". Jeśli pole "tags" jest stringiem lub liczbą, to poszczególne wyrazy trafiają do tymczasowej tablicy, która następnie podmienia pole "tags". Do zidentyfikowania czy pole jest liczba posłużyłem się biblioteką matematyczną od apache. Jeśli pole nie jest żadnym z powyższych typów oznacza to, że jest już tablicą i nie jest konieczna podmiana. Po drodze program zlicza wystąpienia wszystkich tagów, a także tagów różnych i unikalnych. 

Kod: 

Połączenie MongoConnection.java: 

```java
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
```

Podmiana Mongo.java:

```java

import com.mongodb.*;
import org.apache.commons.lang3.math.NumberUtils;
import java.lang.reflect.Array;
import java.util.*;

public class Mongo {

	public static void main(String args[]){
		MongoConnection conn = new MongoConnection("localhost", 27017);
		MongoClient client = conn.getMongoClient();
		DB db = client.getDB("Lab");
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
				if ((o.get("tags") instanceof String)) {
					tags = o.get("tags").toString().split(" ");
					for (String t : tags) {
						int count = tagMap.containsKey(t) ? tagMap.get(t) : 0;
						tagMap.put(t, count + 1);
					}
					o.put("tags", tags);
					collection.save(o);
				} else if (NumberUtils.isNumber(o.get("tags").toString())) {
					String t = o.get("tags").toString();
					int count = tagMap.containsKey(t)?tagMap.get(t):0;
					tagMap.put(t, count + 1);
					o.put("tags", t);
					collection.save(o);
				} else {
					BasicDBList l = (BasicDBList) o.get("tags");
					for (Object t : (Object[]) l.toArray()) {
					int count = tagMap.containsKey(t) ? tagMap.get(t) : 0;
					tagMap.put((String) t, count + 1);
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
```

Podmiana czasy: 

Dla 10k:

![10k](https://github.com/KLamkiewicz/NoSql/blob/master/Images/tags10k.png)

Pierwsze testy przeprowadzałem na mniejszym zestawie danych, który stworzyłem przy pomocy polecenia przedstawionego poniżej, a następnie zimportowałem do mongodb.

```sh
head -10001 Train.csv > 10000.csv
```

Dla całości: 

![całość](https://github.com/KLamkiewicz/NoSql/blob/master/Images/tagsfull.png)

Jak można zauważyć ze względu na mój wolny komputer całość zajęła ponad 72 minuty.

Wykorzystanie pamięci podczas zamiany tagów:

![zamiana](https://github.com/KLamkiewicz/NoSql/blob/master/Images/monitor.png)


	Przykłady dla MongoDB 2.8.0-rc0
	
Zliczenie 10k podczas podmiany tagów:

![10kb](https://github.com/KLamkiewicz/NoSql/blob/master/Images/MongoRC/10ktags.png)

Zliczanie 10k po podmianie tagów:

![10ka](https://github.com/KLamkiewicz/NoSql/blob/master/Images/MongoRC/10kafter.png)

Zliczenie 100k podczas podmiany tagów:

![10kb](https://github.com/KLamkiewicz/NoSql/blob/master/Images/MongoRC/100ktags.png)

Zliczanie 10k po podmianie tagów:

![10ka](https://github.com/KLamkiewicz/NoSql/blob/master/Images/MongoRC/100kafter.png)

Całość:

![allrc](https://github.com/KLamkiewicz/NoSql/blob/master/Images/MongoRC/olddrivertagsrc.png)

Pamięć:

![monrc](https://github.com/KLamkiewicz/NoSql/blob/master/Images/MongoRC/tagimonitor.png)


	Przykłady dla MongoDB 2.8.0-rc1

Zamiana:

![allrc1](https://github.com/KLamkiewicz/NoSql/blob/master/Images/MongoRC/wiredtiger/rc1tags.png)

Pamięć:

![memrc1](https://github.com/KLamkiewicz/NoSql/blob/master/Images/MongoRC/wiredtiger/rc1tagsmonitor.png)

***

### <a id="d"></a>d)

> Wyszukać w sieci dane zawierające obiekty GeoJSON. Następnie dane zapisać w bazie MongoDB.

Jako dane wykorzystałem zapis trzęsień ziemi z dnia poprzedniego z 
http://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php

Pobranie danych do MongoDB: 

![importGeo](https://github.com/KLamkiewicz/NoSql/blob/master/Images/importgeo.png)

Ponieważ pobrane dane posiadały trzy współrzędne w tabeli koordynatów, usunąłem trzecią współrzędna ze wszystkich rekordów.

![updateGeo](https://github.com/KLamkiewicz/NoSql/blob/master/Images/updatecoords.png)

Ustawienie indeksu dla GeoJSONa

```
db.Quakes.ensureIndex({geometry:"2dsphere"}) 
```

Sprawdzenie

```
db.Quakes.getIndexes()
```

___

####*Trzęsienia w odległości od 1km do 150km [-153.1466, 59.2757].*

```
var origin = {type: "Point", coordinates: [-153.1466, 59.2757]}
db.Quakes.find(  {  geometry: {$nearSphere: { $geometry: origin, $minDistance: 1000, $maxDistance: 150000  }  } })
```

Wynik : 
```
{ "type" : "Feature", "properties" : { "mag" : 3.4, "place" : "103km SW of Anchor Point, Alaska", "time" : 1415559370000,  "updated" : 1415564195026, "tz" : -600, "url" : "http://earthquake.usgs.gov/earthquakes/eventpage/ak11436926", "detail" : "http://earthquake.usgs.gov/earthquakes/feed/v1.0/detail/ak11436926.geojson", "felt" : 0, "cdi" : 1, "mmi" : null, "alert" : null, "status" : "automatic", "tsunami" : null, "sig" : 178, "net" : "ak", "code" : "11436926", "ids" : ",ak11436926,", "sources" : ",ak,", "types" : ",dyfi,general-link,geoserve,nearby-cities,origin,tectonic-summary,", "nst" : null, "dmin" : null, "rms" : 0.66, "gap" : null, "magType" : "ml", "type" : "earthquake", "title" : "M 3.4 - 103km SW of Anchor Point, Alaska" }, "geometry" : { "type" : "Point", "coordinates" : [ -152.9566, 59.0462 ] }, "id" : "ak11436926" },
{ "type" : "Feature", "properties" : { "mag" : 2.2, "place" : "53km SSW of Homer, Alaska", "time" : 1415515350000, "updated" : 1415521626776, "tz" : -540, "url" : "http://earthquake.usgs.gov/earthquakes/eventpage/ak11436708", "detail" : "http://earthquake.usgs.gov/earthquakes/feed/v1.0/detail/ak11436708.geojson", "felt" : null, "cdi" : null, "mmi" : null, "alert" : null, "status" : "automatic", "tsunami" : null, "sig" : 74, "net" : "ak", "code" : "11436708", "ids" : ",ak11436708,", "sources" : ",ak,", "types" : ",general-link,geoserve,nearby-cities,origin,tectonic-summary,", "nst" : null, "dmin" : null, "rms" : 0.92, "gap" : null, "magType" : "ml", "type" : "earthquake", "title" : "M 2.2 - 53km SSW of Homer, Alaska" }, "geometry" : { "type" : "Point", "coordinates" : [ -151.9086, 59.2001 ] }, "id" : "ak11436708" },
{ "type" : "Feature", "properties" : { "mag" : 3.2, "place" : "35km S of Old Iliamna, Alaska", "time" : 1415491380000, "updated" : 1415520334505, "tz" : -540, "url" : "http://earthquake.usgs.gov/earthquakes/eventpage/ak11436658", "detail" : "http://earthquake.usgs.gov/earthquakes/feed/v1.0/detail/ak11436658.geojson", "felt" : null, "cdi" : null, "mmi" : null, "alert" : null, "status" : "automatic", "tsunami" : null, "sig" : 158, "net" : "ak", "code" : "11436658", "ids" : ",ak11436658,", "sources" : ",ak,", "types" : ",dyfi,general-link,geoserve,nearby-cities,origin,tectonic-summary,", "nst" : null, "dmin" : null, "rms" : 0.72, "gap" : null, "magType" : "ml", "type" : "earthquake", "title" : "M 3.2 - 35km S of Old Iliamna, Alaska" }, "geometry" : { "type" : "Point", "coordinates" : [ -154.8608, 59.4365 ] }, "id" : "ak11436658" },
{  "type" : "Feature", "properties" : { "mag" : 2.2, "place" : "39km E of Redoubt Volcano, Alaska", "time" : 1415556114000, "updated" : 1415560081135, "tz" : -540, "url" : "http://earthquake.usgs.gov/earthquakes/eventpage/ak11436913", "detail" : "http://earthquake.usgs.gov/earthquakes/feed/v1.0/detail/ak11436913.geojson", "felt" : null, "cdi" : null, "mmi" : null, "alert" : null, "status" : "automatic", "tsunami" : null, "sig" : 74, "net" : "ak", "code" : "11436913", "ids" : ",ak11436913,", "sources" : ",ak,", "types" : ",general-link,geoserve,nearby-cities,origin,tectonic-summary,", "nst" : null, "dmin" : null, "rms" : 0.53, "gap" : null, "magType" : "ml", "type" : "earthquake", "title" : "M 2.2 - 39km E of Redoubt Volcano, Alaska" }, "geometry" : { "type" : "Point", "coordinates" : [ -152.0299, 60.4926 ] }, "id" : "ak11436913" }
```
[GeoJSON](https://github.com/KLamkiewicz/NoSql/blob/master/GeoJson/nearsphere.geojson)

___

####*Polygon oznaczający obszar trzęsień ziemi na południowej Alasce.*

```
var origin = { "type": "Polygon",  "coordinates": [[[-140.7800, 60.2770], [-152.9566, 59.0462], [-154.8608, 59.4365], [-150.5025, 61.2971], [-140.7800, 60.2770] ] ] }
```

Polecenie to wyłuskało z każdego wyniku jedynie koordynaty potrzebne do utworzenia polygonu.

```
(db.Quakes.find({geometry: {$geoWithin: {$geometry: origin}}}).toArray()).forEach(function (e) { print(JSON.stringify(e.geometry.coordinates))});
```

[GeoJSON](https://github.com/KLamkiewicz/NoSql/blob/master/GeoJson/poly.geojson)

___

####*Trzęsienia ziemi w odległości od 1km do 15km jak widać jest to bardzo aktywny sejsmicznie obszar. Podobnie jak w poprzednim przykładzie zwróciłem jedynie koordynaty.*

```
var origin = {type: "Point", coordinates: [-119.6225, 41.8913]}
(db.Quakes.find( { geometry: {   $nearSphere: {$geometry: origin,   $minDistance: 1000,
   $maxDistance: 15000  } } }).toArray()).forEach(function (e) { print(JSON.stringify(e.geometry.coordinates))});
```

[GeoJSON](https://github.com/KLamkiewicz/NoSql/blob/master/GeoJson/point2.geojson)

___

####*Polygon oznaczający powyżyszy aktywny obszar sejsmiczny.*

```
var origin = { "type": "Polygon",  "coordinates": [[[-119.6588, 41.9090], [-119.6402, 41.8474], [-119.5887, 41.9036], [-119.6588, 41.9090]]] }

(db.Quakes.find({geometry: {$geoWithin: {$geometry: origin}}}).toArray()).forEach(function (e) { print(JSON.stringify(e.geometry.coordinates))});
```
[GeoJSON](https://github.com/KLamkiewicz/NoSql/blob/master/GeoJson/poly2.geojson)

___

####*Trzęsienia ziemi na lini pomiędzy koordynatami [-153.1466, 58.2757] ,[-153.1466, 60.2757]].*

```
db.Quakes.find({geometry: {$geoIntersects: {$geometry: origin}}})
```

Otrzymane współrzędne 
```
"coordinates" : [ -153.1466, 59.2757 ] 
```

[GeoJSON](https://github.com/KLamkiewicz/NoSql/blob/master/GeoJson/straightline.geojson)

___

####*Trzęsienia ziemi w odległości od 1km do 100km od stolicy Puerto Rioco - San Juan.*

```
var origin = {type: "Point", coordinates: [- 66.1057200, 18.4663300 ]}
db.Quakes.find( { geometry: { $nearSphere: { $geometry: origin, $minDistance: 1000,  $maxDistance: 100000 } }})
```

Otrzymany wynik to jedynie jedno trzęsienie ziemi.

```
{ "type" : "Feature", "properties" : { "mag" : 2.4, "place" : "5km NW of Adjuntas, Puerto Rico", "time" : 1415524288800, "updated" : 1415543141486, "tz" : -240, "url" : "http://earthquake.usgs.gov/earthquakes/eventpage/pr14313002", "detail" : "http://earthquake.usgs.gov/earthquakes/feed/v1.0/detail/pr14313002.geojson", "felt" : null, "cdi" : null, "mmi" : null, "alert" : null, "status" : "REVIEWED", "tsunami" : null, "sig" : 89, "net" : "pr", "code" : "14313002", "ids" : ",pr14313002,", "sources" : ",pr,", "types" : ",cap,geoserve,nearby-cities,origin,tectonic-summary,", "nst" : 7, "dmin" : 0.16798496, "rms" : 0.21, "gap" : 118.8, "magType" : "Md", "type" : "earthquake", "title" : "M 2.4 - 5km NW of Adjuntas, Puerto Rico" }, "geometry" : { "type" : "Point", "coordinates" : [ -66.8312, 18.1952 ] }, "id" : "pr14313002" }
```

[GeoJSON](https://github.com/KLamkiewicz/NoSql/blob/master/GeoJson/puerto100.geojson)
