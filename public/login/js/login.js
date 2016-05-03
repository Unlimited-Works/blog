$(document).ready(function(){
	var verifyApi = "/signin.jstr";
	var registerApi = "/signin/register.json";

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

	var registerFunc = function(){
		//按下之后要等待返回才能进行下次继续请求,防止注册成功再次点击注册会导致错误的提示
		$("#btn-register").prop('disabled', true);
		var invitationCode = $('#inputInvitationCode-register').val()
		var email = $('#inputEmail-register').val()
		var username = $('#inputUsername-register').val()
//		var phone = $('#inputPhone-register')
		var penName = $('#inputPenname-register').val()
		var password = $('#inputPassword-reigster').val()
		var passwordAgain = $('#inputPassword-ensure-reigster').val()

		$.post(registerApi,
			{
				invitationCode: invitationCode,
				email: email,
				username: username,
				penName: penName,
				password: password,
				passwordAgain: passwordAgain
			},
			function(json) {
				if(json.result == 400) {
					$("#btn-register").prop('disabled', false);
					var danger = $('.alert-danger');
					danger.find('.alert-content').text(JSON.stringify(json));
					$('.alert').show();
				} else if(json.result == 200 ) {
					$("#btn-register").prop('disabled', true);
					var danger = $('.alert-danger');
					danger.find('.alert-content').text("注册成功");
					$('.alert').show();
				}
			}
		)
	}

  $(".ajax-btn-verify").click(verifyFunc);
  $("#btn-register").click(registerFunc);
});
