var minDate = new Date(2012, 0, 1, 0, 0, 0, 0);
var maxDate = new Date(2013, 0, 1, 0, 0, 0, 0);
var delta = maxDate.getTime() - minDate.getTime();
var fromDate = new Date(minDate.getTime() + Math.random() * delta);
fromDate.setHours(0, 0, 0, 0);
var toDate = new Date(fromDate.getTime() + 24 * 60 * 60 * 1000);

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
			hour:{
				$subtract:[
				   "$_id", {
					  $mod:[
						"$_id",  60 * 60 * 1000
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
print("Aggregation took:" + (new Date().getTime() - start.getTime())/1000 + "s");	
if(dataSet.result != null && dataSet.result.length > 0) {
	print("Fetching :" + dataSet.result.length + " documents.");
	dataSet.result.forEach(function(document)  {
		printjson(document);
	});
}
print("Aggregation took:" + (new Date().getTime() - start.getTime())/1000 + "s");