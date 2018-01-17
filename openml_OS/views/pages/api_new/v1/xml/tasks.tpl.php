<oml:tasks xmlns:oml="http://openml.org/openml">
  <?php foreach ($tasks as $task): ?>
  <oml:task>
    <oml:task_id><?php echo $task->task_id; ?></oml:task_id>
    <oml:task_type_id><?php echo $task->ttid; ?></oml:task_type_id>
    <oml:task_type><?php echo $task->name; ?></oml:task_type>
    <oml:did><?php echo $task->did; ?></oml:did>
    <oml:name><?php echo $task->dataset_name; ?></oml:name>
    <oml:status><?php echo $task->status; ?></oml:status>
    <oml:format><?php echo $task->format; ?></oml:format>
  <?php if ($task->task_inputs):
    $task_inputs = str_getcsv($task->task_inputs);
    $input_values = str_getcsv($task->input_values);
    for ($i = 0; $i < count($task_inputs); ++$i): ?>
      <oml:input name="<?php echo $task_inputs[$i]; ?>"><?php echo htmlspecialchars($input_values[$i]); ?></oml:input>
  <?php endfor; endif; ?>
  <?php if ($task->qualities):
    $qualities = str_getcsv($task->qualities);
    $values = str_getcsv($task->quality_values);
    for ($i = 0; $i < count($qualities); ++$i): ?>
      <oml:quality name="<?php echo $qualities[$i]; ?>"><?php echo $values[$i]; ?></oml:quality>
  <?php endfor; endif; ?>
  </oml:task>
  <?php endforeach; ?>
</oml:tasks>
