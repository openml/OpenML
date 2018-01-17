<div class="container-fluid topborder endless guidecontainer openmlsectioninfo">
  <div class="col-xs-12 col-md-10 col-md-offset-1 guidesection" id="mainpanel">

    <div class="tabbed-submenu" style="margin-top:10px;">
    <ul class="nav nav-pills pull-right">
      <?php foreach( $this->directories as $d ): ?>
        <li><a onclick="highlight()" id="detail-btn" class="btn btn-default btn-raised btn-info" href="backend/page/<?php echo $d; ?>"><?php echo str_replace("/","",text_neat_ucwords($d)); ?></a></li>
      <?php endforeach; ?>
    </ul>
    </div>

<?php

echo str_replace("container", "", str_replace("topborder", "", $this->dataoverview->generate_table_static(
  $this->name,
  $this->columns,
  $this->items,
  $this->api_delete_function )));

?>

</div>
</div>
