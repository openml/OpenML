<oml:study_list xmlns:oml="http://openml.org/openml">
  <?php foreach($studies as $study): ?>
  <oml:study>
    <oml:id><?php echo $study->id; ?></oml:id>
    <?php if ($study->alias != null): ?><oml:alias><?php echo $study->alias; ?></oml:alias><?php endif; ?>
    <oml:main_entity_type><?php echo $study->main_entity_type; ?></oml:main_entity_type>
    <?php if ($study->benchmark_suite != null): ?><oml:benchmark_suite><?php echo $study->benchmark_suite; ?></oml:benchmark_suite><?php endif; ?>
    <oml:name><?php echo $study->name; ?></oml:name>
    <oml:status><?php echo $study->status; ?></oml:status>
    <oml:creation_date><?php echo $study->creation_date; ?></oml:creation_date>
    <oml:creator><?php echo $study->creator; ?></oml:creator>
  </oml:study>
  <?php endforeach; ?>
</oml:study_list>

