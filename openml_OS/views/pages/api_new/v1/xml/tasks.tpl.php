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
    $inputs = json_decode('[' . $task->task_inputs . ']');
    foreach ($inputs as $key => $value): ?>
      <oml:input name="<?php echo $key; ?>"><?php echo htmlspecialchars($value); ?></oml:input>
  <?php endforeach; endif; ?>
  <?php if ($task->task_qualities):
    $qualities = json_decode('[' . $task->task_qualities . ']');
    foreach ($qualities as $key => $value): ?>
      <oml:quality name="<?php echo $key; ?>"><?php echo $value; ?></oml:quality>
  <?php endforeach; endif; ?>
  </oml:task>
  <?php endforeach; ?>
</oml:tasks>
