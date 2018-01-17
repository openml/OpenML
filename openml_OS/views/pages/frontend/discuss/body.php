<div class="container-fluid topborder endless openmlsectioninfo">
 <div class="col-xs-12 col-md-10 col-md-offset-1" id="mainpanel">

  <div class="tab-content">

  <div class="tab-pane <?php if($this->activetab == 'recent') echo 'active'; ?>" id="recent">
    <?php subpage('recent'); ?>
  </div>
  <div class="tab-pane <?php if($this->activetab == 'new') echo 'active'; ?>" id="new">
    <?php subpage('new'); ?>
  </div>
  <div class="tab-pane <?php if($this->activetab == 'thread') echo 'active'; ?>" id="thread">
    <?php if(isset($this->thread) and $this->thread) subpage('thread'); ?>
  </div>

  </div> <!-- end tabs content -->

  <div class="submenu">
    <ul class="sidenav nav" id="accordeon">
      <li class="panel guidechapter">
        <a data-toggle="collapse" data-parent="#accordeon" data-target="#pagelist"><i class="fa fa-info-circle fa-fw fa-lg"></i> <b>Discussions</b></a>
        <ul class="sidenav nav collapse in" id="pagelist">
          <li class="<?php if($this->activetab == 'recent') echo 'active'; ?>"><a href="#recent" data-toggle="tab">Recent</a></li>
          <li class="<?php if($this->activetab == 'new') echo 'active'; ?>"><a class="loginfirst" href="#new" data-toggle="tab">New topic</a></li>
        </ul>
      </li>
    </ul>
  </div>

</div>
</div>
