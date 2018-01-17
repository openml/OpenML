<div class="container-fluid topborder endless openmlsectioninfo">
  <div class="col-xs-12 col-md-10 col-md-offset-1" id="mainpanel">

     <div class="tab-content">

        <!-- Eval measures -->
	<div class="tab-pane <?php if(false !== strpos($_SERVER['REQUEST_URI'], '/a/evaluation-measures')) echo 'active';?>">
  		<?php if(false !== strpos($_SERVER['REQUEST_URI'], '/a/evaluation-measures')) subpage('evaluation-measures'); ?>
	</div>
        <!-- Perf estimators -->
	<div class="tab-pane <?php if(false !== strpos($_SERVER['REQUEST_URI'], '/a/estimation-procedures')) echo 'active';?>">
  		<?php if(false !== strpos($_SERVER['REQUEST_URI'], '/a/estimation-procedures')) subpage('estimation-procedures'); ?>
	</div>
        <!-- Data qualities -->
	<div class="tab-pane <?php if(false !== strpos($_SERVER['REQUEST_URI'], '/a/data-qualities')) echo 'active';?>">
  		<?php if(false !== strpos($_SERVER['REQUEST_URI'], '/a/data-qualities')) subpage('data-qualities'); ?>
	</div>

        <!-- Quality values -->
	<div class="tab-pane <?php if(false !== strpos($_SERVER['REQUEST_URI'], '/a/quality-value')) echo 'active';?>">
  		<?php if(false !== strpos($_SERVER['REQUEST_URI'], '/a/quality-value')) subpage('quality-value'); ?>
	</div>
        <!-- Flow qualities -->
	<div class="tab-pane <?php if(false !== strpos($_SERVER['REQUEST_URI'], '/a/flow-qualities')) echo 'active';?>">
  		<?php if(false !== strpos($_SERVER['REQUEST_URI'], '/a/flow-qualities')) subpage('flow-qualities'); ?>
	</div>

   </div> <!-- end tabs content -->

  </div> <!-- end row -->
</div> <!-- end container -->
