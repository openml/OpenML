<oml:data_qualities_list xmlns:oml="http://openml.org/openml">
  <?php foreach( $qualities as $quality ): ?>
    <oml:quality><?php echo $quality; ?></oml:quality>
  <?php endforeach; ?>
</oml:data_qualities_list>
