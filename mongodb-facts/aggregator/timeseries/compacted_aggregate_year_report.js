load(pwd() + "/../../util/date_util.js");
load(pwd() + "/compacted_aggregate_base_report.js");
var minDate = new Date(Date.UTC(2012, 0, 1, 0, 0, 0, 0));
var maxDate = new Date(Date.UTC(2013, 0, 1, 0, 0, 0, 0));
var one_year_millis = (maxDate.getTime() - minDate.getTime())/ONE_DAY_MILLIS;
aggregateData(minDate, maxDate, one_year_millis);