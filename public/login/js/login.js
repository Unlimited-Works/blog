var verifyApi = "/signin.jstr"

//functions
var verifyFunc = function(){
    var accountValue = $('#inputEmail').val();
    var passwordValue = $('#inputPassword').val();

    $.post(verifyApi,
      {
        account: accountValue,
        password: passwordValue
      },
      function(data,status){
          var json = jQuery.parseJSON(data)
          console.log("Data: " + data + "\nStatus: " + status)
          if (json.result === 200) {
            window.location.replace("/blog/index");
          }
          else if (json.result === 400) {
            $("#signin-tip").text(json.msg);
          }
      });
};

$(document).ready(function(){
  $(".ajax-btn-verify").click(verifyFunc);
});
