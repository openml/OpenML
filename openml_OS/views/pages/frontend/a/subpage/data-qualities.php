<?php if(false !== strpos($_SERVER['REQUEST_URI'], '/a/data-qualities/')){ ?>
	<?php if(!isset( $this->measure)) echo 'Sorry, this measure is not known.'; else { ?>
	<h1><?php echo $this->measure['name']; ?></h1>
	<div class="panel">

	<p><?php echo $this->measure['description']; ?></p>
	<ul class="hotlinks">
		<li><a href="https://github.com/openml/OpenML/tree/master/Java/OpenmlWebapplication/src/org/openml/webapplication/fantail/dc"><i class="fa fa-gears"></i> View code</a></li>
	</ul>
	<h2>Value per dataset</h2>
		<div class="table-responsive"><table class="table table-striped">
		<tbody>
		<?php	if(!isset($this->results)) {echo "No values found.";} else { foreach( $this->results as $r ):
        if(array_key_exists($this->measure['name'],$r['_source']['qualities'])){?>
			<tr>
        <td><a href="d/<?php echo $r['_id'];?>"><?php echo $r['_source']['name'] . ' ('. $r['_source']['version'] . ')'; ?></a></td>
        <td><?php echo $r['_source']['qualities'][$this->measure['name']];?></td>
      </tr>
		<?php } endforeach; ?>
		</tbody>
		</table></div>
		</div>
	<?php }}} ?>
