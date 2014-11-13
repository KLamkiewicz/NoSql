## Zadanie 2

___

Baza jaką pobrałem to 2014 Drug and Health Plan Data z http://www.medicare.gov/download/downloaddb.asp


Polecenie import:

![import](https://github.com/KLamkiewicz/NoSql/blob/master/Images/importhospital.png)

Import czas:

![czas](https://github.com/KLamkiewicz/NoSql/blob/master/Images/hospimporttime.png)



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





