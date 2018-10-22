    <a href="search?q=tags.tag%3Astudy_<?php echo $this->id; ?>&type=data" class="btn btn-primary">Search these data sets in more detail</a>

    <div class="searchframefull">
    <?php
      $this->filtertype = 'data';
      $this->sort = 'date';
      $this->specialterms = 'status:all tags.tag:study_'.$this->id;
      loadpage('search', true, 'pre');
      $this->dataonly = true;
      loadpage('search/subpage', true, 'results'); ?>
    </div>
