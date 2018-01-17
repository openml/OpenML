<?php if(!$this->dataonly) {?>
<div id="subtitle"><?php echo $this->terms; ?></div>
<div class="container-fluid topborder endless openmlsectioninfo">
<div class="col-xs-12 col-md-10 col-md-offset-1" id="mainpanel">
<?php }?>
      <?php subpage('results');?>

<?php if(!$this->dataonly) {?>
</div>

</div>
<?php
 }
?>
