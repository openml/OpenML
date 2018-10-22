<oml:task_evaluations xmlns:oml="http://openml.org/openml">
  <oml:task_id><?php echo $task->task_id; ?></oml:task_id>
  <oml:task_name><?php echo 'Task '.$task->task_id; ?></oml:task_name>
  <oml:task_type_id><?php echo $task->ttid; ?></oml:task_type_id>
  <oml:input_data><?php echo $task->source_data; ?></oml:input_data>
  <oml:estimation_procedure><?php echo $estimation_procedure->name; ?></oml:estimation_procedure>
  <?php if( is_array($results) && count($results) ): ?>
    <?php foreach( $results as $rid => $values ): ?>
    <?php
      $interval_str = array_key_exists( 'interval_start', $values ) ?  
        'interval_start="' . $values['interval_start'] . '" interval_end="' . $values['interval_end'] . '"' : '';
    ?>
    <oml:evaluation <?php echo $interval_str; ?>>
      <oml:run_id><?php echo $values['rid']; ?></oml:run_id>
      <oml:setup_id><?php echo $values['setup_id']; ?></oml:setup_id>
      <oml:flow_id><?php echo $values['implementation_id']; ?></oml:flow_id>
      <oml:flow><?php echo $values['implementation']; ?></oml:flow>
      <?php foreach( $values['measures'] as $name => $value ): ?>
      <oml:measure name="<?php echo $name;?>"><?php echo $value;?></oml:measure>
      <?php endforeach; ?>
    </oml:evaluation>
    <?php endforeach; ?>
  <?php endif; ?>
</oml:task_evaluations>
