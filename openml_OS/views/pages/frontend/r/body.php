<?php
 if(!isset($this->run)){ ?>
  <div class="container-fluid topborder endless openmlsectioninfo">
    <div class="col-xs-12 col-md-10 col-md-offset-1" id="mainpanel">

       <div class="tab-content">
        <h3><i class="fa fa-warning"></i> This is not the run you are looking for</h3>
        <p>Sorry, this run does not seem to exist (anymore).</p>
      </div>
    </div>
  </div>
<?php
} else { ?>

<div id="subtitle"><?php echo $this->run_id; ?></div>
<div class="container-fluid topborder endless openmlsectioninfo">
  <div class="col-xs-12 col-md-10 col-md-offset-1" id="mainpanel">

     <div class="tab-content">
      <!-- DETAIL -->
	<?php if(false !== strpos($_SERVER['REQUEST_URI'],'/r/')){ ?>
        <div class="tab-pane active" id="data_overview">
		<?php subpage('run'); ?>
        </div>
  <?php } ?>
     </div> <!-- end tabs content -->

       </div> <!-- end col-2 -->

  </div> <!-- end row -->
</div> <!-- end container -->
<?php } ?>
