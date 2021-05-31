<oml:scores xmlns:oml="http://openml.org/openml">
  <oml:type><?php echo $type; ?></oml:type>
  <?php foreach( $scores as $score ): ?>
  <oml:score>
    <oml:value><?php echo $score->value; ?></oml:value>
    <oml:from><?php echo $score->from; ?></oml:from>
    <oml:to><?php echo $score->to; ?></oml:to>
  </oml:score>
  <?php endforeach; ?>
</oml:scores>

