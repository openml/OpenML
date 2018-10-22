<oml:error xmlns:oml="http://openml.org/openml">
	<oml:code><?php echo htmlspecialchars($code); ?></oml:code>
	<oml:message><?php echo htmlspecialchars($message); ?></oml:message>
	<?php if( $additional != null ): ?>
	<oml:additional_information><?php echo htmlspecialchars($additional); ?></oml:additional_information>
	<?php endif; ?>
</oml:error>
