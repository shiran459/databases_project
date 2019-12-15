$(function(){
 $('#register-form').submit(function(event){
    event.preventDefault();
         $.ajax({
           url: $('#register-form').attr('action'),
           type: 'POST',
           data : $('#register-form').serialize(),
           success: function(data, status){
            if (!data){
                alert("Username already taken");
            } else{
                document.cookie = "session_token=" + data.token;
                window.location = 'http://localhost:8080/'
            }
           }
         });
    event.preventDefault();
 });
});
