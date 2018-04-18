<?php

class Download extends MY_Database_Write_Model {

    function __construct() {
        parent::__construct();
        $this->table = 'downloads';
        $this->id_column = 'did';
        $this->user_id_column = 'user_id';
        $this->knowledge_id_column = 'knowledge_id';
        $this->knowledge_type_column = 'knowledge_type';
        $this->nr_of_downloads_column = 'count';
        $this->time_column = 'time';
    }
    
    function getDownloadsByUser($userid) {
        $sql = 'SELECT `l`.*
            FROM `'.$this->table.'` AS `l`
            WHERE `l`.`'.$this->user_id_column.'` = "'.$userid.'"';

        return $this->Download->query($sql);
    }
    
    function getDownloadsByType($type){
        $sql = 'SELECT `l`.*
            FROM `'.$this->table.'` AS `l`
            WHERE `l`.`'.$this->knowledge_type_column.'` = "'.$type.'"';
        return $this->Download->Query($sql);
    }
    
    function getDownloadsByUserAndType($userid, $type){
        $sql = 'SELECT `l`.*
            FROM `'.$this->table.'` AS `l`
            WHERE `l`.`'.$this->user_id_column.'` = "'.$userid.'"
            AND `l`.`'.$this->knowledge_type_column.'` = "'.$type.'"';

        return $this->Download->query($sql);        
    }
    
    function getDownloadsByKnowledgePiece($type, $id){
        $sql = 'SELECT `l`.*
            FROM `'.$this->table.'` AS `l`
            WHERE `l`.`'.$this->knowledge_type_column.'` = "'.$type.'"
            AND `l`.`'.$this->knowledge_id_column.'` = "'.$id.'"';

        return $this->Download->query($sql);        
    }
    
    function getByIds($u_id,$k_type,$k_id){
        $sql = 'SELECT `l`.*
            FROM `'.$this->table.'` AS `l`
            WHERE `l`.`'.$this->knowledge_type_column.'` = "'.$k_type.'"
            AND `l`.`'.$this->knowledge_id_column.'` = "'.$k_id.'"
            AND `l`.`'.$this->user_id_column.'` = "'.$u_id.'"';
        return $this->Download->query($sql);
    }
    
    function getFromToUser($u_id, $from, $to){
         $sql = 'SELECT `l`.*
            FROM `' . $this->table . '` AS `l`
            WHERE `l`.`' . $this->user_id_column . '` = ' . $u_id . '
                AND `'.$this->time_column.'`>="'.$from.'"';
         if($to!=null){
            $sql.='AND `'.$this->time_column.'` < "'.$to.'"';
         }

        return $this->Download->query($sql);
    }

    function getFromToKnowledge($k_type, $k_id, $from, $to){
         $sql = 'SELECT `l`.*
            FROM `' . $this->table . '` AS `l`
            WHERE `l`.`' . $this->knowledge_type_column . '` = "' . $k_type . '"
                AND `l`.`' . $this->knowledge_id_column . '` = "' . $k_id . '"
                AND `'.$this->time_column.'`>="'.$from.'"';
         if($to!=null){
            $sql.='AND `'.$this->time_column.'` < "'.$to.'"';
         }
        
        return $this->Download->query($sql);
    }
    
    function deleteByIds($u_id,$k_type,$k_id){
        $sql = 'DELETE FROM `'.$this->table.'`
            WHERE `l`.`'.$this->knowledge_type_column.'` = "'.$k_type.'"
            AND `l`.`'.$this->knowledge_id_column.'` = "'.$k_id.'"
            AND `l`.`'.$this->user_id_column.'` = "'.$u_id.'"';
        
        return $this->Download->query($sql);
    }
    
    function increment($id){
        $sql = 'UPDATE `'.$this->table.'`
            SET `'.$this->nr_of_downloads_column.'`= `'.$this->nr_of_downloads_column.'`+1
            WHERE `'.$this->id_column.'`="'.$id.'"';
        return $this->Download->query($sql);
    }
    
    function insertByIds($u_id, $k_type, $k_id) {
        $download_data = array(
            $this->user_id_column => $u_id,
            $this->knowledge_type_column => $k_type,
            $this->knowledge_id_column => $k_id
        );

        return $this->Download->insert($download_data);
    }
    
    function insertOrIncrement($u_id, $k_type, $k_id){
        $d = $this->Download->getByIds($u_id, $k_type, $k_id);
        if($d){
            if($this->Download->increment($d[0]->did)){
                return $d[0]->did;
            }else{
                return false;
            }
        }else{
            return $this->Download->insertByIds($u_id, $k_type, $k_id);
        }
    }
}
