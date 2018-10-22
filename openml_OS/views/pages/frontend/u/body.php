<div id="subtitle"><?php echo $this->userinfo['first_name'] . ' ' . $this->userinfo['last_name']; ?></div>
<div class="container-fluid topborder endless openmlsectioninfo">
  <div class="col-xs-12 col-md-10 col-md-offset-1" id="mainpanel">

    <div class="tab-content">
      <?php if(!in_array($this->subpage,$this->activity_subpages) and false !== strpos($_SERVER['REQUEST_URI'],'/u/') ){ ?>
      <div class="tab-pane active" id="overview">
        <?php
        subpage('user');
        ?>
      </div>
      <?php } ?>

      <?php if(in_array($this->subpage,$this->activity_subpages)) { ?>

      <div class="tab-pane active" id="userdata">
        <?php subpage('user_'.$this->subpage); ?>
      </div>

      <?php } ?>

      <?php if(false !== strpos($_SERVER['REQUEST_URI'],'/u/') and $this->is_owner) { ?>

      <div class="tab-pane" id="edit">
        <?php
        subpage('profile');
        ?>
      </div>
      <div class="tab-pane" id="api">
        <?php
        subpage('api');
        ?>
      </div>
      <?php } ?>

    </div> <!-- end tabs content -->

  </div> <!-- end panel -->
</div> <!-- end container -->
