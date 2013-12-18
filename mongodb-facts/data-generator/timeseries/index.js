var start = new Date();
db.randomData.ensureIndex( { created_on: 1 } );
print('Indexing took ' + (new Date() - start)/1000.0 + 's');