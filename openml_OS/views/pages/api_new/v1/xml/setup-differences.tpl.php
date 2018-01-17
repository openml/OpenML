<oml:setup_differences xmlns:oml="http://openml.org/openml">
  <?php foreach( $data as $d ): ?>
  <oml:task>
    <oml:setupA><?php echo $d->sidA; ?></oml:setupA> <!-- implicit from URL -->
    <oml:setupB><?php echo $d->sidB; ?></oml:setupB> <!-- implicit from URL -->
    <oml:task_id><?php echo $d->task_id; ?></oml:task_id>
    <oml:task_size><?php echo $d->task_size; ?></oml:task_size>
    <oml:differences><?php echo $d->differences; ?></oml:differences>
  </oml:task>
  <?php endforeach; ?>
</oml:setup_differences>