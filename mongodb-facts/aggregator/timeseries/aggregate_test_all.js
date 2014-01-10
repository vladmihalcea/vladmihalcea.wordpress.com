load(pwd() + "/../../util/date_util.js");
load(pwd() + "/aggregate_base_report.js");

var deltas = [ 
{
	matchDeltaMillis: ONE_MINUTE_MILLIS, 
	groupDeltaMillis: ONE_SECOND_MILLIS,
	description: "Aggregate all seconds in a minute"
},
{
	matchDeltaMillis: ONE_HOUR_MILLIS, 
	groupDeltaMillis: ONE_MINUTE_MILLIS,
	description: "Aggregate all minutes in an hour"
},
{
	matchDeltaMillis: ONE_DAY_MILLIS, 
	groupDeltaMillis: ONE_HOUR_MILLIS,
	description: "Aggregate all hours in a day"
}
];

var testFromDate = new Date(Date.UTC(2012, 5, 10, 11, 25, 59));

deltas.forEach(function(delta) {	
	print('Aggregating ' + delta.description);
	var timeInterval = calibrateTimeInterval(testFromDate, delta.matchDeltaMillis);
	var fromDate = timeInterval.fromDate;
	var toDate = timeInterval.toDate;
	aggregateData(fromDate, toDate, delta.groupDeltaMillis, true);	
});