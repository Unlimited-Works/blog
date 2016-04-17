$(document).ready(function($){
  var contentUrl = "/blog/post/content.jStr";
  var authorHomePageUrl = '/blog/'
  $(function(){
    var urlArray = window.location.href.split('/');
    var postId = urlArray[urlArray.length - 2];

    $.get(
      contentUrl,
      {
        id: postId
      },
      function(data,status){
          console.log("Data: " + data + "\nStatus: " + status);

          var json = jQuery.parseJSON(data);
          if (json.result === 200) {
            $('#post-title').text(json.post.title);
            $('#post-introduction').replaceWith(json.post.introduction)

            var homePage = $('.author-home-page');
            homePage.attr('href', '/' + json.post.pen_name + '/blog');
            homePage.text(json.post.pen_name);

            $('#post-issue-time').text(json.post.issue_time);
            postFilter(json.post.body);
          }
      }
    );
  });

  //transfer raw post body format to xml
  function postFilter(body){
    var filter = body;
    var content = '# Markdown text goes in here\n## Markdown text goes in here\n### Markdown text goes in here\n';
    $('#post-body').text(content);
    $('body').append('<script src="http://strapdownjs.com/v/0.2/strapdown.js"></script>');
  }
});