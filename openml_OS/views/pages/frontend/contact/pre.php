<?php
$this->team = $this->Author->getWhere('core = "true"');
array_unshift($this->team, $this->team[1]);
unset($this->team[2]);
?>
