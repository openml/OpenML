<div class="container-fluid topborder endless guidecontainer openmlsectioninfo">
  <div class="col-xs-12 col-md-10 col-md-offset-1 guidesection" id="mainpanel">

    <div class="tabbed-submenu" style="margin-top:10px;">
    <ul class="nav nav-pills pull-right">
      <?php foreach( $this->directories as $d ): ?>
        <li><a onclick="highlight()" id="detail-btn" class="btn btn-default btn-raised btn-info" href="backend/page/<?php echo $d; ?>"><?php echo str_replace("/","",text_neat_ucwords($d)); ?></a></li>
      <?php endforeach; ?>
    </ul>
    </div>

      <div class="tab-pane">
        <ul class="nav nav-tabs" role="tablist">
          <li class="active"><a href="#missing" role="tab" data-toggle="tab">Missing - Page <?php echo $this->page_nr; ?> (<?php echo $this->page_nr * $this->page_limit; ?> - <?php echo $this->page_nr * $this->page_limit + $this->record_count; ?>)</a></li>
          <li><a href="#size" role="tab" data-toggle="tab">Size</a></li>
        </ul>

        <!-- Tab panes -->
        <div class="tab-content">
          <div class="tab-pane active" id="missing">
            <?php
              echo $this->dataoverview->generate_table_static(
                $this->missing_name,
                $this->missing_columns,
                $this->missing_items,
                $this->missing_api_delete_function );
              ?>
          </div>
          <div class="tab-pane" id="size">
            <?php
              echo $this->dataoverview->generate_table_static(
                $this->size_name,
                $this->size_columns,
                $this->size_items,
                $this->size_api_delete_function );
              ?>
          </div>
        </div>
      </div>

  </div>
</div>
