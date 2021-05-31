<oml:flows xmlns:oml="http://openml.org/openml">
	<?php foreach($implementations as $i): ?>
  <oml:flow>
    <oml:id><?php echo $i->id; ?></oml:id>
    <oml:full_name><?php echo $i->fullName; ?></oml:full_name>
    <oml:name><?php echo $i->name; ?></oml:name>
    <oml:version><?php echo $i->version; ?></oml:version>
    <oml:external_version><?php echo $i->external_version; ?></oml:external_version>
    <oml:uploader><?php echo $i->uploader; ?></oml:uploader>
  </oml:flow>
  <?php endforeach; ?>
</oml:flows>
