<div class="row openmlsectioninfo">
  <div class="col-sm-12">

	<h1><?php echo $this->quality->quality; ?></h1>
  <div class="panel">

	<p>The <a href="<?php echo BASE_URL; ?>a/data-qualities/<?php echo strtolower($this->quality->quality); ?>"><?php echo $this->quality->quality; ?></a> of <a href="<?php echo BASE_URL; ?>d/<?php echo $this->data->did; ?>"><?php echo $this->data->name; ?></a> is <?php echo $this->quality->value; ?></p>
	<ul class="hotlinks">
		<li><a href="https://github.com/openml/EvaluationEngine/tree/master/src/main/java/org/openml/webapplication/features"><i class="fa fa-gears"></i> View code</a></li>
	</ul>
  <?php if( $this->quality->description ): ?>
    <h2>Description</h2>
    <?php echo $this->quality->description; ?>
  <?php endif; ?>
	<h2>Overview</h2>
		<div class="table-responsive"><table class="table table-striped">
		<tbody>
		<?php	if( is_array( $this->similar ) ): foreach( $this->similar as $r ):?>
			<tr>
        <td>
          <?php if( $r->did == $this->data->did ){ echo '<b>'; } ?>
          <a href="d/<?php echo $r->did;?>"><?php echo $r->name . ' ('. $r->version . ')'; ?></a>
          <?php if( $r->did == $this->data->did ){ echo '</b>'; } ?>
        </td>
        <td>
          <?php if( $r->did == $this->data->did ){ echo '<b>'; } ?>
          <?php echo $r->value;?>
          <?php if( $r->did == $this->data->did ){ echo '</b>'; } ?>
        </td>
      </tr>
		<?php endforeach; endif; ?>
		</tbody>
		</table></div>
</div>
