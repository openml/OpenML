<?php if($this->task['tasktype']['name'] != 'Learning Curve'){ ?>
  <div class="searchframefull">
  <a href="search?q=+run_task.task_id%3A<?php echo $this->task_id; ?>&type=run" class="btn btn-primary pull-right">Search runs in more detail</a>
  <?php
    $this->filtertype = 'run';
    $this->sort = 'date';
    $this->specialterms = 'run_task.task_id:'.$this->id;
    loadpage('search', true, 'pre');
    loadpage('search/subpage', true, 'results'); ?>
  </div>

<?php } ?>
