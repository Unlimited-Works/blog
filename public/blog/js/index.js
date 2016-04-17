jQuery(document).ready(function($) {
  var penNameUrl = "/blog/pen_name.jStr"
  var overviewUrl = "/blog/overview.jStr"

  var PageBlogsCount = 5;
  var currentBlogsCount = 0;

  //functions
  //show panName at top of page
  $(function(){
      $.get(penNameUrl,
        function(data,status){
            var json = jQuery.parseJSON(data)
            console.log("Data: " + data + "\nStatus: " + status)
            if (json.result === 200) {
              $('.greet').text("Welcome,"+json.penName)
            }
            else if (json.result === 400) {
              $("#signin-tip").text(json.msg);
              alert(json.msg);
            }
        }
      );
  });

  //dispaly data
  var paginatorOfBlogs = function(skip, limit, onSuccess){
    $.get(overviewUrl,
      {
        skip: skip,
        limit: limit
      },
      function(data, status){
        var json = jQuery.parseJSON(data)
        console.log("Data: " + data + "\nStatus: " + status)
        if (json.result === 200) {
          var itemCount = json.blogs.length

          var content = {};//why must init ? bad result if only use `var content;`
          content.size = itemCount;
          content.load = "";
          for(i = 0; i < itemCount; i++) {
            content.load +=
              '<div class="post-preview">' +
                '<a href="/blog/post/' + json.blogs[i].id + '/">' +
                  '<h2 class="post-title">' +
                    json.blogs[i].title +
                  '</h2>' +
                  '<h3 class="post-subtitle">' +
                    json.blogs[i].introduction +
                  '</h3>' +
                '</a>' +
                '<p class="post-meta">Posted on September <i class="post-time">' + json.blogs[i].issueTime + '</i></p>' +
              '</div>' +
              '<hr>'
          }

          currentBlogsCount += limit;
          onSuccess(content);
        }
        else if (json.result === 400) {
          console.log("get blogs preview jsonStr fail");
        }
      }
    );
  };

  //init blog
  $(function(){
    paginatorOfBlogs(0, PageBlogsCount, function(rsp){
      $('.post-preview-template').replaceWith(rsp.load);
    });
  });

  //next blog
  $('.pager > .next').click(function(){
    var ts = $(this);
    paginatorOfBlogs(currentBlogsCount, PageBlogsCount, function(content){
      if(content.size == 0) {
        ts.text("No More");
      } else {
        $('.post-preview-content').append(content.load);
      }
    })
  });

});