<?php
  $p = array();
  $p['index'] = 'openml';
  $p['type'] = 'measure';
  $fs = array('NumberOfInstances' => 'Number of instances','NumberOfFeatures' => 'Number of features','NumberOfMissingValues' => 'Number of missing values','NumberOfClasses' => 'Number of classes','DefaultAccuracy' => 'Default accuracy');
  foreach($fs as $f => $v){
?>
  <li>
      <input type="text" class="form-control floating-label" id="qualities.<?php echo $f; ?>" name="qualities.<?php echo $f; ?>" data-hint="<?php $p['id'] = $f; echo $this->searchclient->get($p)['_source']['description'];?>"
       value="<?php if(array_key_exists("qualities.".$f,$this->filters)){ echo $this->filters["qualities.".$f];}?>" placeholder="<?php echo $v; ?>">
  </li>
<?php } ?>
<li>
    <input type="text" class="form-control floating-label" id="uploader" name="uploader" data-hint="The person who uploaded the dataset"
     value="<?php if(array_key_exists('uploader',$this->filters)){ echo $this->filters['uploader'];}?>" placeholder="Uploader">
</li>
<li>
    <input type="text" class="form-control floating-label" id="tags.tag" name="tags.tag" data-hint="A tag that has been added to this dataset"
     value="<?php if(array_key_exists('tags.tag',$this->filters)){ echo $this->filters['tags.tag'];}?>" placeholder="Tag">
</li>
<li>
     <select class="form-control input-small selectpicker" name="status" id="status" data-hint="Dataset status (active, in_preparation, deactivated,...)">
       <option value="<?php echo $m;?>" <?php if(array_key_exists('evaluation_measures',$this->filters) and $this->filters['evaluation_measures'] == $m){ echo 'selected';}?>><?php echo str_replace('_', ' ', $m);?></option>

         <option value="all" <?php if(array_key_exists('status',$this->filters) and $this->filters['status'] == 'all'){ echo 'selected';}?>>all</option>
         <option value="active" <?php if(array_key_exists('status',$this->filters) and $this->filters['status'] == 'active'){ echo 'selected';}?>>active</option>
         <option value="in_preparation" <?php if(array_key_exists('status',$this->filters) and $this->filters['status'] == 'in_preparation'){ echo 'selected';}?>>in_preparation</option>
         <option value="deactivated" <?php if(array_key_exists('status',$this->filters) and $this->filters['status'] == 'deactivated'){ echo 'selected';}?>>deactivated</option>
    </select>
</li>
