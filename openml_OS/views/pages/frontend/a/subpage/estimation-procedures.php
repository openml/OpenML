 <?php if(false !== strpos($_SERVER['REQUEST_URI'], '/a/estimation-procedures/')){ ?>

	<?php if(!isset( $this->measure)) echo 'Sorry, this procedure is not known.'; else { ?>
	<h1><?php echo $this->measure['name']; ?></h1>
  <div class="panel">

	<p><?php echo $this->measure['description']; ?></p>
	<ul class="hotlinks">
		<li><a href="https://github.com/openml/EvaluationEngine/tree/master/src/main/java/org/openml/webapplication/generatefolds"><i class="fa fa-gears"></i> View code</a></li>
	</ul>
	<h2>Properties</h2>
		<div class='table-responsive'><table class='table table-striped'>
		<tr><td>Folds</td><td><?php echo $this->measure['folds']; ?></td></tr>
		<tr><td>Repeats</td><td><?php echo $this->measure['repeats']; ?></td></tr>
		<tr><td>Holdout percentage</td><td><?php echo $this->measure['percentage']; ?></td></tr>
		<tr><td>Stratified sampling</td><td><?php echo $this->measure['stratified_sampling'];?></td></tr>
		</table></div>
  </div>
	<?php }} ?>
