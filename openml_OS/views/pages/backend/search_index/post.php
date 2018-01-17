<?php
     if($this->input->post('inittypes')){
	$this->init_types = $this->input->post('inittypes');
	foreach( $this->init_types as $t ):
		$this->messages[] = $this->elasticsearch->initialize_index($t);
	endforeach;
     }

     else if($this->input->post('types')){
	$this->index_types = $this->input->post('types');
  if(!$this->input->post('from_id')){
  	foreach( $this->index_types as $t ):
  		$this->messages[] = $this->elasticsearch->index($t);
  	endforeach;
  } else {
    foreach( $this->index_types as $t ):
      $this->messages[] = $this->elasticsearch->index_from($t,$this->input->post('from_id'));
    endforeach;
  }
     }

     else if($this->input->post('settings')){
       $this->messages[] = $this->elasticsearch->initialize_settings();
     }

     else if($this->input->post('type')){
	$this->index_type = $this->input->post('type');
	$this->index_id = $this->input->post('doc_id');
	$this->messages[] = $this->elasticsearch->index($this->index_type,$this->index_id);
     }

     else if($this->input->post('doc_id') and !$this->input->post('type'))
	$this->messages[] = 'No document type given';
     else if(!$this->input->post('doc_id') and $this->input->post('type'))
	$this->messages[] = 'No document id given';



?>
