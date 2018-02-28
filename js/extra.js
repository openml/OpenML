element = document.getElementsByClassName("md-header-nav__topic")[0];
element.innerHTML = "<a href='https://www.openml.org'><b>OpenML</b></a> Documentation";

(function() {
    var startingTime = new Date().getTime();
    // Load the script
    var script = document.createElement("SCRIPT");
    script.src = 'http://code.jquery.com/jquery-3.3.1.slim.min.js';
    script.type = 'text/javascript';
    script.onload = function() {
    	var $ = window.jQuery;
      $(function() {
          $(document).ready(function() {
              if($('.framed-content').length == 1){
                  $(".md-content").css('margin-right',0);
                  $(".md-content__inner").css('margin-left',0);
                  $(".md-content__inner").css('margin-right',0);
                  $(".md-content__inner:before").css('display','none');
                  $("article").find("h1").css('display','none');
                  $("article > a").attr('target','_blank');
              }
              if($('.framed-python-guide').length == 1){
                $("article > a").attr('href','https://github.com/openml/openml-python/edit/master/doc/usage.rst');
              }
              if($('.framed-python-api').length == 1){
                $("article > a").attr('href','https://github.com/openml/openml-python/edit/master/doc/api.rst');
              }
              if($('.framed-python-start').length == 1){
                $("article > a").attr('href','https://github.com/openml/openml-python/edit/master/doc/index.rst');
              }
              if($('.framed-r').length == 1){
                $("article > a").attr('href','https://github.com/openml/openml-r/edit/master/vignettes/OpenML.Rmd');
              }
              if($('.framed-r-api').length == 1){
                $("article > a").css('display','none');
              }
              if($('.framed-java-api').length == 1){
                $("article > a").css('display','none');
              }
          });
        });
    };
    document.getElementsByTagName("head")[0].appendChild(script);
})();
