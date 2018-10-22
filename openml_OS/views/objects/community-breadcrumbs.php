<?php if( isset( $this->breadcrumbs ) && is_array( $this->breadcrumbs ) && count( $this->breadcrumbs ) ): $nr=0; ?>
<div>
	<ul class="breadcrumb">
		<?php
			foreach( $this->breadcrumbs as $name => $link ): $nr++;
				if( $nr < count( $this->breadcrumbs ) ): ?>
					<li><a href="<?php echo $link; ?>"><?php echo stripslashes( $name ); ?></a></li>
				<?php else: ?>
					<li class="active"><?php echo stripslashes( $name ); ?></li>
				<?php 
				endif; 
			endforeach; 
		?>
	</ul>
</div>
<?php endif; ?>
