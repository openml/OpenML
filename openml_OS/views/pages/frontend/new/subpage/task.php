<div class="panel">
  <h1><a href="t"><i class="fa fa-trophy"></i></a> Create new task</h1>
  <div class="row">
  <div class="col-md-6">
		<h2>Choose Task Type</h2>
    <div class="form-group">
		  <label class="control-label" for="input_dataset_licence">Task types</label>
			<select id="selectTaskType" class="form-control">
        <option value="0" selected="selected">Select a task type</option>
			  <?php foreach( $this->task_types as $tt ): ?>
        <option value="<?php echo $tt->ttid; ?>" <?php echo $tt->selected; ?>><?php echo $tt->name; ?></option>
        <?php endforeach; ?>
			</select>
	  </div>

    <ul class="nav nav-tabs" id="tab1" style="display: none;">
      <?php foreach( $this->task_types as $tt ): ?>
      <li><a href="#task-type-desc-<?php echo $tt->ttid; ?>"><?php echo $tt->name; ?></a></li>
      <?php endforeach; ?>
    </ul>

    <div class="tab-content">
      <?php foreach( $this->task_types as $tt ): ?>
      <div class="tab-pane" id="task-type-desc-<?php echo $tt->ttid; ?>">
        <?php echo $tt->description; ?>
      </div>
      <?php endforeach; ?>
    </div>
  </div>

  <!-- task creation form -->

  <div class="col-md-6">
    <ul class="nav nav-tabs" id="form-task-type-tabs" style="display: none; ">
      <li><a href="#task-type-0" role="tab" data-toggle="tab">Select task type</a></li>
      <?php foreach( $this->task_types as $tt ): ?>
        <li><a href="#task-type-<?php echo $tt->ttid; ?>" role="tab" data-toggle="tab"><?php echo $tt->name; ?></a></li>
      <?php endforeach; ?>
    </ul>
    <div class="tab-content">
    <?php foreach( $this->task_types as $tt ): ?>
      <div class="tab-pane" id="task-type-<?php echo $tt->ttid; ?>">
        <h2>Task inputs</h2>
        <form method="post" action="">
          <input type="hidden" name="ttid" value="<?php echo $tt->ttid; ?>" />
          <?php
            foreach( $tt->in as $io ):
              $id = 'input_' . $tt->ttid . '-' . $io->name;
              $template_search = json_decode( $io->template_search );
              $name = text_neat_ucwords( $io->name );
              $default = ($io->name == 'source_data' and isset($this->dataname)) ? $this->dataname : '';
              $placeholder = '';
              if( $template_search ) {
                if( property_exists( $template_search, 'name' ) ) { $name = $template_search->name; }
                if( property_exists( $template_search, 'default' ) ) { $default = $template_search->default; }
                if( property_exists( $template_search, 'placeholder' ) ) {  $placeholder = $template_search->placeholder; }
              }
          ?>
		      <div class="form-group" id="<?php echo  $id ; ?>_formgroup" <?php if($io->requirement == 'hidden') { echo 'style="display: none"; '; } ?>>
		        <label class="control-label" for="<?php echo  $id ; ?>"><?php echo $name; ?></label>
            <?php
              if( $template_search && property_exists( $template_search, 'type' ) && $template_search->type == 'select' ): // make a dropdown
              $sql = 'SELECT * FROM `'.$template_search->table.'` WHERE ttid = ' . $io->ttid;
              $types = $this->Dataset->query( $sql ); ?>
            <select class="form-control input-small selectpicker" id="dropdown_input_<?php echo $io->ttid;?>_<?php echo $io->name;?>" name="<?php echo $io->name;?>">
              <?php foreach($types as $type):
                // parses data to be used in javascript
                $data_str = ''; foreach( get_object_vars( $type ) as $key => $value ) { $data_str .= "data-dbfield_$key=\"$value\" "; } ?>
              <option value="<?php echo $type->{$template_search->key}; ?>" <?php echo $data_str; ?> <?php if($this->input->post($io->name) == $type->{$template_search->key}) echo 'selected="selected"'; ?>><?php echo $type->{$template_search->value}; ?></option>
              <?php endforeach; ?>
            </select>
          <?php else: // makes a plain text input ?>
		        <input type="text" class="form-control" id="<?php echo  $id; ?>" name="<?php echo $io->name;?>" placeholder="<?php echo $placeholder; ?>" value="<?php echo $this->input->post($io->name) ? $this->input->post($io->name) : $default; ?>" />
          <?php endif; ?>
		      </div>
          <?php endforeach; ?>
          <div class="form-group">
		        <input class="btn btn-primary" type="submit" value="Submit"/>
		      </div>
        </form>
      </div>
    <?php endforeach; ?>
    </div>
  </div>
</div>

  <?php if( is_array( $this->task_ids ) && $this->task_ids ): ?>
  <div class="row">
  <div class="col-md-12">
    <h2>Tasks</h2>
    <?php echo implode(', ' , $this->task_ids ); ?>
    <br/>
    <?php echo $this->new_text; ?>
  </div>
</div>
  <?php endif; ?>
</div> <!-- end container -->
