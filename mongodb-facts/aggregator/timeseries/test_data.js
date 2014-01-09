var testFromDates = [
	new Date(Date.UTC(2012, 5, 10, 11, 24, 56)),
	new Date(Date.UTC(2012, 7, 23, 2, 15, 07)),
	new Date(Date.UTC(2012, 9, 25, 7, 18, 46)),
	new Date(Date.UTC(2012, 1, 27, 18, 45, 23)),
	new Date(Date.UTC(2012, 11, 12, 14, 59, 13))
];

function testFromDatesAggregation(deltaMillis, type) {
	var totalDuration = 0;
	testFromDates.forEach(function(testFromDate) {	
		var timeInterval = calibrateTimeInterval(testFromDate, deltaMillis);
		var fromDate = timeInterval.fromDate;
		var toDate = timeInterval.toDate;
		totalDuration += aggregateData(fromDate, toDate, deltaMillis);
	});
	print(type + " aggregation took:" + totalDuration/testFromDates.length + "s");
}