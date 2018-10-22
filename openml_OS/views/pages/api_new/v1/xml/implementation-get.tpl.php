<?php 
$mapping = array(
  'id' => 'id',
  'uploader' => 'uploader',
  'name' => 'name',
  'custom_name' => 'custom_name',
  'class_name' => 'class_name',
  'version' => 'version',
  'external_version' => 'external_version',
  'description' => 'description',
  'creator' => 'creator',
  'contributor' => 'contributor',
  'upload_date' => 'uploadDate',
  'licence' => 'licence',
  'language' => 'language',
  'full_description' => 'fullDescription',
  'installation_notes' => 'installationNotes',
  'dependencies' => 'dependencies',
  'parameters' => 'parameterSetting',
  'components' => 'components',
  'tag' => 'tag',
  'source_url' => 'sourceUrl',
  'binary_url' => 'binaryUrl',
  'source_format' => 'sourceFormat',
  'binary_format' => 'binaryFormat',
  'source_md5' => 'sourceMd5',
  'binary_md5' => 'binaryMd5',
 ); ?>

<oml:flow xmlns:oml="http://openml.org/openml">
  <?php 
  foreach ($mapping as $key => $value) {
    if (property_exists($source, $value)) {
      if (is_array($source->$value)) {
        if (count($source->$value) > 0 ) {
          sub_xml('implementation-get.' . $value, array('source' => $source->$value), 'v1');
        }
      } elseif ($source->$value != false && $source->$value !== null) {
        $val = $source->$value;
        if ($key == 'upload_date') {
          $val = dateXml($val);
        }
        echo '<oml:'.$key.'>'.htmlentities($val).'</oml:'.$key.'>' . "\n";
      }
    }
  }?>
</oml:flow>
