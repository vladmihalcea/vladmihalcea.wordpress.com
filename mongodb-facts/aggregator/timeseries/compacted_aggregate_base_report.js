var enablePrintResult = false;

function printResult(dataSet) {
	dataSet.result.forEach(function(document)  {
		printjson(document);
	});
}

function aggregateData(fromDate, toDate, groupDeltaMillis) {		

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
							"$_id", groupDeltaMillis
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
		},
		{
			$sort: {
				"_id.timestamp" : 1		
			}
		}
	];
	
	var dataSet = db.randomData.aggregate(pipeline);
	var aggregationDuration = (new Date().getTime() - start.getTime())/1000;	
	print("Aggregation took:" + aggregationDuration + "s");	
	if(dataSet.result != null && dataSet.result.length > 0) {
		print("Fetched :" + dataSet.result.length + " documents.");
		if(enablePrintResult) {
			printResult(dataSet);
		}
	}
	var aggregationAndFetchDuration = (new Date().getTime() - start.getTime())/1000;
	if(enablePrintResult) {
		print("Aggregation and fetch took:" + aggregationAndFetchDuration + "s");
	}	
	return {
		aggregationDuration : aggregationDuration,
		aggregationAndFetchDuration : aggregationAndFetchDuration
	};
}