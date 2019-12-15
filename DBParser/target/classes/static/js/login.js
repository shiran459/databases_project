$(function(){
 $('#login-form').submit(function(event){
    event.preventDefault();
         $.ajax({
           url: $('#login-form').attr('action'),
           type: 'POST',
           data : $('#login-form').serialize(),
           success: function(data, status){
            if (!data){
                alert("Username or password incorrect");
            } else{
                document.cookie = "session_token=" + data.token;
                window.location = 'http://localhost:8080/'
            }
           }
         });
    event.preventDefault();
 });
});
