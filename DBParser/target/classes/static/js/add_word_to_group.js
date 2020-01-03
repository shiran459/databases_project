$(function(){
 $('form').submit(function(event){
    event.preventDefault();
    var theForm = $(this);
     $.ajax({
       url: $('#add-word-to-group-form').attr('action'),
       type: 'POST',
       data : $(theForm).serialize(),
       success: function(data, status){
        if (data){
            alert("Added successfully");
        } else{
            alert("Added Failed");
        }
       }
     });
    event.preventDefault();
 });
});
