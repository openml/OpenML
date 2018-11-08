<?php
// JvR: I want to deprecate this class.
class Author extends MY_Community_Model {
	
	function __construct()
    {
		parent::__construct();
		$this->table = 'users';
		$this->id_column = 'id';
		$this->deleted_activated = 'id IS NOT NULL ';
    }
    
    function getGamificationSettings($u_id){
        $sql = "SELECT gamification_visibility FROM ".$this->table." where id=".$u_id;
        
        return $this->query($sql);
    }
}
?>
