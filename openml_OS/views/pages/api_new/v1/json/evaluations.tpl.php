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
   <?php if (property_exists($e, 'parameters')): ?>
   "parameters": [
     <?php for($i = 0; $i < len($e->parameters); ++$i): ?>
       <?php $p = $e->parameters[$i]; 
             if ($i>0) echo ","; ?>
       {"id": <?php echo htmlspecialchars($p->id); ?>,
		   "flow_id": <?php echo htmlspecialchars($p->implementation_id); ?>,
		   "flow_name": "<?php echo htmlspecialchars($p->flow_name); ?>",
		   "full_name": "<?php echo htmlspecialchars($p->flow_fullName) . '_' . htmlspecialchars($p->name); ?>",
		   "parameter_name": "<?php echo htmlspecialchars($p->name); ?>",
		   "data_type": "<?php echo htmlspecialchars($p->dataType); ?>",
		   "default_value": "<?php echo htmlspecialchars($p->defaultValue); ?>",
		   "value": "<?php echo htmlspecialchars($p->value); ?>"}
     <?php endforeach; ?>
   ]
   <?php endif; ?>
   "data_name":"<?php echo $e->name; ?>",
   "function":"<?php echo $e->{'function'}; ?>",
   "upload_time":"<?php echo $e->start_time; ?>"
   <?php if($e->value != null): ?>,"value":<?php echo $e->value; ?><?php endif; ?>
   <?php if($e->array_data != null): ?>,"array_data":<?php echo quote_array_strings($e->array_data); ?><?php endif; ?>
  }
  <?php endforeach; ?>
  ]}
}
