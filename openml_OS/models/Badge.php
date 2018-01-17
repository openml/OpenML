<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of badge
 *
 * @author s092766
 */
class Badge extends Database_write {

    function __construct() {
        parent::__construct();
        $this->table = 'awarded_badges';
        $this->id_column = 'aid';
        $this->user_id_column = 'user_id';
        $this->badge_id_column = 'badge_id';
        $this->rank_column = 'rank';
    }
    
    
    public function getBadge($id){
        $sql = "SELECT * FROM ".$this->table." WHERE ".$this->id_column." = ".$id;
        
        return $this->query($sql);
    }
    
    public function award($u_id, $r, $b_id){
        $rankdata = $this->getAwardedRank($u_id, $b_id);
        if(is_numeric($rankdata)){
           if($rankdata<$r){
               $sql = "UPDATE ".$this->table
                       ." SET ".$this->rank_column." = ".$r
                       ." WHERE ".$this->user_id_column." = ".$u_id
                       ." AND ".$this->badge_id_column." = ".$b_id;
               return $this->query($sql);
           }else{
               return false;
           }
        }else{
            $data = array(
                $this->user_id_column => $u_id,
                $this->badge_id_column => $b_id,
                $this->rank_column => $r
            );
            return $this->db->insert($this->table, $data);
        }
    }
    
    public function getAwardedRank($u_id, $b_id){
        $sql = "SELECT ".$this->rank_column." from ".$this->table." WHERE ".$this->user_id_column." = ".$u_id." AND ".$this->badge_id_column." = ".$b_id;
        
        $res = $this->query($sql);
        
        if($res){
            return $res[0]->{$this->rank_column};
        }else{
            return false;
        }
    }

    public function getBadgesOfUser($u_id){
         $sql = "SELECT * FROM ".$this->table." WHERE ".$this->user_id_column." = ".$u_id;
        
        return $this->query($sql);
    }
    
}
