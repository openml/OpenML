    <a href="search?q=uploader_id%3A<?php echo $this->user_id; ?>&type=run" class="btn btn-primary pull-right">Search these runs in more detail</a>
		<h1><i class="fa fa-star"></i> <?php echo $this->userinfo['first_name'];?>'s runs</h1>

    <div class="searchframefull">
    <?php
      $this->filtertype = 'run';
      $this->sort = 'date';
      $this->specialterms = 'uploader_id:'.$this->user_id;
      loadpage('search', true, 'pre');
      $this->dataonly = true;
      loadpage('search/subpage', true, 'results'); ?>
    </div>
