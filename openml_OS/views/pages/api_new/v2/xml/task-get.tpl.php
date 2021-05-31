<oml:task xmlns:oml="http://openml.org/openml">
	<oml:task_id><?php echo $task->task_id;?></oml:task_id>
  <oml:task_name><?php echo htmlspecialchars($name); ?></oml:task_name>
	<oml:task_type_id><?php echo $task_type->ttid;?></oml:task_type_id>
	<oml:task_type><?php echo $task_type->name;?></oml:task_type>
	<?php foreach( $parsed_io as $key => $item ): ?>
    <?php // TODO: hack. fix this!!
      $inout = $this->Task_type_inout->getWhere( 'ttid = ' . $task_type->ttid . ' AND name = "' . $key . '"' ); 
    ?>
  <oml:<?php echo $inout[0]->io; ?> name="<?php echo $key; ?>">
    <?php echo $item; ?>
  </oml:<?php echo $inout[0]->io; ?>>
	<?php endforeach; ?>
  <?php if(is_array($tags)) foreach( $tags as $tag ): ?>
  <oml:tag><?php echo $tag;?></oml:tag>
  <?php endforeach; ?>
</oml:task>
