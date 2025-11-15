<oml:description_history xmlns:oml="http://openml.org/openml">
  <?php foreach( $descriptions as $d ): ?>
  <oml:description_version>
    <oml:version><?php echo $d->version; ?></oml:version>
    <oml:description><?php echo $d->description; ?></oml:description>
  </oml:description_version>
  <?php endforeach; ?>
</oml:description_history>
