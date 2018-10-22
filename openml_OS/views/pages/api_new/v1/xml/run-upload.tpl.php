<oml:upload_run xmlns:oml="http://openml.org/openml">
	<oml:run_id><?php echo $run_id; ?></oml:run_id>
	<?php if( isset( $metrics ) ): ?>
	<oml:metrics>
		<?php foreach( $metrics as $key => $value ): ?>
		<oml:metric>
			<oml:name><?php echo $key; ?></oml:name>
			<oml:value><?php echo $value; ?></oml:value>
		</oml:metric>
		<?php endforeach;?>
	</oml:metrics>
	<?php endif; ?>
</oml:upload_run>
