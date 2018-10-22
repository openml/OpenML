<?php
	$this->messages = array();
	$this->index_types = array();
	$this->default_types = array("user","task","measure","flow","data","task_type","run","study","like","download","downvote");
	$this->init_types = array();
	$this->types = $this->elasticsearch->get_types();
	$this->directories = directory_map(APPPATH.'views/pages/backend', 1);
?>
