<oml:setups xmlns:oml="http://openml.org/openml">
<?php foreach($setups as $setup_id => $parameters): ?>
  <oml:setup>
    <oml:setup_id><?php echo $setup_id; ?></oml:setup_id> <?php // TODO: fix if ?>
    <oml:flow_id><?php echo $setup_flows[$setup_id]; /*important! this is different from $p->implementation_id; */?></oml:flow_id>
    <?php foreach($parameters as $p): ?>
	    <oml:parameter>
		    <oml:id><?php echo htmlspecialchars($p->id); ?></oml:id>
		    <oml:flow_id><?php echo htmlspecialchars($p->implementation_id); /*important! this is different from $p->flow_id; */?></oml:flow_id>
		    <oml:full_name><?php echo htmlspecialchars($p->fullName); ?></oml:full_name>
		    <oml:parameter_name><?php echo htmlspecialchars($p->name); ?></oml:parameter_name>
		    <oml:data_type><?php echo htmlspecialchars($p->dataType); ?></oml:data_type>
		    <oml:default_value><?php echo htmlspecialchars($p->defaultValue); ?></oml:default_value>
		    <oml:value><?php echo htmlspecialchars($p->value); ?></oml:value>
	    </oml:parameter>
    <?php endforeach; ?>
  </oml:setup>
<?php endforeach; ?>
</oml:setups>
