<?php foreach( $source as $param ): ?>
<oml:parameter>
	<oml:name><?php echo htmlentities($param->name); ?></oml:name>
	<oml:data_type><?php echo htmlentities($param->dataType); ?></oml:data_type>
	<oml:default_value><?php echo htmlentities($param->defaultValue); ?></oml:default_value>
	<oml:description><?php echo htmlentities($param->description); ?></oml:description>
</oml:parameter>
<?php endforeach;?>
