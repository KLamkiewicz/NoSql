##Komputer 

* Dysk twardy WD5000 500GB 7200RPM
* Procesor Pentium Dual-Core E5400 @ 2.70Ghz
* Ram 2GB DDR2

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

Następnie zimportowałem przerobiony plik do mongodb:
![dbCount](https://github.com/KLamkiewicz/NoSql/blob/master/Images/import.png)  

Co ciekawe wcześniej próbowałem zimportować bazę pod systemem Windows XP 32bit i średnia ilość importowanych rekordów na sekundę wynosiła ponad 10k. Jednak wersja 32bitowa MongoDb posiada ograniczenie 2GB na rozmiar bazy danych dlatego zadania musiałem wykonywać na systemie Windows 7 64bit, na którym średnia wynosiła ledwo 2.5k rekordoów na sekundę.


***

### <a id="b"></a>b)

Zliczenie zimportowanych rekordów.

![dbCount](https://github.com/KLamkiewicz/NoSql/blob/master/Images/count.png)

***

### <a id="c"></a>c)

[Program](https://github.com/KLamkiewicz/NoSql/blob/master/src/main/java/Mongo.java) zamieniający tagi na tablice tagów w języku Java.

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

***

### <a id="d"></a>d)
