<oml:evaluation_methods xmlns:oml="http://openml.org/openml">
	<oml:methods>
		<?php foreach( $method as $method ): ?>
		<oml:method>
			<oml:type><?php echo $method->type; ?></oml:type>
			<oml:folds><?php echo 'undefined';// $method->folds; ?></oml:folds>
			<oml:repeats><?php echo 'undefined';//echo $method->repeats; ?></oml:repeats>
		</oml:method>
		<?php endforeach; ?>
	</oml:methods>
</oml:evaluation_methods>
