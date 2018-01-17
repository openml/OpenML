<oml:job xmlns:oml="http://openml.org/openml">
  <?php if( $source != false ): ?>
    <oml:learner><?php echo htmlspecialchars($source->setup_string); ?></oml:learner>
    <oml:task_id><?php echo $source->task_id; ?></oml:task_id>
    <oml:setup_id><?php echo $source->sid; ?></oml:setup_id>
  <?php endif; ?>
</oml:job>
