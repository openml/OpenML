<oml:data_unprocessed xmlns:oml="http://openml.org/openml">
  <?php foreach($res as $data): ?>
  <oml:dataset>
    <oml:did><?php echo $data->did; ?></oml:did>
    <oml:name><?php echo $data->name; ?></oml:name>
    <oml:version><?php echo $data->version; ?></oml:version>
    <oml:status><?php echo $data->status; ?></oml:status>
    <oml:format><?php echo $data->format; ?></oml:format>
	</oml:dataset>
  <?php endforeach; ?>
</oml:data_unprocessed>
