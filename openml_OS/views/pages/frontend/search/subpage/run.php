<?php
  $fs = array('run_id' => 'Run ID','run_task.task_id' => 'Task ID','run_flow.flow_id' => 'Flow ID','uploader' => 'Uploader','tags.tag' => 'Tag');
  $desc = array('run_id' => 'The ID of the run','run_task.task_id' => 'The ID of the task','run_flow.flow_id' => 'The ID of the flow used','uploader' => 'The person who uploaded the run', 'tags.tag' => 'A tag that has been added to this run');
  foreach($fs as $f => $v){
?>
  <li>
      <input type="text" class="form-control floating-label" id="<?php echo $f; ?>" name="<?php echo $f; ?>" data-hint="<?php echo $desc[$f];?>"
       value="<?php if(array_key_exists($f,$this->filters)){ echo $this->filters[$f];}?>" placeholder="<?php echo $v; ?>">
  </li>
<?php } ?>
<li>
    <select class="form-control input-small selectpicker" name="run_task.tasktype.tt_id" id="run_task.tasktype.tt_id">
       <option value="">Task type</option>
	    <?php
        $p = array();
        $p['index'] = 'task_type';
        $p['type'] = 'task_type';
	      $p['body']['query']['match_all'] = (object)[];
        $results = $this->searchclient->search($p);
	      $alltasks = $results['hits']['hits'];
	      foreach($alltasks as $h){?>
	            <option value="<?php echo $h['_id']; ?>" <?php if(array_key_exists('run_task.tasktype.tt_id',$this->filters) and $this->filters['run_task.tasktype.tt_id'] == $h['_id']){ echo 'selected';}?>><?php echo $h['_source']['name']; ?></option>
	      <?php } ?>
     </select>
 </li>
