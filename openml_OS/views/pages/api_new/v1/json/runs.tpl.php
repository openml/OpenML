{"runs":{"run":[
  <?php $first = TRUE;
        foreach( $runs as $r ):
          echo ($first ? "" : ",");
          $first = FALSE; ?>
  {"run_id":<?php echo $r->rid; ?>,
   "task_id":<?php echo $r->task_id; ?>,
   "setup_id":<?php echo  $r->setup; ?>,
   "flow_id":<?php echo $r->flow_id; ?>,
   "uploader":<?php echo $r->uploader; ?>,
   "upload_time":"<?php echo $r->start_time; ?>",
   "error_message":"<?php echo $r->error_message; ?>",
   "run_details":"<?php echo $r->run_details; ?>"
  }
  <?php endforeach; ?>
  ]}
}
