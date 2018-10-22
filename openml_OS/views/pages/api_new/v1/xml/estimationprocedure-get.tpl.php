<oml:estimationprocedure xmlns:oml="http://openml.org/openml">
  <oml:id><?php echo $ep->id; ?></oml:id>
  <oml:ttid><?php echo $ep->ttid; ?></oml:ttid>
	<oml:name><?php echo $ep->name; ?></oml:name>
	<oml:type><?php echo $ep->type; ?></oml:type>
	<?php if( property_exists( $ep, 'repeats' ) && $ep->repeats !== null ): ?><oml:repeats><?php echo $ep->repeats; ?></oml:repeats> <?php endif; ?>
	<?php if( property_exists( $ep, 'folds' ) && $ep->folds !== null ): ?><oml:folds><?php echo $ep->folds; ?></oml:folds><?php endif; ?>
	<?php if( property_exists( $ep, 'percentage' ) && $ep->percentage !== null ): ?><oml:percentage><?php echo $ep->percentage; ?></oml:percentage><?php endif; ?>
	<?php if( property_exists( $ep, 'stratified_sampling' ) && $ep->stratified_sampling !== null ): ?><oml:stratified_sampling><?php echo $ep->stratified_sampling; ?></oml:stratified_sampling><?php endif; ?>
</oml:estimationprocedure>
