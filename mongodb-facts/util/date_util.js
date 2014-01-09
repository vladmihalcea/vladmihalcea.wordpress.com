var ONE_SECOND_MILLIS = 1000;
var ONE_MINUTE_MILLIS = 60 * ONE_SECOND_MILLIS;
var ONE_HOUR_MILLIS = 60 * ONE_MINUTE_MILLIS;
var ONE_DAY_MILLIS = 24 * ONE_HOUR_MILLIS;

function newRandomDate(minDate, maxDate, deltaMillis) {
	var maxDelta = maxDate.getTime() - minDate.getTime();
	return new Date(minDate.getTime() + Math.random() * maxDelta);
}

function calibrateTimeInterval(fromDate, deltaMillis) {	
	var fromMillis = fromDate.getTime();
	fromMillis = fromMillis - (fromMillis % deltaMillis);
	
	return {
		fromDate : new Date(fromMillis),
		toDate : new Date(fromMillis + deltaMillis)
	}
}