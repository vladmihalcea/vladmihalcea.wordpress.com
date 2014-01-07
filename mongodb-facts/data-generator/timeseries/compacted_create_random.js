var minDate = new Date(2012, 0, 1, 0, 0, 0, 0);
var maxDate = new Date(2013, 0, 1, 0, 0, 0, 0);
var delta = maxDate.getTime() - minDate.getTime();

var job_id = arg2;

var documentNumber = arg1;
var batchNumber = 5 * 1000;

var job_name = 'Job#' + job_id
var start = new Date();

var index = 0;

while(index < documentNumber) {
	var date = new Date(minDate.getTime() + Math.random() * delta);
	var value = Math.random();	
	db.randomData.update( { _id: date.getTime() }, { $push: { v: value } }, true );	
	index++;
	if(index % 100000 == 0) {	
		print(job_name + ' inserted ' + index + ' documents.');
	}
}
print(job_name + ' inserted ' + documentNumber + ' in ' + (new Date() - start)/1000.0 + 's');
