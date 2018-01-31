<div id="subtitle">Guide</div>
<div class="container topborder endless guidecontainer openmlsectioninfo">

  <div class="col-md-2" id="sidenav">
        <ul class="sidenav nav" id="guidelist" data-spy="affix">
          <li><a href="guide/bootcamp" class="icongreen"><i class="fa fa-fw fa-lg fa-rocket"></i> Bootcamp</a></li>
          <li><a href="guide/api" class="iconyellow"><i class="fa fa-fw fa-lg fa-code"></i> OpenML APIs</a></li>
          <li><a href="guide/integrations" class="iconblue"><i class="fa fa-fw fa-lg fa-puzzle-piece"></i> Integrations</a></li>
          <li><a href="guide/benchmark" class="iconred"><i class="fa fa-fw fa-lg fa-signal"></i> Benchmarking</a></li>
          <li><a href="guide/developers" class="iconpurple"><i class="fa fa-fw fa-lg fa-users"></i> Developers</a></li>
          <li><a href="https://medium.com/open-machine-learning" class="iconredacc"><i class="fa fa-fw fa-lg fa-heartbeat"></i> Blog</a></li>
        </ul>
  </div>
  <div class="col-xs-10 col-md-8 guidesection" id="mainpanel">
      <?php if(in_array($this->activepage,$this->activity_subpages) and false !== strpos($_SERVER['REQUEST_URI'],'/guide') ){
          subpage($this->activepage);
        } ?>

    </div> <!-- end tabs content -->
    </div> <!-- end col-10 -->

</div>
<!-- end container -->
