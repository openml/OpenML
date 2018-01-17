{"evaluations":{"evaluation":[
  <?php $first = TRUE;
        foreach( $evaluations as $e ):
          echo ($first ? "" : ",");
          $first = FALSE; ?>
  {"run_id":<?php echo $e->rid; ?>,
   "task_id":<?php echo $e->task_id; ?>,
   "setup_id":<?php echo $e->sid; ?>,
   "flow_id":<?php echo $e->implementation_id; ?>,
   "flow_name":"<?php echo $e->fullName; ?>",
   "data_name":"<?php echo $e->name; ?>",
   "function":"<?php echo $e->{'function'}; ?>",
   "upload_time":"<?php echo $e->start_time; ?>"
   <?php if($e->value != null): ?>,"value":<?php echo $e->value; ?><?php endif; ?>
   <?php if($e->array_data != null): ?>,"array_data":<?php echo quote_array_strings($e->array_data); ?><?php endif; ?>
  }
  <?php endforeach; ?>
  ]}
}
