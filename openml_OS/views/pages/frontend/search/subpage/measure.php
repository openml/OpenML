<?php
  $p = array();
  $p['index'] = 'openml';
  $p['type'] = 'measure';
?>

<li>
    <select class="form-control input-small selectpicker" name="tasktype" id="measure_type">
       <option value="">Measure type</option>
	    <?php
        $p['body']['size'] = 0;
	$p['body']['query']['match_all'] = (object)[];
  	$p['body']['aggs']['types']['terms']['field'] = "measure_type";
	echo json_encode($p);
        $results = $this->searchclient->search($p);
	      $measuretypes = $results['aggregations']['types']['buckets'];
	      foreach($measuretypes as $t){?>
	            <option value="<?php echo $t['key']; ?>" <?php if(array_key_exists('measure_type',$this->filters) and $this->filters['measure_type'] == $t['key']){ echo 'selected';}?>><?php echo $t['key']; ?></option>
	      <?php } ?>
     </select>
 </li>
