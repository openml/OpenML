<oml:trace xmlns:oml="http://openml.org/openml">
  <oml:run_id><?php echo $run_id; ?></oml:run_id>
	<?php foreach( $trace as $t ): ?>
	<oml:trace_iteration>
		<oml:repeat><?php echo $t->repeat; ?></oml:repeat>
		<oml:fold><?php echo $t->fold; ?></oml:fold>
		<oml:iteration><?php echo $t->iteration; ?></oml:iteration>
		<oml:setup_string><?php echo $t->setup_string; ?></oml:setup_string>
		<oml:evaluation><?php echo $t->evaluation; ?></oml:evaluation>
		<oml:selected><?php echo $t->selected; ?></oml:selected>
	</oml:trace_iteration>
	<?php endforeach;?> 
</oml:trace>
