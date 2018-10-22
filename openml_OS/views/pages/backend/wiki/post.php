<?php
   if($this->input->post('id')){
     $id = $this->input->post('id');

     $datasets = $this->Dataset->query( 'SELECT did from dataset'.($id != 'all' ? ' where did in ('.$id.')' : '') );
     foreach($datasets as $d){
	$this->messages[] = $this->wiki->export_to_wiki($d->did);
     }
   } else if($this->input->post('flow-id')){
     $id = $this->input->post('flow-id');

     $flows = $this->Dataset->query( 'SELECT id from implementation'.($id != 'all' ? ' where id in ('.$id.')' : '') );
     foreach($flows as $f){
	$this->messages[] = $this->wiki->export_flow_to_wiki($f->id);
     }
   } else if($this->input->post('wiki-id')){
     $id = $this->input->post('wiki-id');

     $datasets = $this->Dataset->query( 'SELECT did from dataset'.($id != 'all' ? ' where did in ('.$id.')' : '') );
     foreach($datasets as $d){
	$this->messages[] = $this->wiki->import_from_wiki($d->did);
     }
   }
?>
