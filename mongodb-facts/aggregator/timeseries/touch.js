var start = new Date();
db.runCommand({ touch: "randomData", data: true, index: true });
print('Compacting took ' + (new Date() - start)/1000.0 + 's');