<div class="container-fluid topborder endless openmlsectioninfo">
  <div class="col-xs-12 col-md-10 col-md-offset-1" id="mainpanel">

<?php
  if(false === strpos($_SERVER['REQUEST_URI'],'/new/')) {
    subpage('data');
  } else {
    subpage($this->subpage);
  }
?>
  </div>
</div>
