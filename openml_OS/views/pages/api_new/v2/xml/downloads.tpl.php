<oml:downloads xmlns:oml="http://openml.org/openml">
  <?php foreach( $downloads as $download ): ?>
  <oml:download>
    <oml:user_id><?php echo $download->user_id; ?></oml:user_id>
    <oml:knowledge_type><?php echo $download->knowledge_type; ?></oml:knowledge_type>
    <oml:knowledge_id><?php echo $download->knowledge_id; ?></oml:knowledge_id>
    <oml:count><?php echo $download->count; ?></oml:count>
  </oml:download>
  <?php endforeach; ?>
</oml:downloads>

