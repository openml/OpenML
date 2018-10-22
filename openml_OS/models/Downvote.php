<?php

class Downvote extends MY_Database_Write_Model {

    function __construct() {
        parent::__construct();
        $this->table = 'downvotes';
        $this->id_column = 'did';
        $this->user_id_column = 'user_id';
        $this->knowledge_id_column = 'knowledge_id';
        $this->knowledge_type_column = 'knowledge_type';
        $this->reason_column = 'reason';
        $this->original_column = 'original';
        $this->time_column = 'time';
        
        $this->reason_table = 'downvote_reasons';
        $this->reason_id_column = 'reason_id';
        $this->reason_description_column = 'description';
        
    }

    function getDownvote($id) {
        $sql = 'SELECT `l`.*, `r`.*
            FROM `' . $this->table . '` AS `l`, `' . $this->reason_table . '` AS `r`
            WHERE `l`.'.$this->reason_column.' = `r`.'.$this->reason_id_column . ($id ? ' AND `l`.`' . $this->id_column . '` = ' . $id : "");

        $ds = $this->Downvote->query($sql);
        if($ds){
            foreach($ds as $d){
                if($d->{$this->original_column}){
                    //$agrees = $this->getDownvotesByKnowledgePieceAndReason($d->{$this->knowledge_type_column},$d->{$this->knowledge_id_column},$d->{$this->reason_column},false);
                    $agrees = $this->getAgreements($d->{$this->knowledge_type_column},$d->{$this->knowledge_id_column},$d->{$this->reason_column});
                    if($agrees){
                        $d->count = count($agrees)+1;
                    }else{
                        $d->count = 1;
                    }
                }else{
                    $d->count = false;
                }
            }
        }
        
        return $ds;
    }

    function getDownvotesByUser($userid, $original=1) {
        $sql = 'SELECT `l`.*, `r`.*
            FROM `' . $this->table . '` AS `l`, `' . $this->reason_table . '` AS `r`
            WHERE `l`.`' . $this->user_id_column . '` = "' . $userid . '"
            AND `l`.'.$this->reason_column.' = `r`.'.$this->reason_id_column
            .' AND `l`.'.$this->original_column.'='.((int)$original);

        $ds = $this->Downvote->query($sql);
        if($ds){
            foreach($ds as $d){
                if($d->{$this->original_column}){
                    //$agrees = $this->getDownvotesByKnowledgePieceAndReason($d->{$this->knowledge_type_column},$d->{$this->knowledge_id_column},$d->{$this->reason_column},false);
                    $agrees = $this->getAgreements($d->{$this->knowledge_type_column},$d->{$this->knowledge_id_column},$d->{$this->reason_column});
                    if($agrees){
                        $d->count = count($agrees)+1;
                    }else{
                        $d->count = 1;
                    }
                }else{
                    $d->count = false;
                }
            }
        }
        
        return $ds;
    }

    function getDownvotesByType($type, $original=1) {
        $sql = 'SELECT `l`.*, `r`.*
            FROM `' . $this->table . '` AS `l`, `' . $this->reason_table . '` AS `r`
            WHERE `l`.`' . $this->knowledge_type_column . '` = "' . $type . '"
            AND `l`.'.$this->reason_column.' = `r`.'.$this->reason_id_column
            .' AND `l`.'.$this->original_column.'='.((int)$original);
        
        $ds = $this->Downvote->query($sql);
        if($ds){
            foreach($ds as $d){
                if($d->{$this->original_column}){
                    //$agrees = $this->getDownvotesByKnowledgePieceAndReason($d->{$this->knowledge_type_column},$d->{$this->knowledge_id_column},$d->{$this->reason_column},false);
                    $agrees = $this->getAgreements($d->{$this->knowledge_type_column},$d->{$this->knowledge_id_column},$d->{$this->reason_column});
                    if($agrees){
                        $d->count = count($agrees)+1;
                    }else{
                        $d->count = 1;
                    }
                }else{
                    $d->count = false;
                }
            }
        }
        
        return $ds;
    }

    function getDownvotesByUserAndType($userid, $type, $original=1) {
        $sql = 'SELECT `l`.*, `r`.*
            FROM `' . $this->table . '` AS `l`, `' . $this->reason_table . '` AS `r`
            WHERE `l`.`' . $this->user_id_column . '` = "' . $userid . '"
            AND `l`.`' . $this->knowledge_type_column . '` = "' . $type . '"
            AND `l`.'.$this->reason_column.' = `r`.'.$this->reason_id_column
            .' AND `l`.'.$this->original_column.'='.((int)$original);

        $ds = $this->Downvote->query($sql);
        if($ds){
            foreach($ds as $d){
                if($d->{$this->original_column}){
                    //$agrees = $this->getDownvotesByKnowledgePieceAndReason($d->{$this->knowledge_type_column},$d->{$this->knowledge_id_column},$d->{$this->reason_column},false);
                    $agrees = $this->getAgreements($d->{$this->knowledge_type_column},$d->{$this->knowledge_id_column},$d->{$this->reason_column});
                    if($agrees){
                        $d->count = count($agrees)+1;
                    }else{
                        $d->count = 1;
                    }
                }else{
                    $d->count = false;
                }
            }
        }
        
        return $ds;
    }

    function getAgreements($type,$id,$reason){
        $sql = 'SELECT `l`.*
            FROM `' . $this->table . '` AS `l`
            WHERE `l`.`' . $this->knowledge_type_column . '` = "' . $type . '"
            AND `l`.`' . $this->knowledge_id_column . '` = "' . $id . '"
            AND `l`.'.$this->reason_column.' = '.$reason
            .' AND `l`.'.$this->original_column.'=0';
        
        return $this->query($sql);
    }
    
    function getDownvotesByKnowledgePieceAndReason($type, $id, $reason, $original=1) {
        $sql = 'SELECT `l`.*
            FROM `' . $this->table . '` AS `l`
            WHERE `l`.`' . $this->knowledge_type_column . '` = "' . $type . '"
            AND `l`.`' . $this->knowledge_id_column . '` = "' . $id . '"
            AND `l`.'.$this->reason_column.' = '.$reason
            .' AND `l`.'.$this->original_column.'='.((int)$original);
        
        $ds = $this->Downvote->query($sql);
        if($ds){
            foreach($ds as $d){
                if($d->{$this->original_column}){
                    //$agrees = $this->getDownvotesByKnowledgePieceAndReason($d->{$this->knowledge_type_column},$d->{$this->knowledge_id_column},$d->{$this->reason_column},false);
                    $agrees = $this->getAgreements($d->{$this->knowledge_type_column},$d->{$this->knowledge_id_column},$d->{$this->reason_column});
                    if($agrees){
                        $d->count = count($agrees)+1;
                    }else{
                        $d->count = 1;
                    }
                }else{
                    $d->count = false;
                }
            }
        }
        
        return $ds;
    }

    function getDownvotesByKnowledgePiece($type, $id, $original=1) {
        $sql = 'SELECT `l`.*, `r`.*
            FROM `' . $this->table . '` AS `l`, `' . $this->reason_table . '` AS `r`
            WHERE `l`.`' . $this->knowledge_type_column . '` = "' . $type . '"
            AND `l`.`' . $this->knowledge_id_column . '` = "' . $id . '"
            AND `l`.'.$this->reason_column.' = `r`.'.$this->reason_id_column
            .' AND `l`.'.$this->original_column.'='.((int)$original);
        
        $ds = $this->Downvote->query($sql);
        if($ds){
            foreach($ds as $d){
                if($d->{$this->original_column}){
                    //$agrees = $this->getDownvotesByKnowledgePieceAndReason($d->{$this->knowledge_type_column},$d->{$this->knowledge_id_column},$d->{$this->reason_column},false);
                    $agrees = $this->getAgreements($d->{$this->knowledge_type_column},$d->{$this->knowledge_id_column},$d->{$this->reason_column});
                    if($agrees){
                        $d->count = count($agrees)+1;
                    }else{
                        $d->count = 1;
                    }
                }else{
                    $d->count = false;
                }
            }
        }
        
        return $ds;
    }

    function getByIds($u_id, $k_type, $k_id) {
        $sql = 'SELECT `l`.*, `r`.*
            FROM `' . $this->table . '` AS `l`, `' . $this->reason_table . '` AS `r`
            WHERE `l`.`' . $this->knowledge_type_column . '` = "' . $k_type . '"
            AND `l`.`' . $this->knowledge_id_column . '` = "' . $k_id . '"
            AND `l`.`' . $this->user_id_column . '` = "' . $u_id . '"
            AND `l`.'.$this->reason_column.' = `r`.'.$this->reason_id_column;
        
        $ds = $this->Downvote->query($sql);
        if($ds){
            foreach($ds as $d){
                if($d->{$this->original_column}){
                    //$agrees = $this->getDownvotesByKnowledgePieceAndReason($d->{$this->knowledge_type_column},$d->{$this->knowledge_id_column},$d->{$this->reason_column},false);
                    $agrees = $this->getAgreements($d->{$this->knowledge_type_column},$d->{$this->knowledge_id_column},$d->{$this->reason_column});
                    if($agrees){
                        $d->count = count($agrees)+1;
                    }else{
                        $d->count = 1;
                    }
                }else{
                    $d->count = false;
                }
            }
        }
        
        return $ds;
    }

    function deleteByIds($u_id, $k_type, $k_id) {
        $sql = 'DELETE FROM `' . $this->table . '`
            WHERE `l`.`' . $this->knowledge_type_column . '` = "' . $k_type . '"
            AND `l`.`' . $this->knowledge_id_column . '` = "' . $k_id . '"
            AND `l`.`' . $this->user_id_column . '` = "' . $u_id . '"';

        return $this->Downvote->query($sql);
    }

    function insertOriginal($u_id, $k_type, $k_id, $reason_descr){
        $sql = 'SELECT * FROM `'.$this->reason_table. '` WHERE `'.$this->reason_description_column.'` = "'.$reason_descr.'"';
        
        $reason_id = $this->Downvote->query($sql);
        if($reason_id){
            return $this->insertByIds($u_id, $k_type, $k_id,$reason_id[0]->reason_id,true);
        }else{        
            $sql = 'INSERT INTO `'.$this->reason_table. '` ('.$this->reason_description_column.')
                    VALUES ("'.$reason_descr.'")';

            $this->Downvote->query($sql);
            
            return $this->insertByIds($u_id, $k_type, $k_id,$this->Downvote->getHighestIndex(array($this->reason_table),$this->reason_id_column)-1,true);
        }
    }
    
    function insertByIds($u_id, $k_type, $k_id, $reason, $original=false) {
        $like_data = array(
            $this->user_id_column => $u_id,
            $this->knowledge_type_column => $k_type,
            $this->knowledge_id_column => $k_id,
            $this->reason_column => $reason,
            $this->original_column => $original
        );

        return $this->Downvote->insert($like_data);
    }

}
