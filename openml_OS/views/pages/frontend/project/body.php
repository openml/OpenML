<div class="container topborder">
  <div class="row">
    <div class="col-md-12">

      <div class="tab-pane">
        <ul class="nav nav-tabs" role="tablist">
          <li><a href="#data" role="tab" data-toggle="tab">Data</a></li>
          <li class="active"><a href="#task" role="tab" data-toggle="tab">Tasks</a></li>
          <li><a href="#setup" role="tab" data-toggle="tab">Setups</a></li>
     <!--     <li><a href="#run" role="tab" data-toggle="tab">Runs</a></li> -->
        </ul>

        <!-- Tab panes -->
        <div class="tab-content">
          <div class="tab-pane" id="data">
            <?php
             echo $this->dataoverview->generate_table_static( 
                $this->data_name, 
                $this->data_columns, 
                $this->data_items );
              ?>
          </div>
          <div class="tab-pane active" id="task">
            <?php
             echo $this->dataoverview->generate_table_static( 
                $this->task_name, 
                $this->task_columns, 
                $this->task_items );
              ?>
          </div>
          <div class="tab-pane" id="setup">
            <?php
              echo $this->dataoverview->generate_table_static( 
                $this->setup_name, 
                $this->setup_columns, 
                $this->setup_items );
              ?>
          </div>
<?php  /* <div class="tab-pane" id="run">
            <?php
              echo $this->dataoverview->generate_table_static( 
                $this->run_name, 
                $this->run_columns, 
                $this->run_items );
              ?>
          </div> */ ?>
        </div>
      </div>

    </div>
  </div>
</div>
