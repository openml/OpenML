/// SHARING

$(document).ready(function() {
  // bind form using ajaxForm
	$('#implementationForm').ajaxForm( {
		beforeSerialize: prepareImplementationDescriptionXML,
		success: implementationFormSubmitted,
		datatype: 'xml'
	} );

	$('.selectpicker').selectpicker();
});

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



function prepareImplementationDescriptionXML(form, options) {
	var fields =  ['name','description','creator','contributor','licence','language','installation_notes'];
	var implode = [false,false,true,true,false,false,false];

	var xml_header = '<oml:implementation xmlns:oml="http://openml.org/openml">'+"\n";
	var xml_footer = '</oml:implementation>'+"\n";
	var xml_content = prepareDescriptionXML('implementation',fields,implode);
	colsole.log(xml_header+xml_content+xml_footer)
	//$('#generated_input_implementation_description').val(xml_header+xml_content+xml_footer);
}


function prepareDescriptionXML(type,fields,implode) {
	var xml_content = '';
	for(i = 0; i < fields.length; i+=1) {
		field = fields[i];
		field_value = $('#input_'+type+'_'+field).val().trim();
		if(field_value != '') {
			if(implode[i] == false) {
				xml_content += "\t"+'<oml:'+field+'>'+field_value+'</oml:'+field+'>'+"\n";
			} else {
				xml_current = field_value.split(',');
				$.each(xml_current, function() {
					xml_content += "\t"+'<oml:'+field+'>'+this.trim()+'</oml:'+field+'>'+"\n";
				});
			}
		}
	}

	return xml_content;
}

function implementationFormSubmitted(responseText,statusText,xhr,formElement) {
	var errorCodes = new Array();
	errorCodes[161] = 'Please make sure that all mandatory fields are filled in. ';
	errorCodes[163] = 'Please make sure that all mandatory fields are filled in. ';
	errorCodes[169] = 'Please login first.';
	errorCodes[170] = 'Please login first.';
	formSubmitted(responseText,statusText,xhr,formElement,'Implementation',errorCodes);
}

function formSubmitted(responseText,statusText,xhr,formElement,type,errorCodes) {
	var message = '';
	var status = '';
	if($('oml\\:id',responseText).length) {
		message = type + ' uploaded with ID ' + $('oml\\:id',responseText).text();
		status = 'alert-success';
	} else {
		var errorcode = $('oml\\:code',responseText).text();
		var errormessage = $('oml\\:message',responseText).text();
		status = 'alert-warning';
		if(errorcode in errorCodes) {
			message = errorCodes[errorcode];
		} else {
			message = 'Errorcode ' + errorcode + ': ' + errormessage;
		}
	}
	$('#response'+type+'Txt').removeClass();
	$('#response'+type+'Txt').addClass('alert');
	$('#response'+type+'Txt').addClass(status);
	$('#response'+type+'Txt').html(message);
}


/// DETAIL
<?php
if(false !== strpos($_SERVER['REQUEST_URI'],'/f/')) {
?>

function fetch_params_succes(xml, setup_id) {
    inner = '<table><thead><tr><th style="border: 0">Parameter Name</th><th style="border: 0">Description</th><th style="border: 0">Default Value</th><th style="border: 0">Chosen value</th></tr></thead><tbody>';
	var xmlDocument = $(xml);

	$(xmlDocument).find('oml\\:parameter').each(function(){
		inner += '<tr>';
		inner += '<td style="border: 0">' + $(this).find('oml\\:parameter_name').text() + '</td>';
		inner += '<td style="border: 0">' + $(this).find('oml\\:general_name').text() + '</td>';
		inner += '<td style="border: 0">' + $(this).find('oml\\:default_value').text() + '</td>';
		inner += '<td style="border: 0">' + $(this).find('oml\\:value').text() + '</td>';
		inner += '</tr>';
	});
	inner += '</tbody></table>';
	$('.setup-record-'+setup_id).html(inner);
}


// Formating function for row details
function fnFetchParams ( oTable, row, column )
{
    var aData = oTable.fnGetData( row );

	$.ajax({
		url: '<?php echo BASE_URL; ?>api/v1/setup/'+aData[column],
		context: document.body,
		dataType: 'text'
	}).done(function(xml){fetch_params_succes(xml,aData[column])});

    return '<div class="setup-record-'+aData[column]+'" style="margin: 0px 20px;">Loading...</div>';
}

var oTableRunsShowAll = false;
var evaluation_measure = "<?php echo $this->current_measure; ?>";
var current_task = "<?php echo $this->current_task; ?>";
var oTableRuns = false;

/// RESULT TABLE
function buildTable(dataSet) {
    $('#flowtable').removeAttr('width').DataTable( {
        dom: 'Bfrtip',
        buttons: ['copy', 'csv', 'excel', 'pdf', 'print'],
        data: dataSet,
        columns: [
            { title: "Run ID" },
            { title: "Dataset", width: 100},
            { title: evaluation_measure },
            { title: "Hyperparameters" }
        ],
        scrollY: "600px",
        scrollCollapse: false,
        paging: false,
        scrollX: "100%",
        destroy: true,
        order: [[ 2, "desc" ]],
    } );
    $('#table-spinner').css('display','none');
}


  /// RESULT QUERY

function showData(){
  client.search({
    index: 'openml',
    type: 'run',
    size: '10000',
    body: {
  		_source: [ "run_id", "run_task.source_data.name", "run_task.source_data.data_id", "run_flow.parameters.parameter", "run_flow.parameters.value", "uploader", "evaluations.evaluation_measure", "evaluations.value" ],
      query: {
        bool: {
        	must: [
                    {
                        term: {
                            "run_flow.flow_id": <?php echo $this->id; ?>
                        }
                    },
                    {
                        match: {
                            "run_task.tasktype.name": current_task
                        }
                    }
                ]
  	}
      },
      sort: [
      {
        "evaluations.value": {
          "order": "desc",
          "nested_path": "evaluations",
          "nested_filter": {
            "term": {
              "evaluations.evaluation_measure": evaluation_measure
            }
          }
        }
      }
    ]
    }
  }).then(function (resp) {
    var data = resp.hits.hits;
    if(data == null){
      $('#runcount').html('0');
      $('#code_result_visualize').html("No data to show.");
    } else {
      $('#runcount').html(data.length);
      redrawchart(data);
      buildTable(buildTableData(data));
    }
  }, function (err) {
	    console.log(err.message);
	}).fail(function(){ $('#code_result_visualize').html("No data to show."); console.log('failure', arguments); });
}

function buildTableData(data){
	tabledata = [];
	for(key in data){
		o = data[key]['_source'];
		eval = o['evaluations'].filter(function(obj){return obj.evaluation_measure == evaluation_measure;})[0];
		if(eval){
			tabledata.push(['<a href="r/'+o['run_id']+'">'+o['run_id']+'</a>','<a href="d/'+o['run_task']['source_data']['data_id']+'">'+o['run_task']['source_data']['name']+'</a>',eval['value'],o['run_flow']['parameters'].map(shortParam)]);
		}
	}
	return tabledata;
}

function shortParam(a){
	par = a.parameter.split('_').pop();
	val = a.value;
	if(val.length>10){
		val = val.split('.').pop();
	}
	return par+':'+val;
}

function updateTableHeader(){
	$("#value").html(evaluation_measure.charAt(0).toUpperCase()+evaluation_measure.slice(1).replace(/_/g,' '));
}

$(document).ready(function() {
  updateTableHeader();
  showData();
});

function redrawchart(data){
categoryMap = {};
options = {
            chart: {
                renderTo: 'code_result_visualize',
                type: 'scatter',
                zoomType: 'xy',
		spacingTop: 40,
                events: {
                    load: function (event) {
                        $('.tip').tooltip();
                    }
                }
            },
            yAxis: {
                title: {
                    enabled: false,
                    text: 'Datasets'
                },
                categories: [],
		labels: {
                  formatter: function() {
                    return '<a href="d/'+ categoryMap[this.value] +'">'+ this.value +'</a>';
                  },
        	  useHTML: true
		},
                alternateGridColor: '#eeeeff',
                gridLineColor: '#eeeeff',
		reversed: true
            },
            xAxis: {
                title: {
                    text: evaluation_measure.charAt(0).toUpperCase()+evaluation_measure.slice(1).replace(/_/g,' ')
                },
                gridLineWidth: 1
            },
            title: {
              text: 'Evaluations per dataset (multiple parameter settings)'
            },
      	    subtitle: {
      	        text: 'every point is a run, click for details'
      	    },
            legend: {
                enabled: false
            },
            credits: {
                enabled: false
            },
            plotOptions: {
                scatter: {
                    marker: {
                        states: {
                            hover: {
                                enabled: true,
                                lineColor: 'rgb(100,100,100)'
                            }
                        },
			symbol: 'diamond'
                    },
                    states: {
                        hover: {
                            marker: {
                                enabled: false
                            }
                        }
                    }
		}
            },
            tooltip:{
                followTouchMove: false,
                formatter:function(){
                    return '<p>Task data:<b> '+this.series.yAxis.categories[this.y]+'</b><br>'+ this.series.xAxis.axisTitle.element.textContent + '<b>: ' + this.x+'</b><br>'+ ((typeof this.point.options.z !== 'undefined') ? 'Parameter '+selected_parameter+': <b>'+this.point.options.z+'</b>' : '<i>No parameter selected. Click for info.</i>') + '</p>';
                }
            },
            series: [{
		turboThreshold: 0,
		color: 'rgba(119, 152, 191, .5)',
                data: [],
		point: {
                    events: {
                        //click: function(){$('#runModal').modal({remote: 'r/' + this.r + '/html'}); $('#runModal').modal('show');}
                        click: function() { window.open('https://www.openml.org/r/' + this.r);}

                    }
                }
            }]
        };

    var catcount = 0;
  	var map = {};
  	options.series[0].data = [];
  	options.yAxis.categories = [];
  	var parvaluenumeric = true;
  	var parvalues = [];
  	var heatmap = new Rainbow();

  	if (typeof selected_parameter !== 'undefined' && selected_parameter != 'none'){
  		if (isNaN(data[0]['_source']['run_flow']['parameters'][0]['value'])){
  			parvaluenumeric = false;
  		}
  		for(var i=0;i<data.length;i++){
        var run = data[i]['_source'];
        var parval = getPar(run['run_flow']['parameters'],selected_parameter);
  			if (parvalues.indexOf(parval)<0){
  				parvalues.push(parval);
  			}
  		}
   		if (parvaluenumeric){
  			if (parvalues.length > 2){
  				var min = Math.min.apply( Math, parvalues );
  				var max = Math.max.apply( Math, parvalues );
  				if(min<max){
  					heatmap.setNumberRange(min,max);
  				}
  			} else {
  				parvalues.sort(function(a,b){return parseInt(a)-parseInt(b)});
  				heatmap.setNumberRange(0, parvalues.length);
  			}
  			heatmap.setSpectrum('blue','green','yellow','orange','red');
  		} else {
  			heatmap.setNumberRange(0, parvalues.length);
  		}

  	}
  	for(var i=0;i<data.length;i++){
      var run = data[i]['_source'];
      var dataset = run['run_task']['source_data'];
  		if (!(dataset['name'] in map)){
  			map[dataset['name']] = catcount++;
  			options.yAxis.categories.push(dataset['name']);
  			categoryMap[dataset['name']]= dataset['data_id'];
  		}
  		if (typeof selected_parameter !== 'undefined' && selected_parameter != 'none'){
  			var col = "#000000";
        var parval = getPar(run['run_flow']['parameters'],selected_parameter);
  			if (parvaluenumeric && parvalues.length > 2){
  				col = heatmap.colourAt(parval);
  			} else {
  				col = heatmap.colourAt(parvalues.indexOf(parval));
  			}
  			options.series[0].data.push({x: parseFloat(getEval(run['evaluations'],evaluation_measure)), y: map[dataset['name']], z: parval, r: run['run_id'], fillColor: 'rgba('+parseInt(col.substring(0,2),16)+','+parseInt(col.substring(2,4),16)+','+parseInt(col.substring(4,6),16)+',0.5)'});
  		} else {
  			options.series[0].data.push({x: parseFloat(getEval(run['evaluations'],evaluation_measure)), y: map[dataset['name']], r: run['run_id']});
  		}
  	}
  	options.chart.height = options.yAxis.categories.length*18+120;
  	coderesultchart = new Highcharts.Chart(options);
    $(".dataTables_scrollHeadInner").css({"width":"100%"});
}

function getEval(arr, value) {
  if(arr != null){
    var result  = arr.filter(function(o){return o.evaluation_measure == value;} );
    return result ? (result[0] ? result[0]['value'] : null) : null; // or undefined
  }
}

function getPar(arr, value) {
  var result  = arr.filter(function(o){return o.parameter == value;} );
  return result ? (result[0] ? result[0]['value'] : null) : null; // or undefined
}

$(document).on('click', '.openRunModal', function(){updateRunModal($(this).data('id'))});

/// GAMIFICATION

var isliked;
var reason_id = -1;
var maxreason = -1;
<?php
if ($this->ion_auth->logged_in()) {
    if ($this->ion_auth->user()->row()->id != $this->flow['uploader_id']) {?>

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
        url: '<?php echo BASE_URL; ?>api_new/v1/xml/votes/up/f/<?php echo $this->id ?>'
    }).done(function(resultdata){
        if(resultdata.getElementsByTagName('like').length>0){
            //changes already done
        }else{
            //undo changes
            flipLikeHTML();
        }
    }).fail(function(resultdata){
        //undo changes
        flipLikeHTML();
    });
    //change as if the api call is succesful
    flipLikeHTML();
}

function doDownload(){
    $.ajax({
            method: 'POST',
            url: '<?php echo BASE_URL; ?>api_new/v1/xml/downloads/f/<?php echo $this->id ?>'
           }
    ).always(function(){
        refreshNrDownloads();
    });
}

function doDownvote(rid){
    if(reason_id==rid){
        meth= 'DELETE';
        u = '<?php echo BASE_URL?>api_new/v1/xml/votes/down/f/<?php echo $this->id ?>';
    }else{
        meth= 'POST';
        u = '<?php echo BASE_URL?>api_new/v1/xml/votes/down/f/<?php echo $this->id ?>/'+rid
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

function getYourDownvote(){
    $.ajax({
        method:'GET',
        url: '<?php echo BASE_URL; ?>api_new/v1/xml/votes/down/<?php echo $this->ion_auth->user()->row()->id; ?>/f/<?php echo $this->id; ?>'
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
<?php }}?>

function refreshNrLikes(){
    $.ajax({
        method:'GET',
        url:'<?php echo BASE_URL; ?>api_new/v1/xml/votes/up/any/f/<?php echo $this->id ?>'
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
       url:'<?php echo BASE_URL; ?>api_new/v1/xml/downloads/any/f/<?php echo $this->id ?>'
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
           url: '<?php echo BASE_URL; ?>api_new/v1/xml/votes/down/f/<?php echo $this->id; ?>/'+reason
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
    $('#issues_content').append('<i class="fa fa-spinner fa-pulse"></i> Refreshing issues');
    $.ajax({
        method:'GET',
        url: '<?php echo BASE_URL?>api_new/v1/xml/votes/down/f/<?php echo $this->id ?>'
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
        if ($this->ion_auth->user()->row()->id != $this->flow['uploader_id']) {?>
    getYourDownvote();
    <?php }} ?>
}

<?php
}
?>
