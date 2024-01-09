<oml:data_features xmlns:oml="http://openml.org/openml">
  <?php foreach( $features as $feature ): ?>
  <oml:feature>
    <oml:index><?php echo $feature->index; ?></oml:index>
    <oml:name><?php echo htmlspecialchars($feature->name); ?></oml:name>
    <oml:data_type><?php echo $feature->data_type; ?></oml:data_type>
    <?php if (is_array($index_ontologies)):
      if (array_key_exists($feature->index, $index_ontologies)): foreach($index_ontologies[$feature->index] as $value): ?>
        <oml:ontology><?php echo htmlspecialchars($value); ?></oml:ontology>
    <?php endforeach; endif; endif; ?>
    <?php if (array_key_exists($feature->index, $index_values)): foreach($index_values[$feature->index] as $value): ?>
      <oml:nominal_value><?php echo htmlspecialchars($value); ?></oml:nominal_value>
    <?php endforeach; endif; ?>
    <oml:is_target><?php echo $feature->is_target; ?></oml:is_target>
    <oml:is_ignore><?php echo $feature->is_ignore; ?></oml:is_ignore>
    <oml:is_row_identifier><?php echo $feature->is_row_identifier; ?></oml:is_row_identifier>
    <oml:number_of_missing_values><?php echo $feature->NumberOfMissingValues; ?></oml:number_of_missing_values>
  </oml:feature>
  <?php endforeach; ?>
</oml:data_features>
