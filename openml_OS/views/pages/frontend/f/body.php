<?php

  if(!isset($this->flow)){ ?>
    <div class="container-fluid topborder endless openmlsectioninfo">
      <div class="col-xs-12 col-md-10 col-md-offset-1" id="mainpanel">

         <div class="tab-content">
          <h3><i class="fa fa-warning"></i> This is not the flow you are looking for</h3>
          <p>Sorry, this flow does not seem to exist (anymore).</p>
        </div>
      </div>
    </div>
  <?php
  } else {

  //placeholder
  $this->wikiwrapper = '<div class="rawtext">'.str_replace('**','',$this->flow['description']).'</div>';

  //crop long descriptions
  $this->hidedescription = false;
  if(strlen($this->wikiwrapper)>400)
    $this->hidedescription = true;
?>
<div id="subtitle"><?php echo $this->flow['name']; ?></div>
<div class="container-fluid topborder endless openmlsectioninfo">
  <div class="col-xs-12 col-md-10 col-md-offset-1" id="mainpanel">

     <div class="tab-content">

     <div class="tab-pane <?php if(false !== strpos($_SERVER['REQUEST_URI'],'/f/')) { echo 'active'; } ?>" id="flow_overview">
     	<?php
	 if(false !== strpos($_SERVER['REQUEST_URI'],'/f/')) {
		subpage('implementation');
	}?>
     </div>
     </div> <!-- end tabs content -->

  </div> <!-- end row -->
</div> <!-- end container -->
<?php } ?>
