function aggregateData(fromDate, toDate, deltaMillis) {		

	print("Aggregating from " + fromDate + " to " + toDate);

	var start = new Date();

	var pipeline = [
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
							"$_id", deltaMillis
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
	];
	
	var dataSet = db.randomData.aggregate(pipeline);	 
	print("Aggregation took:" + (new Date().getTime() - start.getTime())/1000 + "s");	
	if(dataSet.result != null && dataSet.result.length > 0) {
		print("Fetching :" + dataSet.result.length + " documents.");
		dataSet.result.forEach(function(document)  {
			printjson(document);
		});
	}
	var duration = (new Date().getTime() - start.getTime())/1000;
	print("Aggregation and fetch took:" + duration + "s");
	return duration;
}