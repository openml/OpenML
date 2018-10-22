<?php
     foreach( $this->taskio as $r ):
	if($r['category'] != 'input') continue;
	if($r['type'] == 'Dataset'){
		$dataset = $r['dataset'];
		$dataset_id = $r['value'];
	}
     endforeach; ?>

		<?php if($this->task['source_data']['name'] != 'Learning Curve'){ ?>
        <div class="pull-right">
		        Metric:
				<select class="selectpicker" data-width="auto" onchange="evaluation_measure = this.value; updateTableHeader(); redrawtimechart();">
					<?php foreach($this->allmeasures as $m): ?>
					<option value="<?php echo $m;?>" <?php echo ($m == $this->current_measure) ? 'selected' : '';?>><?php echo str_replace('_', ' ', $m);?></option>
					<?php endforeach; ?>
				</select>
       </div>

      <h2>Timeline</h2>

			<div class="col-sm-12 panel reflow-chart" id="data_result_time">Plotting contribution timeline <i class="fa fa-spinner fa-spin"></i></div>

      <h3>Leaderboard</h3>

      <div class='table-responsive panel reflow-table'><table id="leaderboard" class='table table-striped' style="margin:0">
        <thead>
          <tr>
            <th>Rank</th>
            <th>Name</th>
            <th>Top Score</th>
            <th>Entries</th>
            <th>Highest rank</th>
          </tr>
        </thead>
      </table>
      <p>Note: The leaderboard ignores resubmissions of previous solutions, as well as parameter variations that do not improve performance. </p>
    </div>
<?php } ?>
