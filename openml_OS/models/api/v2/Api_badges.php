<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
class Api_badges extends Api_gamification {

    protected $version = 'v2';
    
    private $badges = array();

    function __construct() {
        parent::__construct();
        // load models
        $this->load->model('Badge');
        
        $descriptions = array();
        $descriptions[0] = "You don't have this badge yet.";
        $descriptions[1] = "Increase your activity by at least 1 every day, for a week.";
        $descriptions[2] = "Increase your activity by at least 1 every day, for 30 days.";
        $descriptions[3] = "Increase your activity by at least 1 every day, for a year.";
        $descriptions[4] = "There is no higher rank for this badge.";
        $images = array();
        $images[0] = "img/clockwork_scientist_0.svg";
        $images[1] = "img/clockwork_scientist_1.svg";
        $images[2] = "img/clockwork_scientist_2.svg";
        $images[3] = "img/clockwork_scientist_3.svg";
        $id = 0;
        $name = "Clockwork Scientist";        
        $this->badges[0] = new BadgeStruct($id,$name,$images,$descriptions);
        
        $descriptions = array();
        $descriptions[0] = "You don't have this badge yet.";
        $descriptions[1] = "Have at least one of your uploads achieve a Reach of at least 10";
        $descriptions[2] = "Have at least one of your uploads achieve a Reach of at least 100";
        $descriptions[3] = "Have at least one of your uploads of every category (data,flow,task,run) achieve a Reach of at least 100";
        $descriptions[4] = "There is no higher rank for this badge.";
        $images = array();
        $images[0] = "img/good_news_everyone_0.svg";
        $images[1] = "img/good_news_everyone_1.svg";
        $images[2] = "img/good_news_everyone_2.svg";
        $images[3] = "img/good_news_everyone_3.svg";
        $id = 1;
        $name = "Good News Everyone";
        $this->badges[1] = new BadgeStruct($id,$name,$images,$descriptions);
        
        $descriptions = array();
        $descriptions[0] = "You don't have this badge yet.";
        $descriptions[1] = "Reuse a dataset, flow or task uploaded by someone who has reused a dataset, flow or task from you";
        $descriptions[2] = "Reuse a dataset, flow or task from 10 people who have reused a dataset, flow or task from you";
        $descriptions[3] = "There is no higher rank for this badge.";
        $images = array();
        $images[0] = "img/team_player_0.svg";
        $images[1] = "img/team_player_1.svg";
        $images[2] = "img/team_player_2.svg";
        $id = 2;
        $name = "Team Player";
        $this->badges[2] = new BadgeStruct($id,$name,$images,$descriptions);
    }
    
    function bootstrap($format, $segments, $request_type, $user_id){
        $this->outputFormat = $format;
        if($segments[0]=='check'){
            $this->testAward($segments[1],$segments[2]);
            return;
        }else if($segments[0]=='get'){
            $this->getBadge($segments[1],$segments[2]);
            return;
        }else if($segments[0]=='list'){
            $this->getBadges($segments[1]);
            return;
        }
        
        $this->returnError(100, $this->version, 450, implode("/",$segments));
    }
    
    function testAward($u_id,$b_id){
        if($u_id!=$this->user_id){
            //check gamification settings if the requesting user is not the requested user
            if(!$this->checkGamificationSettings($u_id)){
                return;
            }
        }
        if($b_id>=count($this->badges)){
            $this->returnError(950, $this->version);            
        }
        $b = new stdClass();
        $b->key = 'rank';
        $method = 'testAward'.str_replace(" ","",$this->badges[$b_id]->name);
        $b->value = $this->$method($u_id,$b_id);
        //update badges of this user
        $this->elasticsearch->index('user',$u_id);
        
        $this->xmlContents('simplepair',$this->version,$b);
    }
    
    private function testAwardGoodNewsEveryone($u_id){
        $uploads = $this->KnowledgePiece->listAllUploadsOfUser($u_id);
        $rank = 0;
        $rank_two = array('d'=>false,'f'=>false,'t'=>false,'r'=>false);
        if($uploads){
            foreach($uploads as $up){
                if($rank<2){
                    $score = $this->Gamification->getReach($up->kt,$up->id,"2013-1-1",null);
                    if($score['reach']>100){
                        $rank = 2;
                        $rank_two[$up->kt] = true;
                    }else if($score['reach']>10){
                        $rank = 1;
                    }
                }else if(!$rank_two[$up->kt]){
                    $score = $this->Gamification->getReach($up->kt,$up->id,"2013-1-1",null);
                    if($score['reach']>100){
                        $rank_two[$up->kt] = true;
                    }
                }
            }
            foreach($rank_two as $type){
                if(!$type){        
                    $this->Badge->award($u_id,$rank,1);
                    return $rank;                
                }
            }
            $rank=3;
        }
        $this->Badge->award($u_id,$rank,1);
        return $rank;
    }
    
    private function testAwardClockworkScientist($u_id){
        $scores = $this->Gamification->getActivityArray($u_id,"2013-1-1",date('Y-m-d'));
        $days = 0;
        $maxdays = 0;
        for($i=0; $i<count($scores); $i++){
            if($scores[$i]['activity']<1){
                $maxdays = max($maxdays,$days);
                $days = 0;
            }else{
                $days++;
            }
        }
        if($days>364){
            $rank = 3;
        }else if($days>29){
            $rank = 2;
        }else if($days>6){
            $rank = 1;
        }else{
            $rank = 0;
        }
        $this->Badge->award($u_id,$rank,0);
        return $rank;
    }
    
    private function testAwardTeamPlayer($u_id){
        $nr_of_collabs = 0;
        $rank = 0;
        $uploaders = $this->KnowledgePiece->getUploadersOfReusesOfUploadsOfUser($u_id);
        if($uploaders){
            foreach($uploaders as $up){
                $connections = $this->KnowledgePiece->getUploadersOfReusesOfUploadsOfUser($up->uploader);
                if($connections){
                    foreach($connections as $con){
                        if($con->uploader == $u_id){
                            $nr_of_collabs++;
                            $rank = 1;
                        }
                        if($nr_of_collabs==10){
                            $rank = 2;
                            break 2;
                        }
                    }
                }
            }
        }
        $this->Badge->award($u_id,$rank,2);
        return $rank;        
    }
    
    private function getDescriptionCurrentRank($u_id,$b_id){
        $rank = $this->Badge->getAwardedRank($u_id,$b_id);
        if(!$rank){
            $rank = 0;
        }
        return $this->badges[$b_id]->descriptions[$rank];
    }
    
    private function getDescriptionNextRank($u_id, $b_id){
        $rank = $this->Badge->getAwardedRank($u_id,$b_id);
        if(!$rank){
            $rank = 0;
        }
        return $this->badges[$b_id]->descriptions[$rank+1];
    }
    
    private function getName($b_id){
        return $this->badges[$b_id]->name;
    }
    
    private function getImage($u_id,$b_id){
        $rank = $this->Badge->getAwardedRank($u_id,$b_id);
        if(!$rank){
            $rank = 0;
        }
        return $this->badges[$b_id]->images($rank);
    }
    
    private function getBadge($u_id,$b_id){
        $b = array('description_next'=>null,'description_current'=>null,'image'=>null,'name'=>null);
        $rank = $this->Badge->getAwardedRank($u_id,$b_id);
        if(!$rank){
            $rank = 0;
        }
        $b['description_next'] = $this->badges[$b_id]->descriptions[$rank+1];
        $b['description_current'] = $this->badges[$b_id]->descriptions[$rank];
        $b['image'] = $this->badges[$b_id]->images[$rank];
        $b['name'] = $this->badges[$b_id]->name;
        $b['rank'] = $rank;
        $this->xmlContents('badge',$this->version,$b);
    }
    
    private function getBadges($u_id){
        $result = array();
        foreach($this->badges as $badge){
            $b = array('description_next'=>null,'description_current'=>null,'image'=>null,'name'=>null);
            $rank = $this->Badge->getAwardedRank($u_id,$badge->id);
            if(!$rank){
                $rank = 0;
            }
            $b['description_next'] = $this->badges[$badge->id]->descriptions[$rank+1];
            $b['description_current'] = $this->badges[$badge->id]->descriptions[$rank];
            $b['image'] = $this->badges[$badge->id]->images[$rank];
            $b['name'] = $this->badges[$badge->id]->name;
            $b['rank'] = $rank;
            $b['id'] = $badge->id;
            $result[] = $b;
        }
        
        $this->xmlContents('badges',$this->version,array("badges"=>$result));
    }
}

class BadgeStruct{
    public $descriptions;
    public $images;
    public $id;
    public $name;

    function __construct($i, $n, $im, $d) {
        $this->id=$i;
        $this->name=$n;
        $this->descriptions=$d;
        $this->images=$im;
    }
    
}
