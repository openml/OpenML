<?php

class Api_gamification extends MY_Api_Model {

    protected $version = 'v2';
    

    function __construct() {
        parent::__construct();

        // load models
        $this->load->model('KnowledgePiece');
        $this->load->model('Author');
        $this->load->model('Gamification');
    }

    function bootstrap($format, $segments, $request_type, $user_id) {
        $this->outputFormat = $format;

        if(count($segments)>=3){
            $score = $segments[0];
            $type = $segments[1];
            $id = $segments[2];
            
            
            if($type=='u' && $id!=$user_id){
                //check gamification settings if the requesting user is not the requested user
                if(!$this->checkGamificationSettings($id)){
                    return;
                }
            }
            if(count($segments)==3){
                $meth = 'get_progress_'.$score.'_whole';
                $this->$meth($type,$id,"2013-1-1");
                return;
            }else if($segments[3]=='today'){
                $meth = 'get_progress_'.$score.'_whole';
                $this->$meth($type,$id,date("Y-m-d"));
                return;
            }else if($segments[3]=='thismonth'){ //progress from the start of the current month until today
                $meth = 'get_progress_'.$score.'_whole';
                $this->$meth($type,$id,date("Y-m"));
                return;
            }else if($segments[3]=='thismonth_perday'){ //progress for each day from the start of the current month until today
                $meth = 'get_progress_'.$score.'_perday';
                $now = date("Y-m-d");
                $this->$meth($type,$id,date("Y-m")."-1",date("Y-m-d",strtotime($now. ' +1 day')));
                return;
            }else if($segments[3]=='thisyear'){ //progress form the start of the current year until today
                $meth = 'get_progress_'.$score.'_whole';
                $this->$meth($type,$id,date("Y"));
                return;
            }else if($segments[3]=='thisyear_perday'){//progress for each day from the start of the current year until today
                $meth = 'get_progress_'.$score.'_perday';
                $now = date("Y-m-d");
                $this->$meth($type,$id,date("Y")."-1-1",date("Y-m-d",strtotime($now. ' +1 day')));
                return;
            }else if($segments[3]=='lastday'){//progress of the last 24 hours
                $meth = 'get_progress_'.$score.'_whole';
                $now = date("Y-m-d H:i:s");
                $this->$meth($type,$id,date('Y-m-d H:i:s', strtotime($now . ' -1 day'),date("Y-m-d",strtotime($now. ' +1 day'))));
                return;
            }else if($segments[3]=='lastmonth'){//progress of the last 28/29/30/31 days
                $meth = 'get_progress_'.$score.'_whole';
                $now = date("Y-m-d");
                $this->$meth($type,$id,date("Y-m-d",strtotime($now. ' -1 month')));
                return;
            }else if($segments[3]=='lastmonth_perday'){//progress for each day of the last 28/29/30/31 days
                $meth = 'get_progress_'.$score.'_perday';
                $now = date("Y-m-d");
                $this->$meth($type,$id,date("Y-m-d",strtotime($now. ' -1 month')),date("Y-m-d",strtotime($now. ' +1 day')));
                return;
            }else if($segments[3]=='lastyear'){//progress of the last 365/366 days
                $meth = 'get_progress_'.$score."_whole";
                $now = date("Y-m-d");
                $this->$meth($type,$id,date("Y-m-d",strtotime($now. ' -1 year')));
                return;
            }else if($segments[3]=='lastyear_perday'){//progress for each day of the last 365/366 days
                $meth = 'get_progress_'.$score.'_perday';
                $now = date("Y-m-d");
                $this->$meth($type,$id,date("Y-m-d",strtotime($now. ' -1 year')),date("Y-m-d",strtotime($now. ' +1 day')));
                return;                
            }else if(count($segments)==4 && is_numeric($segments[3])){ //progress of $segment[3] (ex. 2014)
                $meth = 'get_progress_'.$score.'_whole';
                $this->$meth($type,$id,date('Y-m-d',mktime(0,0,0,1,1,$segments[3])),date('Y-m-d',mktime(0,0,0,1,1,$segments[3]+1)));
                return;
            }else if(count($segments)==4 && is_numeric(explode("_",$segments[3])[0]) && explode("_",$segments[3])[1]=="perday"){//progress for each day of (first part of) $segment[3] (ex. 2014)
                $meth = 'get_progress_'.$score.'_perday';
                $this->$meth($type,$id,date('Y-m-d',mktime(0,0,0,1,1,explode("_",$segments[3])[0])),date('Y-m-d',mktime(0,0,0,1,1,explode("_",$segments[3])[0]+1)));
                return;
            }else if(count($segments)==5 && is_numeric($segments[3]) && is_numeric($segments[4])){//progress of $segment[3]-$segment[4] (ex. 2014-2)
                $meth = 'get_progress_'.$score.'_whole';
                $this->$meth($type,$id,date('Y-m-d',mktime(0,0,0,$segments[4],1,$segments[3])),date('Y-m-d',mktime(0,0,0,$segments[4]+1,1,$segments[3])));
                return;
            }else if(count($segments)==5 && is_numeric($segments[3]) && is_numeric(explode("_",$segments[4])[0]) && explode("_",$segments[4])[1]=="perday"){//progress for each day of {$segment[3]-(first part of) $segment[4]} (ex. 2014-2)
                $meth = 'get_progress_'.$score.'_perday';
                $this->$meth($type,$id,date('Y-m-d',mktime(0,0,0,explode("_",$segments[4])[0],1,$segments[3])),date('Y-m-d',mktime(0,0,0,explode("_",$segments[4])[0],1,$segments[3]+1)));
                return;
            }else if(count($segments)==6 && is_numeric($segments[3]) && is_numeric($segments[4])&& is_numeric($segments[5])){ //progress of $segment[3]-$segment[4]-$segment[5] (ex. 2014-2-24)
                $meth = 'get_progress_'.$score.'_whole';
                $this->$meth($type,$id,date('Y-m-d',mktime(0,0,0,$segments[4],$segments[5],$segments[3])),date('Y-m-d',mktime(0,0,0,$segments[4],$segments[5]+1,$segments[3])));
                return;
            }        
        }
        
        $this->returnError(100, $this->version, 450, implode("/",$segments));
    }
    
    protected function get_progress_impact_perday($type,$id,$from,$to){
        $result = $this->Gamification->getImpactArray($type,$id,$from,$to);
        $result_wrapper = array("results"=>$result);
        $this->xmlContents('impact-progress', $this->version, $result_wrapper);
    }
    
    protected function get_progress_impact_whole($type,$id,$from,$to=null){
        $result_val = $this->Gamification->getImpact($type,$id,$from,$to);
        $result_wrapper = array("results"=>array($result_val));
        $this->xmlContents('impact-progress', $this->version, $result_wrapper);
    }
    
    protected function get_progress_reach_perday($type,$id,$from,$to){
        $result = $this->Gamification->getReachArray($type,$id,$from,$to);
        $result_wrapper = array("results"=>$result);
        $this->xmlContents('reach-progress', $this->version, $result_wrapper);
    }
    
    protected function get_progress_reach_whole($type,$id,$from,$to=null){
        $result_val = $this->Gamification->getReach($type,$id,$from,$to);
        $result_wrapper = array("results"=>array($result_val));
        $this->xmlContents('reach-progress', $this->version, $result_wrapper);
    }
    
    protected function get_progress_activity_perday($type,$id,$from,$to){
        if($type!='u'){
            //return invalid type
            $this->returnError(903, $this->version);
            return;            
        }
        $result = $this->Gamification->getActivityArray($id,$from,$to);
        $result_wrapper = array("results" => $result);
        $this->xmlContents('activity-progress', $this->version, $result_wrapper);
    }
    
    protected function get_progress_activity_whole($type,$id,$from,$to=null){
        if($type!='u'){
            //return invalid type
            $this->returnError(903, $this->version);
            return;
        }
        $result_val = $this->Gamification->getActivity($id,$from,$to);
        $result_wrapper = array("results" => array($result_val));
        $this->xmlContents('activity-progress', $this->version, $result_wrapper);
    }
    
    protected function checkGamificationSettings($u_id){
        $settings = $this->Author->getGamificationSettings($u_id);
        if ($settings) {
            if ($settings[0]->gamification_visibility != 'show') {
                //return access denied
                $this->returnError(902, $this->version);
                return false;
            }
        }else{
            //return user not found
            $this->returnError(901, $this->version);
            return false;
        }
        return true;
    }
}

