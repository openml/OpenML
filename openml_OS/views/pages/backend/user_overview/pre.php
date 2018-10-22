<?php
$this->directories = directory_map(APPPATH.'views/pages/backend', 1);

$sql = 'SELECT `u`.`id`, `u`.`username` AS `name`, '.
       'CONCAT(`u`.`first_name`, " ", `u`.`last_name`) AS `full_name`,'.
       '`u`.`affiliation`, `u`.`country`, '.
       'CONCAT(\'<i class="fa fa-fw fa-times" onclick="askConfirmation(\', `u`.`id`, \',\\\'\', `u`.`username`, \'\\\')"></i>\') AS `delete`,' .
       'CONCAT(\'<a href="u/\', `u`.`id`, \'">\', `u`.`username`, \'</a>\') AS `name_link` ' .
       'FROM `users` `u`; ';

$this->columns = array( 'delete', 'id', 'name_link', 'full_name', 'affiliation', 'country' );
$this->items = $this->Author->query( $sql );
$this->name = 'User overview';

$this->api_delete_function = array(
  'function'        => 'openml.user.delete',
  'key'             => 'user_id',
  'filter'          => 'may_delete',
  'id_field'        => 'id',
  'identify_field'  => 'name' );
?>
