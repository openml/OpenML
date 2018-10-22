
/// Wiki
// Loading the Wiki through CORS. This allows it to be loaded from anywhere.
$.ajax({
  type: 'GET',
  url: '<?php echo WIKI_URL . '/'.$this->url;?>',
  contentType: 'text/plain',
  xhrFields: { withCredentials: false },
  headers: {  },
  success: function(data) {
    data = data.match(/<body[^>]*>[\s\S]*<\/body>/gi)[0];
    data = '<?php echo $this->preamble; ?>' + data.replace('body>', 'div>');
    data = data.replace('action="/edit/<?php echo $this->wikipage; ?>','');
    data = data.replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi,'');
    $(".description").html(data);

    //customizations
    $("#gollum-editor-message-field").val("Write a small message explaining the change.");
    $("#gollum-editor-submit").addClass("btn btn-success pull-left");
    $("#gollum-editor-preview").removeClass("minibutton");
    $("#gollum-editor-preview").addClass("btn btn-default padded-button");
    $("#function-help").addClass("wiki-help-button");
    $("#function-help").html("Need help?");
    $("#gollum-editor-preview").attr("href","preview");
    $("#version-form").attr('action', "s/<?php echo $this->id; ?>/compare/<?php echo $this->wikipage; ?>");
    $("a[title*='View commit']").each(function() {
       var _href = $(this).attr("href");
       $(this).attr('href', 's/<?php echo $this->id; ?>/view' + _href);
    });
    $("#wiki-waiting").css("display","none");
    $("#wiki-ready").css("display","block");

    //load gollum javascript
    var headID = document.getElementsByTagName("head")[0];
    var newScript = document.createElement('script');
    newScript.type = 'text/javascript';
    newScript.src = 'js/libs/gollum.js';
    headID.appendChild(newScript);
  },
  error: function() {
    // Here's where you handle an error response.
    // Note that if the error was due to a CORS issue,
    // this function will still fire, but there won't be any additional
    // information about the error.
    console.log('Woops, there was an error making the request.');
  }
});

$( "#gollum-editor-preview" ).click(function() {
	var $form = $($('#gollum-editor form').get(0));
        $form.attr('action', '');
});

$("a[title*='View commit']").each(function() {
   var _href = $(this).attr("href");
   $(this).attr('href', 's/<?php echo $this->id; ?>/view' + _href);
});
