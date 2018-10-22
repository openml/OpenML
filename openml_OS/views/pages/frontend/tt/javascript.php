/// DETAIL
<?php
if(false !== strpos($_SERVER['REQUEST_URI'],'/t/') and false === strpos($_SERVER['REQUEST_URI'],'/t/type')) {
?>

var oTableRunsShowAll = false;
var evaluation_measure = "<?php echo $this->current_measure; ?>";
var latestOnly = true;
var current_task = "<?php echo $this->task_id; ?>";
var current_task_type = "<?php echo $this->record['type_id']; ?>";
var higherIsBetter = true;
var oTableRuns = false;

$(document).ready(function() {
	$('.pop').popover();
	$('.selectpicker').selectpicker();
});



$(document).ready(function() {
    <?php echo simple_datatable('oTableGeneral','#datatable_general'); ?>
    //Initialse DataTables, with no sorting on the 'details' column
    oTableRuns = $('#datatable_main').dataTable( {
		"bServerSide": true,
		"sAjaxSource": "api_query/table_feed",
		"sServerMethod": "POST",
		"fnServerParams": function ( aoData ) {
			if(oTableRunsShowAll) {
				<?php echo array_to_parsed_string($this->dt_main_all, "aoData.push( { 'value': '[VALUE]', 'name' : '[KEY]' } );\n" ); ?>
			} else {
				<?php echo array_to_parsed_string($this->dt_main, "aoData.push( { 'value': '[VALUE]', 'name' : '[KEY]' } );\n" ); ?>
			}
			aoData.push( { 'value': 'AND function = "'+evaluation_measure+'" AND r.task_id = '+current_task, 'name' : 'base_sql_additional' } );
		},
        "aoColumnDefs": [
            { "bSortable": false, "aTargets": [ 0 ] },
            { "bSearchable": false, "bVisible":    false, "aTargets": [ 1, 2 ] }
        ],
		"sDom": "<'row'<'col-md-6'T><'col-md-6'f>r>t<'row'<'col-md-6'i><'col-md-6'p>>",
		"oTableTools": {
			"sSwfPath": "swf/copy_csv_xls_pdf.swf",
			"aButtons": [
				"copy","print","csv", "pdf",
                {
                    "sExtends":    "text",
                    "sButtonText": "Show all/best results",
					"fnClick": function toggleResults(nButton,oConfig,oFlash) {
						oTableRunsShowAll = !oTableRunsShowAll;
						oTableRuns.fnDraw(true);
					}
                }
            ]
		},
		"aLengthMenu": [[10, 50, 100, 250], [10, 50, 100, 250]],
		"iDisplayLength" : 50,
		"bAutoWidth": false,
		<?php echo column_widths($this->dt_main['column_widths']); ?>
        "bPaginate": true
    });

    /* Add event listener for opening and closing details
     * Note that the indicator for showing which row is open is not controlled by DataTables, rather it is done here
     */
    $('#datatable_main tbody td img').on('click', function () {
        var nTr = $(this).parents('tr')[0];
        if ( oTableRuns.fnIsOpen(nTr) )
        {
            // This row is already open - close it
            this.src = "img/datatables/details_open.png";
            oTableRuns.fnClose( nTr );
        }
        else
        {
            // Open this row
            this.src = "img/datatables/details_close.png";
            oTableRuns.fnOpen( nTr, fnFetchParams(oTableRuns, nTr, 2), 'details' );
        }
    } );
} );

function updateTableHeader(){
	$("#value").html(evaluation_measure.charAt(0).toUpperCase()+evaluation_measure.slice(1).replace(/_/g,' '));
	if(evaluation_measure.indexOf("error") > -1 || evaluation_measure.indexOf("cost") > -1)
		higherIsBetter = false;
	else
		higherIsBetter = true;
}

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
  size: '100000',
  body: {
    filter: {
      term: {
        'run_task.task_id': current_task
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

	for(var i=0;i<data.length;i++){
		var run = data[i]['_source'];
		var evals = run['evaluations'];

		if (!(run['uploader'] in map)){
			map[run['uploader']] = usercount++;
			d[map[run['uploader']]] = [];
			names[map[run['uploader']]] = run['uploader'];
			if(higherIsBetter)
				leaders.push({rank: Infinity, name: run['uploader'], userId: run['uploader_id'], topScore: 0, entries: 0, highRank: Infinity});
			else
				leaders.push({rank: Infinity, name: run['uploader'], userId: run['uploader_id'], topScore: Infinity, entries: 0, highRank: Infinity});
		}
		if(typeof evals[evaluation_measure] !== 'undefined'){
			var dat = Date.parse(run['date']);
			var e = parseFloat(evals[evaluation_measure]);
			d[map[run['uploader']]].push({x: dat, y: e, f: run['run_flow']['name'], r: run['run_id'], u: run['uploader'], t: evals['build_cpu_time']} );
			if(d[0].length==0 || (higherIsBetter && e > d[0][d[0].length-1]['y']) || (!higherIsBetter && e < d[0][d[0].length-1]['y'])){
				d[0].push({x: dat, y: e, f: run['run_flow']['name'], r: run['run_id'], u: run['uploader'], t: evals['build_cpu_time']});
			}

			if(!pairs.has(run['run_flow']['name']+e)){ //check if submission is new
				pairs.add(run['run_flow']['name']+e);
				var l = leaders[map[run['uploader']]-1];
				l.entries = l.entries+1;
				if((higherIsBetter && l.topScore<e) || (!higherIsBetter && l.topScore>e)){ //if new best score for this person
					l.topScore = e;
					sortedLeaders = leaders.slice().sort(function(a,b){if(higherIsBetter){return b.topScore-a.topScore;}else{return a.topScore-b.topScore;}});
					rankedLeaders = leaders.slice().map(function(v){return sortedLeaders.indexOf(v)+1 });
					if(l.highRank > rankedLeaders[map[run['uploader']]-1])
						l.highRank = rankedLeaders[map[run['uploader']]-1];
				}
			}
		}
	}
	options2.chart.height = 500;

	for(var i=0;i<sortedLeaders.length;i++){
		 sortedLeaders[i].rank = i+1;
		 sortedLeaders[i].name = '<a href=u/'+sortedLeaders[i].userId+'>'+sortedLeaders[i].name+'</a>';
		}

	for(var i=0;i<usercount;i++){
		options2.series[i] = {};
		if(i==0){options2.series[i].type= 'line';}
		options2.series[i].turboThreshold = 0;
		options2.series[i].name = names[i];
		options2.series[i].data = d[i];
		options2.series[i].color = colors[i%9];
		options2.series[i].fillOpacity = 0.25;
		<?php if($this->record['type_id']!=6){ ?>
		options2.series[i].point = {
                    events: {
                        click: function(){$('#runModal').modal({remote: 'r/' + this.r + '/html'}); $('#runModal').modal('show');}
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
function leaderboard(data){
	console.log(data);
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

/**
$('#tableview').dataTable( {
	"aaData": data,
	"scrollY": "600px",
	"scrollCollapse": true,
	"paging":         false,
	"aLengthMenu": [[10, 50, 100, 250, -1], [10, 50, 100, 250, "All"]],
	"iDisplayLength" : 50,
	"bSort" : true,
	"bInfo": false,
	"aaSorting" : [],
	"aoColumns": [
	<?php $cnt = sizeOf($cols);
	foreach( $this->tableview[0] as $k => $v ) {
		$newcol = '{ "mData": "'.$k.'" , "defaultContent": "", ';
			if(is_numeric($v))
			$newcol .= '"sType":"numeric", ';
			if($cnt<6)
			$newcol .= '"bVisible":true},';
			else
			$newcol .= '"bVisible":false},';
			if(array_key_exists($k,$cols)){
				$cols[$k] = $newcol;
			} else {
				$cols[] = $newcol;
				$cnt++;
			}
		}
		foreach( $cols as $k => $v ) {
			echo $v;
		}?>

		]
	} );
**/

function redrawchart(){
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
            xAxis: {
                title: {
                    text: evaluation_measure.charAt(0).toUpperCase()+evaluation_measure.slice(1).replace(/_/g,' ')
                },
                gridLineWidth: 1
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
                        click: function(){$('#runModal').modal({remote: 'r/' + this.r + '/html'}); $('#runModal').modal('show');}
                    }
                }
            }]
        };

sorts = {};
sorts['evaluations.'+evaluation_measure] = 'desc';

client.search({
  index: 'openml',
  type: 'run',
  size: '100000',
  body: {
    filter: {
      term: {
        'run_task.task_id': current_task
      }
    },
    sort: sorts
  }
}).then(function (resp) {
        var data = resp.hits.hits;
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
		d.push({x: parseFloat(evals[evaluation_measure]), y: map[flow['name']], r: run['run_id'], u: run['uploader'], t: evals['build_cpu_time']} );
	}

	options.yAxis.categories = c;
	options.series[0].data = d;
	options.chart.height = c.length*18+120;

	coderesultchart = new Highcharts.Chart(options);

}, function (err) {
    console.trace(err.message);
});
}

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


$(document).ready(function() {
        <?php if($this->record['type_name'] == 'Learning Curve')
		echo 'redrawCurves();';
	      else
		echo 'redrawchart(); redrawtimechart();';
	?>
});

<?php
}
?>
