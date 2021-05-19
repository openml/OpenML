<?php

function validateXml( $xmlDocument, $xsdDocument, &$xmlErrors, $from_file = true ) {
  $xmlErrors = '';
  libxml_use_internal_errors(true);

  $xml = new DOMDocument();
  if($from_file)
    $xml->load( $xmlDocument );
  else
    $xml->loadXML( $xmlDocument );

  foreach (libxml_get_errors() as $error) {
    $xmlErrors .= $error->message . '. ';
  }

  if ( $xml->schemaValidate( $xsdDocument ) ) {
     return true;
  } else {
    $xmlErrors .= 'XML does not correspond to XSD schema. ';
    foreach (libxml_get_errors() as $error) {
      $xmlErrors .= 'Error ' . $error->message . ' on line ' . $error->line . ' column ' . $error->column . '. ';
    }
    return false;
  }
}

/**
 * Puts quotes around all non-numeric values in an array-like string
 */
function quote_array_strings($in){
  $array = explode(",",str_replace(array('[',']'),'',$in));
  return "[".implode(',', array_map(function($value) {
    if(!is_numeric($value)) {
        return '"' . trim(utf8_decode($value)) . '"';
        //adds double quotes, but if you prefer single quotes, use:
        //return "'" . $value . "'";
    } else {
        return $value;
    }
  }, $array))."]";
}

/**
 *  @function all_tags_from_xml()
 *    returns an asociative array containing all selected tag-names as keys
 *    and all tag content as the values.
 *
 *  @param $xml (object): The an XML object containing the relevant children
 *    obtained from the XML. Could be obtained by XML->children('oml', true)
 *    or something similar, to get all childeren in the OML namespace.
 *  @param $configuration (2d string array): an array configuring which fields to be
 *    included in the return value. $configuration can contain 4 sub arrays, 'array',
 *    'csv', 'plain' and 'string'. Putting a field in one of these sub arrays, deter-
 *    mines
 *  @param $return_array (string array): The base array to add tags to. Usually, when
 *    some fields are already set, these can be joined with the fields to
 *    be set within this function.
 *
 *  @return (MULTIPLE VALUES array): asociative array containing all selected tag-names
 *    as keys and all tag content as the values.
 */

function all_tags_from_xml( $xml, $configuration = array(), $return_array = array() ) {
  $csv_tags = array();
  $include = array_collapse($configuration);
  print_r($include);

  foreach( $xml as $key => $value ) {

    if( in_array( $key, $include ) ) {

      if( array_key_exists( 'array', $configuration ) && in_array( $key, $configuration['array'] ) ) {
        // returned in plain array
        if( !array_key_exists( $key, $return_array ) ) {
          $return_array[$key] = array();
        }
        $return_array[$key][] = $value;
      } elseif( array_key_exists( 'csv', $configuration ) && in_array( $key, $configuration['csv'] ) ) {
        // returned in CSV format
        if( !array_key_exists( $key, $csv_tags ) ) {
          $csv_tags[$key] = array();
        }
        $csv_tags[$key][] = trim( $value );
      } elseif( array_key_exists( 'plain', $configuration ) && in_array( $key , $configuration['plain'] ) ) {
        // returned plain (xml object)
        $return_array[$key] = $value;
      } elseif( array_key_exists( 'string', $configuration ) && in_array( $key , $configuration['string'] ) ) {
        // returned as string
        $return_array[$key] = trim($value);
      } else {
        print_r($key);
        // an illegal or undefined category
      }
    }
    else
      {print_r("NOT FOUND ");
    print_r($key);
        print_r("NOT FOUND\n");}
  }

  foreach( $csv_tags as $key => $value ) {
    $return_array[$key] = putcsv( $value );
  }
 
  return $return_array;
}

function xsd( $name, $controller, $versionName = null ) {
  $version = '';
  
  if ($versionName) {
    $version = $versionName . '/';
  }
  $filename = APPPATH.'views/pages/' . $controller . '/' . $version . 'xsd/' . $name . '.xsd';
  if (file_exists($filename) == false) {
    return false;
  } else{
    return $filename;
  }
}

function sub_xml( $xmlFile, $source, $version = 0 ) {
  $ci = &get_instance();
  if ($version === 0) {
    $view = 'pages/rest_api/xml/'.$xmlFile.'.tpl.php';
  } else {
    $view = 'pages/api_new/'.$version.'/xml/'.$xmlFile.'.tpl.php';
  }
  $ci->load->view( $view, $source );
}

function camelcaseToUnderscores( $string ) {
  return strtolower( preg_replace( '/(?<=\\w)(?=[A-Z])/', "_$1", $string ) );
}

function underscoresToCammelcase( $string ) {
  return lcfirst( str_replace( ' ', '', ucwords( str_replace( '_', ' ', $string ) ) ) );
}

function get_arff_features( $datasetUrl, $class = false ) {
  $ci = &get_instance();
  $eval = PATH . APPPATH . 'third_party/OpenML/Java/evaluate.jar';
  $res = array();
  $code = 0;

  $heap = '-Xmx' . ( $ci->input->is_cli_request() ?
    $ci->config->item('java_heap_space_cli') :
    $ci->config->item('java_heap_space_web') );

  $command = "java $heap -jar $eval -f data_features -d $datasetUrl";
  if($class != false)
    $command .= ' -c ' . $class;

  $ci->Log->cmd( 'ARFF Feature Extractor', $command );

  if(function_enabled('exec') === false ) {
    return false;
  }
  exec( CMD_PREFIX . $command, $res, $code );

  if( $code == 0 && is_array( $res ) ) {
    return json_decode( implode( "\n", $res ) );
  } else {
    return false;
  }
}

// replaces a tmp uploaded file with one that has been imported and exported by weka
function validate_arff( $to_folder, $filepath, $name, $did ) {
  $ci = &get_instance();
  $weka = PATH . APPPATH . 'third_party/OpenML/Java/weka.jar';
  $res = array();
  $code = 0;
  $oldUrl = DATA_PATH . $filepath;
  $newpath = strrev(implode(strrev('/openml_'), explode('/', strrev($filepath), 2)));
  $newUrl = DATA_PATH . $newpath;

  $heap = '-Xmx' . ( $ci->input->is_cli_request() ?
    $ci->config->item('java_heap_space_cli') :
    $ci->config->item('java_heap_space_web') );

  $command = "java $heap -cp $weka weka.core.converters.ArffLoader $oldUrl > $newUrl";
  $ci->Log->cmd( 'ARFF Validation', $command );

  if(function_enabled('exec') === false ) {
    return false;
  }

  exec( CMD_PREFIX . $command, $res, $code );

  //Guess the id of the dataset and add it to the top of the file
  $info = '% Data set "'.$name.'". For more information, see http:\/\/openml.org\/d\/'.$did;
  $string = '1s/^/'.$info.'\n/';
  $command2 = "sed -i -e '$string' $newUrl";
  exec( CMD_PREFIX . $command2, $res, $code );

  if( $code == 0 ) {
    return $newpath;
  } else {
    return false;
  }
}

function features_array_contains( $value, $array, $case_insensitive = false ) {
  foreach( $array as $item ) {
    if( $item->name == $value ) {
      return true;
    } elseif( $case_insensitive && strtolower($item->name) == strtolower($value) ) {
      return true;
    }
  }
  return false;
}

function insert_arff_features( $did, $features ) {
  $ci = &get_instance();
  foreach( $features as $f ) {
    $feature_array = (array) $f;
    $feature_array['did'] = $did;
    $ci->Data_feature->insert( $feature_array );
  }
}

function insert_arff_qualities($did, $qualities) {
  $ci = &get_instance();
  foreach( $qualities as $q ) {
    $quality = array( 'data' => $did, 'quality' => $q->name, 'value' => $q->value );
    if( property_exists($q, 'label') ) {
      $quality['label'] = $q->label;
    }

    if( $ci->Data_quality->getWhere( assoc_to_string( $quality, array('value') ) ) === false ) {
      $ci->Data_quality->insert( $quality );
    }
  }
}

// @param: $filter: all entries of filter will NOT be used in result
function assoc_to_string( $arr, $filter ) {
  $new_arr = array();
  foreach( $arr as $key => $value ) {
    if( in_array( $key, $filter ) == false ) {
      $new_arr[] = $key . ' = "' . $value . '" ';
    }
  }
  return implode( ' AND ', $new_arr );
}

function xml_size( $xml_resource, $xml_field ) {
  if( $xml_resource->children('oml', true)->{$xml_field} ) {
    return count( $xml_resource->children('oml', true)->{$xml_field} );
  } else {
    return 0;
  }
}

function xml_subsize( $xml_resource, $xml_field, $subfield ) {
  if( $xml_resource->children('oml', true)->{$xml_field} ) {
    return count( $xml_resource->children('oml', true)->{$xml_field}->children('oml', true)->{$subfield} );
  } else {
    return 0;
  }
}

function xml2object ( $xmlObject, $attributes = false ) {
    $out = new stdClass ();
    foreach ( (array) $xmlObject as $index => $node )
      $out->{$index} = ( is_object ( $node ) ) ? xml2object ( $node ) : $node;
    if( $attributes ) {
      foreach ( $xmlObject->attributes() as $index => $node ) {
        $out->{$index} = ''. $node;
      }
    }

    return $out;
}

function xml2assoc ( $xmlObject, $attributes = false ) {
    $out = array ();
    foreach ( (array) $xmlObject as $index => $node )
      $out[$index] = ( is_object ( $node ) ) ? xml2object ( $node ) : $node;
    if( $attributes ) {
      foreach ( $xmlObject->attributes() as $index => $node ) {
        $out[$index] = ''. $node;
      }
    }

    return $out;
}
?>
