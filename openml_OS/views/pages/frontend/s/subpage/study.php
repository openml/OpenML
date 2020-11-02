    <?php if($this->blocked){
		o('no-access');
	  } else {
    ?>

  <div class="panel" onclick="showmore()" style="margin-top: 60px;">
    <div class="cardactions">
      <div class="wiki-buttons">
        <div class="pull-right" id="wiki-waiting">
          <i class="fa fa-spinner fa-pulse"></i> Loading wiki
        </div>
        <div class="pull-right" id="wiki-ready">
          <?php //if($this->is_owner){ ?>
            <a class="pull-right greenheader loginfirst" href="s/<?php echo $this->id; ?>/edit"><i class="fa fa-edit fa-lg"></i> Edit</a>
          <?php //}
                if ($this->show_history) { ?>
          <a class="pull-right" href="s/<?php echo $this->id; ?>/history"><i class="fa fa-clock-o fa-lg"></i> History</a>
          <?php } ?>
        </div>
      </div>
    </div>
    <div class="card-content">
     <div class="description <?php if($this->hidedescription) echo 'hideContent';?>">
	    <?php
        echo $this->wikiwrapper;
      ?>
     </div>
    </div>
  </div>
  <?php } ?>
