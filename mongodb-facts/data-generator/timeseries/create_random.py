import sys
import os
import pymongo
import time
import random

from datetime import datetime

min_date = datetime(2012, 1, 1)
max_date = datetime(2013, 1, 1)
delta = (max_date - min_date).total_seconds()

job_id = '1'

if len(sys.argv) < 2:
	sys.exit("You must supply the item_number argument")
elif len(sys.argv) > 2:
	job_id = sys.argv[2]	

documents_number = int(sys.argv[1])
batch_number = 5 * 1000;

job_name = 'Job#' + job_id
start = datetime.now();

# obtain a mongo connection
connection = pymongo.Connection("mongodb://localhost", safe=True)

# obtain a handle to the random database
db = connection.random
collection = db.randomData

batch_documents = [i for i in range(batch_number)];

for index in range(documents_number):
	try:			
		date = datetime.fromtimestamp(time.mktime(min_date.timetuple()) + int(round(random.random() * delta)))
		value = random.random()
		document = {
			'created_on' : date,	
			'value' : value,	
		}
		batch_documents[index % batch_number] = document
		if (index + 1) % batch_number == 0:
			collection.insert(batch_documents)		
		index += 1;
		if index % 100000 == 0:	
			print job_name, ' inserted ', index, ' documents.'		
	except:
		print 'Unexpected error:', sys.exc_info()[0], ', for index ', index
		raise
print job_name, ' inserted ', documents_number, ' in ', (datetime.now() - start).total_seconds(), 's'
