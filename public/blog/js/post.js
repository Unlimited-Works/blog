$(document).ready(function($){
  var contentUrl = "/blog/post/content.jStr";
  var authorHomePageUrl = '/blog/'

  var converter = new showdown.Converter();

  $(function(){
		var searchParams = Common.getQueryString('share_sha');
    $.get(
      contentUrl,
      {
        id: postId,
        shareSHA: searchParams
      },
      function(json,status){
        console.log("Data: " + json + "\nStatus: " + status);

        if (json.result === 200) {
          $('#post-title').text(json.title);
          $('#post-introduction').replaceWith(json.introduction)

          var homePage = $('.author-home-page');
          homePage.attr('href', '/' + json.pen_name + '/blog');
          homePage.text(json.pen_name);

          $('#post-issue-time').text(json.issue_time);
          postRender(json.body);

					//visitor
					if(json.is_visitor == true) {
						$('#btngp-edit-delete').remove();
					} else { //author
						$('#btngp-edit-delete').show();
					}
        } else if (json.result === 401) {
        	window.location.replace("/401");
        }
      }
    );
  });

  //transfer raw post body format to xml
  function postRender(body){
//    var content = '# Markdown text goes in here\n## Markdown text goes in here\n### Markdown text goes in here\n```\nvar x = "123"\n```';
    content = body;
    var html = converter.makeHtml(content);
    $('#message-text').val(content);
    $('#post-body').html(html);
  }

  $('#btn-save-blog').click(function(){
    var txt = $('#message-text').val();
    $.post(
      "/blog/post/save.jStr",
      {
        postId: postId,
        blog: txt
      },
      function(data, status) {
        var json = jQuery.parseJSON(data);
        if (json.result == 200) {
          postRender(txt)
        } else {
          alert("保存失败 - " + json.error)
        }
      }
    )
  });

  $('#btn-delete-post').click(function(){
		var delt = confirm("确定要删除帖子?");
		if (delt == true) {
			$.post(
				"/blog/post/delete.json",
				{
					postId: postId
				},
				function(json, status) {
					if (json.result == 200) {
						window.location.replace("/blog/index");
					} else {
						alert("删除失败 - " + json.error)
					}
				}
			);
  	}
	});
});