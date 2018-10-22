<div class="container-fluid topborder endless openmlsectioninfo">
  <div class="col-xs-12 col-md-10 col-md-offset-1" id="mainpanel">

     <div class="tab-content">
        <!-- ADVANCED -->
	<div class="tab-pane fade <?php if($this->active_tab == 'exampletab') echo 'active';?>" id="exampletab">
		<?php subpage('examples'); ?>
	</div>
        <!-- GRAPH -->
	<div class="tab-pane fade <?php if($this->active_tab == 'querygraphtab') echo 'active';?>" id="querygraphtab">
		<?php subpage('querygraph'); ?>
	</div>
        <!-- SQL -->
	<div class="tab-pane <?php if($this->active_tab == 'sqltab') echo 'active';?>" id="sqltab">
		<?php subpage('sql'); ?>
	</div>
  <!-- COMPARE -->
<div class="tab-pane fade <?php if($this->active_tab == 'wizardtab') echo 'active';?>" id="wizardtab">
<?php subpage('wizard'); ?>
</div>
  <!-- CURVES -->
<div class="tab-pane fade <?php if($this->active_tab == 'curvestab') echo 'active';?>" id="curvestab">
<?php subpage('curves'); ?>
</div>
     <div class="tab-pane fade <?php if($this->active_tab == 'resultstab') echo 'active';?>" id="resultstab">
		<?php subpage('results'); ?>
     </div>

     </div> <!-- end tabs content -->

     <div class="submenu">
       <ul class="sidenav nav" id="accordeon">
         <li class="panel guidechapter">
           <a data-toggle="collapse" data-parent="#accordeon"  data-target="#qtabs"><i class="fa fa-search fa-fw fa-lg"></i> <b>Query</b></a>
           <ul class="sidenav nav collapse in" id="qtabs">
             <li class="active"><a href="#sqltab" data-toggle="tab">SQL editor</a></li>
             <li><a href="#wizardtab" data-toggle="tab">Compare flows</a></li>
         		 <li><a href="#curvestab" data-toggle="tab">Draw learning curves</a></li>
         		 <li><a href="#exampletab" data-toggle="tab">Advanced queries</a></li>
             <!-- <li><a href="#querygraphtab" data-toggle="tab">Query graph</a></li> -->
           	 <li style="display:none;"><a href="#resultstab" data-toggle="tab">Results</a></li>
           </ul>
         </li>
         <li class="panel guidechapter">
           <a data-toggle="collapse" data-parent="#accordeon"  data-target="#vtabs"><i class="fa fa-bar-chart fa-fw fa-lg"></i> <b>Visualize</b></a>
           <ul class="sidenav nav collapse" id="vtabs">
               <li><a href="#tabletab" data-toggle="tab" onclick="showResultTab();">Table</a></li>
               <li><a href="#scatterplottab" data-toggle="tab" onclick="showResultTab();onclickScatterPlot();">Scatterplot</a></li>
               <li><a href="#linetab" data-toggle="tab" onclick="showResultTab();onclickLinePlot();">Line plot</a></li>
           </ul>
         </li>
       </ul>
     </div>

       </div> <!-- end col-2 -->

  </div> <!-- end row -->
</div> <!-- end container -->
