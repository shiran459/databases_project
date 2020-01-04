$(function(){
 $('form').submit(function(event){
    event.preventDefault();
    var theForm = $(this);
     $.ajax({
       url: $('#expression-form').attr('action'),
       type: 'POST',
       data : $(theForm).serialize(),
       success: function(data, status){
        if (data){
            alert("Expression created successfully");
        } else{
            alert("Expression creation Failed");
        }
       }
     });
    event.preventDefault();
 });
});
