<oml:setup_parameters xmlns:oml="http://openml.org/openml">
  <oml:setup_id><?php echo $setup->sid; ?></oml:setup_id>
  <oml:flow_id><?php echo $setup->implementation_id; ?></oml:flow_id>
<?php if($parameters): /* in case of parameterless flows? */ ?>
<?php foreach($parameters as $p): ?>
	<oml:parameter>
		<oml:id><?php echo htmlspecialchars($p->id); ?></oml:id>
		<oml:flow_id><?php echo htmlspecialchars($p->implementation_id); ?></oml:flow_id>
		<oml:full_name><?php echo htmlspecialchars($p->fullName); ?></oml:full_name>
		<oml:name><?php echo htmlspecialchars($p->name); ?></oml:name>
		<oml:parameter_name><?php echo htmlspecialchars($p->name); ?></oml:parameter_name> <!-- legacy -->
		<oml:data_type><?php echo htmlspecialchars($p->dataType); ?></oml:data_type>
		<oml:default_value><?php echo htmlspecialchars($p->defaultValue); ?></oml:default_value>
		<oml:value><?php echo htmlspecialchars($p->value); ?></oml:value>
	</oml:parameter>
<?php endforeach; ?>
<?php endif; ?>
</oml:setup_parameters>

