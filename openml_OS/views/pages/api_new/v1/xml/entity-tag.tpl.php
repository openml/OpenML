<oml:<?php echo $xml_tag_name; ?> xmlns:oml="http://openml.org/openml">
  <oml:id><?php echo $id; ?></oml:id>
  <?php if ($tags != false): ?><?php foreach($tags as $tag): ?>
  <oml:tag><?php echo $tag; ?></oml:tag>
  <?php endforeach; endif; ?>
</oml:<?php echo $xml_tag_name; ?>>
