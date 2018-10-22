 <?php if(false !== strpos($_SERVER['REQUEST_URI'], '/a/flow-qualities/')){ ?>
	<?php if(!isset( $this->measure)) echo 'Sorry, this measure is not known.'; else { ?>
	<h1><?php echo $this->measure['name']; ?></h1>
  <div class="panel">
	<p><?php echo $this->measure['description']; ?></p>
  <h2>Overview</h2>
		<div class="table-responsive"><table class="table table-striped">
		<tbody>
		<?php	if(!isset($this->results)) {echo "No values found.";} else { foreach( $this->results as $r ):
        if(array_key_exists($this->measure['name'],$r['fields'])){?>
			<tr>
        <td><a href="f/<?php echo $r['_id'];?>"><?php echo $r['fields']['name'][0] . ' ('. $r['fields']['version'][0] . ')'; ?></a></td>
        <td><?php echo $r['fields'][$this->measure['name']][0];?></td>
      </tr>
		<?php } endforeach; ?>
		</tbody>
		</table></div>
  </div>
	<?php }}} ?>
