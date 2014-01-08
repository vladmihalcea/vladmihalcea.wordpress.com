var fromDate = new Date(2012, 0, 1, 0, 0, 0, 0);
var toDate = new Date(2013, 0, 1, 0, 0, 0, 0);

fromDate = new Date(Date.UTC(fromDate.getUTCFullYear(), fromDate.getUTCMonth(), fromDate.getUTCDate()));
toDate = new Date(Date.UTC(toDate.getUTCFullYear(), toDate.getUTCMonth(), toDate.getUTCDate()));

print("Aggregating from " + fromDate + " to " + toDate);

var start = new Date();

var dataSet = db.randomData.aggregate([
	{
		$match:{
			"_id":{
				$gte: fromDate.getTime(), 
				$lt : toDate.getTime()	
			}
		}
	},
	{
		$unwind:"$v"
	},
	{
		$project:{         
			timestamp:{
				$subtract:[
				   "$_id", {
					  $mod:[
						"$_id", 366 * 24 * 60 * 60 * 1000
					  ]
				   }
				]
			},
			v:1
		}
	},
	{
		$group: {
			"_id": {
					"timestamp" : "$timestamp"
			}, 
			"count": { 
				$sum: 1 
			}, 
			"avg": { 
				$avg: "$v" 
			}, 
			"min": { 
				$min: "$v" 
			}, 
			"max": { 
				$max: "$v" 
			}		
		}
	}
]);
if(dataSet.result != null && dataSet.result.length > 0) {
	print("Aggregated:" + dataSet.result.length + " days.");	
	db.dailyReport.insert(dataSet.result);
}
var end = new Date();
print("Aggregation took:" + (end.getTime() - start.getTime())/1000 + "s");