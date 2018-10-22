<?php
  $fs = array('flow_id' => 'Flow ID','version' => 'Flow version','uploader' => 'Uploader');
  $desc = array('flow_id' => 'The ID of the flow','version' => 'The version number of the flow','uploader' => 'The person who uploaded the flow');
  foreach($fs as $f => $v){
?>
  <li>
      <input type="text" class="form-control floating-label" id="<?php echo $f; ?>" name="<?php echo $f; ?>" data-hint="<?php echo $desc[$f];?>"
       value="<?php if(array_key_exists($f,$this->filters)){ echo $this->filters[$f];}?>" placeholder="<?php echo $v; ?>">
  </li>
<?php } ?>
<li>
    <input type="text" class="form-control floating-label" id="tags.tag" name="tags.tag" data-hint="A tag that has been added to this dataset"
     value="<?php if(array_key_exists('tags.tag',$this->filters)){ echo $this->filters['tags.tag'];}?>" placeholder="Tag">
</li>
