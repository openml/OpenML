<oml:<?php echo $xml_tag_name; ?>_tag_list xmlns:oml="http://openml.org/openml">
  <?php foreach($tags as $tag): ?>
  <oml:tag><?php echo $tag; ?></oml:tag>
  <?php endforeach; ?>
</oml:<?php echo $xml_tag_name; ?>_tag_list>
