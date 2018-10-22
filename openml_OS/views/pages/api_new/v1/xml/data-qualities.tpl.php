<oml:data_qualities xmlns:oml="http://openml.org/openml">
  <?php foreach( $qualities as $quality ): ?>
  <?php $start = property_exists( $quality, 'interval_start' ) ? ' interval_start="' . $quality->interval_start . '"' : ''; ?>
  <?php $end   = property_exists( $quality, 'interval_end'   ) ? ' interval_end="'   . $quality->interval_end   . '"' : ''; ?>
  
  <oml:quality <?php echo $start . $end; ?> >
    <oml:name><?php echo $quality->quality; ?></oml:name>
  <?php if ($quality->value != null): ?>
    <oml:value><?php echo $quality->value; ?></oml:value>
  <?php else: ?>
    <oml:value />
  <?php endif; ?>
  </oml:quality>
  <?php endforeach; ?>
</oml:data_qualities>
