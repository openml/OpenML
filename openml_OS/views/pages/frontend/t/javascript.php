/// DETAIL
<?php
if(false !== strpos($_SERVER['REQUEST_URI'],'/t/') and false === strpos($_SERVER['REQUEST_URI'],'/t/type')) {
?>
$.fn.dataTable.ext.errMode = 'none';


function highlight(){
	setTimeout(function(){ highlightnow(); }, 500);
}
function highlightnow(){
	$("#detail-btn").removeClass("btn-info");
	$("#people-btn").removeClass("btn-info");
	$("#runs-btn").removeClass("btn-info");
	$("#add-btn").removeClass("btn-info");
	if($( "#detail" ).hasClass( "active" )){
		$("#detail-btn").addClass("btn-info");}
	if($( "#taskruns" ).hasClass( "active" )){
		$("#runs-btn").addClass("btn-info");}
	if($( "#people" ).hasClass( "active" )){
		$("#people-btn").addClass("btn-info");}
	if($( "#submit" ).hasClass( "active" )){
		$("#add-btn").addClass("btn-info");}
}

$(function(){
highlight();
});

var oTableRunsShowAll = false;
var evaluation_measure = "<?php echo $this->current_measure; ?>";
var latestOnly = true;
var current_task = "<?php echo $this->task_id; ?>";
var current_task_type = "<?php echo $this->task['tasktype']['tt_id']; ?>";
var higherIsBetter = true;
var oTableRuns = false;

/// CHANGE EVALUATION MEASURE

function updateTableHeader(){
	$("#value").html(evaluation_measure.charAt(0).toUpperCase()+evaluation_measure.slice(1).replace(/_/g,' '));
	if(evaluation_measure.indexOf("error") > -1 || evaluation_measure.indexOf("cost") > -1)
		higherIsBetter = false;
	else
		higherIsBetter = true;
}

/// TIMELINE

function redrawtimechart(){
var colors = ['rgba(166, 206, 227, .6)','rgba(51, 160, 44, .6)','rgba(251, 154, 153, .6)','rgba(253, 191, 111, .6)','rgba(202, 178, 214, .6)','rgba(255, 255, 153, .6)','rgba(177, 89, 40, .6)','rgba(31, 120, 180, .6)','rgba(178, 223, 138, .6)','rgba(227, 26, 28, .6)','rgba(255, 127, 0, .5)','rgba(106, 61, 154, .6)'];

options2 = {
            chart: {
                renderTo: 'data_result_time',
                type: 'scatter',
								pinchType: 'x',
								spacingTop: 40,
                events: {
                    load: function (event) {
                        $('.tip').tooltip();
                    }
                }
            },
	    title: {
	        text: 'Contributions over time'
	    },
	    subtitle: {
	        text: 'every point is a run, click for details'
	    },
	    xAxis: {
								type: 'datetime',
                title: {
                    text: 'Date'
                }
            },
            yAxis: {
                title: {
                    text: evaluation_measure.charAt(0).toUpperCase()+evaluation_measure.slice(1).replace(/_/g,' ')
                }
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
    		backgroundColor: '#FFFFFF',
		useHTML: true,
                formatter:function(){
                    return '<div>'+((current_task_type==6) ? '': 'Flow:<b> '+this.point.options.f+'</b><br>')+this.series.yAxis.axisTitle.element.textContent + '<b>: ' + this.y+'</b><br>'+ ((typeof this.point.options.t !== 'undefined') ? 'Training time (seconds): <b>'+this.point.options.t+'</b><br>': '')+'Uploader: ' + this.point.options.u +'<br>' + ((typeof this.point.options.z !== 'undefined') ? 'Parameter '+selected_parameter+': <b>'+this.point.options.z+'</b>' : ((current_task_type==6) ? '' : '<i>Click for more info</i>')) + '</div>';
                }
            },
            series: []
        };

client.search({
  index: 'openml',
  type: 'run',
  size: '5000',
	body: {
		_source: [ "run_id", "date", "run_flow.name", "run_flow.flow_id", "uploader", "uploader_id", "evaluations.evaluation_measure", "evaluations.value" ],
		query: {
			 bool: {
				 must: [{term: {'run_task.task_id': current_task }},
							 <?php if($this->task['visibility'] == 'private' and !$this->is_admin){?>
								{term: {'uploader_id': <?php echo $this->user_id; ?>}},
							 <?php }?>
								{nested: {path: "evaluations",
													query: {exists: {field: "evaluations"}}}}]
			 }
		},
		sort: { 'date' : 'asc' }
	}
}).then(function (resp) {
        var data = resp.hits.hits;
	var usercount = 1;
	var map = {};
	var d=[];
	var names=[];
	var leaders=[];
	var sortedLeaders=[];
	var rankedLeaders=[];
  var pairs = new Set();
	d[0] = [];
	names[0] = 'frontier';

	var embargo_end_date = null;
	<?php if(array_key_exists('embargo_end_date', $this->task)){
		echo 'embargo_end_date = Date.parse("'. str_replace(" ","T",$this->task['embargo_end_date']) .'");';
	}?>

	for(var i=0;i<data.length;i++){
		var run = data[i]['_source'];
		var evals = run['evaluations'];

		if (!(run['uploader'] in map)){
			map[run['uploader']] = usercount++;
			d[map[run['uploader']]] = [];
			names[map[run['uploader']]] = run['uploader'];
			if(higherIsBetter)
				leaders.push({rank: Infinity, name: run['uploader'], userId: run['uploader_id'], topRun: run['run_id'], topScore: 0, topTime: run['date'], entries: 0, highRank: Infinity});
			else
				leaders.push({rank: Infinity, name: run['uploader'], userId: run['uploader_id'], topRun: run['run_id'], topScore: Infinity, topTime: run['date'], entries: 0, highRank: Infinity});
		}
		if(typeof getEval(evals,evaluation_measure) !== 'undefined'){
			var dat = Date.parse(run['date'].replace(" ", "T"));
			var e = getEval(evals,evaluation_measure);
			if(e!== null){
				e = parseFloat(e);
			d[map[run['uploader']]].push({x: dat, y: e, f: run['run_flow']['name'], r: run['run_id'], u: run['uploader'], t: getEval(evals,'build_cpu_time')} );
			if(d[0].length==0 || (higherIsBetter && e > d[0][d[0].length-1]['y']) || (!higherIsBetter && e < d[0][d[0].length-1]['y'])){
				d[0].push({x: dat, y: e, f: run['run_flow']['name'], r: run['run_id'], u: run['uploader'], t: getEval(evals,'build_cpu_time')});
			}
			console.log(dat+"  "+embargo_end_date);
			//check if submission is new or submitted during closed phase
			if(!pairs.has(run['run_flow']['name']+e) || (embargo_end_date !== null && embargo_end_date > dat)){
				pairs.add(run['run_flow']['name']+e);
				var l = leaders[map[run['uploader']]-1];
				l.entries = l.entries+1;
				if((higherIsBetter && l.topScore<e) || (!higherIsBetter && l.topScore>e)){ //if new best score for this person
					l.topScore = e;
					l.topTime = +new Date(run['date']);
					l.topRun = run['run_id'];
					sortedLeaders = leaders.slice().sort(function(a,b){
						if(a.topScore == b.topScore){return a.topTime-b.topTime;}
						else if(higherIsBetter){return b.topScore-a.topScore;}else{return a.topScore-b.topScore;}});
					rankedLeaders = leaders.slice().map(function(v){return sortedLeaders.indexOf(v)+1 });
					if(l.highRank > rankedLeaders[map[run['uploader']]-1])
						l.highRank = rankedLeaders[map[run['uploader']]-1];
				}
			}}
		}
	}
	options2.chart.height = 500;

	for(var i=0;i<sortedLeaders.length;i++){
		 sortedLeaders[i].rank = i+1;
		 sortedLeaders[i].name = '<a href=u/'+sortedLeaders[i].userId+'>'+sortedLeaders[i].name+'</a>';
		 sortedLeaders[i].topScore = '<a href=r/'+sortedLeaders[i].topRun+'>'+sortedLeaders[i].topScore+'</a>';
		}

	for(var i=0;i<usercount;i++){
		options2.series[i] = {};
		if(i==0){options2.series[i].type= 'line';}
		options2.series[i].turboThreshold = 5000;
		options2.series[i].name = names[i];
		options2.series[i].data = d[i];
		options2.series[i].color = colors[i%9];
		options2.series[i].fillOpacity = 0.25;
		<?php if($this->task['tasktype']['tt_id']!=6){ ?>
		options2.series[i].point = {
                    events: {
                        //click: function(){$('#runModal').modal({remote: 'r/' + this.r + '/html'}); $('#runModal').modal('show');}
												click: function() { window.open('https://www.openml.org/r/' + this.r);}
										}
                };
		<?php } ?>
	}
	timechart = new Highcharts.Chart(options2);
	leaderboard(sortedLeaders);

}, function (err) {
    console.trace(err.message);
});
}

/// LEADERBOARD

function leaderboard(data){
	$('#leaderboard').dataTable( {
		"processing": true,
		"scrollY": "600px",
		"scrollCollapse": true,
		"paging":         false,
		"iDisplayLength" : 10,
		"bSort" : true,
		"bInfo": false,
		"data": data,
		"bDestroy" : true,
		"columns": [
		{ "data" : "rank" },
		{ "data" : "name" },
		{ "data" : "topScore" },
		{ "data" : "entries" },
		{ "data" : "highRank" }
		]
	} );
}

/// RESULT QUERY

function showData(){
	updateTableHeader();
	client.search({
	  index: 'openml',
	  type: 'run',
	  size: 0,
	  body: {
	    query: {
				 bool: {
					 must: [{term: {'run_task.task_id': current_task }},
					 			 <?php if($this->task['visibility'] == 'private' and !$this->is_admin){?>
									{term: {'uploader_id': <?php echo $this->user_id; ?>}},
								 <?php }?>
					 				{nested: {path: "evaluations",
														query: {exists: {field: "evaluations"}}}}]
				 }
			},
			aggs : {
					'flows' : {
							terms : {
								field : "run_flow.flow_id",
								size: 1000
							},
							aggs : {
									'top_score': {
										  top_hits: {
												_source: [ "run_id", "run_flow.name", "run_flow.parameters", "run_flow.flow_id", "uploader", "evaluations.evaluation_measure", "evaluations.value" ],
												sort: [
										    {
										      "evaluations.value": {
										        "order": (higherIsBetter ? "desc" : "asc"),
										        "nested_path": "evaluations",
										        "nested_filter": {
										          "term": {
										            "evaluations.evaluation_measure": evaluation_measure
										          }
										        }
										      }
										    }
										    ],
												size: 1000
											}
									}
							}
					}
			}
		}
	}).then(function (resp) {
		data = [];
		buckets = resp.aggregations.flows.buckets;
		buckets.sort(function(a, b) {
			return parseFloat(b['top_score']['hits']['hits'][0]['sort'][0]) - parseFloat(a['top_score']['hits']['hits'][0]['sort'][0]);
		});
		if(!higherIsBetter){
			buckets.reverse();
		}
		for(flowid in buckets){
			data.push(...buckets[flowid].top_score.hits.hits);
		}
		redrawchart(data);
		buildTable(buildTableData(data));
	}, function (err) {
	    console.trace(err.message);
	});
}

function buildTableData(data){
	tabledata = [];
	for(key in data){
		try{
		o = data[key]['_source'];
		eval = o['evaluations'].filter(function(obj){return obj.evaluation_measure == evaluation_measure;})[0];
		if(eval){
			tabledata.push(['<a href="r/'+o['run_id']+'">'+o['run_id']+'</a>','<a href="f/'+o['run_flow']['flow_id']+'">'+o['run_flow']['name']+'</a>',eval['value'],(o['run_flow']['parameters'] != null ? o['run_flow']['parameters'].map(shortParam) : '')]);
		}
	} catch(e){}
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

  /// RESULT TABLE
function buildTable(dataSet) {
	    $('#tasktable').removeAttr('width').DataTable( {
					dom: 'Bfrtip',
					buttons: ['copy', 'csv', 'excel', 'pdf', 'print'],
	        data: dataSet,
	        columns: [
	            { title: "Run ID" },
	            { title: "Flow", width: 100},
	            { title: evaluation_measure },
	            { title: "Hyperparameters" }
	        ],
					scrollY: "600px",
					scrollCollapse: true,
					paging: false,
					scrollX: "100%",
					destroy: true,
					order: [[ 2, "desc" ]]
	    } );
			$('#table-spinner').css('display','none');
	}

	/// RESULT CHART

function redrawchart(data){
categoryMap = {};
options = {
            chart: {
                renderTo: 'data_result_visualize',
                type: 'scatter',
		pinchType: 'x',
		spacingTop: 40,
                events: {
                    load: function (event) {
                        $('.tip').tooltip();
                    }
                }
            },
	    title: {
	        text: 'Evaluations per flow (multiple parameter settings)'
	    },
	    subtitle: {
	        text: 'every point is a run, click for details'
	    },
	    yAxis: {
                title: {
                    enabled: false,
                    text: 'Flows'
                },
                categories: [],
		labels: {
                  formatter: function() {
		    var lab = this.value.length > 50 ? this.value.substring(0, 25) + '...' +  this.value.substring(this.value.length - 25) : this.value;
                    return '<a class="hccategory tip" href="f/'+ categoryMap[this.value] +'" title="' + this.value + '">'+ lab +'</a>';
                  },
        	  useHTML: true
		},
                alternateGridColor: '#eeeeff',
                gridLineColor: '#eeeeff',
		reversed: true
            },
            xAxis: [{
                title: {
                    text: evaluation_measure.charAt(0).toUpperCase()+evaluation_measure.slice(1).replace(/_/g,' ')
                },
                gridLineWidth: 1
            },{
								linkedTo: 0,
                title: {
                    text: evaluation_measure.charAt(0).toUpperCase()+evaluation_measure.slice(1).replace(/_/g,' ')
                },
                gridLineWidth: 1,
								opposite: true
            }],
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
    		backgroundColor: '#FFFFFF',
		useHTML: true,
                formatter:function(){
                    return '<div>Flow:<b> '+this.series.yAxis.categories[this.y]+'</b><br>'+ this.series.xAxis.axisTitle.element.textContent + '<b>: ' + this.x+'</b><br>'+ ((typeof this.point.options.t !== 'undefined') ? 'Training time (seconds): <b>'+this.point.options.t+'</b><br>': '')+'Uploader: ' + this.point.options.u +'<br>' + ((typeof this.point.options.z !== 'undefined') ? 'Parameter '+selected_parameter+': <b>'+this.point.options.z+'</b>' : '<i>Click for more info</i>') + '</div>';
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
	var d=[];
	var c=[];

	for(var i=0;i<data.length;i++){
		var run = data[i]['_source'];
		var flow = run['run_flow'];
		var evals = run['evaluations'];

		if (!(flow['name'] in map)){
			map[flow['name']] = catcount++;
			categoryMap[flow['name']]= flow['flow_id'];
			c.push(flow['name']);
		}
		var sc = getEval(evals,evaluation_measure);
		if(sc !== null){
		d.push({x: parseFloat(sc), y: map[flow['name']], r: run['run_id'], u: run['uploader'], t: parseFloat(getEval(evals,'usercpu_time_millis_training'))/1000} );
	}}

	options.yAxis.categories = c;
	options.series[0].data = d;
	options.chart.height = c.length*25+120;

	coderesultchart = new Highcharts.Chart(options);
}

function getEval(arr, value) {

  var result  = arr.filter(function(o){return o.evaluation_measure.toLowerCase() == value.toLowerCase();} );

  return result ? (result[0] ? result[0]['value'] : null) : null; // or undefined

}

/// LEARNING CURVES

function redrawCurves(){
	var options = [];
	var colors = ['#4572A7', '#AA4643', '#89A54E', '#80699B', '#3D96AE', '#DB843D', '#92A8CD', '#A47D7C', '#B5CA92'];

        //build options
	options.chart = {};
	options.chart.renderTo='learning_curve_visualize';
	//options.chart.type='line';
	options.chart.width = $('#learning_curve_visualize').width();
	options.chart.height = $('#learning_curve_visualize').width()/2;

	options.credits = {enabled: false};
	options.title = {text: ' '};
	options.xAxis = {};
	options.xAxis.title = 'Sample size';
	options.series = [];
	options.yAxis = {};
	options.legend = {};

	options.tooltip = {
    backgroundColor: '#FFFFFF',
		useHTML: true,
		formatter: function() {return '<b>'+ this.series.name +'</b><br/>'+	this.x +' '+ this.y;}};

  var implementationConstraint = '';

  var sql =
    'SELECT `e`.`sample_size`, concat_ws("_",`i`.`name`,`i`.`version`)  AS `name`, `r`.`setup`, avg(`e`.`value`) as `score`, stddev(`e`.`value`) as `stdev`, `i`.`name` as `iname` FROM `run` `r`, `evaluation_sample` `e`, `algorithm_setup` `a`, `implementation` `i`, `task` `t` WHERE `e`.`function` = "'+evaluation_measure+'" AND `t`.`ttid` = 3 AND `r`.`rid` = `e`.`source` AND `r`.`setup` = `a`.`sid` AND `a`.`implementation_id` = `i`.`id` AND `r`.`task_id` = `t`.`task_id` AND `t`.`task_id` = '+<?php echo $this->task_id; ?>+' GROUP BY `e`.`sample`, `r`.`setup` ORDER BY `sample` ASC, `name` DESC';
  if(latestOnly){
    sql = 'select * from ('+sql+') as a group by iname, sample_size order by sample_size';
  }

  var query =  encodeURI("<?php echo BASE_URL; ?>"+"api_query/?q="+sql, "UTF-8");

  $.getJSON(query,function(jsonData){
  var data = jsonData.data;
	var setupcount = 0;
	var map = {}; // setup -> name
	var names = [];
	var ranges = [];
	var averages = [];
	for(var i=0;i<data.length;i++){
		if (!(data[i][2] in map)){
			map[data[i][2]] = setupcount++;
			names.push(data[i][1]);
			ranges.push([]);
			averages.push([]);
			options.series.push({});
			options.series.push({});
		}
		averages[map[data[i][2]]].push([parseFloat(data[i][0]), parseFloat(data[i][3])]);
		ranges[map[data[i][2]]].push([parseFloat(data[i][0]), parseFloat(data[i][3]) - parseFloat(data[i][4]), parseFloat(data[i][3]) + parseFloat(data[i][4])]);
	}
	for(var i=0;i<setupcount;i++){
		options.series[i*2].name = names[i];
		options.series[i*2].data = averages[i];
		options.series[i*2].color = colors[i%9];
		options.series[i*2].zIndex = 1;
		options.series[i*2].marker = {};
		options.series[i*2].marker.lineWidth = 1;
		options.series[i*2].marker.lineColor = colors[i%9];
		options.series[i*2].marker.fillColor = colors[i%9];
		options.series[i*2+1].name = 'range';
		options.series[i*2+1].data = ranges[i];
		options.series[i*2+1].type = 'arearange';
		options.series[i*2+1].linkedTo = ':previous';
		options.series[i*2+1].color = colors[i%9];
		options.series[i*2+1].fillOpacity = 0.3;
		options.series[i*2+1].zIndex = 0;
		options.series[i*2+1].lineWidth = 0;
	}
	coderesultchart = new Highcharts.Chart(options);

}).fail(function(){ console.log('failure', arguments); });

}

/// LOAD CHARTS ASYNCHRONOUSLY
setTimeout(function() {
	<?php if($this->task['tasktype']['name'] == 'Learning Curve')
		echo 'redrawCurves();';
				else
		echo 'showData(); redrawtimechart();';
	?>
}, 0);

/// BOOTSTRAP STUFF
$(document).ready(function() {
	$('.pop').popover();
	$('.selectpicker').selectpicker();
});


/// GAMIFICATION AND ISSUE REPORTING

//if ($this->ion_auth->user()->row()->id != $this->task['uploader_id'])

<?php if ($this->ion_auth->logged_in()) { ?>

var isliked;
var reason_id = -1;
var maxreason = -1;
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
        url: '<?php echo BASE_URL?>api_new/v1/xml/votes/up/t/<?php echo $this->id ?>'
    }).done(function(resultdata){
        if(resultdata.getElementsByTagName('like').length>0){
            //changes already done
        }else{
            //undo changes
            console.log(resultdata);
            flipLikeHTML();
        }
    }).fail(function(resultdata){
        //undo changes
        console.log(resultdata);
        flipLikeHTML();
    });
    //change as if the api call is succesful
    flipLikeHTML();
}

function doDownload(){
    $.ajax({
            method: 'POST',
            url: '<?php echo BASE_URL?>api_new/v1/xml/downloads/t/<?php echo $this->id ?>'
           }
    ).always(function(){
        refreshNrDownloads();
    });
}


function getYourDownvote(){
    $.ajax({
        method:'GET',
        url: '<?php echo BASE_URL; ?>api_new/v1/xml/votes/down/<?php echo $this->ion_auth->user()->row()->id; ?>/t/<?php echo $this->id; ?>'
    }).done(function(resultdata){
        reason_id = resultdata.getElementsByTagName('oml:value')[0].textContent;
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
        u = '<?php echo BASE_URL?>api_new/v1/xml/votes/down/t/<?php echo $this->id ?>';
    }else{
        meth= 'POST';
        u = '<?php echo BASE_URL?>api_new/v1/xml/votes/down/t/<?php echo $this->id ?>/'+rid
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

function refreshNrLikes(){
    $.ajax({
        method:'GET',
        url:'<?php echo BASE_URL; ?>api_new/v1/xml/votes/up/any/t/<?php echo $this->id ?>'
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
       url:'<?php echo BASE_URL; ?>api_new/v1/xml/downloads/any/t/<?php echo $this->id ?>'
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
           url: '<?php echo BASE_URL; ?>api_new/v1/xml/votes/down/t/<?php echo $this->id; ?>/'+reason
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
        url: '<?php echo BASE_URL?>api_new/v1/xml/votes/down/t/<?php echo $this->id ?>'
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
	}
    <?php if ($this->ion_auth->logged_in()) {?>
    getYourDownvote();
    <?php } ?>

<?php }?> //logged in
<?php }?>
