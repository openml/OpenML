<?php
class Author extends Community {
	
	function __construct()
    {
		parent::__construct();
		$this->table = 'users';
		$this->deleted_activated = 'id IS NOT NULL ';
    }
    
    function getGamificationSettings($u_id){
        $sql = "SELECT gamification_visibility FROM ".$this->table." where id=".$u_id;
        
        return $this->query($sql);
    }
}
?>
