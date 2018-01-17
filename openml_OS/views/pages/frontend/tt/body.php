<div class="container-fluid topborder endless openmlsectioninfo">
  <div class="col-xs-12 col-md-10 col-md-offset-1" id="mainpanel">

    <div class="tab-content">

      <div class="tab-pane <?php if( isset($this->id) ) echo 'active'; ?>" id="typedetail">
        <?php
        if(false !== strpos($_SERVER['REQUEST_URI'],'/tt/')) {
        subpage('tasktype');
        }?>
      </div> <!-- end task_type tab -->

    </div> <!-- end tabs content -->

</div> <!-- end tabs content -->
</div> <!-- end container -->
