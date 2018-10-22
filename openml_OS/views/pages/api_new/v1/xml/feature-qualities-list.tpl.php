<oml:feature_qualities_list xmlns:oml="http://openml.org/openml">
  <?php foreach( $feature_qualities as $quality ): ?>
    <oml:quality><?php echo $quality; ?></oml:quality>
  <?php endforeach; ?>
</oml:feature_qualities_list>
