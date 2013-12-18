var minDate = new Date(2012, 0, 1, 0, 0, 0, 0);
var maxDate = new Date(2013, 0, 1, 0, 0, 0, 0);
var delta = maxDate.getTime() - minDate.getTime();
var fromDate = new Date(minDate.getTime() + Math.random() * delta);
var toDate = new Date(fromDate.getTime() + 60 * 60 * 1000);

print("Aggregating from " + fromDate + " to " + toDate);

var start = new Date();

var dataSet = db.randomData.aggregate([
	{
		$match: {
			"created_on" : {
				$gte: fromDate, 
				$lt : toDate	
			}
		}
	},
	{
		$group: {
				"_id": { 
					"year" : {
						$year : "$created_on"
					}, 
					"dayOfYear" : {
						$dayOfYear : "$created_on"
					},
					"hour" : {
						$hour : "$created_on"
					}
				}, 
				"count": { 
					$sum: 1 
				}, 
				"avg": { 
					$avg: "$value" 
				}, 
				"min": { 
					$min: "$value" 
				}, 
				"max": { 
					$max: "$value" 
				}		
			}
	},
	{
		$sort: {
			"_id.year" : 1, 
			"_id.dayOfYear" : 1,
			"_id.hour" : 1
		} 	
	}
]);
if(dataSet.result != null && dataSet.result.length > 0) {
	dataSet.result.forEach(function(document)  {
		printjson(document);
	});
}
var end = new Date();
print("Aggregation took:" + (end.getTime() - start.getTime())/1000 + "s");