load(pwd() + "/../../util/date_util.js");
var minDate = new Date(Date.UTC(2012, 1, 10, 11, 25, 30));
var maxDate = new Date(Date.UTC(2012, 1, 10, 11, 25, 35));
var result = db.randomData.runCommand('aggregate', { pipeline: 
[
	{
		$match: {
			"created_on" : {
				$gte: minDate, 
				$lt : maxDate	
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
				"_id": { 
					"year" : {
						$year : "$created_on"
					}, 
					"dayOfYear" : {
						$dayOfYear : "$created_on"
					},
					"hour" : {
						$hour : "$created_on"
					},
					"minute" : {
						$minute : "$created_on"
					},
					"second" : {
						$second : "$created_on"
					},
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
			"_id.hour" : 1,
			"_id.minute" : 1,
			"_id.second" : 1
		} 	
	}
], explain: true});
printjson(result);