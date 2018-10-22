$(document).ready( function() {
  //check for change on the categories menu
  $('#form-task-type-tabs li a').eq($('#selectTaskType option:selected').attr('name')).tab('show');

  $('#selectTaskType').change(function() {
    $('#form-task-type-tabs li a').eq($('#selectTaskType option:selected').attr('name')).tab('show');
  });

  var oDatatable = $('.taskstable').dataTable( {
    "bPaginate": true,
    "aLengthMenu": [[10, 50, 100, 250, -1], [10, 50, 100, 250, "All"]],
    "iDisplayLength" : 50,
    "bLengthChange": true,
    "bFilter": false,
    "bSort": true,
    "aaSorting": [],
    "bInfo": true
  } );

  var oDuplicateTable = $('.duplicatetable').dataTable( {
    "bPaginate": false,
    "iDisplayLength" : -1,
    "bLengthChange": false,
    "bFilter": false,
    "bSort": false,
    "aaSorting": [],
    "bInfo": true
  } );
} );

function deleteTask( tid, msg ) {
  $.ajax({
    type: "DELETE",
    url: "<?php echo BASE_URL; ?>/api/v1/task",
    data: 'task_id='+tid,
    dataType: "xml"
  }).done( function( resultdata ) {

      id_field = $(resultdata).find("oml\\:id");

      if( id_field.length ) {
        $("#duplicate_task_" + id_field.text() ).remove();
        if( msg ) { alert( "Task " + id_field.text() + " was deleted. " ); }
      } else {
        code_field = $(resultdata).find("oml\\:code");
        message_field = $(resultdata).find("oml\\:message");
        if( msg ) { alert( "Error " + code_field.text() + ": " + message_field.text() ); }
      }
    } );
}

function selectDuplicateTasks() {
  $('.duplicate_checkbox').each(function() {
    if( $( this ).data("in_group_nr") > 0 && $( this ).data("runs") == 0 ) {
      $('#duplicate_checkbox_' + $( this ).data("task_id") ).prop('checked', true);
    } else {
      $('#duplicate_checkbox_' + $( this ).data("task_id") ).prop('checked', false);
    }
  });
}

function removeSelectedTasks() {
  $('.duplicate_checkbox').each(function() {
    if( $('#duplicate_checkbox_' + $( this ).data("task_id") ).prop('checked') == true ) {
      deleteTask( $( this ).data("task_id"), false );
    }
  } );
}
