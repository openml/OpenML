<oml:run xmlns:oml="http://openml.org/openml">
  <oml:run_id><?php echo $source->rid; ?></oml:run_id>
  <oml:uploader><?php echo $source->uploader; ?></oml:uploader>
  <oml:uploader_name><?php echo $source->user_name; ?></oml:uploader_name>
  <oml:task_id><?php echo $source->task_id; ?></oml:task_id>
  <oml:task_type><?php echo $source->task_type; ?></oml:task_type>
  <?php if($source->task_evaluation){ ?>
    <oml:task_evaluation_measure><?php echo $source->task_evaluation->value; ?></oml:task_evaluation_measure>
  <?php } ?>
  <oml:flow_id><?php echo $source->setup->implementation_id; ?></oml:flow_id>
  <oml:flow_name><?php echo $source->flow_name; ?></oml:flow_name>
  <oml:setup_id><?php echo $source->setup->sid; ?></oml:setup_id>
  <?php if($source->eval_data != false):?> <oml:error><?php echo htmlspecialchars($source->eval_data->error); ?></oml:error> <?php endif; ?>
  <?php if($source->error_message !== null):?> <oml:error_message><?php echo $source->error_message; ?></oml:error_message> <?php endif; ?>
  <?php if($source->run_details !== null):?> <oml:run_details><?php echo $source->run_details; ?></oml:run_details> <?php endif; ?>
  <oml:setup_string><?php echo htmlspecialchars($source->setup->setup_string); ?></oml:setup_string>
  <?php if(is_array($source->inputSetting)) foreach( $source->inputSetting as $parameter ): ?>
    <oml:parameter_setting>
      <oml:name><?php echo $parameter->name;?></oml:name>
      <oml:value><?php echo htmlspecialchars($parameter->value);?></oml:value>
      <oml:component><?php echo $parameter->implementation_id;?></oml:component>
    </oml:parameter_setting>
  <?php endforeach; ?>
  <?php if(is_array($source->tags)) foreach( $source->tags as $tag ): ?>
    <oml:tag><?php echo $tag;?></oml:tag>
  <?php endforeach; ?>
  <?php if(is_array($source->inputData)): ?>
    <oml:input_data>
    <?php foreach( $source->inputData as $d ): ?>
      <oml:dataset>
        <oml:did><?php echo $d->did; ?></oml:did>
        <oml:name><?php echo $d->name; ?></oml:name>
        <oml:url><?php echo $d->url; ?></oml:url>
      </oml:dataset>
    <?php endforeach; ?>
    </oml:input_data>
  <?php endif; ?>
  <?php if(is_array($source->outputData) ): ?>
    <oml:output_data>
    <?php if(array_key_exists('dataset',$source->outputData) ): ?>
      <?php foreach( $source->outputData['dataset'] as $d ): ?>
      <oml:dataset>
        <oml:did><?php echo $d->did; ?></oml:did>
        <oml:name><?php echo $d->name; ?></oml:name>
        <oml:url><?php echo $d->url; ?></oml:url>
      </oml:dataset>
    <?php endforeach; ?>
    <?php endif; if(array_key_exists('runfile',$source->outputData) ): ?>
      <?php foreach( $source->outputData['runfile'] as $r ): ?>
      <oml:file>
        <oml:did>-1</oml:did> <!-- Deprecated field, will be removed during next upgrade. -->
        <oml:file_id><?php echo $r->file_id; ?></oml:file_id>
        <oml:name><?php echo $r->field; ?></oml:name>
        <oml:url><?php $f = $this->File->getById($r->file_id); echo fileRecordToUrl( $f ); ?></oml:url>
      </oml:file>
      <?php endforeach; ?>
    <?php endif; if(array_key_exists('evaluations', $source->outputData) ): ?>
      <?php foreach( $source->outputData['evaluations'] as $e ): ?>
        <oml:evaluation>
          <oml:name><?php echo $e->{'name'}; ?></oml:name>
          <?php if ($e->value != null): ?><oml:value><?php echo $e->value; ?></oml:value><?php endif; ?>
          <?php if ($e->array_data != null): ?><oml:array_data><?php echo $e->array_data; ?></oml:array_data><?php endif; ?>
        </oml:evaluation>
      <?php endforeach; ?>
    <?php endif; if(array_key_exists('evaluations_fold', $source->outputData) ): ?>
      <?php foreach( $source->outputData['evaluations_fold'] as $e ): ?>
        <oml:evaluation repeat="<?php echo $e->{'repeat'}; ?>" fold="<?php echo $e->{'fold'}; ?>">
          <oml:name><?php echo $e->{'name'}; ?></oml:name>
          <?php if ($e->value != null): ?><oml:value><?php echo $e->value; ?></oml:value><?php endif; ?>
          <?php if ($e->array_data != null): ?><oml:array_data><?php echo $e->array_data; ?></oml:array_data><?php endif; ?>
        </oml:evaluation>
      <?php endforeach; ?>
    <?php endif; ?>
  </oml:output_data>
  <?php endif; ?>
</oml:run>
