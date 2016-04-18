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
            postRender(json.post.body);
          }
      }
    );
  });

  //transfer raw post body format to xml
  function postRender(body){
    var content = '# Markdown text goes in here\n## Markdown text goes in here\n### Markdown text goes in here\n';
    var converter = new showdown.Converter();
//        text      = '#hello, markdown!',
    var    html      = converter.makeHtml(content);
    $('#post-body').html(html);
  }
});