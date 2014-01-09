var start = new Date();
db.randomData.runCommand("compact");
print('Compacting took ' + (new Date() - start)/1000.0 + 's');