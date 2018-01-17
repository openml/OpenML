<script>
$(function() {

 $("#datasetDropdown").autocomplete({
  html: true,
  minLength: 0,
  source: function(request, fresponse) {
    client.suggest({
    index: 'openml',
    type: 'data',
    body: {
     mysuggester: {
      text: request.term.split(/[, ]+/).pop(),
      completion: {
       field: 'suggest',
       fuzzy : true,
       size: 10
      }
     }
    }
   }, function (error, response) {
       fresponse($.map(response['mysuggester'][0]['options'], function(item) {
        if(item['payload']['type'] == 'data')
  return { 
    type: item['payload']['type'], 
    id: item['payload']['data_id'], 
    description: item['payload']['description'].substring(0,50), 
    text: item['text'] 
    };
  }));
   });
  },
  select: function( event, ui ) {
  $val = $('#datasetDropdown').val().split(/[, ]+/);
  $val.pop();
  $val = $val.join(", ");
  if($val.length>0)
    $val = $val + ", ";
  $('#datasetDropdown').val( $val + ui.item.id);  console.log(ui.item.id); return false;
  }

}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
      return $( "<li>" )
        .append( '<a><i class="' + icons[item.type] + '"></i> ' + item.text + ' <span>' + item.description + '</span></a>' )
        .appendTo( ul );
    }

 $("#flowDropdown").autocomplete({
  html: true,
  minLength: 0,
  source: function(request, fresponse) {
    client.suggest({
    index: 'openml',
    type: 'flow',
    body: {
     mysuggester: {
      text: request.term.split(/[, ]+/).pop(),
      completion: {
       field: 'suggest',
       fuzzy : true,
       size: 10
      }
     }
    }
   }, function (error, response) {
       fresponse($.map(response['mysuggester'][0]['options'], function(item) {
        if(item['payload']['type'] == 'flow')
  return { 
    type: item['payload']['type'], 
    id: item['payload']['flow_id'], 
    description: item['payload']['description'].substring(0,50), 
    text: item['text'] 
    };
  }));
   });
  },
  select: function( event, ui ) {
  $val = $('#flowDropdown').val().split(/[, ]+/);
  $val.pop();
  $val = $val.join(", ");
  if($val.length>0)
    $val = $val + ", ";
  $('#flowDropdown').val( $val + ui.item.id);  console.log(ui.item.id); return false;
  }

}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
      return $( "<li>" )
        .append( '<a><i class="' + icons[item.type] + '"></i> ' + item.text + ' <span>' + item.description + '</span></a>' )
        .appendTo( ul );
    }
});


</script>


<div class="redheader">
  <h1>Compare flows</h1>
  <p>Compare how well different flows perform over several tasks.</p>
</div>
<div class="form-group">
  <label class="col-md-2 control-label" for="datasetDropdown">Task type</label>
  <div class="col-md-10">
    <select class="form-control input-small selectpicker" name="tasktype" id="ttDropDown">
      <option value="">Any task type</option>
      <?php
        $taskparams['index'] = 'openml';
        $taskparams['type']  = 'task_type';
        $taskparams['body']['query']['match_all'] = array();
        $searchclient = $this->searchclient->search($taskparams);
        $alltasks = $searchclient['hits']['hits'];
        foreach($alltasks as $h){?>
      <option value="<?php echo $h['_id']; ?>"><?php echo $h['_source']['name']; ?></option>
      <?php } ?>
    </select>
    <span class="help-block"></span>
  </div>
  </div>
  <div class="form-group">
    <label class="col-md-2 control-label" for="flowDropdown">Flows</label>
    <div class="col-md-10">
      <input type="text" class="form-control" id="flowDropdown" placeholder="Include all flows" value="">
      <span class="help-block">A comma separated list of flows. Leave empty to include all flows.</span>
    </div>
  </div>
  <div class="form-group">
    <label class="col-md-2 control-label" for="datasetDropdown">Datasets</label>
    <div class="col-md-10">
      <input type="text" class="form-control" id="datasetDropdown" placeholder="Include all datasets" value="" />
      <span class="help-block">A comma separated list of datasets. Leave empty to include all datasets.</span>
    </div>
  </div>

  <div id="accordion2" style="margin-bottom:15px">
    <div class="query-heading">
      <a data-toggle="collapse" href="#collapseOne">
      <i class="fa fa-caret-down fa-fw"></i>  Advanced options
      </a>
    </div>
    <div id="collapseOne" class="panel-collapse collapse">
      <div class="query-body">
  <div class="form-group">
    <label class="col-md-2 control-label" for="algorithmDefault">Default settings</label>
    <div class="col-md-10">
      <input type="checkbox" id="algorithmDefault" checked="checked" />
      <span class=" help-block2" >
        Only include default parameter settings. Deselect with caution: allowing all parameter settings may yield many results.
      </span>
    </div>
  </div>

  <div class="form-group">
    <label class="col-md-2 control-label" for="evaluationmetricDropdown">Evaluation metric</label>
    <div class="col-md-10">
      <input type="text" class="form-control input-small" id="evaluationmetricDropdown" value="predictive_accuracy" />
      <span class="help-block">Select the desired evaluation metric. </span>
    </div>
  </div>


  <div class="form-group">
    <label class="col-md-2 control-label" for="evaluationmethodDropdown">Evaluation method</label>
    <div class="col-md-10">
      <input type="text" class="form-control input-small" id="evaluationmethodDropdown" placeholder="" disabled="disabled" value="CrossValidation" />
      <span class="help-block">Currently, cross validation is the only evaluation method used.</span>
    </div>
  </div>

  <div class="form-group">
    <label class="col-md-2 control-label" for="inputCtAlgorithm">Crosstabulation</label>
    
    <div class="col-md-10">
      <label class="radio">
        <input type="radio" name="crosstabulate" value="none"  />
        None <small>(Columns are algorithm, dataset and evaluation, respectively)</small>
      </label>
      <label class="radio">
        <input type="radio" name="crosstabulate" value="algorithm" />
        Crosstabulate over algorithms <small>(rows contain algorithms, columns contains datasets)</small>
      </label>
      <label class="radio">
        <input type="radio" name="crosstabulate" value="dataset" checked />
        Crosstabulate over datasets <small>(rows contain datasets, columns contain algorithms)</small>
      </label>
    </div>
  </div>
      </div>
    </div>
</div>
  <div class="form-group">
    <button id="wizardquery-btn" data-loading-text="Querying..." autocomplete="off" type="button" onclick="wizardQuery( $('#flowDropdown').val(), $('#algorithmDefault').prop('checked'), $('#datasetDropdown').val(), $('#evaluationmethodDropdown').val(), $('#evaluationmetricDropdown').val(), $('input:radio[name=crosstabulate]:checked').val() );showResultTab();" class="btn btn-primary">
      Run Query
    </button>
  </div>
  
</form>
