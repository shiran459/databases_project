function crateUserExpression(value){
     $.ajax({
       url: '/expressions/add',
       type: 'POST',
       data: { expressionString: value },
       success: function(data, status){
        if (data){
            alert("Expression created successfully");
        } else{
            alert("Expression creation failed");
        }
       }
     });
 };
