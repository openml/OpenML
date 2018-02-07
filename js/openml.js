var es_config = {};
es_config['host'] = ES_URL;
var client = new $.es.Client(es_config);

client.ping({
  requestTimeout: 30000,
}, function (error) {
  if (error) {
    console.error('elasticsearch cluster is down!');
  } else {
    console.log('All is well');
  }
});

var icons = {
  estimation_procedure : 'fa fa-signal',
  evaluation_measure : 'fa fa-signal',
  data_quality : 'fa fa-signal',
  flow_quality : 'fa fa-signal',
  measure : 'fa fa-signal',
  flow : 'fa fa-cogs',
  data : 'fa fa-database',
  run  : 'fa fa-star',
  user : 'fa fa-user',
  task : 'fa fa-trophy',
  study : 'fa fa-flask'
};


var urlprefix = {
  estimation_procedure : 'a/estimation-procedures',
  evaluation_measure : 'a/evaluation-measures',
  data_quality : 'a/data-qualities',
  flow_quality : 'a/flow-qualities',
  flow : 'f',
  data : 'd',
  run  : 'r',
  user : 'u',
  task : 't',
  study : 's'
};

// scrolls left menu to top
function scrollMenuTop(){
  $('#mainmenu').animate({ scrollTop: 0 }, { duration: 500, queue: false });
}

$(function(){

//scrollbars
var container = document.getElementById('mainmenu');
Ps.initialize(container);
//$("#mainmenu").find(".ps-scrollbar-y-rail").css("opacity",0);

//var container2 = document.getElementsByClassName('openmlsectioninfo');
//if(container2.length > 0){
//  Ps.initialize(container2[0], {minScrollbarLength:100});
//}

// DELETING ACTIONS

  //bind to action icons
  $(document).on('click', '.delete_action', function(event) {
       var itemName = $(this).data("name");
       var itemID = $(this).data("id");
       var itemType = $(this).data("type");
       swal({title: "Are you sure?",
             text: "You are about to delete "+itemName+". You will not be able to recover this "+itemType+"!",
             type: "warning",
             showCancelButton: true,
             confirmButtonColor: "#DD6B55",
             confirmButtonText: "Yes, delete it!",
             closeOnConfirm: false},
           function(){
             deleteItem( itemType, itemID, itemName );
           });
      return false;
  });

  // deleting items
  function deleteItem( type, id, name ) {

  $.ajax({
    type: "DELETE",
    url: "https://" + location.host + "/api/v1/json/"+type+"/"+id,
    dataType: "json"
  }).done( function( resultdata ) {
      if(type+"_delete" in resultdata){
      id_field = resultdata[type+"_delete"]["id"];
      if( id_field.length ) {
        swal("Deleted!", name + " has been deleted.", "success");
        location.reload();
      }} else {
        rdata = resultdata.responseJSON;
        code_field = rdata.error["code"];
        message_field = rdata.error["message"];
        swal("Error " + code_field, message_field, "error");
      }
    }).fail( function( resultdata ) {
        code_field = resultdata.responseJSON.error["code"];
        message_field = resultdata.responseJSON.error["message"];
        if(code_field == 102)
          swal("Error", "Your login has expired. Log in and try again.", "error");
        else
          swal("Error " + code_field, message_field, "error");
    });
  }
  // wiki. Hide the annoying uploading file spinner.
  $('#gollum-editor-edit-header').prev('div').css('display','none');

});

$(function() {

  // brings up login window on restricted links
  $('.loginfirst').focus(function() { if(!logged_in){$('#login-dialog').modal('show'); return false;}});
  $('.loginfirst').click(function() { if(!logged_in){$('#login-dialog').modal('show'); return false;}});

  // attaches page-specific menu below main menu
  $("#submenucontainer").append($(".submenu"));
  $(".submenu").css('float', 'none');

  var colors = ['#F44336','#E91E63','#9C27B0','#673AB7','#2196F3','#4CAF50','#CDDC39','#FFEB3B','#FF9800'];
  $('#colorchange').children('span').each(function (i){
    $(this).hover(function () {
      $(this).css("color",colors[Math.floor(Math.random() * colors.length)]);
    });
  });
  $('#colorchange').children('span').each(function (i){
    $(this).css("color",colors[i]);
  });



  // shows/hides menu when menu icon is clicked
  $(".menubutton").click(function () {

    if($('#mainmenu').is(":visible" )){
      //$('#mainmenu').scrollTop($("#submenucontainer").offset().top - 80);
      $('#mainpanel').addClass('col-md-10');
      $('#mainpanel').addClass('col-md-offset-1');
      $('.openmlsectioninfo').css("margin-left","0");
    } else {
      if(window.innerWidth>992){
        $('#mainpanel').removeClass('col-md-10');
        $('#mainpanel').removeClass('col-md-offset-1');
        $('.openmlsectioninfo').css("margin-left","265px");
      }
      if($('#sectiontitle').html() == 'OpenML'){
          $('.searchbar').css("z-index","1050");
          $('.sidebar-overlay').css("visibility", "visible");}
      else{
          $('.searchbar').css("z-index","100");
          $('.sidebar-overlay').css("visibility", "hidden");}
    }

    var options = { direction: 'left' };
    $('#mainmenu').toggle('slide', options, 500);

  });

  // automatically show/hide the menu depending on the page width
  if($('#sectiontitle').html() == 'OpenML' || $('#sectiontitle').html() == 'Guide'){
    $('#mainmenu').css("display","none");
  } else if($('#wrap').width()>992){
    $('#mainmenu').css("display","block");
  } else {
    $('a[data-toggle="tab"]').click(function () {
      var options = { direction: 'left' };
      $('#mainmenu').toggle('slide', options, 500);
    });
  }


  // highlighting for subsections in submenu
  $(".collapse").on('shown.bs.collapse', function () {
    if($(this).attr('id') != "mainlist"){
      $(this).prev().css( "color", $(".topactive > :first-child").css("color"));
      $(this).prev().css( "font-weight", "bold" );
    }
  });
  $(".collapse").on('hidden.bs.collapse', function () {
    $(this).prev().css( "color", "#424242" );
    $(this).prev().css( "font-weight", "normal" );
  });

  // auto-suggest for the search box (if shown)
  if( $('#openmlsearch').length ){
    $("#openmlsearch").autocomplete({
      html: true,
      position: {
          my: "left top+13" // Shift 0px to the left, 20px down.
      },
      source: function(request, fresponse) {
        client.search({
          index: 'openml',
          body: {
            suggest: {
              mysuggester: {
                prefix: request.term,
                completion: { field: 'suggest' }
              }
            }
          }
        }, function (error, response) {
          fresponse($.map(response['suggest']['mysuggester'][0]['options'], function(item) {
            if(item['_type'] == 'data' && (item['_source']['visibility'] != 'public' || item['_source']['status'] != 'active')){}
            else{
             obj_description = '';
             obj_text = item['text'];
             obj_image = '<i class="' + icons[item['_type']] + '"></i>';
             if(item['_type'] == 'data'){
               obj_description = (typeof item['_source']['suggest']['input'][1] === 'string' ? item['_source']['suggest']['input'][1].substring(0,70) : '')
             }
             else if(item['_type'] == 'task'){
               obj_description = item['_source']['suggest']['input'][0] + ', target ' + item['_source']['suggest']['input'][4] + ', with ' +  item['_source']['suggest']['input'][2]
             }
             else if(item['_type'] == 'user'){
               obj_text = item['_source']['first_name']+' '+item['_source']['last_name'];
               obj_description = item['_source']['affiliation']+', '+item['_source']['bio'];
               obj_image = '<img src="'+item['_source']['image']+'" width=25, height=25, class="img-circle" onerror="this.src=\'img/community/misc/anonymousMan.png\'">';
             }
             else if(item['_type'] == 'flow'){
               obj_text = item['_source']['name'];
               obj_description = 'v.'+item['_source']['version']+', '+item['_source']['dependencies']+', '+item['_source']['description']
             }
             return {
                type: item['_type'],
                id: item['_id'],
                description: obj_description,
                text: obj_text,
                image: obj_image
              }
            };
          }));
        });
      }
    }).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
      return $( "<li>" )
      .append( '<a href="' + urlprefix[item.type] + '/' + item.id +'">' + item.image + ' ' + item.text + ' <span>' + item.description + '</span></a>' )
      .appendTo( ul );
    }
  }

  //reflow hidden charts and tables when they become visible
  jQuery(document).on( 'shown.bs.tab', 'a[data-toggle="tab"]', function (e) { // on tab selection event
    jQuery( ".reflow-chart" ).each(function() { // target each element with the .contains-chart class
        var chart = jQuery(this).highcharts(); // target the chart itself
        if(chart) chart.reflow() // reflow that chart
    });
    jQuery( ".reflow-table" ).each(function() { // target each element with the .contains-chart class
        $( $.fn.dataTable.tables( true ) ).DataTable().columns.adjust();
    });
  });

  // ENDLESS SCROLL (is used on multiple pages, and thus defined system-wide)

  var next_data_url; // replaced when loading more
  var prev_data_url; // replaced when loading more
  var next_data_cache = false;
  var prev_data_cache = false;
  var last_scroll = 0;
  var is_loading = 0; // simple lock to prevent loading when loading
  var hide_on_load = false; // ID that can be hidden when content has been loaded

  function loadFollowing() {
  if(getParameterByName('table') != 1){ //only scroll if cards are shown
    is_loading = 1; // note: this will break when the server doesn't respond
    function showFollowing(data) {
      next_data_cache = false;
      if(!$(data).find("#itempage").is(':empty') && $(data).find("#itempage")[0].childElementCount > 0) {
        $(data).find("#itempage").appendTo("#scrollingcontent");
        next_data_url = $(data).find("#itempage").attr("data-next-url");
        if(next_data_url)
          $.get(next_data_url+'&dataonly=1', function(preview_data) {
            next_data_cache = preview_data;
          });
      } else {
        $( ".loadingmore" ).html(" ");
        $( ".pagination" ).css("display","block");
      }
    }
    if (next_data_cache) {
      showFollowing(next_data_cache);
      is_loading = 0;
    } else if(next_data_url) {
      $( ".loadingmore" ).html('Loading more... <a href="'+next_data_url+'">Not loading? Go to the next results now.</a>.' );
      $.get(next_data_url+'&dataonly=1', function(data) {
          showFollowing(data);
          is_loading = 0;
      });
    }
  }
  }

  function loadPrevious() {
  if(getParameterByName('table') != 1){ //only scroll if cards are shown
    is_loading = 1; // note: this will break when the server doesn't respond
    function showPrevious(data) {
      prev_data_cache = false;
      var scroll_pos = $('.openmlsectioninfo').scrollTop();
      $(data).find("#itempage").prependTo("#scrollingcontent");
      item_height = $(".listitempage:first").height();
      $('.openmlsectioninfo').animate({
        scrollTop: scroll_pos + item_height
      }, 0);
      prev_data_url = $(data).find("#itempage").attr("data-prev-url");
      if(prev_data_url && curr_data_url != prev_data_url){
        $.get(prev_data_url+'&dataonly=1', function(preview_data) {
          prev_data_cache = preview_data;
        });}
      }
      if (prev_data_cache) {
        showPrevious(prev_data_cache);
        is_loading = 0;
      } else if (prev_data_url && curr_data_url != prev_data_url){
        $.get(prev_data_url+'&dataonly=1', function(data) {
          showPrevious(data);
          is_loading = 0;
        });
      } else
      is_loading = 0;
    }
  }

  function mostlyVisible(element) {
    // if ca 25% of element is visible
    var scroll_pos = $('#scrollingcontent').scrollTop();
    var window_height = $('.openmlsectioninfo').height();
    var el_top = $(element).offset().top;
    var el_height = $(element).height();
    var el_bottom = el_top + el_height;

    return ((el_bottom - el_height*0.25 > scroll_pos) &&  (el_top < (scroll_pos+0.5*window_height)));
  }

  function isElementInViewport (el) {

    //special bonus for those using jQuery
    if (typeof jQuery === "function" && el instanceof jQuery) {
        el = el[0];
    }

    var rect = el.getBoundingClientRect();

    //console.log(rect.bottom + ' > 0 && ' + rect.top + ' < ' + $(window).height() + ' = ' + (rect.top <= $(window).height() && rect.bottom >= 0 ));

    return (
      rect.top <= $(window).height() &&
      rect.bottom >= 0
    );
}

  function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
    results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
  }



  function initPaginator() {

    var seenUrls = new Set();
    var currentUrl = $(document).find("#itempage").attr("data-url");
    var scrcount = 0;

    $('.openmlsectioninfo').scroll(function() {
      $(".listitempage").each(function(index) {
        if (mostlyVisible(this)) {
          if(currentUrl != $(this).attr("data-url")){
            if(window.location.href.indexOf('search') >= 0)
              history.replaceState(null, null, $(this).attr("data-url").replace("&dataonly=1",""));
            currentUrl = $(this).attr("data-url");
            if(!seenUrls.has($(this).attr("data-next-url"))){
              seenUrls.add($(this).attr("data-next-url"));
              loadFollowing();
            }
            else if(!seenUrls.has($(this).attr("data-prev-url"))){
              seenUrls.add($(this).attr("data-prev-url"));
              loadPrevious();
            }
          }
          return(false);
        }
      });
    });

    // special behavior on mobile devices
    window.addEventListener( "scroll", function( event ) {
      scrcount++;
      if(scrcount % 10 == 0){
        $(".listitempage").each(function(index) {
          if (isElementInViewport(this)) {
            if(currentUrl != $(this).attr("data-url")){
              if(window.location.href.indexOf('search') >= 0)
                history.replaceState(null, null, $(this).attr("data-url").replace("&dataonly=1",""));
              currentUrl = $(this).attr("data-url");
              if(!seenUrls.has($(this).attr("data-next-url"))){
                seenUrls.add($(this).attr("data-next-url"));
                loadFollowing();
              }
              else if(!seenUrls.has($(this).attr("data-prev-url"))){
                seenUrls.add($(this).attr("data-prev-url"));
                loadPrevious();
              }
            }
            return(false);
          }
        });
      }
    });


    curr_data_url = $(document).find("#itempage").attr("data-url");
    next_data_url = $(document).find("#itempage").attr("data-next-url");
    prev_data_url = $(document).find("#itempage").attr("data-prev-url");
    seenUrls.add(curr_data_url);
    if(curr_data_url != prev_data_url){
      seenUrls.add(prev_data_url);
      loadPrevious();
    }
    seenUrls.add(next_data_url);
    loadFollowing();
  }


      // initialize endless scrolling
      initPaginator();

      // handle clicks on cards
      $(document).on('click', '.searchresult', function() {
          window.location = $(this).find("a:first").attr("href");
          return false;
      });

      $(".searchresult").hover(function () {
          window.status = $(this).find("a:first").attr("href");
      }, function () {
          window.status = "";
      });

});

function makeCommaSeperatedAutoComplete( selector, datasource ) {
  function split( val ) {
    return val.split( /,\s*/ );
  }
  function extractLast( term ) {
    return split( term ).pop();
  }

  $( selector )
  // don't navigate away from the field on tab when selecting an item
  .bind( "keydown", function( event ) {
    if ( event.keyCode === $.ui.keyCode.TAB &&
      $( this ).data( "autocomplete" ).menu.active ) {
        event.preventDefault();
      }

    })
    .autocomplete({
      minLength: 0,
      source: function( request, response ) {
        // delegate back to autocomplete, but extract the last term
        response( $.ui.autocomplete.filter(
          datasource, extractLast( request.term ) ) );
        },
        focus: function() {
          // prevent value inserted on focus
          return false;
        },
        select: function( event, ui ) {
          var terms = split( this.value );
          // remove the current input
          terms.pop();
          // add the selected item
          terms.push( ui.item.value );
          // add placeholder to get the comma-and-space at the end
          terms.push( "" );
          this.value = terms.join( ", " );
          $( selector ).trigger('change');
          return false;
        }
      });
    }

    function makeAutoComplete( selector, datasource ) {
      $( selector ).autocomplete({
        source: datasource
      });
    }

    // CARDS

    function showmore(){
      $('.description').switchClass("hideContent", "showContent", 400);
      $('.show-more').hide();
    }

    function showmoreprops(){
      $('.properties').switchClass("hideProperties", "showProperties", 400);
      $('.show-more-props').hide();
    }

    function showmorefeats(){
      $('.features').switchClass("hideFeatures", "showFeatures", 400);
      visualize_all();
      $('.show-more-features').hide();
      $('.show-all-features').show();
    }

    function showsearch(){
      if($('#menusearchframe').hasClass("hidden-xs")){
        $('#menusearchframe').removeClass("hidden-xs");
        $('#menusearchframe').addClass("col-xs-12");
      } else {
        $('#menusearchframe').addClass("hidden-xs");
        $('#menusearchframe').removeClass("col-xs-12");
      }
    }

    // GUIDE MENU

    $(function(){
      // set correct url
      $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
        $('ul.sidenav li.active').removeClass('active');
        $(this).parent('li').addClass('active');
        window.location.hash = '!'+$(this).attr("href").replace('#','');
      });

      // show page indicated by #!
      if (location.hash.substr(0,2) == "#!") {
        var e = $("a[href='#" + location.hash.substr(2) + "']");
        e.tab("show");
        e.closest("ul").collapse('show');
        e.closest("li").addClass('active');
      } else { //set default
        //$("#startlist").collapse('show');
      }

      //scroll to top when selected tab changes
      $('.switchtab').click(function (e) {
        e.preventDefault();
        $('a[href="' + $(this).attr('href') + '"]').tab('show');
        $('.guidesection').animate({ scrollTop: 0 }, 0);
      });

    });


    function scrollToAnchor(aid){
      console.log($(aid).offset().top);
      $('#rest_services').animate({scrollTop: $(aid).offset().top},2000);
    }
