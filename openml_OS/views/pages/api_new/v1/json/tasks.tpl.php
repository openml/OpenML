{"tasks":{"task":[
  <?php $first = TRUE;
        if($tasks):
        foreach( $tasks as $task ):
          echo ($first ? "" : ",");
          $first = FALSE; ?>
  { "task_id":<?php echo $task->task_id; ?>,
    "task_type_id":<?php echo $task->ttid; ?>,
    "task_type":"<?php echo $task->name; ?>",
    "did":<?php echo $task->did; ?>,
    "name":"<?php echo $task->dataset_name; ?>",
    "status":"<?php echo $task->status; ?>",
    "format":"<?php echo $task->format; ?>"
    <?php if ($task->task_inputs): ?>
    ,"input": [
      <?php $inputs = json_decode($task->task_inputs);
            $i = 0;
            foreach ($inputs as $key => $value):
              echo ($i == 0 ? "" : ","); $i += 1; ?>
              {"name":"<?php echo $key; ?>", "value":"<?php echo $value; ?>"}
            <?php endforeach; ?>]
    <?php endif; ?>
    <?php  if ($task->qualities): ?>
    ,"quality": [
      <?php $qualities = json_decode($task->task_qualities);
            $i = 0;
            foreach ($qualities as $key => $value):
              echo ($i == 0 ? "" : ","); $i += 1; ?>
              {"name":"<?php echo $key; ?>", "value":"<?php echo $value; ?>"}
            <?php endforeach; ?>]
    <?php endfor; ?>]
    <?php endif; ?>
    <?php if( property_exists( $task, 'tags' ) ): ?>
    ,"tags": [
      <?php $first_t = TRUE;
            foreach( str_getcsv($task->tags) as $tag ):
            echo ($first_t ? "" : ",");
            $first_t = FALSE; ?>
      "<?php echo $tag;?>"
    <?php endforeach; ?>
    ]
    <?php endif; ?>
  }
<?php endforeach; endif; ?>
  ]}
}
