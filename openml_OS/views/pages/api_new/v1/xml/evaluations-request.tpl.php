<oml:evaluation_request xmlns:oml="http://openml.org/openml">
  <?php foreach($res as $r): ?>
  <oml:run>
    <oml:run_id><?php echo $r->rid; ?></oml:run_id>
    <oml:task_id><?php echo $r->task_id; ?></oml:task_id>
    <oml:setup_id><?php echo $r->setup; ?></oml:setup_id>
    <oml:uploader><?php echo $r->uploader; ?></oml:uploader>
		<oml:upload_time><?php echo $r->start_time; ?></oml:upload_time>
	</oml:run>
  <?php endforeach; ?>
</oml:evaluation_request>
