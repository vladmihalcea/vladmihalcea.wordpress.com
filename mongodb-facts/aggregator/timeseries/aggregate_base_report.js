var enablePrintResult = false;

function printResult(dataSet) {
	dataSet.result.forEach(function(document)  {
		printjson(document);
	});
}

function aggregateData(fromDate, toDate, groupDeltaMillis) {		

	print("Aggregating from " + fromDate + " to " + toDate);

	var start = new Date();
	
	var groupBy = { 
		"year" : {
			$year : "$created_on"
		}, 
		"dayOfYear" : {
			$dayOfYear : "$created_on"
		}
	};
	
	var sortBy = { 
			"_id.year" : 1, 
			"_id.dayOfYear" : 1
	}; 	
	
	switch(groupDeltaMillis) {
		case ONE_SECOND_MILLIS :
			groupBy["second"] = {
				$second : "$created_on"	
			};
			sortBy["_id.second"] = 1;
		case ONE_MINUTE_MILLIS :
			groupBy["minute"] = {
				$minute : "$created_on"	
			};
			sortBy["_id.minute"] = 1;
		case ONE_HOUR_MILLIS :
			groupBy["hour"] = {
				$hour : "$created_on"	
			};
			sortBy["_id.hour"] = 1;		
	}	
	
	var pipeline = [
		{
			$match: {
				"created_on" : {
					$gte: fromDate, 
					$lt : toDate	
				}
			}
		},
		{
			$project: {
				_id : 0,
				created_on : 1,
				value : 1
			}
		},
		{
			$group: {
					"_id": groupBy, 
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
			$sort: sortBy
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