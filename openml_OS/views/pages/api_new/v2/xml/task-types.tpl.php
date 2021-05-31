<oml:task_types xmlns:oml="http://openml.org/openml">
	<?php foreach( $task_types as $tt ): ?>
	<oml:task_type>
		<oml:id><?php echo $tt->ttid; ?></oml:id>
		<oml:name><?php echo $tt->name; ?></oml:name>
		<oml:description><?php echo htmlspecialchars( $tt->description ); ?></oml:description>
		<oml:creator><?php echo $tt->creator; ?></oml:creator>
	</oml:task_type>
	<?php endforeach;?> 
</oml:task_types>
