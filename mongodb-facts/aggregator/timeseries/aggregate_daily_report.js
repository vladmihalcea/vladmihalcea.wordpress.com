var start = new Date();
var dataSet = db.randomData.aggregate([
	{
		$group: {
				"_id": { 
					"year" : {
						$year : "$created_on"
					}, 
					"dayOfYear" : {
						$dayOfYear : "$created_on"
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
			"_id.dayOfYear" : 1
		} 	
	}
]);
if(dataSet.result != null && dataSet.result.length > 0) {
	print("Aggregated:" + dataSet.result.length + " days.");	
	db.dailyReport.insert(dataSet.result);
}
var end = new Date();
print("Aggregation took:" + (end.getTime() - start.getTime())/1000 + "s");