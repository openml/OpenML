<oml:<?php echo $xml_tag_name; ?>_list xmlns:oml="http://openml.org/openml">
  <?php foreach($tags as $tag): ?>
  <oml:tag><?php echo $tag; ?></oml:tag>
  <?php endforeach; endif; ?>
</oml:<?php echo $xml_tag_name; ?>_list>
