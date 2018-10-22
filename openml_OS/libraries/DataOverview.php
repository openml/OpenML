<?php  if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class DataOverview {

  private $counter;

  public function __construct() {
    $this->CI = &get_instance();
  }

  public function generate_table( $table_name, $columns, $column_widths, $sql, $api_delete_function = null, $sort = '[[1, \'desc\']]' ) {
    $variables = array();
    $variables['table_name'] = $table_name;
    $variables['columns'] = $columns;
    $variables['column_widths'] = $column_widths;
    $variables['sql'] = $sql;
    $variables['sort'] = $sort;
    $variables['api_delete_function'] = $api_delete_function;

    return $this->CI->load->view('library_templates/data_overview_default', $variables, true);
  }

  public function generate_table_static( $table_name, $columns, $items, $api_delete_function = null ) {
    $variables = array();
    $variables['table_name'] = $table_name;
    $variables['columns'] = $columns;
    $variables['items'] = $items;
    $variables['api_delete_function'] = $api_delete_function;

    return $this->CI->load->view('library_templates/data_overview_static', $variables, true);
  }

  public function generate_xml( $root, $tag_configuration ) {
    $xml = new SimpleXMLElement('<oml:'.$root.' xmlns:oml="http://openml.org/openml"/>');

    // first obtain the indices
    $indices = array();
    foreach( $tag_configuration as $key => $value ) {
      $indices = array_merge( $indices, array_keys( $value ) );
    }
    sort( $indices );
    foreach( $indices as $index ) {
      foreach( $tag_configuration as $tag_type => $tags ) {
        foreach( $tags as $tag_index => $tag_name ) {
          if( $tag_index == $index ) {
            if( $this->CI->input->post( $tag_name ) ) {
              if( $tag_type == 'csv' ) {
		if(is_array($this->CI->input->post($tag_name)))
		  $values_exploded = $this->CI->input->post($tag_name);
		else
		  $values_exploded = explode( ',', $this->CI->input->post($tag_name) );
                foreach( $values_exploded as $value ) {
                  $xml->addChild('oml:'.$tag_name, $value);
                }
              } else { // TODO: add support for plain and array.
                $value = $this->CI->input->post($tag_name);
                $xml->addChild('oml:'.$tag_name, str_replace('&','&amp;',htmlentities($value)));
              }
            }
          }
        }
      }
    }
    return $xml->asXML();
  }
}
