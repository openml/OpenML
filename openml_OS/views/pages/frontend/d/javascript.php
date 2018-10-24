/// Feature graphs
<?php

//get data from ES for visualizations
$this->p = array();
$this->p['index'] = 'data';
$this->p['type'] = 'data';
$this->p['id'] = $this->id;

try{
  $this->data = $this->searchclient->get($this->p)['_source'];
} catch (Exception $e) {}

$fgraphs = '';
$fgraphs_all = '';

if (!empty($this->data['features'])){
	$featCount = 0;
  //get the target values
  $this->classvalues = array();
	foreach( $this->data['features'] as $r ) {
    if(array_key_exists('target', $r) and array_key_exists('distr', $r)){
      $this->classvalues = $r['distr'][0];
    }
  }
	foreach( $this->data['features'] as $r ) {
		$newGraph = '';

		if($r['type'] == "numeric"){
			$newGraph = '$(\'#feat'.$r['index'].'\').highcharts({chart:{type:\'boxplot\',inverted:true,backgroundColor:null},exporting:false,credits:false,title: null,legend:{enabled: false},tooltip:false,xAxis:{title:null,labels:{enabled:false},tickLength:0},yAxis:{title:null,labels:{style:{fontSize:\'8px\'}}},series: [{data: [['.$r['min'].','.($r['mean']-$r['stdev']).','.$r['mean'].','.($r['mean']+$r['stdev']).','.$r['max'].']]}]});';
		} else if (count($r['distr'])>0 ) {
			$distro = $r['distr'];
      $this->featvalues = $distro[0];

      if (count($this->classvalues) > 0 && count($this->featvalues)>100) {
        $newGraph = '$(\'#feat'.$r['index'].'\').html("Too many values to plot")';
      } else {
	    $newGraph = '$(\'#feat'.$r['index'].'\').highcharts({chart:{type:\'column\',backgroundColor:null},exporting:false,credits:false,title:false,xAxis:{title:false,labels:{'.(count($distro[0])>10 ? 'enabled:false' : 'style:{fontSize:\'9px\'}').'},tickLength:0,categories:[\''.implode("','", str_replace("'", "", $distro[0])).'\']},yAxis:{min:0,title:false,gridLineWidth:0,minorGridLineWidth:0,labels:{enabled:false},stackLabels:{enabled:true,useHTML:true,style:{fontSize:\'9px\'}}},legend:{enabled: false},tooltip:{useHTML:true,shared:true},plotOptions:{column:{stacking:\'normal\'}},series:[';

      if(count($this->classvalues) > 0){ # classification
    		for($i=0; $i<count($this->classvalues); $i++){
    			$newGraph .= '{name:\''.$this->classvalues[$i].'\',data:['.implode(",",array_column($distro[1], $i)).']}';
    			if($i!=count($this->classvalues)-1)
    				$newGraph .= ',';
    		}
      } else if(count($distro[1])>1){ # classification but no explicit target feature
        $newGraph .= '{name:\'\',data:['.implode(",",array_map("array_sum", $distro[1])).']}';
      } else { # regression
        $newGraph .= '{name:\'\',data:['.implode(",",array_column($distro[1], 0)).']}';
      }
  		if(count($this->featvalues)==0){
  			$newGraph .= '{name:\'count\',data:['.implode(",",array_column($distro[1], 0)).']}';
  		}
  		$newGraph .= ']});';
      }
    }
		if($featCount<3 or $this->showallfeatures){
			$fgraphs = $newGraph . PHP_EOL . $fgraphs;
			$featCount = $featCount + 1;
		}
		else
			$fgraphs_all = $newGraph . PHP_EOL . $fgraphs_all;
		}
	}


  if(isset($fgraphs)){
	echo $fgraphs;
	echo 'function visualize_all(){'.$fgraphs_all.'}';
	}

?>

//Update form
$(function() {
		$('#licence').change(function(){
				$('.licences').hide();
				$('#' + $(this).val()).show();
		});
});

$(document)
	.on('change', '.btn-file :file', function() {
		var input = $(this),
		numFiles = input.get(0).files ? input.get(0).files.length : 1,
		label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
		input.trigger('fileselect', [numFiles, label]);
});

$(document).ready( function() {
	$('.btn-file :file').on('fileselect', function(event, numFiles, label) {

		var input = $(this).parents('.input-group').find(':text'),
			log = numFiles > 1 ? numFiles + ' files selected' : label;

		if( input.length ) {
			input.val(log);
		} else {
			if( log ) alert(log);
		}

	});
});

$('#comment').bind('input', function() {
		var cname = $(this).val();
		if(cname.length > 0){
			 $(this).parent().removeClass('has-error');
			 $(this).parent().addClass('has-success');
		} else {
			 $(this).parent().removeClass('has-success');
			 $(this).parent().addClass('has-error');
		}
});


/// Wiki
jQuery.loadScript = function (url, callback) {
    jQuery.ajax({
        url: url,
        dataType: 'script',
        success: callback,
        async: true
    });
}

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
    $("#version-form").attr('action', "d/<?php echo $this->id; ?>/compare/<?php echo $this->wikipage; ?>");
    $("a[title*='View commit']").each(function() {
       var _href = $(this).attr("href");
       $(this).attr('href', 'd/<?php echo $this->id; ?>/view' + _href);
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
   $(this).attr('href', 'd/<?php echo $this->id; ?>/view' + _href);
});



var isliked;
var reason_id = -1;
var maxreason = -1;
<?php if ($this->ion_auth->logged_in()) {
      if ($this->ion_auth->user()->row()->id != $this->data['uploader_id']) {
?>

getYourDownvote();
setSubmitBehaviour();

 function doLike(liked){
    isliked = liked;
    if(isliked){
        meth = 'DELETE';
    }else{
        meth = 'POST';
    }
    $.ajax({
        method: meth,
        url: '<?php echo BASE_URL; ?>api_new/v1/xml/votes/up/d/<?php echo $this->id; ?>'
    }).done(function(resultdata){
        if(resultdata.getElementsByTagName('like').length>0){
            //changes already done
        }else{
            //undo changes
            flipLikeHTML();
        }
    }).fail(function(resultdata){
        //undo changes
        console.log('failed');
        flipLikeHTML();
    });
    //change as if the api call is succesful
    flipLikeHTML();
}

function doDownload(){
    $.ajax({
            method: 'POST',
            url: '<?php echo BASE_URL; ?>api_new/v1/xml/downloads/d/<?php echo $this->id; ?>'
           }
    ).done(function(){
        refreshNrDownloads();
    });
}


function getYourDownvote(){
    $.ajax({
        method:'GET',
        url: '<?php echo BASE_URL; ?>api_new/v1/xml/votes/down/<?php echo $this->ion_auth->user()->row()->id; ?>/d/<?php echo $this->id; ?>'
    }).done(function(resultdata){
        reason_id = resultdata.getElementsByTagName('value')[0].textContent;
        if(reason_id!=-1){
            if(!$('#downvoteicon-'+reason_id).length){
                $('#downvotebutton-'+reason_id).append('<i id="downvoteicon" class="fa fa-thumbs-down"/>');
            }else{
                $('#downvoteicon-'+reason_id).removeclass("fa-thumbs-o-down").addclass("fa-thumbs-down");
            }
            $('#downvotebutton-'+reason_id).prop('title', 'Click to remove your downvote');
            $('#issueform').remove();
        }else{
            if(!$('a[id^="downvotebutton"] > a[id^="downvoteicon"]').length){
                $('a[id^="downvotebutton"]').append('<i id="downvoteicon" class="fa fa-thumbs-o-down"/>');
            }
            if(!$('#issueform').length){
            $('#issues').append(
                '<form role="form" id="issueform">'+
                    '<h5>Submit a new issue for this dataset</h5>'+
                    '<div class="form-group">'+
                      '<label for="Reason">Issue:</label>'+
                      '<input type="text" class="form-control" id="reason">'+
                    '</div>'+
                    '<button type="submit" class="btn btn-default">Submit</button>'+
                    '<div id="succes" class="text-center hidden">Issue Submitted!</div>'+
                    '<div id="fail" class="text-center hidden">Can\'t submit issue </div>'+
                '</form>');
            setSubmitBehaviour();
            }
        }
    });
}

function doDownvote(rid){
    if(reason_id==rid){
        meth= 'DELETE';
        u = '<?php echo BASE_URL; ?>api_new/v1/xml/votes/down/d/<?php echo $this->id; ?>';
    }else{
        meth= 'POST';
        u = '<?php echo BASE_URL; ?>api_new/v1/xml/votes/down/d/<?php echo $this->id; ?>/'+rid
    }
    $.ajax({
        method: meth,
        url: u
    }).done(function(resultdata){
        reason_id = parseInt(resultdata.getElementsByTagName('reason_id').item(0).textContent);
        getDownvotes();
    }).fail(function(resultdata){

    });
}
<?php }} ?>

function refreshNrLikes(){
    $.ajax({
        method:'GET',
        url:'<?php echo BASE_URL; ?>api_new/v1/xml/votes/up/any/d/<?php echo $this->id; ?>'
        }).done(function(resultdata){
            if(resultdata.getElementsByTagName('like').length>0){
                var nrlikes = resultdata.getElementsByTagName('like').length;
                $('#likecount').html(nrlikes+" likes");
            }else{
                $('#likecount').html("0 likes");
            }
        }).fail(function(resultdata){
            $('#likecount').html("0 likes");
     });
 }

 function refreshNrDownloads(){
    $.ajax({
       method:'GET',
       url:'<?php echo BASE_URL; ?>api_new/v1/xml/downloads/any/d/<?php echo $this->id; ?>'
    }).done(function(resultdata){
       if(resultdata.getElementsByTagName('download').length>0){
           var nrdownloads = resultdata.getElementsByTagName('download').length;
           var totaldownloads = 0;
           for(var i=0; i<nrdownloads; i++){
               totaldownloads+=parseInt(resultdata.getElementsByTagName('download')[i].getElementsByTagName('count')[0].textContent);
           }
           $('#downloadcount').html("downloaded by "+nrdownloads+" people, "+totaldownloads+" total downloads");
       }else{
           $('#downloadcount').html("downloaded by 0 people, 0 total downloads");
       }
    }).fail(function(resultdata){
       $('#downloadcount').html("downloaded by 0 people, 0 total downloads");
    });
 }

function flipLikeHTML(){
    if(isliked){
        isliked = false;
        $('#likeicon').removeClass("fa-heart").addClass("fa-heart-o");
        $('#likebutton').prop('title', 'Click to like');
        $('#likebutton').attr('onclick', 'doLike(false)');
        var likecounthtml = $('#likecount').html();
        var nrlikes = parseInt(likecounthtml.split(" ")[0]);
        nrlikes = nrlikes-1;
        $('#likecount').html(nrlikes+" likes");
        var reachhtml = $('#reach').html();
        var reach = parseInt(reachhtml.split(" ")[0]);
        reach = reach-2;
        $('#reach').html(reach+" reach");
    }else{
        isliked = true;
        $('#likeicon').removeClass("fa-heart-o").addClass("fa-heart");
        $('#likebutton').prop('title', 'Click to unlike');
        $('#likebutton').attr('onclick', 'doLike(true)');
        var likecounthtml = $('#likecount').html();
        var nrlikes = parseInt(likecounthtml.split(" ")[0]);
        nrlikes = nrlikes+1;
        $('#likecount').html(nrlikes+" likes");
        var reachhtml = $('#reach').html();
        var reach = parseInt(reachhtml.split(" ")[0]);
        reach = reach+2;
        $('#reach').html(reach+" reach");
    }
}

function setSubmitBehaviour(){
    $("#issueform").submit(function(event){
       // cancels the form submission
       event.preventDefault();
       var reason = $("#reason").val();
       $("#reason").val('');
       $.ajax({
           type: 'POST',
           url: '<?php echo BASE_URL; ?>api_new/v1/xml/votes/down/d/<?php echo $this->id; ?>/'+reason
       }).done(function(resultdata){
           reason_id = parseInt(resultdata.getElementsByTagName('reason_id').item(0).textContent);
           getDownvotes();
           $("fail").addClass("hidden");
           $("#success").removeClass("hidden");
       }).fail(function(resultdata){
           $("fail").append(resultdata.getElementsByTagName("message")[0].textContent);
           $("fail").removeClass("hidden");
           $("#success").addClass("hidden");
       });
   });
}

function getDownvotes(){
    $('#issues_content').append('<i class="fa fa-spinner fa-pulse"/> Refreshing issues');
    $.ajax({
        method:'GET',
        url: '<?php echo BASE_URL; ?>api_new/v1/xml/votes/down/d/<?php echo $this->id; ?>'
    }).done(function(resultdata){
        if(resultdata.getElementsByTagName('downvotes').length>0){
            var dvotes = resultdata.getElementsByTagName('downvote');
            $('#issues_content').html("<tr><th>Issue</th><th>#Downvotes for this reason</th><th>By</th><th></th></tr>");
            for(var i=0; i<dvotes.length; i++){
                var id = dvotes[i].getElementsByTagName('reason_id')[0].textContent;
                maxreason = Math.max(id,maxreason);
                $('#issues_content').append('<tr id="issuerow-'+id+'">');
                $('#issuerow-'+id).append('<td>'+dvotes[i].getElementsByTagName('reason')[0].textContent+'</td>');
                $('#issuerow-'+id).append('<td>'+dvotes[i].getElementsByTagName('count')[0].textContent+'</td>');
                $('#issuerow-'+id).append('<td><a href="u/'+dvotes[i].getElementsByTagName('user_id')[0].textContent+'">User '+dvotes[i].getElementsByTagName('user_id')[0].textContent+'</a></td>');
                $('#issuerow-'+id).append('<td><a id="downvotebutton-'+id+'" class="loginfirst btn btn-link" onclick="doDownvote('+id+')" title="Click to agree"> </a></td>');
                $('#issues_content').append('</tr>');
            }
            if(reason_id!=-1){
                if(!$('#downvoteicon-'+reason_id).length){
                    $('#downvotebutton-'+reason_id).append('<i id="downvoteicon-'+reason_id+'" class="fa fa-thumbs-down"/>');
                }else{
                    $('#downvoteicon-'+reason_id).removeclass("fa-thumbs-o-down").addclass("fa-thumbs-down");
                }
                $('#downvotebutton-'+reason_id).prop('title', 'Click to remove your downvote');
                $('#issueform').remove();
            }else{
                for(var i=0; i<dvotes.length; i++){
                    var id = dvotes[i].getElementsByTagName('reason_id')[0].textContent;
                    if(!$('#downvotebutton-'+id).length){
                        $('#downvotebutton-'+id).append('<i id="downvoteicon-'+id+'" class="fa fa-thumbs-o-down"/>');
                    }
                }
                if(!$('#issueform').length){
                    $('#issues').append(
                        '<form role="form" id="issueform">'+
                            '<h5>Submit a new issue for this dataset</h5>'+
                            '<div class="form-group">'+
                              '<label for="Reason">Issue:</label>'+
                              '<input type="text" class="form-control" id="reason">'+
                            '</div>'+
                            '<button type="submit" class="btn btn-default">Submit</button>'+
                            '<div id="succes" class="text-center hidden">Issue Submitted!</div>'+
                            '<div id="fail" class="text-center hidden">Can\'t submit issue </div>'+
                        '</form>');
                    setSubmitBehaviour();
                }
            }
            $('#issues_content').append('<br>');
        }
    }).fail(function(resultdata){
        $('#issues_content').html("<tr><th>Issue</th><th>#Downvotes for this reason</th><th>By</th><th>Click to agree</th></tr>");
    });
    <?php
    if ($this->ion_auth->logged_in()) {
        if ($this->ion_auth->user()->row()->id != $this->data['uploader_id']) { ?>
        getYourDownvote();
    <?php }} ?>
}
