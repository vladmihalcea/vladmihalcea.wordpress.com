var start = new Date();
db.runCommand({ touch: "randomData", data: false, index: true });
print('Touch {index: true} took ' + (new Date() - start)/1000.0 + 's');