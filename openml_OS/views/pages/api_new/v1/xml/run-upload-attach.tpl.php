<oml:upload_run_attach xmlns:oml="http://openml.org/openml">
	<oml:run_id><?php echo $run_id; ?></oml:run_id>
	<?php if(isset($files)): ?>
		<?php foreach($files as $key => $value): ?>
		<oml:predictionfile>
			<oml:name><?php echo $value->name; ?></oml:name>
			<oml:upload_time><?php echo $value->upload_time; ?></oml:upload_time>
			<oml:file_id><?php echo $value->file_id; ?></oml:file_id>
		</oml:predictionfile>
		<?php endforeach;?>
	<?php endif; ?>
</oml:upload_run_attach>
