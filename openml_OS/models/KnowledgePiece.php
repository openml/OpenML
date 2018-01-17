<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

class KnowledgePiece extends Database_write{

    function __construct() {
        parent::__construct();

        $this->load->model('Dataset');
        $this->load->model('Implementation');
        $this->load->model('Task');
        $this->load->model('Run');
    }


    function getUploader($type, $id){
        if($type === 'd' || $type === 'data'){
            $d = $this->Dataset->getUploaderOf($id);
        }else if($type === 'f' || $type === 'flow'){
            $d = $this->Implementation->getUploaderOf($id);
        }else if($type === 't' || $type === 'task'){
            $d = $this->Task->getUploaderOf($id);
        }else if($type === 'r' || $type === 'run'){
            $d = $this->Run->getUploaderOf($id);
        }
        if($d){
            return $d[0]->uploader;
        }else{
            return -1;
        }
    }

    function listAllUploadsOfUser($u_id){
       $data_sql = "SELECT d.did as id, 'd' as kt FROM dataset as d WHERE d.uploader=".$u_id;
        $flow_sql = "SELECT f.id, 'f' as kt FROM implementation as f WHERE f.uploader=".$u_id;
        $task_sql = "SELECT t.task_id as id, 't' as kt FROM task as t WHERE t.creator=".$u_id;
        $run_sql = "SELECT r.rid as id, 'r' as kt FROM run as r WHERE r.uploader=".$u_id;

        return $this->KnowledgePiece->query($data_sql." UNION ".$flow_sql." UNION ".$task_sql." UNION ".$run_sql);
    }

    function getNumberOfUploadsOfUser($u_id, $from=null, $to=null){
        $data_sql = "SELECT d.did as id, d.uploader, d.upload_date as time, 'd' as kt FROM dataset as d WHERE d.uploader=".$u_id;
        $flow_sql = "SELECT f.id, f.uploader, f.uploadDate as time, 'f' as kt FROM implementation as f WHERE f.uploader=".$u_id;
        $task_sql = "SELECT t.task_id as id, t.creator as uploader, t.creation_date as time, 't' as kt FROM task as t WHERE t.creator=".$u_id;
        $run_sql = "SELECT r.rid as id, r.uploader, r.start_time as time, 'r' as kt FROM run as r WHERE r.uploader=".$u_id;
        $upload_sql = "SELECT uploads.kt, count(uploads.id) as count, DATE(uploads.time) as date FROM (".$data_sql." UNION ".$flow_sql." UNION ".$task_sql." UNION ".$run_sql.") as uploads";
        if ($from != null && $to!=null) {
            $upload_sql .= ' WHERE uploads.time>="' . $from . '"';
            $upload_sql .= ' AND uploads.time<"' . $to . '"';
            $upload_sql.=" GROUP BY uploads.kt, DATE(uploads.time) ORDER BY date;";
        }else if ($to != null) {
            $upload_sql .= ' WHERE uploads.time<"' . $to . '"';
            $upload_sql.=" GROUP BY uploads.kt, DATE(uploads.time) ORDER BY date;";
        }else if($from!=null){
            $upload_sql .= ' WHERE uploads.time>="' . $from . '"';
            $upload_sql.=" GROUP BY uploads.kt, DATE(uploads.time) ORDER BY date;";
        }else{
            $upload_sql.=" GROUP BY uploads.kt, DATE(uploads.time) ORDER BY date;";
        }

        return $this->KnowledgePiece->query($upload_sql);
    }

    function getLikesOnUploadsOfUser($u_id, $from=null, $to=null){
        $like_sql = "SELECT uploads.*,likes.user_id, likes.time FROM `likes`, (SELECT d.did as id, d.uploader, 'd' as kt FROM dataset as d WHERE d.uploader=".$u_id."
                                                                    UNION
                                                                    SELECT f.id, f.uploader, 'f' as kt FROM implementation as f WHERE f.uploader=".$u_id."
                                                                    UNION
                                                                    SELECT t.task_id as id, t.creator as uploader, 't' as kt FROM task as t WHERE t.creator=".$u_id."
                                                                    UNION
                                                                    SELECT r.rid as id, r.uploader, 'r' as kt FROM run as r WHERE r.uploader=".$u_id.") as uploads
                    WHERE likes.knowledge_id=uploads.id AND likes.knowledge_type=uploads.kt";
        if ($from != null) {
            $like_sql.=' AND `time`>="' . $from . '"';
        }
        if ($to != null) {
            $like_sql.=' AND `time` < "' . $to . '"';
        }
        $like_sql.=" ORDER BY likes.time";
        return $this->KnowledgePiece->query($like_sql);
    }

    function getDownloadsOnUploadsOfUser($u_id, $from=null, $to=null){
        $download_sql = "SELECT uploads.*,downloads.user_id, downloads.time FROM `downloads`, (SELECT d.did as id, d.uploader, 'd' as kt FROM dataset as d WHERE d.uploader=".$u_id."
                                                                                UNION
                                                                                SELECT f.id, f.uploader, 'f' as kt FROM implementation as f WHERE f.uploader=".$u_id."
                                                                                UNION
                                                                                SELECT t.task_id as id, t.creator as uploader, 't' as kt FROM task as t WHERE t.creator=".$u_id."
                                                                                UNION
                                                                                SELECT r.rid as id, r.uploader, 'r' as kt FROM run as r WHERE r.uploader=".$u_id.") as uploads
                    WHERE downloads.knowledge_id=uploads.id AND downloads.knowledge_type=uploads.kt";
        if ($from != null) {
            $download_sql.=' AND `time`>="' . $from . '"';
        }
        if ($to != null) {
            $download_sql.=' AND `time` < "' . $to . '"';
        }
        $download_sql.=" ORDER BY downloads.time";
        return $this->KnowledgePiece->query($download_sql);
    }

    function getNumberOfLikesAndDownloadsOnUploadsOfUser($u_id, $from=null, $to=null){
        $sql = "select ld.knowledge_type as kt, count(ld.user_id) as count, SUM(ld.count) as sum, ld.ldt, DATE(ld.time) as date
                FROM (
                SELECT l.user_id, l.knowledge_id, l.knowledge_type, 1 as count, l.time, 'l' as ldt FROM likes l, dataset d WHERE l.knowledge_id=d.did AND l.knowledge_type='d' AND d.uploader=".$u_id."
                UNION
                SELECT l.user_id, l.knowledge_id, l.knowledge_type, 1 as count, l.time, 'l' as ldt FROM likes l, implementation i WHERE l.knowledge_id=i.id AND l.knowledge_type='f' AND i.uploader=".$u_id."
                UNION
                SELECT l.user_id, l.knowledge_id, l.knowledge_type, 1 as count, l.time, 'l' as ldt FROM likes l, task t WHERE l.knowledge_id=t.task_id AND l.knowledge_type='t' AND t.creator=".$u_id."
                UNION
                SELECT l.user_id, l.knowledge_id, l.knowledge_type, 1 as count, l.time, 'l' as ldt FROM likes l, run r WHERE l.knowledge_id=r.rid AND l.knowledge_type='r' AND r.uploader=".$u_id."
                UNION
                SELECT l.user_id, l.knowledge_id, l.knowledge_type, l.count as count, l.time, 'd' as ldt FROM downloads l, dataset d WHERE l.knowledge_id=d.did AND l.knowledge_type='d' AND d.uploader=".$u_id."
                UNION
                SELECT l.user_id, l.knowledge_id, l.knowledge_type, l.count as count, l.time, 'd' as ldt FROM downloads l, implementation i WHERE l.knowledge_id=i.id AND l.knowledge_type='f' AND i.uploader=".$u_id."
                UNION
                SELECT l.user_id, l.knowledge_id, l.knowledge_type, l.count as count, l.time, 'd' as ldt FROM downloads l, task t WHERE l.knowledge_id=t.task_id AND l.knowledge_type='t' AND t.creator=".$u_id."
                UNION
                SELECT l.user_id, l.knowledge_id, l.knowledge_type, l.count as count, l.time, 'd' as ldt FROM downloads l, run r WHERE l.knowledge_id=r.rid AND l.knowledge_type='r' AND r.uploader=".$u_id."
                ) as ld";
        if ($from != null) {
            $sql.=' AND ld.time>="' . $from . '"';
        }
        if ($to != null) {
            $sql.=' AND ld.time < "' . $to . '"';
        }
        if($from!=null || $to!=null){
            $sql.=" GROUP BY DATE(ld.time), ld.ldt, kt ORDER BY date;";
        }else{
            $sql.=" GROUP BY DATE(ld.time), ld.ldt, ld.knowledge_type;";
        }

        return $this->KnowledgePiece->query($sql);
    }

    function getNumberOfLikesAndDownloadsOnUpload($type,$id,$from=null,$to=null){
        $sql = "SELECT upload.kt, count(ld.user_id) as count, SUM(ld.count) as sum, ld.ldt".($from != null || $to !=null ? ", DATE(ld.time) as date" : "")." FROM (";
        if($type=='d'){
            $sql.="SELECT d.did as id, d.uploader, 'd' as kt FROM dataset as d WHERE d.did=".$id;
        }else if($type=='f'){
            $sql.="SELECT f.id, f.uploader, 'f' as kt FROM implementation as f WHERE f.id=".$id;
        }else if($type=='t'){
            $sql.="SELECT t.task_id as id, t.creator as uploader, 't' as kt FROM task as t WHERE t.task_id=".$id;
        }else /*if($type=='r')*/{
            $sql.="SELECT r.rid as id, r.uploader, 'r' as kt FROM run as r WHERE r.rid=".$id;
        }
        $sql.=") as upload,";
        $like_sql = "SELECT user_id, knowledge_id, knowledge_type, 1 as count, time, 'l' as ldt FROM likes";
        $download_sql = "SELECT user_id, knowledge_id, knowledge_type, count, time, 'd' as ldt FROM downloads";
        $sql.="(".$like_sql." UNION ".$download_sql.") as ld WHERE ld.knowledge_id=upload.id AND ld.knowledge_type=upload.kt";
        if ($from != null) {
            $sql.=' AND ld.time>="' . $from . '"';
        }
        if ($to != null) {
            $sql.=' AND ld.time < "' . $to . '"';
        }
        if($from!=null || $to!=null){
            $sql.=" GROUP BY DATE(ld.time), ld.ldt ORDER BY date;";
        }else{
            $sql.=" GROUP BY ld.ldt;";
        }
        return $this->KnowledgePiece->query($sql);
    }

    function getNumberOfLikesAndDownloadsOfuser($u_id, $from=null, $to=null){
        $like_sql = "SELECT user_id, knowledge_id, knowledge_type, 1 as count, time, 'l' as ldt FROM likes WHERE user_id=".$u_id;
        $download_sql = "SELECT user_id, knowledge_id, knowledge_type, count, time, 'd' as ldt FROM downloads WHERE user_id=".$u_id;
        $sql = "SELECT ld.ldt, ld.knowledge_type, count(ld.user_id) as count, sum(ld.count) as sum, DATE(ld.time) as date FROM (".$like_sql." UNION ".$download_sql.") as ld ";
        if ($from != null && $to!=null) {
            $sql .= ' WHERE ld.time>="' . $from . '"';
            $sql .= ' AND ld.time<"' . $to . '"';
            $sql.=" GROUP BY ld.ldt, ld.knowledge_type, DATE(ld.time) ORDER BY date;";
        }else if ($to != null) {
            $sql .= ' WHERE ld.time<"' . $to . '"';
            $sql.=" GROUP BY ld.ldt, ld.knowledge_type, DATE(ld.time) ORDER BY date;";
        }else if($from!=null){
            $sql .= ' WHERE ld.time>="' . $from . '"';
            $sql.=" GROUP BY ld.ldt, ld.knowledge_type, DATE(ld.time) ORDER BY date;";
        }else{
            $sql.=" GROUP BY ld.ldt, ld.knowledge_type, DATE(ld.time) ORDER BY date;";
        }
        return $this->KnowledgePiece->query($sql);
    }

    // This function is changed a bit from the original definition. Merely creating a task does not count any more since this is very easy and could even be automated. What does count instead is whether a user's dataset was reused in runs. We also use the run original start time instead of the run processing time.
    function getNumberOfReusesOfUploadsOfUser($u_id,$from=null,$to=null){
        //$datareuse_sql = "SELECT 'd' as ot, task_inputs.task_id as reuse_id, task.creation_date as time FROM  `task`, `task_inputs`, `dataset` WHERE dataset.uploader=".$u_id." AND task_inputs.value=dataset.did AND task_inputs.input='source_data' AND task.task_id=task_inputs.task_id AND (task.creator IS NULL OR dataset.uploader<>task.creator)";
        $datareuse_sql = "SELECT 'd' as ot, count(run.rid) as count, DATE(run.start_time) as date FROM `run`, `task_inputs`, `dataset` WHERE dataset.uploader=".$u_id." AND task_inputs.value=dataset.did AND task_inputs.input='source_data' AND run.task_id=task_inputs.task_id AND dataset.uploader<>run.uploader";
        $flowreuse_sql = "SELECT 'f' as ot, count(run.rid) as count, DATE(run.start_time) as date FROM `implementation`, `run`, `algorithm_setup` WHERE implementation.uploader=".$u_id." AND algorithm_setup.implementation_id=implementation.id AND run.setup=algorithm_setup.sid AND implementation.uploader<>run.uploader";
        //$taskreuse_sql = "SELECT 't' as ot, run.rid as reuse_id, run.start_time as time FROM `task`, `run` WHERE task.creator=".$u_id." AND run.task_id=task.task_id  AND (task.creator IS NULL OR run.uploader<>task.creator)";
        //$sql="SELECT reuse.ot, count(reuse.reuse_id) as count, DATE(reuse.time) as date FROM (".$datareuse_sql." UNION ".$flowreuse_sql." UNION ".$taskreuse_sql.") as reuse";

        $sqlfrom = ' AND run.start_time>=STR_TO_DATE("'. $from .'", "%Y-%m-%d")';
        $sqlto   = ' AND run.start_time<STR_TO_DATE("'. $to .'", "%Y-%m-%d")';
        $sqlgroup = ' GROUP BY date ORDER BY date';

        if ($from != null && $to!=null) {
            $datareuse_sql .= $sqlfrom . $sqlto . $sqlgroup;
            $flowreuse_sql .= $sqlfrom . $sqlto . $sqlgroup;
        }else if ($to != null) {
            $datareuse_sql .= $sqlto . $sqlgroup;
            $flowreuse_sql .= $sqlto . $sqlgroup;
        }else if($from!=null){
            $datareuse_sql .= $sqlfrom . $sqlgroup;
            $flowreuse_sql .= $sqlfrom . $sqlgroup;
        }else{
            $datareuse_sql .= $sqlgroup;
            $flowreuse_sql .= $sqlgroup;
        }
        $sql= "(".$datareuse_sql.") UNION (".$flowreuse_sql.")";

        return $this->KnowledgePiece->query($sql);
    }

    function getNumberOfReusesOfUpload($type,$id,$from,$to){
        if($type=='d'){
            $sql = "SELECT 'd' as ot, count(task_inputs.task_id) as count, DATE(task.creation_date) as date FROM  `task`, `task_inputs`, `dataset` WHERE dataset.did=".$id." AND task_inputs.value=dataset.did AND task_inputs.input='source_data' AND task.task_id=task_inputs.task_id AND (task.creator IS NULL OR dataset.uploader<>task.creator)";
        }else if($type=='f'){
            $sql = "SELECT 'f' as ot, count(run.rid) as count, DATE(run.start_time) as date FROM `implementation`, `run`, `algorithm_setup` WHERE implementation.id=".$id." AND algorithm_setup.implementation_id=implementation.id AND run.setup=algorithm_setup.sid AND implementation.uploader<>run.uploader";
        }else /*if($type=='t')*/{
            $sql = "SELECT 't' as ot, count(run.rid) as count, DATE(run.start_time) as date FROM `task`, `run` WHERE task.task_id=".$id." AND run.task_id=task.task_id  AND (task.creator IS NULL OR run.uploader<>task.creator)";
        }
        if($type=='d'){
            if ($from != null && $to!=null) {
                $sql .= ' AND task.creation_date>="' . $from . '"';
                $sql .= ' AND task.creation_date<"' . $to . '"';
                $sql .=" GROUP BY ot, date ORDER BY date;";
            }else if ($to != null) {
                $sql .= ' AND task.creation_date<"' . $to . '"';
                $sql .=" GROUP BY ot, date ORDER BY date;";
            }else if($from!=null){
                $sql .= ' WHERE task.creation_date>="' . $from . '"';
                $sql .=" GROUP BY ot, date ORDER BY date;";
            }else{
                $sql.=" GROUP BY ot;";
            }

        }else{
            if ($from != null && $to!=null) {
                $sql .= ' AND run.start_time>="' . $from . '"';
                $sql .= ' AND run.start_time<"' . $to . '"';
                $sql .=" GROUP BY ot, date ORDER BY date;";
            }else if ($to != null) {
                $sql .= ' AND run.start_time<"' . $to . '"';
                $sql .=" GROUP BY ot, date ORDER BY date;";
            }else if($from!=null){
                $sql .= ' WHERE run.start_time>="' . $from . '"';
                $sql .=" GROUP BY ot, date ORDER BY date;";
            }else{
                $sql.=" GROUP BY ot;";
            }
        }

        return $this->KnowledgePiece->query($sql);
    }

    function bgetNumberOfLikesAndDownloadsOnReuseOfUploadsOfUser($u_id,$from=null,$to=null){
        $datareuse_sql = "SELECT 'd' as ot, 'r' as rt, run.rid as reuse_id, DATE(run.start_time) as date FROM `run`, `task_inputs`, `dataset` WHERE dataset.uploader=".$u_id." AND task_inputs.value=dataset.did AND task_inputs.input='source_data' AND run.task_id=task_inputs.task_id AND dataset.uploader<>run.uploader";
        $flowreuse_sql = "SELECT 'f' as ot, 'r' as rt, run.rid as reuse_id, DATE(run.start_time) as date FROM `implementation`, `run`, `algorithm_setup` WHERE implementation.uploader=".$u_id." AND algorithm_setup.implementation_id=implementation.id AND run.setup=algorithm_setup.sid AND implementation.uploader<>run.uploader";

        $sqlfrom = ' AND run.start_time>=STR_TO_DATE("'. $from .'", "%Y-%m-%d")';
        $sqlto   = ' AND run.start_time<STR_TO_DATE("'. $to .'", "%Y-%m-%d")';

        if ($from != null && $to!=null) {
            $datareuse_sql .= $sqlfrom . $sqlto;
            $flowreuse_sql .= $sqlfrom . $sqlto;
        }else if ($to != null) {
            $datareuse_sql .= $sqlto;
            $flowreuse_sql .= $sqlto;
        }else if($from!=null){
            $datareuse_sql .= $sqlfrom;
            $flowreuse_sql .= $sqlfrom;
        }

        $sql="SELECT reuse.ot,count(ld.user_id) as count, SUM(ld.count) as sum, ld.ldt, DATE(ld.time) as date FROM ((".$datareuse_sql.") UNION (".$flowreuse_sql.")) as reuse, (";

        $like_sql = "SELECT user_id, knowledge_id, knowledge_type, 1 as count, time, 'l' as ldt FROM likes";
        $download_sql = "SELECT user_id, knowledge_id, knowledge_type, count, time, 'd' as ldt FROM downloads";

        $sql.=$like_sql." UNION ".$download_sql.") as ld WHERE ld.knowledge_type=reuse.rt AND ld.knowledge_id=reuse.reuse_id";
        if ($from != null) {
            $sql.=' AND ld.time>="' . $from . '"';
        }
        if ($to != null) {
            $sql.=' AND ld.time < "' . $to . '"';
        }
        if($from!=null || $to!=null){
            $sql.=" GROUP BY DATE(ld.time), ld.ldt, reuse.ot ORDER BY date;";
        }else{
            $sql.=" GROUP BY ld.ldt, reuse.ot;";
        }

        echo $sql;
        return $this->KnowledgePiece->query($sql);
    }

    //Adapted in same way as above
    function getNumberOfLikesAndDownloadsOnReuseOfUploadsOfUser($u_id,$from=null,$to=null){
        $datareuse_sql = "SELECT 'd' as ot, run.rid as reuse_id, 'r' as rt FROM `run`, `task_inputs`, `dataset` WHERE dataset.uploader=".$u_id." AND task_inputs.value=dataset.did AND task_inputs.input='source_data' AND run.task_id=task_inputs.task_id AND dataset.uploader<>run.uploader";
        $flowreuse_sql = "SELECT 'f' as ot, run.rid as reuse_id, 'r' as rt FROM `implementation`, `run`, `algorithm_setup` WHERE implementation.uploader=".$u_id." AND algorithm_setup.implementation_id=implementation.id AND run.setup=algorithm_setup.sid AND implementation.uploader<>run.uploader";

        $like_sql = "select likes.user_id, knowledge_id, knowledge_type, 1 as count, likes.time, 'l' as ldt FROM `likes`, `run`, `task_inputs`, `dataset` WHERE knowledge_type='r' AND knowledge_id=run.rid AND dataset.uploader=".$u_id." AND task_inputs.value=dataset.did AND task_inputs.input='source_data' AND run.task_id=task_inputs.task_id AND dataset.uploader<>run.uploader";
        $download_sql = "select downloads.user_id, knowledge_id, knowledge_type, downloads.count, downloads.time, 'd' as ldt FROM `downloads`,`implementation`, `run`, `algorithm_setup` WHERE knowledge_type='r' AND knowledge_id=run.rid AND implementation.uploader=".$u_id." AND algorithm_setup.implementation_id=implementation.id AND run.setup=algorithm_setup.sid AND implementation.uploader<>run.uploader";

        if ($from != null) {
            $like_sql.=' AND time>=STR_TO_DATE("'. $from .'", "%Y-%m-%d")';
            $download_sql.=' AND time>=STR_TO_DATE("'. $from .'", "%Y-%m-%d")';
        }
        else if ($to != null) {
            $like_sql.=' AND time < STR_TO_DATE("'. $to .'", "%Y-%m-%d")';
            $download_sql.=' AND time < STR_TO_DATE("'. $to .'", "%Y-%m-%d")';
        }

        $sql="select ld.knowledge_type ,count(ld.user_id) as count, SUM(ld.count) as sum, ld.ldt, DATE(ld.time) as date FROM (".$like_sql." UNION ".$download_sql.") as ld";

        if($from!=null || $to!=null){
            $sql.=" GROUP BY date, ldt, knowledge_type ORDER BY date;";
        }else{
            $sql.=" GROUP BY ldt, knowledge_type;";
        }

        return $this->KnowledgePiece->query($sql);
    }

    function getNumberOfLikesAndDownloadsOnReuseOfUpload($type,$id,$from=null,$to=null){
        if($type=='d'){
            $reuse_sql = "SELECT dataset.did as oringal_id, 'd' as ot, task_inputs.task_id as reuse_id, 't' as rt FROM  `task`, `task_inputs`, `dataset` WHERE dataset.did=".$id." AND task_inputs.value=dataset.did AND task_inputs.input='source_data' AND task.task_id=task_inputs.task_id AND (task.creator IS NULL OR dataset.uploader<>task.creator)";
        }else if($type=='f'){
            $reuse_sql = "SELECT implementation.id as oringal_id, 'f' as ot, run.rid as reuse_id, 'r' as rt FROM `implementation`, `run`, `algorithm_setup` WHERE implementation.id=".$id." AND algorithm_setup.implementation_id=implementation.id AND run.setup=algorithm_setup.sid AND implementation.uploader<>run.uploader";
        }else /*if($type=='t')*/{
            $reuse_sql = "SELECT task.task_id as oringal_id, 't' as ot, run.rid as reuse_id, 'r' as rt FROM `task`, `run` WHERE task.task_id=".$id." AND run.task_id=task.task_id AND (task.creator IS NULL OR run.uploader<>task.creator)";
        }
        $sql="SELECT reuse.ot,count(ld.user_id) as count, SUM(ld.count) as sum, ld.ldt, DATE(ld.time) as date FROM (".$reuse_sql.") as reuse, (";

        $like_sql = "SELECT user_id, knowledge_id, knowledge_type, 1 as count, time, 'l' as ldt FROM likes";
        $download_sql = "SELECT user_id, knowledge_id, knowledge_type, count, time, 'd' as ldt FROM downloads";

        $sql.=$like_sql." UNION ".$download_sql.") as ld WHERE ld.knowledge_type=reuse.rt AND ld.knowledge_id=reuse.reuse_id";
        if ($from != null) {
            $sql.=' AND ld.time>="' . $from . '"';
        }
        if ($to != null) {
            $sql.=' AND ld.time < "' . $to . '"';
        }
        if($from!=null || $to!=null){
            $sql.=" GROUP BY DATE(ld.time), ld.ldt, reuse.ot ORDER BY date;";
        }else{
            $sql.=" GROUP BY ld.ldt, reuse.ot;";
        }

        return $this->KnowledgePiece->query($sql);
    }

    function getUploadersOfReusesOfUploadsOfUser($u_id){
        $datareuse_sql = "SELECT task.creator as uploader FROM `task`, `task_inputs`, `dataset` WHERE dataset.uploader=".$u_id." AND task_inputs.value=dataset.did AND task_inputs.input='source_data' AND task.task_id=task_inputs.task_id";
        $flowreuse_sql = "SELECT run.uploader FROM `implementation`, `run`, `algorithm_setup` WHERE implementation.uploader=".$u_id." AND algorithm_setup.implementation_id=implementation.id AND run.setup=algorithm_setup.sid";
        $taskreuse_sql = "SELECT run.uploader FROM `task`, `run` WHERE task.creator=".$u_id." AND run.task_id=task.task_id";

        $sql = "SELECT DISTINCT(u.uploader) FROM (".$datareuse_sql." UNION ".$flowreuse_sql." UNION ".$taskreuse_sql.") as u WHERE u.uploader<>".$u_id." AND u.uploader IS NOT NULL;";

        return $this->KnowledgePiece->query($sql);
    }

}
