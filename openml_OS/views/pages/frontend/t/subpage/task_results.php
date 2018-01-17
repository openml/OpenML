
		<?php if($this->task['tasktype']['name'] != 'Learning Curve'){ ?>
        <div class="pull-right">
		        Metric:
				<select class="selectpicker" data-width="auto" onchange="evaluation_measure = this.value; showData();">
					<?php foreach($this->allmeasures as $m): ?>
					<option value="<?php echo $m;?>" <?php echo ($m == $this->current_measure) ? 'selected' : '';?>><?php echo str_replace('_', ' ', $m);?></option>
					<?php endforeach; ?>
				</select>
      </div>
			<h2 style="margin-top:0px;"><?php echo $this->task['runs']; ?> Runs</h2>


      <?php if($this->task['tasktype']['tt_id'] != 6){ ?>

      <div class="col-xs-12 panel">
			     <div id="data_result_visualize" class="reflow-chart">Rendering chart <i class="fa fa-spinner fa-spin"></i></div>
      </div>

      <div class="col-xs-12 panel">
        <div class="table-responsive reflow-table">
           <div id="table-spinner">Rendering table <i class="fa fa-spinner fa-spin"></i></div>
           <table id="tasktable" class="display" width="100%"></table>
        </div>
      </div>

		<?php }} else { ?>
		        Metric:
				<select class="selectpicker" data-width="auto" onchange="evaluation_measure = this.value; redrawCurves();">
					<?php foreach($this->allmeasures as $m): ?>
					<option value="<?php echo $m;?>" <?php echo ($m == $this->current_measure) ? 'selected' : '';?>><?php echo str_replace('_', ' ', $m);?></option>
					<?php endforeach; ?>
				</select>
				<h2 style="margin-top:0px;"><?php echo $this->task['runs']; ?> Runs</h2>

      <h3>Curves</h3>
      <div class="col-xs-12 panel">
			<div class="checkbox"><label>
			<input type="checkbox" name="latestOnly" checked onchange="latestOnly = this.checked; redrawCurves();"> Only newest flow versions</label></div>

			<div id="learning_curve_visualize" style="width: 100%">Plotting curves <i class="fa fa-spinner fa-spin"></i></div>
      </div>

      <h3>Table</h3>
        <div class="panel table-responsive">
				<table id="datatable_main" class="table table-bordered table-condensed table-responsive">
					<?php echo generate_table(
								array('img_open' => '',
										'rid' => 'Run',
										'sid' => 'setup id',
										'name' => 'Flow',
										'value' => str_replace('_',' ',$this->current_measure), ) ); ?>
				</table></div>

		<?php } ?>
