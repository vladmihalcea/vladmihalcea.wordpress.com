//mongeez formatted javascript
//changeset system:v1_2
db.product.update(
    {
        name : 'TV'
    },
    {
         $inc : {
             price : -10,
             version : 1
         }
    },
    {
        multi: true
    }
);