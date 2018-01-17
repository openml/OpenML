<div id="subtitle">Guide</div>
<div class="container topborder endless guidecontainer openmlsectioninfo">
  <div class="col-xs-10 col-md-8 col-md-offset-1 guidesection" id="mainpanel">
      <?php if(in_array($this->activepage,$this->activity_subpages) and false !== strpos($_SERVER['REQUEST_URI'],'/guide') ){
          subpage($this->activepage);
        } ?>

    </div> <!-- end tabs content -->
    </div> <!-- end col-10 -->

</div>
<!-- end container -->
