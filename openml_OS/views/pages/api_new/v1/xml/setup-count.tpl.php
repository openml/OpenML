<oml:setup_count xmlns:oml="http://openml.org/openml">
  <?php foreach($setups as $setup): ?>
  <oml:setup>
    <oml:id><?php echo $setup->sid; ?></oml:id>
    <oml:flow_id><?php echo $setup->implementation_id; ?></oml:flow_id>
    <oml:num_runs><?php echo $setup->num_runs; ?></oml:num_runs>
  </oml:setup>
  <?php endforeach; ?>
</oml:setup_count>
