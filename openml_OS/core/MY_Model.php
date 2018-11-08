<?php
/* TODO: IT'S A SLIGHT HACK TO PUT ALL THE CUSTOM MODELS IN HERE .. */

class MY_Model extends CI_Model {
  function __construct() {
    parent::__construct();
  }
}

require_once('MY_Database_Read_Model.php');
require_once('MY_Database_Write_Model.php');
require_once('MY_Community_Model.php');
require_once('MY_Tag_Model.php');
require_once('MY_Api_Model.php');

?>
