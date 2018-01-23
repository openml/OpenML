<?php
// block unauthorized access
$this->blocked = false;
if($this->study['visibility'] == 'private' and (!$this->ion_auth->logged_in() or $this->ion_auth->user()->row()->id != $this->study['uploader_id'])){
  $this->blocked = true;
} else {
  //placeholder
  $this->wikiwrapper = '<div class="rawtext">'.str_replace('**','',$this->study['description']).'</div>';

  //crop long descriptions
  $this->hidedescription = false;
  //if(strlen($this->wikiwrapper)>400)
  //  $this->hidedescription = true;
}

?>
<div id="subtitle"><?php echo $this->study['name']; ?></div>
<div class="container-fluid topborder endless openmlsectioninfo">
  <div class="col-xs-12" id="mainpanel">


    <h1 class="pull-left"><a href="d"><i class="fa fa-flask"></i></a>
	     <?php echo $this->study['name']; ?>
    </h1>

    <div class="datainfo">
       <i class="fa fa-cloud-upload"></i> Created <?php echo dateNeat( $this->study['date']); ?> by <a href="u/<?php echo $this->study['uploader_id'] ?>"><?php echo $this->study['uploader'] ?></a>
       <i class="fa fa-eye-slash"></i> Visibility: <?php echo strtolower($this->study['visibility']); ?>
       <?php if ($this->ion_auth->logged_in()) { ?>
       <i class="fa fa-refresh"></i>
         <form method="post" action="" enctype="multipart/form-data" style="display:inline;">
           <input type="hidden" name="index_type" id="index_type" value="study" />
           <input type="hidden" name="index_id" id="index_id" value="<?php echo $this->id; ?>" />
           <input type="submit" class="btn-link datalink" value="Refresh" />
         </form>
       <?php } ?>
    </div>

    <div class="tabbed-submenu" style="margin-bottom: -60px;">
    <ul class="nav nav-pills pull-right">
      <li class="active"><a class="btn btn-raised btn-default <?php echo (!in_array($this->activepage,$this->activity_subpages) ? 'btn-info' : ''); ?>" href="<?php echo BASE_URL .'s/'. $this->id;?>">Description</a></li>
      <li><a class="btn btn-raised btn-default <?php echo ($this->activepage == 'data' ? 'btn-info' : ''); ?>" href="<?php echo BASE_URL .'s/' . $this->id . '/data';?>"><i class="fa fa-database"></i> <span class="counter"><?php echo $this->study['datasets_included']; ?></span> Data sets</a></li>
      <li><a class="btn btn-raised btn-default <?php echo ($this->activepage == 'tasks' ? 'btn-info' : ''); ?>" href="<?php echo BASE_URL .'s/' . $this->id . '/tasks';?>"><i class="fa fa-trophy"></i> <span class="counter"><?php echo $this->study['tasks_included']; ?></span> Tasks</a></li>
      <li><a class="btn btn-raised btn-default <?php echo ($this->activepage == 'flows' ? 'btn-info' : ''); ?>" href="<?php echo BASE_URL .'s/' . $this->id . '/flows';?>"><i class="fa fa-gears"></i> <span class="counter"><?php echo $this->study['flows_included']; ?></span> Flows</a></li>
      <li><a class="btn btn-raised btn-default <?php echo ($this->activepage == 'runs' ? 'btn-info' : ''); ?>" href="<?php echo BASE_URL .'s/' . $this->id . '/runs';?>"><i class="fa fa-star"></i> <span class="counter"><?php echo $this->study['runs_included']; ?></span> Runs</a></li>
    </ul>
    </div>

    <div class="tab-content">
      <?php if(!in_array($this->activepage,$this->activity_subpages) and false !== strpos($_SERVER['REQUEST_URI'],'/s/') ){ ?>
      <div class="tab-pane active" id="overview">
        <?php
        subpage('study');
        ?>
      </div>
      <?php } ?>

      <?php if(in_array($this->activepage,$this->activity_subpages)) { ?>

      <div class="tab-pane active" id="studydata">
        <?php subpage('study_'.$this->activepage); ?>
      </div>

      <?php } ?>

    </div> <!-- end tabs content -->

  </div> <!-- end panel -->
</div> <!-- end container -->
