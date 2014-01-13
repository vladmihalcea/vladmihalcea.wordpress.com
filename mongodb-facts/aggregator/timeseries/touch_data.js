var start = new Date();
db.runCommand({ touch: "randomData", data: true, index: false });
print('Touch {data: true} took ' + (new Date() - start)/1000.0 + 's');