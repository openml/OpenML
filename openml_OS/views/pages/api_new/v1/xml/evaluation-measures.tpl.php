<oml:evaluation_measures xmlns:oml="http://openml.org/openml">
	<oml:measures>
		<?php foreach( $measures as $measure ): ?>
			<oml:measure><?php echo $measure->name; ?></oml:measure>
		<?php endforeach; ?>
	</oml:measures>
</oml:evaluation_measures>
