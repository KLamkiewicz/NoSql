var MongoClient = require('mongodb').MongoClient
, format = require('util').format;

MongoClient.connect('mongodb://127.0.0.1:27017/Hospital', function(err, db) {
if(err) throw err;

	db.collection('plan', function(err, col){


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
			db.close();
		});

	});


})
