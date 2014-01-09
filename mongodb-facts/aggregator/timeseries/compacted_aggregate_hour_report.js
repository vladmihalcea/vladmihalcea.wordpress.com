load(pwd() + "/../../util/date_util.js");
load(pwd() + "/compacted_aggregate_base_report.js");
load(pwd() + "/test_data.js");

testFromDatesAggregation(ONE_HOUR_MILLIS, ONE_MINUTE_MILLIS, 'One hour minutes');

