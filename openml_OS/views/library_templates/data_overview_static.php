<script>
  $(document).ready( function() {
    $('.data_overview_table').dataTable( {
      "bPaginate": true,
      "iDisplayLength" : 30,
      "bLengthChange": false,
      "bFilter": false,
      "bSort": true,
      "aaSorting": [],
      "bInfo": true
    } );
  } );

<?php if( $api_delete_function ): ?>

function askConfirmation( id, name ) {
  if(confirm('Are you sure you want to delete ' + name + '? This can not be undone. ')) {
    deleteItem( id, name, true );
  }
}

function deleteItem( id, name, msg ) {
$.ajax({
  type: "POST",
  url: "<?php echo BASE_URL; ?>api/?f=<?php echo $api_delete_function['function']; ?>",
  data: "<?php echo $api_delete_function['key']; ?>="+id,
  dataType: "xml"
}).done( function( resultdata ) {
    id_field = $(resultdata).find("oml\\:id, id");

    if( id_field.length ) {
      $("#overviewtable_row_" + id_field.text() ).remove();
      if( msg ) { alert( name + " was deleted. " ); }
    } else {
      code_field = $(resultdata).find("oml\\:code, code");
      message_field = $(resultdata).find("oml\\:message, message");
      if( msg ) { alert( "Error " + code_field.text() + ": " + message_field.text() ); }
    }
  } );
}
<?php endif; ?>
</script>

<div class="topborder">
  <div class="container">
    <div class="col-sm-12">
      <?php if($table_name): ?><h2><?php echo $table_name; ?></h2><?php endif; ?>
      <table class="table table-striped data_overview_table_<?php echo $counter; ?>">
        <thead>
          <tr>
            <?php foreach( $columns as $key ): ?>
              <td><?php echo $key; ?></td>
            <?php endforeach; ?>
          </tr>
        </thead>
        <tbody>
          <?php if( is_array($items) ) foreach( $items as $item ): ?>
              <tr id="overviewtable_row_<?php echo $item->id; ?>">
                <?php foreach( $columns as $key ): ?>
                  <td><?php if( property_exists ($item, $key ) ) { echo $item->$key; } ?></td>
                <?php endforeach; ?>
              </tr>
          <?php endforeach; ?>
        </tbody>
      </table>
    </div>
  </div>
</div>
