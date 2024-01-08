<oml:<?php echo $xml_tag_name; ?> xmlns:oml="http://openml.org/openml">
  <oml:id><?php echo $id; ?></oml:id>
  <?php if ($descriptions != false): ?><?php foreach($descriptions as $description): ?>
  <oml:<?php echo $description_type; ?></oml:<?php echo $description; ?></oml:<?php echo $description_type; ?>>
  <?php endforeach; endif; ?>
</oml:<?php echo $xml_tag_name; ?>>
