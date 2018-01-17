<?php
class Api_query extends CI_Controller {

  function __construct() {
    parent::__construct();
    
    $this->db = $this->load->database('read',true);
  }

  function index() {
    $this->free_query();
  }

  function free_query() {
    if(isset($_POST['id']))
       $this->id = $this->input->get_post('id');
    $query = $this->input->get_post('q');

    $query = html_entity_decode($query, ENT_COMPAT | ENT_HTML401, 'ISO-8859-1');
    $starttime = microtime(true);
    $result = $this->db->query( $query );
    $this->msc = microtime(true) - $starttime;

    $this->myStatus = '';
    $this->error = false;
    $this->query = $query;

    $partialCommand = ltrim($query); // strip leading characters eg. spaces
    $partialCommand = substr ($partialCommand, 0, 4); /* get first 4 characters for switch()*/
    $partialCommand = strtolower($partialCommand);

    switch($partialCommand) {
      case 'sele':
      case 'desc':
        if( $result !== false ) {
          $this->myStatus = 'SQL was processed: ' . $result->num_rows() . ' rows selected. ';
        } else {
          $mysql_error = $this->db->error();
          $this->error = true;
          $this->myStatus = 'Error '. $mysql_error['code'] . ': '. $mysql_error['message'];
          $this->load->view('json/free_query');
          return;
        }
        break;
      default:
        $this->myStatus = 'Error : Interface only allows DESCRIBE &amp; SELECT.';
        $this->load->view('json/free_query');
        return;
    }

    $fields = array();
    $types = array();
    $results = array();

    $data = $result->field_data();
    for ($k = 0; $k < count($data); $k++)
    {
      $fname = $data[$k]->name;
      $type = $data[$k]->type;

      //if string, check if it can be cast
      if($type == "string" and $result->row()){
        $value = htmlentities($result->row()->$fname);
        $type = $this->guessType($value);
      }

      //make field names unique
      $ctr=1;
      while(array_key_exists($fname,$results))
      {
        $fname = $fname.'_'.$ctr;
        $ctr++;
      }
      array_push($fields,$fname);
      array_push($types, $type);
      $results[$fname] = array();
    }

    $numericColumns = array('double','real','int');
    $this->columns = array();
    for ($k = 0; $k < count($data); $k++)
    {
      $this->columns[] = array('title' => $fields[$k], 'datatype' => $types[$k]);
    }

    $this->rows = array();
    foreach ($result->result_array() as $r) {
      $row = array();
      foreach( $r as $col ) $row[] = htmlentities($col);
      $this->rows[] = $row;
    }

    $this->load->view('json/free_query');
  }

  function guessType($v){
  if($v == "0" or filter_var($v, FILTER_VALIDATE_INT))
    return "integer";
  else if(is_numeric($v))
    return "double";
  else
    return "string";
  }

  function table_feed() {
    $columns = explode(',', $this->input->post('columns'));
    if($this->input->post('column_source'))
      $column_source   = explode(',', $this->input->post('column_source')); // {db,content,wrapper}
    else
      $column_source   = array();

    if($this->input->post('column_content'))
      $column_content = explode(',', $this->input->post('column_content'));
    else
      $column_content  = array();

    $base_sql     = htmlspecialchars_decode( $this->input->post('base_sql') ) . ' ' . $this->input->post('base_sql_additional');
    $columns_count = count($columns);

    $sLimit = '';
    $sWhere = '';
    $sGroup = '';
    $sOrder = '';

    if( $this->input->post('group_by') != false ) {
      $sGroup = 'GROUP BY ' . $this->input->post('group_by');
    }

    if( is_array($columns) == false || $base_sql == false || count($column_source) != count($column_content) ) {
      return;
    }

    $columnsOutput = array();
    $columnsSQL = array();
    for($i = 0; $i < count($columns); ++$i) {
      $columnsSQL[$i]   = '`' . str_replace('.','`.`',$columns[$i]) . '`';
      $array_variable = explode('.',$columns[$i]);
      $columnsOutput[$i]   = end($array_variable);
    }

    if ( $this->input->post('iDisplayStart') !== false && $this->input->post('iDisplayLength') != '-1' ) {
      $sLimit = 'LIMIT ' . $this->input->post('iDisplayStart') . ', ' . $this->input->post('iDisplayLength');
    }

    if ( $this->input->post('iSortCol_0') !== false ) {
      $sOrder = 'ORDER BY  ';
      for ( $i=0 ; $i < intval( $this->input->post('iSortingCols')) ; $i++ ) {
        if ( $this->input->post('bSortable_'.intval($this->input->post('iSortCol_'.$i))) == "true" ) {
          $sOrder .= $columnsSQL[ intval( $this->input->post('iSortCol_'.$i ) ) ] . ' ' . $this->input->post('sSortDir_'.$i ) .', ';
        }
      }

      $sOrder = substr_replace( $sOrder, '', -2 );
      if ( $sOrder == 'ORDER BY' )
      {
        $sOrder = '';
      }
    }

    if ( $this->input->post('sSearch') != false )
    {
      $sWhere = ' AND (';
      for ( $i=0 ; $i<count($columns) ; $i++ )
      {
        if( ($i < count($column_source) && $column_source[$i] != 'content') || $i >= count($column_source) ) {
          $sWhere .= $columnsSQL[$i].' LIKE "%'.$this->input->post('sSearch').'%" OR ';
        }
      }
      $sWhere = substr_replace( $sWhere, '', -3 );
      $sWhere .= ')';
    }

    // Individual column filtering
    for ( $i=0 ; $i<count($columns) ; $i++ )
    {
      if( ($i < count($column_source) && $column_source[$i] != 'content') || $i >= count($column_source) ) {
        if ( $this->input->post('bSearchable_'.$i) == 'true' && $this->input->post('sSearch_'.$i) != '' )
        {
          $sWhere .= ' AND ';
          $sWhere .= $columnsSQL[$i].' LIKE "%'.$this->input->post('sSearch_'.$i).'%" ';
        }
      }
    }

    $result  = $this->db->query($base_sql . ' ' . $sWhere . ' ' . $sGroup . ' ' .$sOrder . ' ' . $sLimit);
    $display = $this->db->query('SELECT FOUND_ROWS() AS found_rows')->row();
    $total   = $this->db->query($base_sql . ' ' . $sGroup);

    $output = array(
      'sEcho' => intval($this->input->post('sEcho')),
      'iTotalRecords' => $total->num_rows(),
      'iTotalDisplayRecords' => $display->found_rows,
      'sColumns' => implode(',', $columnsOutput),
      'aaData' => array()
    );

    foreach( $result->result_array() as $row) {
      $formatted_row = array();
      for($i = 0; $i < $columns_count; ++$i) {
        if($i >= count($column_source) || $column_source[$i] == 'db') {
          $formatted_row[] = $row[$columnsOutput[$i]];
        } else if($column_source[$i] == 'content') {
          $formatted_row[] = $column_content[$i];
        } else if($column_source[$i] == 'wrapper') {
          $formatted_row[] = str_replace( '[CONTENT]', $row[$columnsOutput[$i]],$column_content[$i]);
        } else if($column_source[$i] == 'doublewrapper') {
          $helpVar = explode("~", $row[$columnsOutput[$i]]);
          $formatted_row[] = str_replace( '[CONTENT2]', $helpVar[1], str_replace( '[CONTENT1]', $helpVar[0],$column_content[$i]));
        } else {
          $formatted_row[] = '';
        }
      }
      $output['aaData'][] = $formatted_row;
    }

    echo json_encode( $output );
  }
}
?>
