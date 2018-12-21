<?php
$allteam = $this->Author->getWhere('core = "true"');
array_unshift($allteam, $allteam[1]);
unset($allteam[2]);

$this->team = array();
$this->coreteam = array();
$core_ids = array(1,2,27,86,348,970,1140);
foreach( $allteam as $t ) {
  if( in_array($t->id, $core_ids) ) {
    array_push($this->coreteam, $t);
  } else {
    array_push($this->team, $t);
  }
}
?>
