<div class="container-fluid topborder">
  <div class="row">
    <div class="col-lg-10 col-sm-12 col-lg-offset-1 openmlsectioninfo">
      <h2>Meta-dataset</h2>
      <ul class="nav nav-tabs" role="tablist">
        <li<?php if( $this->check == false ) echo ' class="active"'; ?>><a href="#create" role="tab" data-toggle="tab">Create</a></li>
        <li><a href="#overview" role="tab" data-toggle="tab">Meta datasets</a></li>
        <?php if( $this->check ) echo '<li class="active"><a href="#check" role="tab" data-toggle="tab">Check</a></li>'; ?>
      </ul>
      
      <!-- Tab panes -->
      <div class="tab-content">
        <div class="tab-pane<?php if( $this->check == false ) echo ' active'; ?>" id="create">
          <div class="form-group">
            <form method="post" action="">
              <div class="form-group">
                <label class="col-md-2 control-label">Meta dataset type</label>
                <div class="col-md-10">
                  <input type="radio" name="type" value="evaluations" checked="checked" /> Evaluations 
                  <input type="radio" name="type" value="qualities" /> Meta-features
                  <input type="radio" name="type" value="inputs" /> Parameter Settings
                  <span class="help-block">Select output: meta-features per dataset or evaluations per run.</span>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-2 control-label" for="tasktypeDropdown">Task type</label>
                <div class="col-md-10">
                  <select id="tasktypeDropdown" name="task_type" class="form-control">
                    <?php foreach( $this->task_types as $tt ): ?>
                    <option value="<?php echo $tt->ttid; ?>" <?php if($tt->ttid == $this->ttid) echo 'selected="selected"'; ?>><?php echo $tt->name; ?></option>
                    <?php endforeach; ?>
                  </select>
                  <span class="help-block">Select the task type the results should cover.</span>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-2 control-label" for="datasetDropdown">Datasets</label>
                <div class="col-md-10">
                  <input type="text" class="form-control" id="datasetDropdown" name="datasets" placeholder="Include all datasets" value="<?php echo $this->input->post('datasets'); ?>" />
                  <span class="help-block">A comma separated list of dataset tags. Leave empty to include all datasets.</span>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-2 control-label" for="taskDropdown">Tasks</label>
                <div class="col-md-10">
                  <input type="text" class="form-control" id="taskDropdown" name="tasks" placeholder="Include all tasks" value="<?php echo $this->input->post('tasks'); ?>" />
                  <span class="help-block">A comma separated list of task tags. Leave empty to include all tasks on the specified datasets.</span>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-2 control-label" for="flowDropdown">Flows</label>
                <div class="col-md-10">
                  <input type="text" class="form-control" id="flowDropdown" name="flows" placeholder="Include all flows" value="<?php echo $this->input->post('flows'); ?>">
                  <span class="help-block">A comma separated list of flow tags. Leave empty to include all flows.</span>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-2 control-label" for="setupDropdown">Setups</label>
                <div class="col-md-10">
                  <input type="text" class="form-control" id="setupDropdown" name="setups" placeholder="Include all setups" value="<?php echo $this->input->post('setups'); ?>">
                  <span class="help-block">A comma separated list of setups tags. Leave empty to include all setups on the specified flows.</span>
                </div>
              </div>
              <div class="form-group">
                <label class="col-md-2 control-label" for="functionSelect">Evaluation Measures</label>
                <div class="col-md-10">
                  <?php foreach( $this->measures as $m ): ?>
                  <input type="checkbox" id="functionSelect" name="functions[]" value="<?php echo $m; ?>" checked />&nbsp;<?php echo str_replace( '_', ' ', $m ); ?><br/>
                  <?php endforeach; ?>
                  <span class="help-block">Select at least one evaluation measure you&#39;re interested in </span>
                </div>
              </div>
              <?php if( $this->ion_auth->is_admin() ): ?> 
              <div class="form-group">
                <label class="col-md-2 control-label" for="scheduke">Schedule</label>
                <div class="col-md-10">
                   <input type="checkbox" id="schedule" name="schedule" />&nbsp;Schedule absent runs to be performed.<br/>
                  <span class="help-block">Only check this box prior to clicking on the check button. Will be ignored for the create button.  </span>
                </div>
              </div>
              <?php endif; ?> 
              <div class="form-group">
                <input class="btn btn-primary" type="submit" name="check" value="Check"/>
                <input class="btn btn-primary" type="submit" name="create" value="Create"/>
              </div>
            </form>
          </div>
        </div>
        <div class="tab-pane" id="overview">
          <?php
            echo $this->dataoverview->generate_table_static( 
              $this->name, 
              $this->columns, 
              $this->items );
          ?>
        </div>
        <div class="tab-pane<?php if( $this->check ) echo ' active'; ?>" id="check">
          <div>
            <?php if( $this->data == false ): ?>
              No runs.
            <?php else: ?>
              <table>
                <tr>
                  <td>&nbsp;</td>
                  <?php foreach( end($this->data) as $task => $value ) : // TODO: remove center tag and replace by css identifier ?>
                    <td data-toggle="tooltip" data-placement="top" title="Task <?php echo $task; ?> - <?php echo $this->task_reference[$task]['task_type']; ?> on <?php echo $this->task_reference[$task]['dataset']; ?>"><center>T</center></td>
                  <?php endforeach; ?>
                </tr>
                <?php foreach( $this->data as $setup_id => $tasks ) : ?>
                <tr>
                  <td data-toggle="tooltip" data-placement="top" title="<?php echo $this->setup_reference[$setup_id]['setup_string']; ?>">Setup <?php echo $setup_id; ?>: <span><?php echo cutoff( $this->setup_reference[$setup_id]['name'], 20 ); ?></span></td>
                  <?php foreach( $tasks as $task => $present ): ?>
                    <?php       if ($present && $this->error[$setup_id][$task] != false) { ?>
                      <td data-toggle="tooltip" title="<?php echo $this->error[$setup_id][$task]; ?>" style="width: 20px; " class="table-error">&nbsp;</td>
                    <?php } elseif ($present && $this->warning[$setup_id][$task] != false) { ?>
                      <td data-toggle="tooltip" title="<?php echo $this->warning[$setup_id][$task]; ?>" style="width: 20px; " class="table-warning">&nbsp;</td>
                    <?php } elseif ($present) { ?>
                      <td style="width: 20px; " class="table-present">&nbsp;</td>
                    <?php } else { ?>
                      <td style="width: 20px; " class="table-absent">&nbsp;</td>
                    <?php } ?>
                  <?php endforeach; ?>
                </tr>
                <?php endforeach; ?>
              </table>
              <div>Done: <?php echo $this->runs_done; ?> / <?php echo $this->runs_total; ?></div>
            <?php endif; ?>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
