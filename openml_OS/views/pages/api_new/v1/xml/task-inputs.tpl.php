<oml:task_inputs xmlns:oml="http://openml.org/openml">
	<oml:task_id><?php echo $task->task_id;?></oml:task_id>
	<oml:task_type_id><?php echo $task->ttid;?></oml:task_type_id>
	<?php foreach($inputs as $input => $value): ?>
  <oml:input name="<?php echo $input; ?>"><?php echo $value;?></oml:input>
  <?php endforeach; ?>
</oml:task_inputs>
