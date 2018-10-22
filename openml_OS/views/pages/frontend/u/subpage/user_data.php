    <a href="search?q=uploader_id%3A<?php echo $this->user_id; ?>&type=data" class="btn btn-primary pull-right">Search these datasets in more detail</a>
    <h1><i class="fa fa-database"></i> <?php echo $this->userinfo['first_name'];?>'s datasets</h1>

    <div class="searchframefull">
    <?php
      $this->filtertype = 'data';
      $this->sort = 'date';
      $this->specialterms = 'uploader_id:'.$this->user_id.' status:all';
      loadpage('search', true, 'pre');
      $this->dataonly = true;
      loadpage('search/subpage', true, 'results'); ?>
    </div>
