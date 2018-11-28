<div class="panel flushpanel">
    <?php
    //var_dump($this);
    $authimg = "img/community/misc/anonymousMan.png";
    if ($this->author)
        $authimg = htmlentities(authorImage($this->author->image));
    ?>
    <div class="col-sm-2">
        <img src="<?php echo $authimg; ?>" width="130" height="130" class="img-circle userimage" />
    </div>
    <div class="col-sm-10 userdata">
        <h1 class="username"><?php echo $this->userinfo['first_name'] . ' ' . $this->userinfo['last_name']; ?></h1>
        <div class="userbio"><?php echo $this->userinfo['bio']; ?></div>
        <div class="userdetails">
            <?php if ($this->userinfo['company']) echo '<i class="fa fa-fw fa-institution"></i> ' . $this->userinfo['company']; ?>
            <?php if ($this->userinfo['country']) echo '<i class="fa fa-fw fa-map-marker"></i> ' . $this->userinfo['country']; ?>
            <i class="fa fa-fw fa-clock-o"></i> Joined <?php echo substr($this->userinfo['date'],0,10); ?>
        </div>
    </div>
</div>

<div class="panel">
  <?php if ($this->ion_auth->logged_in()) {?>
        <div class="gamestat-row">
        <?php if ($this->ion_auth->user()->row()->gamification_visibility == 'show' && $this->userinfo['gamification_visibility'] == 'show') {?>
            <div class="gamestat">
              <div class="gamestat-label">Activity</div>
              <div class="gamestat-value" title="Activity is: 3x uploads done + 2x likes given + downloads done."><i class="fa fa-heartbeat activity"></i> <?php if(in_array('activity', $this->userinfo)){ echo $this->userinfo['activity']; }else{echo 0;}?></div>
            </div>
            <div class="gamestat">
              <div class="gamestat-label">Reach</div>
              <div class="gamestat-value" title="Reach is: 2x likes received + downloads received."><i class="fa fa-rss reach"></i> <?php if(in_array('reach', $this->userinfo)){ echo $this->userinfo['reach']; }else{echo 0;}?></div>
            </div>
            <div class="gamestat">
              <div class="gamestat-label">Impact</div>
              <div class="gamestat-value" title="Impact is: number or reuses of your uploads + 0.5*reach of reuse of your uploads + 0.5*impact of reuse of your uploads"><i class="fa fa-bolt impact"></i><!--<i class="material-icons impact" style="font-size: 16px;">flare</i>--> <?php if(in_array('impact', $this->userinfo)){ echo $this->userinfo['impact']; }else{echo 0;} ?></div>
            </div>
        <?php }?>
  <?php } ?>
            <div class="gamestat">
              <div class="gamestat-label">Uploads</div>
              <div class="gamestat-value">
                  <span><i class="fa fa-database dataset"></i> <?php if(in_array('datasets_uploaded', $this->userinfo)){ echo $this->userinfo['datasets_uploaded']; }else{echo 0;} ?></span>
                  <span><i class="fa fa-cogs flow"></i> <?php if(in_array('flows_uploaded', $this->userinfo)){ echo $this->userinfo['flows_uploaded']; }else{echo 0;} ?></span>
                  <span><i class="fa fa-trophy task"></i> <?php if(in_array('tasks_uploaded', $this->userinfo)){ echo $this->userinfo['tasks_uploaded']; }else{echo 0;} ?></span>
                  <span><i class="fa fa-star run"></i> <?php if(in_array('runs_uploaded', $this->userinfo)){ echo $this->userinfo['runs_uploaded'];}else{echo 0;} ?></span>
              </div>
            </div>
        </div>
  <?php if ($this->is_owner || $this->ion_auth->is_admin()) { ?>
      <a href="#edit" data-toggle="tab" class="btn btn-primary pull-right">Edit Profile</a>
      <a href="#api" data-toggle="tab" class="btn btn-primary pull-right">API Authentication</a><br />
  <?php } ?>
</div>
