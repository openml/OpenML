<?php
  $p = array();
  $p['index'] = 'openml';
  $p['type'] = 'task_type';

  // TODO: Update ES to replace this DB call.
  $this->allmeasures = $this->Math_function->getColumnWhere('name','functionType = "EvaluationFunction"');
?>

<li>
  <input type="text" class="form-control floating-label" id="task_id" name="task_id"
   data-hint="The ID of the task"
   value="<?php if(array_key_exists('task_id',$this->filters)){ echo $this->filters['task_id'];}?>"
   placeholder="Task ID">
</li>
<li>
    <input type="text" class="form-control floating-label" id="tags.tag" name="tags.tag" data-hint="A tag that has been added to this task"
     value="<?php if(array_key_exists('tags.tag',$this->filters)){ echo $this->filters['tags.tag'];}?>" placeholder="Tag">
</li>
<li>
    <select class="form-control input-small selectpicker" name="tasktype" id="tasktype.tt_id">
       <option value="">Task type</option>
	    <?php
	      $p['body']['query']['match_all'] = (object)[];
        $results = $this->searchclient->search($p);
	      $alltasks = $results['hits']['hits'];
	      foreach($alltasks as $h){?>
	            <option value="<?php echo $h['_id']; ?>" <?php if(array_key_exists('tasktype.tt_id',$this->filters) and $this->filters['tasktype.tt_id'] == $h['_id']){ echo 'selected';}?>><?php echo $h['_source']['name']; ?></option>
	      <?php } ?>
     </select>
 </li>

   <?php
  	  if(array_key_exists('tasktype.tt_id',$this->filters)){
        $p['id'] = $this->filters['tasktype.tt_id'];
        unset($p['body']);
        $inputs = $this->searchclient->get($p)['_source']['input'];
  		  foreach($inputs as $v){
  		  if($v['io'] == 'input' and $v['requirement'] != 'hidden'){
   ?>
   <li>
      <?php
      if($v['name'] == 'source_data'){
        $v['name'] = 'source_data.name';
      }
      if($v['name'] == 'evaluation_measures'){ ?>
        <select class="form-control input-small selectpicker" name="evaluation_measures" id="evaluation_measures">
            <option value="">Estimation measure</option>
            <?php foreach($this->allmeasures as $m): ?>
            <option value="<?php echo $m;?>" <?php if(array_key_exists('evaluation_measures',$this->filters) and $this->filters['evaluation_measures'] == $m){ echo 'selected';}?>><?php echo str_replace('_', ' ', $m);?></option>
            <?php endforeach; ?>
				</select>
      <?php }
      elseif($v['name'] == 'estimation_procedure'){?>
        <select class="form-control input-small selectpicker" name="tasktype" id="estimation_procedure.proc_id">
           <option value="">Estimation procedure</option>
          <?php
            unset($p['id']);
            $p['type'] = 'measure';
            $p['body']['query']['bool']['must'] = array(
                array('match' => array('measure_type' => 'estimation_procedure')),
                array('match' => array('task_type' => $this->filters['tasktype.tt_id'])),
            );
            $results = $this->searchclient->search($p);
            $alltasks = $results['hits']['hits'];
            foreach($alltasks as $h){?>
                  <option value="<?php echo $h['_source']['proc_id']; ?>" <?php if(array_key_exists('estimation_procedure.proc_id',$this->filters) and $this->filters['estimation_procedure.proc_id'] == $h['_source']['proc_id']){ echo 'selected';}?>><?php echo $h['_source']['name']; ?></option>
            <?php } ?>
         </select>
      <?php } else { ?>
      <input type="text" class="form-control floating-label" id="<?php echo $v['name']; ?>" name="<?php echo $v['name']; ?>" data-hint="<?php echo $v['description'];?>"
       value="<?php if(array_key_exists($v['name'],$this->filters)){ echo $this->filters[$v['name']];}?>" placeholder="<?php echo ucfirst(str_replace('.',' ',str_replace('_',' ',$v['name']))); ?>">
      <?php } ?>
   </li>
   <?php
       }}}
  	?>
<!-- end filters -->
