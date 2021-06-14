<oml:feature_qualities xmlns:oml="http://openml.org/openml">
  <?php foreach( $feature_qualities as $quality ): ?>
  
  <oml:quality>
    <oml:name><?php echo $quality->quality; ?></oml:name>
    <oml:value><?php echo $quality->value; ?></oml:value>
    <oml:feature_index><?php echo $quality->feature_index; ?></oml:feature_index>
  </oml:quality>
  <?php endforeach; ?>
</oml:feature_qualities>
