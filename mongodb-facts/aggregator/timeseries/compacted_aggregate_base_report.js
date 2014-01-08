var minDate = new Date(Date.UTC(2012, 0, 1, 0, 0, 0, 0));
var maxDate = new Date(Date.UTC(2013, 0, 1, 0, 0, 0, 0));

var ONE_SECOND_MILLIS = 1000;
var ONE_MINUTE_MILLIS = 60 * ONE_SECOND_MILLIS;
var ONE_HOUR_MILLIS = 60 * ONE_MINUTE_MILLIS;
var ONE_DAY_MILLIS = 24 * ONE_HOUR_MILLIS;

function aggregateData(span) {
	var delta = maxDate.getTime() - minDate.getTime();
	
	var fromDate = new Date(minDate.getTime() + Math.random() * delta);

	if(span == MillisSpan.minute) {
		fromDate = new Date(Date.UTC(fromDate.getUTCFullYear(), fromDate.getUTCMonth(), fromDate.getUTCDate(), fromDate.getUTCHours(), fromDate.getUTCMinutes()));
	} else if(span == MillisSpan.hour) {
		fromDate = new Date(Date.UTC(fromDate.getUTCFullYear(), fromDate.getUTCMonth(), fromDate.getUTCDate(), fromDate.getUTCHours()));
	} else if(span == MillisSpan.day) {
		fromDate = new Date(Date.UTC(fromDate.getUTCFullYear(), fromDate.getUTCMonth(), fromDate.getUTCDate()));
	}	
	
	var toDate = new Date(fromDate.getTime() + span);

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
							"$_id", span
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
	print("Aggregation and fetch took:" + (new Date().getTime() - start.getTime())/1000 + "s");
}