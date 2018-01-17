// auto-suggest for the filters
function updateUploader(name){
  $('#uploader').val(name+'');
  updateQuery("uploader");
  submitSearch();
}


// Update search query upon user actions
function updateQuery(type)
{
  var constr = '';
  if(type == 'source_data.name')
    constr = $("#"+type.replace('.name','\\.name')).val().replace(/\s/g,"_");
  else if(type == 'run_task.tasktype._tt_id')
    constr = $("#run_task\\.tasktype\\.tt_id").val();
  else
    constr = $("#"+type.replace('.','\\.')).val().replace(/\s/g,"_");
  var query = $("#openmlsearch").val();
  if(query.indexOf(type+":") > -1){
    var qparts = query.match(/(?:[^\s"]+|"[^"]*")+/g);
    for (i = 0; i < qparts.length; i++) {
      if(qparts[i].indexOf(type+":") > -1){
        attr = qparts[i].split(":");
        attr[1] = constr;
        qparts[i] = attr.join(":");
        query = qparts.join(" ");
      }
    }
  } else {
    query += " "+type+":"+constr;
  }
  if(!constr){
    query = query.replace(" "+type+":",'');
    query = query.replace(type+":",'');
  }
  $("#openmlsearch").val(query);
}

function submitSearch() {
  var omlq = $('#openmlsearch').val().replace('>','gt;').replace('<','lt;');
  $('#openmlsearch').val(encodeURI(omlq));
  $('#searchform').submit();
}

$(document).ready(function () {

// Reset all search filters
function removeFilters()
{
  var query = $("#openmlsearch").val();
  var newQuery = "";
  if(query.indexOf(":") > -1){
    var qparts = query.match(/(?:[^\s"]+|"[^"]*")+/g);
    for (i = 0; i < qparts.length; i++) {
      if(qparts[i].indexOf(":") == -1){
        newQuery += " "+qparts[i];
      }
    }
  } else {
    newQuery = query;
  }
  $("#openmlsearch").val(newQuery);
}

 function bindInput(elem){
   $('#'+elem.replace(/\./g, '\\.')).bind("keyup change", function(event) {
     if (event.keyCode == 13) { submitSearch(); }
     else {
       updateQuery(elem);
     }});
 }

    // fetch counts for menu bar
    client.search(<?php echo json_encode($this->alltypes); ?>).then(function (body) {
      var buckets = body.aggregations.type.buckets;
      for (var b in buckets.reverse()){
        $('#'+buckets[b].key+'counter').html(buckets[b].doc_count);
      }
      if($("#openmlsearch").val().length==0)
        $('#task_typecounter').html('8');
    }, function (error) {
      console.trace(error.message);
    });

    //autocomplete
    $(document).on("change, keyup", "#uploader", function() { updateQuery("uploader"); });

    //normal typing
    bindInput("qualities.NumberOfInstances");
    bindInput("qualities.NumberOfFeatures");
    bindInput("qualities.NumberOfMissingValues");
    bindInput("qualities.NumberOfClasses");
    bindInput("qualities.DefaultAccuracy");
    bindInput("tags.tag");
    bindInput("tasktype.tt_id");
    bindInput("task_id");
    bindInput("estimation_procedure.proc_id");
    bindInput("source_data.name");
    bindInput("run_id");
    bindInput("run_task.task_id");
    bindInput("run_task.tasktype.tt_id");
    bindInput("run_flow.flow_id");
    bindInput("flow_id");
    bindInput("version");
    bindInput("type");
    bindInput("measure_type");
    bindInput("task_id");
    bindInput("source_data");
    bindInput("target_feature");
    bindInput("evaluation_measures");
    bindInput("status");

    //buttons
    $("#removefilters").click(function() { removeFilters(); submitSearch();});
    $("#removefilters2").click(function() { removeFilters(); submitSearch();});
    $('#research').click(function() { submitSearch(); });


    <?php
    if($this->table) {
    ?>
      $('#tableview').dataTable( {
    		"responsive": "true",
    		"dom": 'CT<"clear">lfrtip',
    		"aaData": <?php echo json_encode($this->tableview); ?>,
        "scrollY": "600px",
        "scrollCollapse": true,
    		"deferRender": true,
        "paging": false,
    		"processing": true,
    		"bSort" : true,
    		"bInfo": false,
    		"tableTools": {
    						"sSwfPath": "//cdn.datatables.net/tabletools/2.2.3/swf/copy_csv_xls_pdf.swf"
    				},
    		"aaSorting" : [],
    		"aoColumns": <?php echo json_encode($this->mCols); ?>
    	    } );

    	$('.topmenu').show();


    function toggleResults( resultgroup ) {
    	var oDatatable = $('#tableview').dataTable(); // is not reinitialisation, see docs.

    	redrawScatterRequest = true;
    	redrawLineRequest = true;
    	for( var i = 1; i < colcount; i++) {
    		if( i > colmax * resultgroup && i <= colmax * (resultgroup+1) )
    			oDatatable.fnSetColumnVis( i, true );
    		else
    			oDatatable.fnSetColumnVis( i, false );
    	}
    }
    <?php } ?>

    if ( $( "#uploader" ).length ) {
    $("#uploader").autocomplete({
      html: true,
      position: {
          my: "left top+13" // Shift 0px to the left, 20px down.
      },
      source: function(request, fresponse) {
        client.search({
          index: 'openml',
          type: 'user',
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
            if(item['_type'] == 'user'){
            return {
              type: item['_type'],
              id: item['_id'],
              text: item['_source']['first_name']+' '+item['_source']['last_name']
            };}
          }));
        });
      }
    }).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
      return $( "<li>" )
      .append( '<a onclick="updateUploader(\'' + item.text + '\')"><i class="' + icons[item.type] + '"></i> ' + item.text + '</a>' )
      .appendTo( ul );
    }
  }

  });
