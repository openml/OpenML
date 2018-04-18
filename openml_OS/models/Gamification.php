<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

class Gamification extends CI_Model{

    protected $scores = array();

    function __construct() {
        parent::__construct();

        $this->load->model('KnowledgePiece');

        $this->scores['activity']['uploads'] = 1;
        $this->scores['activity']['likes'] = 0.5;
        $this->scores['activity']['downloads'] = 0.5;
        $this->scores['reach']['likes'] = 1;
        $this->scores['reach']['downloads'] = 1;
        $this->scores['impact']['reuse'] = 1;
        $this->scores['impact']['reach'] = 0.5;
        $this->scores['impact']['recursive'] = 0.5;
    }

    public function getActivityArray($id,$from,$to){
        $size = $this->getNumberOfDaysFromTo($from,$to);
        $empty_val = array("uploads"=>0,"likes"=>0,"downloads"=>0,"activity"=>0,"date"=>"");
        $result = array_fill(0,$size,$empty_val);
        $uploads = $this->KnowledgePiece->getNumberOfUploadsOfUser($id, $from, $to);
        $ld = $this->KnowledgePiece->getNumberOfLikesAndDownloadsOfuser($id, $from, $to);
        if ($uploads) {
            foreach ($uploads as $up) {
                $datediff = (strtotime($up->date) - strtotime($from)) / (60 * 60 * 24);
                $result[floor($datediff)]['uploads']+=$up->count;
                $result[floor($datediff)]['activity']+=($up->count * $this->scores['activity']['uploads']);
            }
        }
        if ($ld) {
            foreach ($ld as $likeordownload) {
                $datediff = (strtotime($likeordownload->date) - strtotime($from)) / (60 * 60 * 24);
                if ($likeordownload->ldt == 'd') {
                    $result[floor($datediff)]['downloads']+=$likeordownload->count;
                    $result[floor($datediff)]['activity']+=($likeordownload->count * $this->scores['activity']['downloads']);
                } else if ($likeordownload->ldt == 'l') {
                    $result[floor($datediff)]['likes']+=$likeordownload->count;
                    $result[floor($datediff)]['activity']+=($likeordownload->count * $this->scores['activity']['likes']);
                }
            }
        }
        for ($i = 0; $i < count($result); $i++) {
            $result[$i]['date'] = date("l Y-m-d", strtotime($from . '+' . $i . ' days'));
        }
        return $result;
    }

    public function getActivity($id,$from,$to){
        $result_val = array("uploads"=>0,"likes"=>0,"downloads"=>0,"activity"=>0,"date"=>$from);
        $uploads = $this->KnowledgePiece->getNumberOfUploadsOfUser($id, $from, $to);
        $ld = $this->KnowledgePiece->getNumberOfLikesAndDownloadsOfuser($id, $from, $to);
        if ($uploads) {
            foreach ($uploads as $up) {
                $result_val['uploads']+=$up->count;
                $result_val['activity']+=($up->count * $this->scores['activity']['uploads']);
            }
        }
        if ($ld) {
            foreach ($ld as $likeordownload) {
                if ($likeordownload->ldt == 'd') {
                    $result_val['downloads']+=$likeordownload->count;
                    $result_val['activity']+=($likeordownload->count * $this->scores['activity']['downloads']);
                } else if ($likeordownload->ldt == 'l') {
                    $result_val['likes']+=$likeordownload->count;
                    $result_val['activity']+=($likeordownload->count * $this->scores['activity']['likes']);
                }
            }
        }
        return $result_val;
    }

    public function getReachArray($type,$id,$from,$to){
        $size = $this->getNumberOfDaysFromTo($from,$to);
        $empty_val = array("likes"=>0,"downloads"=>0,"reach"=>0,"date"=>"");
        $result = array_fill(0,$size,$empty_val);
        if($type=='u'){
            $ld_received = $this->KnowledgePiece->getNumberOfLikesAndDownloadsOnUploadsOfUser($id,$from,$to);
        }else{
            $ld_received = $this->KnowledgePiece->getNumberOfLikesAndDownloadsOnUpload($type,$id,$from,$to);
        }
        if($ld_received){
            foreach($ld_received as $likeordownload){
                $datediff = (strtotime($likeordownload->date) - strtotime($from))/(60*60*24);
                if($likeordownload->ldt=='d'){
                    $result[floor($datediff)]['downloads']+=$likeordownload->count;
                    $result[floor($datediff)]['reach']+=($likeordownload->count*$this->scores['reach']['downloads']);
                }else if($likeordownload->ldt=='l'){
                    $result[floor($datediff)]['likes']+=$likeordownload->count;
                    $result[floor($datediff)]['reach']+=($likeordownload->count*$this->scores['reach']['likes']);
                }
            }
        }
        for($i=0; $i<count($result); $i++){
            $result[$i]['date'] = date("l Y-m-d",strtotime($from. '+'.$i.' days'));
        }
        return $result;
    }

    public function getReach($type,$id,$from,$to){
        $result_val = array("likes"=>0,"downloads"=>0,"reach"=>0,"date"=>$from);
        if($type=='u'){
            $ld_received = $this->KnowledgePiece->getNumberOfLikesAndDownloadsOnUploadsOfUser($id,$from,$to);
        }else{
            $ld_received = $this->KnowledgePiece->getNumberOfLikesAndDownloadsOnUpload($type,$id,$from,$to);
        }
        if($ld_received){
            foreach($ld_received as $likeordownload){
                if($likeordownload->ldt=='d'){
                    $result_val['downloads']+=$likeordownload->count;
                    $result_val['reach']+=($likeordownload->count*$this->scores['reach']['downloads']);
                }else if($likeordownload->ldt=='l'){
                    $result_val['likes']+=$likeordownload->count;
                    $result_val['reach']+=($likeordownload->count*$this->scores['reach']['likes']);
                }
            }
        }
        return $result_val;
    }

    public function getImpactArray($type,$id,$from,$to){
        $size = $this->getNumberOfDaysFromTo($from,$to);
        $empty_val = array("reuse"=>0,"reuse_reach"=>0,"recursive_impact"=>0,"impact"=>0,"date"=>"");
        $result = array_fill(0,$size,$empty_val);
        if($type=='u'){
            $reuse = $this->KnowledgePiece->getNumberOfReusesOfUploadsOfUser($id,$from,$to);
            $ld_reuse = $this->KnowledgePiece->getNumberOfLikesAndDownloadsOnReuseOfUploadsOfUser($id,$from,$to);
        }else{
            $reuse = $this->KnowledgePiece->getNumberOfReusesOfUploadsOfUser($id,$from,$to);
            $ld_reuse = $this->KnowledgePiece->getNumberOfLikesAndDownloadsOnReuseOfUpload($type,$id,$from,$to);
        }
        if($reuse){
            foreach($reuse as $r){
                $datediff = (strtotime($r->date) - strtotime($from))/(60*60*24);
                $result[floor($datediff)]['reuse']+=$r->count;
                $result[floor($datediff)]['impact']+=$r->count*$this->scores['impact']['reuse'];
            }
            if($ld_reuse){
                foreach($ld_reuse as $likeordownload){
                    $datediff = (strtotime($likeordownload->date) - strtotime($from))/(60*60*24);
                    if($likeordownload->ldt=='d'){
                        $result[floor($datediff)]['reuse_reach']+=($likeordownload->count*$this->scores['reach']['downloads']);
                        $result[floor($datediff)]['impact']+=(($likeordownload->count*$this->scores['reach']['downloads'])*$this->scores['impact']['reach']);
                    }else if($likeordownload->ldt=='l'){
                        $result[floor($datediff)]['reuse_reach']+=($likeordownload->count*$this->scores['reach']['likes']);
                        $result[floor($datediff)]['impact']+=(($likeordownload->count*$this->scores['reach']['likes'])*$this->scores['impact']['reach']);
                    }
                }
            }
        }
        for($i=0; $i<count($result); $i++){
            $result[$i]['date'] = date("l Y-m-d",strtotime($from. '+'.$i.' days'));
        }
        return $result;
    }

    public function getImpact($type,$id,$from,$to){

        $result_val = array("reuse"=>0,"reuse_reach"=>0,"recursive_impact"=>0,"impact"=>0,"date"=>$from);
        if($type=='u'){
            $reuse = $this->KnowledgePiece->getTotalNumberOfReusesOfUploadsOfUser($id,$from,$to);
            $ld_reuse = $this->KnowledgePiece->getNumberOfLikesAndDownloadsOnReuseOfUploadsOfUser($id,$from,$to);
        }else{
            $reuse = $this->KnowledgePiece->getNumberOfReusesOfUpload($type,$id,$from,$to);
            $ld_reuse = $this->KnowledgePiece->getNumberOfLikesAndDownloadsOnReuseOfUpload($type,$id,$from,$to);
        }

        if($reuse){
            foreach($reuse as $r){
                $result_val['reuse']+=$r->count;
                $result_val['impact']+=($r->count*$this->scores['impact']['reuse']);
            }
            if($ld_reuse){
                foreach($ld_reuse as $likeordownload){
                    if($likeordownload->ldt=='d'){
                        $result_val['reuse_reach']+=($likeordownload->count*$this->scores['reach']['downloads']);
                        $result_val['impact']+=(($likeordownload->count*$this->scores['reach']['downloads'])*$this->scores['impact']['reach']);
                    }else if($likeordownload->ldt=='l'){
                        $result_val['reuse_reach']+=($likeordownload->count*$this->scores['reach']['likes']);
                        $result_val['impact']+=(($likeordownload->count*$this->scores['reach']['likes'])*$this->scores['impact']['reach']);
                    }
                }
            }
        }

        return $result_val;
    }

    // TODO: Optimize further by removing from - to entirely
    public function getTotalImpact($type,$id,$from,$to){

        $result_val = array("reuse"=>0,"reuse_reach"=>0,"recursive_impact"=>0,"impact"=>0,"date"=>$from);
        if($type=='u'){
            $reuse = $this->KnowledgePiece->getTotalNumberOfReusesOfUploadsOfUser($id);
            $ld_reuse = $this->KnowledgePiece->getNumberOfLikesAndDownloadsOnReuseOfUploadsOfUser($id,$from,$to);
        }else{
            $reuse = $this->KnowledgePiece->getNumberOfReusesOfUpload($type,$id,$from,$to);
            $ld_reuse = $this->KnowledgePiece->getNumberOfLikesAndDownloadsOnReuseOfUpload($type,$id,$from,$to);
        }

        if($reuse){
            foreach($reuse as $r){
                $result_val['reuse']+=$r->count;
                $result_val['impact']+=($r->count*$this->scores['impact']['reuse']);
            }
            if($ld_reuse){
                foreach($ld_reuse as $likeordownload){
                    if($likeordownload->ldt=='d'){
                        $result_val['reuse_reach']+=($likeordownload->count*$this->scores['reach']['downloads']);
                        $result_val['impact']+=(($likeordownload->count*$this->scores['reach']['downloads'])*$this->scores['impact']['reach']);
                    }else if($likeordownload->ldt=='l'){
                        $result_val['reuse_reach']+=($likeordownload->count*$this->scores['reach']['likes']);
                        $result_val['impact']+=(($likeordownload->count*$this->scores['reach']['likes'])*$this->scores['impact']['reach']);
                    }
                }
            }
        }

        return $result_val;
    }

    private function getNumberOfDaysFromTo($from,$to){
        $from_e = explode("-",$from);
        $to_e = explode("-",$to);
        $size = 0;
        if ($to_e[0] == $from_e[0]) {
            if ($to_e[1] == $from_e[1]) {
                $size = $to_e[2] - $from_e[2]; //begin month and year are equal to end month and year, so count number of days
            } else {
                $size = cal_days_in_month(CAL_GREGORIAN, $from_e[1], $to_e[0]) - $from_e[2]; //number of days in the first month
                for ($i = $from_e[1] + 1; $i < $to_e[1]; $i++) { //skip first month and last month
                    $size+=cal_days_in_month(CAL_GREGORIAN, $i, $to_e[0]); //number of days for all middle months
                }
                $size+=$to_e[2]; //numer of days in final month
            }
        } else {
            $size = cal_days_in_month(CAL_GREGORIAN, $from_e[1], $to_e[0]) - $from_e[2]; //number of days in the first month
            //var_dump($size);
            for ($i = $from_e[1] + 1; $i < 13; $i++) { //number of months in the first year, skip first one
                $size+=cal_days_in_month(CAL_GREGORIAN, $i, $from_e[0]);
            }
            //var_dump($size);
            for ($j = $from_e[0] + 1; $j < $to_e[0]; $j++) { //skip first year and last year
                for ($i = 1; $i < 13; $i++) { //count all months in middle years
                    $size+=cal_days_in_month(CAL_GREGORIAN, $i, $j);
                }
            }
            //var_dump($size);
            for ($i = 1; $i < $to_e[1]; $i++) {//number of months in final year, skip last one
                $size+=cal_days_in_month(CAL_GREGORIAN, $i, $to_e[0]);
            }
            //var_dump($size);
            $size+=$to_e[2]; //number of days in final month
            //var_dump($size);
        }
        return $size;
    }


    public function getImpactFromParts($reuse,$reuse_reach,$recurisve_impact){
        return $reuse*$this->scores['impact']['reuse'] +
                $reuse_reach*$this->scores['impact']['reach'] +
                $recurisve_impact*$this->scores['impact']['recursive'];
    }

    public function getReachFromParts($likes,$downloads){
        return $likes*$this->scores['reach']['likes'] +
                $downloads*$this->scores['reach']['downloads'];
    }

    public function getActivityFromParts($uploads,$likes,$downloads){
        return $uploads*$this->scores['activity']['uploads'] +
                $likes*$this->scores['activity']['likes'] +
                $downloads*$this->scores['activity']['downloads'];
    }
}
