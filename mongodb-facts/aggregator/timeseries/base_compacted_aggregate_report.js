var minDate = new Date(Date.UTC(2012, 0, 1, 0, 0, 0, 0));
var maxDate = new Date(Date.UTC(2013, 0, 1, 0, 0, 0, 0));

var MillisSpan = {
	second : 1000,
	minute : 60 * 1000,
	hour : 60 * 60 * 1000,
	day : 24 * 60 * 60 * 1000
};

function aggregateData(span) {
	var delta = maxDate.getTime() - minDate.getTime();
	
	var fromDate = new Date(minDate.getTime() + Math.random() * delta);

	if(span == MillisSpan.minute) {
		fromDate.setSeconds(0, 0);
	} else if(span == MillisSpan.hour) {
		fromDate.setMinutes(0, 0, 0);
	} else if(span == MillisSpan.day) {
		fromDate = new Date(Date.UTC(fromDate.getUTCFullYear(), fromDate.getUTCMonth(), fromDate.getUTCDate()));
	}	
	
	var toDate = new Date(fromDate.getTime() + span);

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
		},
		{
			$sort: {
				"_id.timestamp" : 1
			} 	
		}
	]);	
	print("Aggregation took:" + (new Date().getTime() - start.getTime())/1000 + "s");	
	if(dataSet.result != null && dataSet.result.length > 0) {
		print("Fetching :" + dataSet.result.length + " documents.");
		dataSet.result.forEach(function(document)  {
			printjson(document);
		});
	}
	print("Fetch took:" + (new Date().getTime() - start.getTime())/1000 + "s");
}