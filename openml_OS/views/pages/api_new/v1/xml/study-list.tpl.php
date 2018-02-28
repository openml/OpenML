<oml:study_list xmlns:oml="http://openml.org/openml">
  <?php foreach($studies as $study): ?>
  <oml:study>
    <oml:id><?php echo $study->id; ?></oml:id>
    <?php if ($study->alias != null): ?>
    <oml:alias><?php echo $study->alias; ?></oml:alias>
    <?php endif; ?>
    <oml:name><?php echo $study->name; ?></oml:name>
    <oml:creation_date><?php echo $study->created; ?></oml:creation_date>
    <oml:creator><?php echo $study->creator; ?></oml:creator>
  </oml:study>
  <?php endforeach; ?>
</oml:study_list>

