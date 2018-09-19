<oml:task_type xmlns:oml="http://openml.org/openml">
	<oml:id><?php echo $task_type->ttid; ?></oml:id>
	<oml:name><?php echo $task_type->name; ?></oml:name>
	<oml:description><?php echo htmlspecialchars( $task_type->description ); ?></oml:description>
	<?php if($task_type->creator): foreach( getcsv($task_type->creator) as $c ): ?>
	<oml:creator><?php echo $c; ?></oml:creator>
	<?php endforeach; endif; ?>
	<?php if($task_type->contributors): foreach( getcsv($task_type->contributors) as $c ): ?>
	<oml:contributor><?php echo $c; ?></oml:contributor>
	<?php endforeach; endif; ?>
  <oml:creation_date><?php echo $task_type->creationDate; ?></oml:creation_date>
	<?php foreach($io as $item): ?>
		<oml:<?php echo $item->io; ?> name="<?php echo $item->name; ?>">
		  <?php if ($item->requirement == "required"): ?><oml:requirement><?php echo $item->requirement; ?></oml:requirement><?php endif; ?>
		  <?php if ($item->api_constraints and property_exists($item->api_constraints, 'data_type')): ?><oml:data_type><?php echo $item->api_constraints['data_type']; ?></oml:data_type><?php endif; ?>
		</oml:<?php echo $item->io; ?>>
	<?php endforeach; ?>
</oml:task_type>
