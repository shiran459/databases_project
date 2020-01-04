$(function(){
 $('#article-content').on("mouseup", function(event){
    var selected = window.getSelection().toString();
    var button = document.getElementById("expression-btn");
    if(selected.length > 0){
        button.disabled = false;
        button.value = selected;
    } else {
       button.disabled = true;
       button.value = "";
    }
 });
});
