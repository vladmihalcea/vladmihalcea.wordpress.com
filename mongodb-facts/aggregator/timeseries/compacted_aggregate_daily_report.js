var start = new Date();
var fromDate = new Date(2012, 0, 1, 0, 0, 0, 0);
var toDate = new Date(2013, 0, 1, 0, 0, 0, 0);
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
			hour:{
				$subtract:[
				   "$_id", {
					  $mod:[
						"$_id", 24 * 60 * 60 * 1000
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
				"timestamp" : "$hour"
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
	},
	{
		$sort: {
			"_id.timestamp" : 1
		} 	
	}
]);
if(dataSet.result != null && dataSet.result.length > 0) {
	print("Aggregated:" + dataSet.result.length + " days.");	
	db.dailyReport.insert(dataSet.result);
}
var end = new Date();
print("Aggregation took:" + (end.getTime() - start.getTime())/1000 + "s");