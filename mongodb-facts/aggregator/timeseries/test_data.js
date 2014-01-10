var testFromDates = [
	new Date(Date.UTC(2012, 5, 10, 11, 25, 59)),
	new Date(Date.UTC(2012, 7, 23, 2, 15, 07)),
	new Date(Date.UTC(2012, 9, 25, 7, 18, 46)),
	new Date(Date.UTC(2012, 1, 27, 18, 45, 23)),
	new Date(Date.UTC(2012, 11, 12, 14, 59, 13))
];

function testFromDatesAggregation(matchDeltaMillis, groupDeltaMillis, type, enablePrintResult) {
	var aggregationTotalDuration = 0;
	var aggregationAndFetchTotalDuration = 0;
	testFromDates.forEach(function(testFromDate) {	
		var timeInterval = calibrateTimeInterval(testFromDate, matchDeltaMillis);
		var fromDate = timeInterval.fromDate;
		var toDate = timeInterval.toDate;
		var duration = aggregateData(fromDate, toDate, groupDeltaMillis, enablePrintResult);
		aggregationTotalDuration += duration.aggregationDuration;
		aggregationAndFetchTotalDuration += duration.aggregationAndFetchDuration;		
	});
	print(type + " aggregation took:" + aggregationTotalDuration/testFromDates.length + "s");
	if(enablePrintResult) {
		print(type + " aggregation and fetch took:" + aggregationAndFetchTotalDuration/testFromDates.length + "s");
	}
}