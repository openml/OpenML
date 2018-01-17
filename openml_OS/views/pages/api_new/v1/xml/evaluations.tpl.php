<oml:evaluations xmlns:oml="http://openml.org/openml">
  <?php foreach( $evaluations as $e ): ?>
  <oml:evaluation>
    <oml:run_id><?php echo $e->rid; ?></oml:run_id>
    <oml:task_id><?php echo $e->task_id; ?></oml:task_id>
    <oml:setup_id><?php echo $e->sid; ?></oml:setup_id>
    <oml:flow_id><?php echo $e->implementation_id; ?></oml:flow_id>
    <oml:flow_name><?php echo $e->fullName; ?></oml:flow_name>
    <oml:data_id><?php echo $e->did; ?></oml:data_id>
    <oml:data_name><?php echo $e->name; ?></oml:data_name>
    <oml:function><?php echo $e->{'function'}; ?></oml:function>
    <oml:upload_time><?php echo $e->start_time; ?></oml:upload_time>
    <?php if($e->value != null): ?><oml:value><?php echo $e->value; ?></oml:value><?php endif; ?>
    <?php if($e->array_data != null): ?><oml:array_data><?php echo $e->array_data; ?></oml:array_data><?php endif; ?>
  </oml:evaluation>
  <?php endforeach; ?>
</oml:evaluations>
