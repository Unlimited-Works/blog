$(document).ready(function($){
	//global
	var globalShareSHA = "";

	var shareElem = $('#btn-share-post')
	$(function(){
		$.get(
			'/blog/post/access/' + postId + '.json',
			function(json, status) {
				if (json.result == 200) {
					shareElem.data('is-public', json.isPublic);

					if (json.isPublic) {
					  globalShareSHA = json.share_sha;
						shareElem.addClass("btn-warning");
						shareElem.text("public");
					} else {
						shareElem.addClass("btn-info");
            shareElem.text("private");
					}
				}
			}
		);
	});

	$('#btn-share-post').click(function(){
		//setting status
		var isPublic = $(this).data('is-public');
		if (!isPublic) {
			onPrivate();
		} else {
			onPublic();
		}
	});

	$('#btn-post-access-modify').click(function(){
		var isPublic = $('#btn-share-post').data('is-public');
		$.post(
			'/blog/post/access/modify.json',
			{
				//Q: why pass the parameter?
				//A: though server know the user's status, is a good practice make logic non-context relate
				postId: postId,
				currentIsPublic: isPublic
			},
			function(json) {
				if (json.result == 200) {
					if(json.hasSHA) {//to open status
						globalShareSHA = json.share_sha;
						shareElem.addClass("btn-warning");
						shareElem.text("public");
						shareElem.data('is-public', true);

						onPublic();
					} else {//to close status
						globalShareSHA = "";
						shareElem.addClass("btn-info");
						shareElem.text("private");
						shareElem.data('is-public', false);

						onPrivate();
					}
				}
			}
		);
	});

	function onPublic() {
		$('#access-tips').html('当前帖子是公开状态,访问链接为 ' +
		window.location.protocol + "\\\\" +
		window.location.host +
		window.location.pathname +
		'?share_sha=' + globalShareSHA)
		$('#btn-post-access-modify').text('Close Access');
	}

	function onPrivate() {
		$('#access-tips').text('当前帖子为私有状态,是否开放链接?');
    $('#btn-post-access-modify').text('Open Access');
	}
});