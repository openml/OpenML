<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class ElasticSearch {

    public function __construct() {
        $this->CI = &get_instance();
        $this->CI->load->model('Dataset');
        $this->CI->load->model('Author');
        $this->CI->load->model('Data_quality');
        $this->CI->load->model('Dataset_tag');
        $this->CI->load->model('Dataset_topic');
        $this->CI->load->model('Dataset_description');
        $this->CI->load->model('Implementation_tag');
        $this->CI->load->model('Setup_tag');
        $this->CI->load->model('Task_tag');
        $this->CI->load->model('Run_tag');
        $this->CI->load->model('Algorithm_quality');
        $this->CI->load->model('Estimation_procedure');

        $this->CI->load->model('Downvote');
        $this->CI->load->model('KnowledgePiece');
        $this->CI->load->model('Gamification');
        $this->CI->load->model('Badge');
        $this->db = $this->CI->Dataset;
        $this->userdb = $this->CI->Author;

        $this->client = Elastic\Elasticsearch\ClientBuilder::create()
              ->setHosts([ES_URL])
              ->setBasicAuthentication(ES_USERNAME, ES_PASSWORD)
              ->build();
        $this->init_indexer = False;
    }

    public function initialize(){
        $this->data_names = $this->CI->Dataset->getAssociativeArray('did', 'name', 'name IS NOT NULL');
        $this->flow_names = $this->CI->Implementation->getAssociativeArray('id', 'fullName', 'name IS NOT NULL');
        $this->procedure_names = $this->CI->Estimation_procedure->getAssociativeArray('id', 'name', 'name IS NOT NULL');
        $this->user_names = array();
        $author = $this->userdb->get();
        if (is_array($author))
            foreach ($author as $a) {
                $this->user_names[$a->id] = $a->first_name . ' ' . $a->last_name;
            }

        $this->mappings['badge'] = array(
            'properties' => array(
                'time' => array(
                    'type' => 'date',
                    'format' => 'yyyy-MM-dd HH:mm:ss')
            )
        );

        $this->mappings['downvote'] = array(
            'properties' => array(
                'time' => array(
                    'type' => 'date',
                    'format' => 'yyyy-MM-dd HH:mm:ss'
                )
            )
        );

        $this->mappings['like'] = array(
            'properties' => array(
                'time' => array(
                    'type' => 'date',
                    'format' => 'yyyy-MM-dd HH:mm:ss')
            )
        );

        $this->mappings['download'] = array(
            'properties' => array(
                'time' => array(
                    'type' => 'date',
                    'format' => 'yyyy-MM-dd HH:mm:ss')
            )
        );

        $this->mappings['data'] = array(
            'properties' => array(
                'date' => array(
                    'type' => 'date',
                    'format' => 'yyyy-MM-dd HH:mm:ss',
            'fields' => array(
                    'keyword' => array(
               'type' => 'keyword'))
        ),
                'uploader' => array(
                    'type' => 'text',
                    'analyzer' => 'keyword'
                ),
                'exact_name' => array(
                    'type' => 'keyword'
                ),
                'tags' => array(
                    'type' => 'nested',
                    'properties' => array(
                        'tag' => array('type' => 'text'),
                        'uploader' => array('type' => 'text'))),
                'last_update' => array(
                    'type' => 'date',
                    'format' => 'yyyy-MM-dd HH:mm:ss'),
                'description' => array(
                    'type' => 'text',
                    'analyzer' => 'snowball'),
                'data_id' => array('type' => 'long'),
                'version' => array('type' => 'float'),
                'name' => array(
                    'type' => 'text',
                    'analyzer' => 'snowball'),
                'visibility' => array(
                    'type' => 'keyword'),
                'format' => array(
                    'type' => 'keyword'),
                'suggest' => array(
                    'type' => 'completion',
                    'analyzer' => 'standard')
            )
        );
    $this->mappings['flow'] = array(
            'properties' => array(
                'date' => array(
                    'type' => 'date',
                    'format' => 'yyyy-MM-dd HH:mm:ss'
                ),
                'exact_name' => array(
                    'type' => 'keyword',
                ),
                'tags' => array(
                    'type' => 'nested',
                    'properties' => array(
                        'tag' => array('type' => 'text'),
                        'uploader' => array('type' => 'text'))),
                'flow_id' => array('type' => 'long'),
                'version' => array('type' => 'float'),
                'last_update' => array(
                    'type' => 'date',
                    'format' => 'yyyy-MM-dd HH:mm:ss'
                ),
                'uploader' => array(
                    'type' => 'text',
                    'analyzer' => 'keyword'
                ),
                'description' => array(
                    'type' => 'text',
                    'analyzer' => 'snowball'
                ),
                'full_description' => array(
                    'type' => 'text',
                    'analyzer' => 'snowball'
                ),
                'name' => array(
                    'type' => 'text',
                    'analyzer' => 'snowball'
                ),
                'suggest' => array(
                    'type' => 'completion',
                    'analyzer' => 'standard'
                )
            )
        );
    $this->mappings['user'] = array(
            'properties' => array(
                'date' => array(
                    'type' => 'date',
                    'format' => 'yyyy-MM-dd HH:mm:ss'
                ),
                'last_update' => array(
                    'type' => 'date',
                    'format' => 'yyyy-MM-dd HH:mm:ss'
                ),
                'suggest' => array(
                    'type' => 'completion',
                    'analyzer' => 'standard'
                )
            )
        );
    $this->mappings['task'] = array(
            'properties' => array(
                'date' => array(
                    'type' => 'date',
                    'format' => 'yyyy-MM-dd HH:mm:ss'),
                'suggest' => array(
                    'type' => 'completion',
                    'analyzer' => 'standard'
                ),
                'tags' => array(
                    'type' => 'nested',
                    'properties' => array(
                        'tag' => array('type' => 'text'),
                        'uploader' => array('type' => 'text'))),
                'collections' => array(
                    'type' => 'nested',
                    'properties' => array(
                        'id' => array('type' => 'long'),
                        'type' => array('type' => 'text'))),
                'task_id' => array('type' => 'long'),
                'tasktype.tt_id' => array('type' => 'long'),
                'runs' => array('type' => 'long')
            )
        );
    $this->mappings['task_type'] = array(
            'properties' => array(
                'description' => array(
                    'type' => 'text',
                    'analyzer' => 'snowball'
                ),
                'name' => array(
                    'type' => 'text',
                    'analyzer' => 'snowball'
                ),
                'date' => array(
                    'type' => 'date',
                    'format' => 'yyyy-MM-dd HH:mm:ss'),
                'suggest' => array(
                    'type' => 'completion',
                    'analyzer' => 'standard'
                )
            )
        );
    $this->mappings['run'] = array(
            'properties' => array(
                'date' => array(
                    'type' => 'date',
                    'format' => 'yyyy-MM-dd HH:mm:ss'
                ),
                'tags' => array(
                    'type' => 'nested',
                    'properties' => array(
                        'tag' => array('type' => 'text'),
                        'uploader' => array('type' => 'text'))),
                'collections' => array(
                    'type' => 'nested',
                    'properties' => array(
                        'id' => array('type' => 'long'),
                        'type' => array('type' => 'text'))),
                'run_id' => array('type' => 'long'),
                'run_flow.flow_id' => array('type' => 'long'),
                'run_flow.name'  => array('type' => 'text', 'fielddata' => true),
                'last_update' => array(
                    'type' => 'date',
                    'format' => 'yyyy-MM-dd HH:mm:ss'
                ),
                'uploader' => array(
                    'type' => 'text',
                    'analyzer' => 'keyword'
                ),
                'evaluations' => array(
                    'type' => 'nested',
                    'properties' => array(
                        'evaluation_measure' => array('type' => 'text'),
                        'value' => array('type' => 'text', 'fielddata' => true)
                    )
                )
            )
        );
    $this->mappings['study'] = array(
            'properties' => array(
                'name' => array(
                    'type' => 'text',
                    'analyzer' => 'snowball'),
                'visibility' => array(
                    'type' => 'keyword'),
                'date' => array(
                    'type' => 'date',
                    'format' => 'yyyy-MM-dd HH:mm:ss'
                ),
                'description' => array(
                    'type' => 'text',
                    'analyzer' => 'snowball'
                ),
                'study_id' => array('type' => 'long'),
                'uploader' => array(
                    'type' => 'text',
                    'analyzer' => 'keyword'
                ),
                'suggest' => array(
                    'type' => 'completion',
                    'analyzer' => 'standard'
                )
            )
        );

    $this->mappings['measure'] = array(
            'properties' => array(
                'description' => array(
                    'type' => 'text',
                    'analyzer' => 'snowball'
                ),
                'name' => array(
                    'type' => 'text',
                    'analyzer' => 'snowball'
                ),
                'measure_type' => array(
                    'type' => 'text',
                    'fielddata' => true
                ),
                'date' => array(
                    'type' => 'date',
                    'format' => 'yyyy-MM-dd HH:mm:ss'),
                'suggest' => array(
                    'type' => 'completion',
                    'analyzer' => 'standard'
                )
            )
  );
  $this->init_indexer = True;
    }

    public function test() {
        return $this->client->ping();
    }

    public function get_types() {
        $params['index'] = '_all';
    $array_data = $this->client->indices()->getMapping($params);
        return array_keys($array_data);
    }

    public function index($type, $id = false, $altmetrics=True, $verbosity=0) {
        //bootstrap
        $indexParams['index'] = $type;
        if(! $this->client->indices()->getMapping($indexParams))
          echo $this->initialize_index($type);
        elseif (! $this->init_indexer)
          $this->initialize();

        $method_name = 'index_' . $type;
        if (method_exists($this, $method_name)) {
            try {
                return $this->$method_name($id, $altmetrics, $verbosity);
        } catch (Exception $e) {
        echo $e->getMessage();
                // TODO: log?
            }
        } else {
            return 'No function exists to build index of type ' . $type;
        }
    }

    public function index_from($type, $id = false, $verbosity=1, $altmetrics=False) {
        //bootstrap
        $indexParams['index'] = $type;
        if(! $this->client->indices()->getMapping($indexParams))
          echo $this->initialize_index($type);
        elseif (! $this->init_indexer)
          $this->initialize();

        $method_name = 'index_' . $type;
        if (method_exists($this, $method_name)) {
            try {
                return $this->$method_name(false, $id, $altmetrics, $verbosity);
        } catch (Exception $e) {
        echo $e->getMessage();
                // TODO: log?
            }
        } else {
            return 'No function exists to build index of type ' . $type;
        }
    }

    public function delete($type, $id = false) {
        $deleteParams = array();
        $deleteParams['index'] = $type;
    $deleteParams['type'] = $type;
        $deleteParams['id'] = $id;
        $response = $this->client->delete($deleteParams);
        return $response;
    }

    public function initialize_settings() {

        $params['index'] = '_all';
        $params['body']['index']['analysis']['analyzer']['keyword-ci'] = array('tokenizer' => 'keyword', 'filter' => 'lowercase');
        $this->client->indices()->putSettings($params);

        return 'Successfully updated settings';
    }

    public function initialize_index($t) {
        if(!$this->init_indexer)
             $this->initialize();
        $createparams = array(
            'index' => $t,
            'body' => array(
                'settings' => array(
                    'index' => array(
                        'number_of_shards' => 1,
                        'number_of_replicas' => 0
                     )
                 ),
                 'mappings' => $this->mappings[$t]
            )
        );
    $this->client->indices()->create($createparams);
        return '[Initialized mapping for ' . $t. '] ';
    }

    public function index_downvote($id, $start_id = 0, $altmetrics=True, $verbosity=0){

        $params['index'] = 'downvote';

        $downvotes = $this->CI->Downvote->getDownvote($id);

        if ($id and ! $downvotes)
            return 'Error: downvote ' . $id . ' is unknown';
        elseif (! $downvotes)
            return 'Nothing to index';
        foreach ($downvotes as $d) {
            $params['body'][] = array(
                'index' => array(
                    '_id' => $d->did
                )
            );

            $params['body'][] = $this->build_downvote($d);
        }

        $responses = $this->client->bulk($params);
        return 'Successfully indexed ' . sizeof($responses['items']) . ' out of ' . sizeof($downvotes) . ' downvotes.';
    }

    public function index_like($id, $start_id = 0, $altmetrics=True, $verbosity=0){

        $params['index'] = 'like';
        $likes = $this->db->query('select * from likes' . ($id ? ' where lid=' . $id : ''));

        if ($id and ! $likes)
            return 'Error: like ' . $id . ' is unknown';

        elseif (! $likes)
            return 'Nothing to index';

        foreach ($likes as $l) {
            $params['body'][] = array(
                'index' => array(
                    '_id' => $l->lid
                )
            );

            $params['body'][] = $this->build_like($l);
        }

        $responses = $this->client->bulk($params);
        return 'Successfully indexed ' . sizeof($responses['items']) . ' out of ' . sizeof($likes) . ' likes.';
    }

    private function build_downvote($d){
        $downvote = array(
            'downvote_id' => $d->did,
            'user_id' => $d->user_id,
            'knowledge_type' => $d->knowledge_type,
            'knowledge_id' => $d->knowledge_id,
            'reason' => $d->description,
            'reason_id' => $d->reason_id,
            'original' => $d->original,
            'count' => $d->count,
            'time' => $d->time
        );
        return $downvote;
    }

    private function build_like($l){
        $like = array(
            'like_id' => $l->lid,
            'user_id' => $l->user_id,
            'knowledge_type' => $l->knowledge_type,
            'knowledge_id' => $l->knowledge_id,
            'time' => $l->time
        );
        return $like;
    }

    public function index_download($id, $start_id = 0, $altmetrics=True, $verbosity=0){
        $params['index'] = 'download';
        $downloads = $this->db->query('select * from downloads' . ($id ? ' where did=' . $id : ''));

        if ($id and ! $downloads)
            return 'Error: download ' . $id . ' is unknown';

        elseif (! $downloads)
            return 'Nothing to index';

        foreach ($downloads as $d) {
            $params['body'][] = array(
                'index' => array(
                    '_id' => $d->did
                )
            );

            $params['body'][] = $this->build_download($d);
        }

        $responses = $this->client->bulk($params);
        return 'Successfully indexed ' . sizeof($responses['items']) . ' out of ' . sizeof($downloads) . ' downloads.';
    }

    private function build_download($d){
        $download = array(
            'download_id' => $d->did,
            'user_id' => $d->user_id,
            'knowledge_type' => $d->knowledge_type,
            'knowledge_id' => $d->knowledge_id,
            'count' => $d->count,
            'time' => $d->time
        );
        return $download;

    }

    public function index_user($id, $start_id = 0, $altmetrics=True, $verbosity=0) {

        $params['index'] = 'user';
        $users = $this->userdb->query('select id, first_name, last_name, email, company, country, bio, image, created_on, gamification_visibility from users where active="1"' . ($id ? ' and id=' . $id : ''));

        if ($id and ! $users)
            return 'Error: user ' . $id . ' is unknown';

        foreach ($users as $d) {
            $params['body'][] = array(
                'index' => array(
                    '_id' => $d->id
                )
            );

            $params['body'][] = $this->build_user($d);
        }

        $responses = $this->client->bulk($params);
        return 'Successfully indexed ' . sizeof($responses['items']) . ' out of ' . sizeof($users) . ' users.';
    }

    private function build_user($d) {

        $user = array(
            'user_id' => $d->id,
            'first_name' => $d->first_name,
            'last_name' => $d->last_name,
            'email' => $d->email,
            'company' => $d->company,
            'country' => $d->country,
            'bio' => $d->bio,
            'image' => $d->image,
            'date' => date("Y-m-d H:i:s", $d->created_on),
            'visibility' => 'public',
            'suggest' => array(
                'input' => array($d->first_name . ' ', $d->last_name . ' '),
                'weight' => 5
            ),
            'gamification_visibility' => $d->gamification_visibility
        );

        $uploads = $this->CI->KnowledgePiece->getTotalNumberOfUploadsOfUser($d->id);
        $data_up = 0;
        $flow_up = 0;
        $task_up = 0;
        $run_up = 0;
        $nr_of_uploads = 0;
        if($uploads){
            foreach($uploads as $up){
                if($up->kt == 'd'){
                    $data_up+=$up->count;
                }else if($up->kt =='f'){
                    $flow_up+=$up->count;
                }else if($up->kt == 't'){
                    $task_up+=$up->count;
                }else if($up->kt == 'r'){
                    $run_up+=$up->count;
                }
                $nr_of_uploads+=$up->count;
            }
            $user['nr_of_uploads'] =$nr_of_uploads;
        }else{
            $user['nr_of_uploads'] = 0;
        }
        $user['datasets_uploaded'] = $data_up;
        $user['flows_uploaded'] = $flow_up;
        $user['tasks_uploaded'] = $task_up;
        $user['runs_uploaded'] = $run_up;


        $runs_data = $this->db->query('select count(rid) as count FROM run r, task_inputs t, dataset d WHERE r.task_id=t.task_id and t.input="source_data" and t.value=d.did and r.uploader<>d.uploader and d.uploader=' . $d->id);
        if ($runs_data){
            $user['runs_on_datasets'] = $runs_data[0]->count;
        }else{
            $user['runs_on_datasets'] = 0;
        }

        $runs_flows = $this->db->query('select count(rid) as count FROM run r, algorithm_setup s, implementation i WHERE r.setup=s.sid and s.implementation_id=i.id and r.uploader<>i.uploader and i.uploader=' . $d->id);
        if ($runs_flows){
            $user['runs_on_flows'] = $runs_flows[0]->count;
        }else{
            $user['runs_on_flows'] = 0;
        }
        // Activity: how many likes and downloads
        $ld_of_user = $this->CI->KnowledgePiece->getNumberOfLikesAndDownloadsOfuser($d->id);
        $likes_of_user = 0;
        $nr_of_likes_data = 0;
        $nr_of_likes_flow = 0;
        $nr_of_likes_task = 0;
        $nr_of_likes_run = 0;
        $downloads_of_user = 0;
        $total_downloads = 0;
        $nr_of_downloads_data = 0;
        $nr_of_downloads_flow = 0;
        $nr_of_downloads_task = 0;
        $nr_of_downloads_run = 0;
        if($ld_of_user){
            foreach($ld_of_user as $ld){
                if($ld->ldt=='l'){
                    if($ld->knowledge_type=='d'){
                        $nr_of_likes_data+=$ld->sum;
                    }else if($ld->knowledge_type=='f'){
                        $nr_of_likes_flow+=$ld->sum;
                    }else if($ld->knowledge_type=='t'){
                        $nr_of_likes_task+=$ld->sum;
                    }else if($ld->knowledge_type=='r'){
                        $nr_of_likes_run+=$ld->sum;
                    }
                    $likes_of_user+=$ld->sum;
                }else if($ld->ldt=='d'){
                    if($ld->knowledge_type=='d'){
                        $nr_of_downloads_data+=$ld->sum;
                    }else if($ld->knowledge_type=='f'){
                        $nr_of_downloads_flow+=$ld->sum;
                    }else if($ld->knowledge_type=='t'){
                        $nr_of_downloads_task+=$ld->sum;
                    }else if($ld->knowledge_type=='r'){
                        $nr_of_downloads_run+=$ld->sum;
                    }
                    $downloads_of_user+=$ld->sum;
                }
                $total_downloads+=$ld->sum;
            }
        }
        $user['nr_of_likes'] = $likes_of_user;
        $user['nr_of_likes_data'] = $nr_of_likes_data;
        $user['nr_of_likes_flow'] = $nr_of_likes_flow;
        $user['nr_of_likes_task'] = $nr_of_likes_task;
        $user['nr_of_likes_run'] = $nr_of_likes_run;
        $user['nr_of_downloads'] = $downloads_of_user;
        $user['total_downloads'] = $total_downloads;
        $user['nr_of_downloads_data'] = $nr_of_downloads_data;
        $user['nr_of_downloads_flow'] = $nr_of_downloads_flow;
        $user['nr_of_downloads_task'] = $nr_of_downloads_task;
        $user['nr_of_downloads_run'] = $nr_of_downloads_run;
        if($d->gamification_visibility=='show'){
            $user['activity'] = $this->CI->Gamification->getActivityFromParts($user['nr_of_uploads'],$user['nr_of_likes'],$user['nr_of_downloads']);
        }

        // Reach: how many likes and downloads on uploads
        $ld_received = $this->CI->KnowledgePiece->getNumberOfLikesAndDownloadsOnUploadsOfUser($d->id);
        $likes_received = 0;
        $likes_received_data = 0;
        $likes_received_flow = 0;
        $likes_received_task = 0;
        $likes_received_run = 0;
        $downloads_received = 0;
        $downloads_received_data = 0;
        $downloads_received_flow = 0;
        $downloads_received_task = 0;
        $downloads_received_run = 0;
        if($ld_received){
            foreach($ld_received as $ld){
                if($ld->ldt=='l'){
                    if($ld->kt=='d'){
                        $likes_received_data+=$ld->sum;
                    }else if($ld->kt=='f'){
                        $likes_received_flow+=$ld->sum;
                    }else if($ld->kt=='t'){
                        $likes_received_task+=$ld->sum;
                    }else if($ld->kt=='r'){
                        $likes_received_run+=$ld->sum;
                    }
                    $likes_received+=$ld->sum;
                }else if($ld->ldt=='d'){
                  if (property_exists($ld, 'kt')) { // TODO: THIS IS A BUG! How can it not have a knowlegde piece ? (issue #70 on github)
                    if($ld->kt=='d'){
                        $downloads_received_data+=$ld->sum;
                    }else if($ld->kt=='f'){
                        $downloads_received_flow+=$ld->sum;
                    }else if($ld->kt=='t'){
                        $downloads_received_task+=$ld->sum;
                    }else if($ld->kt=='r'){
                        $downloads_received_run+=$ld->sum;
                    }
                  }
                  $downloads_received+=$ld->sum;
                }
            }
        }
        $user['likes_received'] = $likes_received;
        $user['likes_received_data'] = $likes_received_data;
        $user['likes_received_flow'] = $likes_received_flow;
        $user['likes_received_task'] = $likes_received_task;
        $user['likes_received_run'] = $likes_received_run;
        $user['downloads_received'] = $downloads_received;
        $user['downloads_received_data'] = $downloads_received_data;
        $user['downloads_received_flow'] = $downloads_received_flow;
        $user['downloads_received_task'] = $downloads_received_task;
        $user['downloads_received_run'] = $downloads_received_run;


        if($d->gamification_visibility=='show'){
            $user['reach'] = $this->CI->Gamification->getReachFromParts($user['likes_received'],$user['downloads_received']);

            $impact_struct = $this->CI->Gamification->getTotalImpact('u',$d->id,"2013-1-1",date("Y-m-d"));

            $user['reuse'] = $impact_struct['reuse'];

            $user['impact_of_reuse'] = floor($impact_struct['recursive_impact']);

            $user['reach_of_reuse'] = floor($impact_struct['reuse_reach']);

            $user['impact'] = floor($impact_struct['impact']);

            $user['badges'] = array();
            $badges = $this->CI->Badge->getBadgesOfUser($d->id);
            if($badges){
                foreach($badges as $b){
                    $badge = array('badge_id'=>$b->badge_id,'rank'=>$b->rank);
                    $user['badges'][] = $badge;
                }
            }
        }

        return $user;
    }

    public function index_study($id, $start_id = 0, $altmetrics=True, $verbosity=0) {

        $params['index'] = 'study';
        $studies = $this->db->query('select * from study' . ($id ? ' where id=' . $id : ''));

        if ($id and ! $studies)
            return 'Error: study ' . $id . ' is unknown';
        elseif (! $studies)
            return 'Nothing to index';
        foreach ($studies as $s) {
            $params['body'][] = array(
                'index' => array(
                    '_id' => $s->id
                )
            );

            $params['body'][] = $this->build_study($s);
        }

        $responses = $this->client->bulk($params);

        if($responses['errors'] == True and array_key_exists('error', $responses['items'][0]['index'])){
          $err = $responses['items'][0]['index']['error'];
          return 'ERROR: Type:' . $err['type'] . ' Reason: ' . $err['reason'] . (array_key_exists('caused_by', $err) ? ' Caused by: ' . $err['caused_by']['reason'] : '');
        }

        return 'Successfully indexed ' . sizeof($responses['items']) . ' out of ' . sizeof($studies) . ' studies.';
    }

    private function build_study($d) {
        $study = array(
            'study_id' => $d->id,
            'name' => $d->name,
            'alias' => $d->alias,
            'description' => $d->description,
            'date' => $d->creation_date,
            'uploader_id' => $d->creator,
            'uploader' => array_key_exists($d->creator, $this->user_names) ? $this->user_names[$d->creator] : 'Unknown',
            'visibility' => $d->visibility,
            'study_type' => $d->main_entity_type,
            'legacy' => $d->legacy,
            'suggest' => array(
                'input' => array($d->name, $d->description . ' '),
                'weight' => 5
            )
        );
        $study['datasets_included'] = 0;
        $study['tasks_included'] = 0;
        $study['flows_included'] = 0;
        $study['runs_included'] = 0;
        $data_tagged = NULL;
        $task_tagged = NULL;
        $flows_tagged = NULL;
        $runs_tagged = NULL;

        if($d->legacy == 'y'){
          $data_tagged = $this->db->query("select id from dataset_tag where tag='study_" . $d->id . "'");
          $task_tagged = $this->db->query("select id from task_tag where tag='study_" . $d->id . "'");
          $flows_tagged = $this->db->query("select id from implementation_tag where tag='study_" . $d->id . "'");
          $runs_tagged = $this->db->query("select id from run_tag where tag='study_" . $d->id . "'");
        } elseif ($d->main_entity_type == 'task') {
          $task_tagged = $this->db->query("select task_id as id from task_study where study_id=" . $d->id);
          $data_tagged = $this->db->query("select value as id from task_inputs where input='source_data' and task_id in (select task_id from task_study where study_id=" . $d->id . ")");
        } elseif ($d->main_entity_type == 'run') {
          $runs_tagged = $this->db->query("select run_id as id from run_study where study_id=" . $d->id);
          $flows_tagged = $this->db->query("select distinct implementation_id as id from algorithm_setup where sid in (select setup from run where rid in (select run_id from run_study where study_id=" . $d->id . "))");
          $task_tagged = $this->db->query("select distinct task_id as id from run where rid in (select run_id from run_study where study_id=" . $d->id . ")");
          $data_tagged = $this->db->query("select distinct value as id from task_inputs where input='source_data' and task_id in (select task_id from run where rid in (select run_id from run_study where study_id=" . $d->id . "))");
        }
        if ($data_tagged){
          $study['datasets_included'] = count($data_tagged);
          foreach ($data_tagged as $t){
              $this->index_data($t->id);
            }
        }
        if ($task_tagged){
          $study['tasks_included'] = count($task_tagged);
          foreach ($task_tagged as $t){
              $this->index_task($t->id);
            }
        }
        if ($flows_tagged){
          $study['flows_included'] = count($flows_tagged);
          foreach ($flows_tagged as $t){
              $this->index_flow($t->id);
            }
        }
        if ($runs_tagged){
          $study['runs_included'] = count($runs_tagged);
          foreach ($runs_tagged as $t){
              $this->index_run($t->id);
            }
        }
        return $study;
    }

    public function index_task($id, $start_id = 0, $altmetrics=True, $verbosity=0) {
        $params['index'] = 'task';
        $taskmaxquery = $this->db->query('SELECT min(task_id) as mintask, max(task_id) as maxtask from task' . ($id ? ' where task_id=' . $id : ''));
        $taskcountquery = $this->db->query('SELECT count(task_id) as taskcount from task' . ($id ? ' where task_id=' . $id : ''));
        $taskmin = intval($taskmaxquery[0]->mintask);
        $taskmax = intval($taskmaxquery[0]->maxtask);
        $taskcount = intval($taskcountquery[0]->taskcount);

        $task_id = max($taskmin, $start_id);
        $submitted = 0;
        $incr = min(50, $taskcount);
        if ($verbosity)
          echo "Processing task          ";
        while ($task_id <= $taskmax) {
            if ($verbosity) {
              echo "\033[9D";
              echo str_pad($task_id, 9, ' ', STR_PAD_RIGHT);
            }
            $tasks = null;
            $params['body'] = array();
            $tasks = $this->db->query('select a.*, b.runs from (SELECT t.task_id, tt.ttid, tt.name, t.creation_date, t.embargo_end_date, t.creator FROM task t, task_type tt where t.ttid=tt.ttid and task_id>=' . $task_id . ' and task_id<' . ($task_id + $incr) . ') as a left outer join (select task_id, count(rid) as runs from run r group by task_id) as b on a.task_id=b.task_id');
            if ($tasks) {
                foreach ($tasks as $t) {
                    $params['body'][] = array(
                        'index' => array(
                            '_id' => $t->task_id
                        )
                    );
                    $params['body'][] = $this->build_task($t);
                }
                $responses = $this->client->bulk($params);

                $submitted += sizeof($responses['items']);
            }
            $task_id += $incr;
        }

        return 'Successfully indexed ' . $submitted . ' out of ' . $taskcount . ' tasks.';
    }

    private function build_single_task($id) {
        $time = microtime(true);
        $task = $this->db->query('select a.*, b.runs from (SELECT t.task_id, tt.ttid, tt.name, t.creation_date, t.embargo_end_date, t.creator FROM task t, task_type tt where t.ttid=tt.ttid and task_id=' . $id . ') as a left outer join (select task_id, count(rid) as runs from run r group by task_id) as b on a.task_id=b.task_id');
        $t = $this->build_task($task[0]);
        echo "Build task: ".(microtime(true) - $time)."\r\n";
        $time = microtime(true);
        return $t;
    }

    private function build_task($d) {

        $newdata = array(
            'task_id' => $d->task_id,
            'runs' => (int) $this->checkNumeric($d->runs),
            'visibility' => ((strtotime($d->embargo_end_date) < time()) ? 'public' : 'private'),
            'embargo_end_date' => $d->embargo_end_date,
            'tasktype' => array(
                'tt_id' => (float) $d->ttid,
                'name' => $d->name
            ),
            'date' => $d->creation_date,
            'uploader_id' => $d->creator
        );

        $description = array();
        $description[] = $d->name;

        $task = $this->db->query('SELECT i.input, ti.type, i.value  FROM task_inputs i, task_type_inout ti, task t  where i.input=ti.name and ti.ttid=t.ttid and t.task_id=i.task_id and i.task_id=' . $d->task_id . ' order by ti.type');
        $did = 0;
        if ($task) {
            foreach ($task as $t) {
                if ($t->input == 'source_data') {
                    $description[] = $this->data_names[$t->value];
                    $newdata[$t->input] = array(
                        'type' => $t->type,
                        'data_id' => $t->value,
                        'name' => $this->data_names[$t->value]
                    );
                    $did = $t->value;
                } else if ($t->input == 'estimation_procedure') {
                    $description[] = $this->procedure_names[$t->value];
                    $newdata[$t->input] = array(
                        'type' => $t->type,
                        'proc_id' => $t->value,
                        'name' => $this->procedure_names[$t->value]
                    );
                } else if ($t->input == 'target_feature') {
                    $description[] = $t->value;
                    $newdata[$t->input] = $t->value;
                    $targets = $this->db->query('SELECT data_type, ClassDistribution FROM data_feature where did=' . $did . ' and name="' . $t->value . '"');
                    if ($targets) {
                        if ($targets[0]->data_type == "nominal") {
                            $distr = json_decode($targets[0]->ClassDistribution);
                            if ($distr) {
                                $newdata['target_values'] = $distr[0];
                            }
                        }
                    }
                } else {
                    if (strpos($t->value, 'http') === false) {
                        $description[] = $t->value;
                    }
                    $newdata[$t->input] = $t->value;
                }
            }
        }

        $newdata['tags'] = array();
        $newdata['collections'] = array();
        $studies = array();
        $tags = $this->CI->Task_tag->getAssociativeArray('tag', 'uploader', 'id = ' . $d->task_id);
        if ($tags != false) {
            foreach ($tags as $t => $u) {
                $newdata['tags'][] = array(
                    'tag' => $t,
                    'uploader' => $u);
                if(substr( $t, 0, 6 ) === "study_"){
                    $study_id = substr($t, strpos($t, "_") + 1);
                    $studies[] = $study_id;
                    $newdata['collections'][] = array(
                        'type' => 'task',
                        'id' => $study_id);
                }
            }
        }

        // replace with study list in new indexer
        $new_studies = array();
        $task_studies = $this->db->query("select study_id from task_study where task_id=" . $d->task_id);
        if ($task_studies != false) {
            foreach ($task_studies as $t) { 
                if (!in_array($t->study_id, $studies)){ 
                    $new_studies[] = $t->study_id;
                    $newdata['collections'][] = array(
                        'type' => 'task',
                        'id' => $t->study_id);
                }}
        }
        $run_studies = $this->db->query("select distinct study_id from run_study where run_id in (select rid from run where task_id=" . $d->task_id . ")");
        if ($run_studies != false) {
            foreach ($run_studies as $t) { 
                if (!in_array($t->study_id, $studies)){ 
                    $new_studies[] = $t->study_id; 
                    $newdata['collections'][] = array(
                        'type' => 'run',
                        'id' => $t->study_id);
                }}
        }
        if ($new_studies) {
            foreach ($new_studies as $t) { $new_data['tags'][] = array('tag' => 'study_' . $t, 'uploader' => '0'); }
        }

        $newdata['suggest'] = array(
            'input' => $description,
            'weight' => '3'
        );

        $nr_of_downvotes = 0;
        $nr_of_issues = $this->CI->Downvote->getDownvotesByKnowledgePiece('t',$d->task_id,1);
        if($nr_of_issues){
            $newdata['nr_of_issues'] = count($nr_of_issues);
            $nr_of_downvotes+=count($nr_of_issues);
            $downvote_agrees = $this->CI->Downvote->getDownvotesByKnowledgePiece('t',$d->task_id,0);
            if($downvote_agrees){
                $nr_of_downvotes+=count($downvote_agrees);
            }
        } else{
            $newdata['nr_of_issues'] = 0;
        }
        $newdata['nr_of_downvotes'] = $nr_of_downvotes;

        $ld_task = $this->CI->KnowledgePiece->getNumberOfLikesAndDownloadsOnUpload('t',$d->task_id);
        $reach = 0;
        $nr_of_likes = 0;
        $nr_of_downloads = 0;
        $total_downloads = 0;
        if($ld_task){
            foreach($ld_task as $ld){
                if($ld->ldt=='l'){
                    $reach += $this->CI->Gamification->getReachFromParts($ld->count,0);
                    $nr_of_likes+=$ld->count;
                }else if($ld->ldt=='d'){
                    $reach += $this->CI->Gamification->getReachFromParts(0,$ld->count);
                    $nr_of_downloads+=$ld->count;
                    $total_downloads+=$ld->sum;
                }
            }
        }
        $newdata['nr_of_likes'] = $nr_of_likes;
        $newdata['nr_of_downloads'] = $nr_of_downloads;
        $newdata['total_downloads'] = $total_downloads;
        $newdata['reach'] = $reach;

        $impact_struct = $this->CI->Gamification->getImpact('t',$d->task_id,"2013-1-1",date("Y-m-d"));

        $newdata['reuse'] = $impact_struct['reuse'];

        $newdata['impact_of_reuse'] = floor($impact_struct['recursive_impact']);

        $newdata['reach_of_reuse'] = floor($impact_struct['reuse_reach']);

        $newdata['impact'] = floor($impact_struct['impact']);

        return $newdata;
    }

    private function fetch_setups($id = false) {
        $index = array();
        $setups = $this->db->query('SELECT s.setup, f.fullName, i.name, s.value FROM input_setting s, input i, implementation f where i.id=s.input_id AND i.implementation_id = f.id' . ($id ? ' and s.setup=' . $id : ''));
        if ($setups)
            foreach ($setups as $v) {
                // JvR: if we keep relying on the fullname, there should be a central convenience fn that concatenates the full name in an uniform manner.
                $parameter_fullname = $v->fullName . '_' . $v->name;
                $index[$v->setup][] = array('parameter' => $parameter_fullname, 'value' => $v->value);
            }
        elseif($id != false)
          $index[$id] = array();
        return $index;
    }

    private function fetch_tasks($id = false) {
        $index = array();
    $tasks = $this->db->query("SELECT t.task_id, t.embargo_end_date, tt.name as ttname, i.value AS did, d.name AS dname, ep.name AS epname FROM task_inputs i, task_type tt, dataset d, task t LEFT JOIN task_inputs i2 on (t.task_id = i2.task_id AND i2.input = 'estimation_procedure') LEFT JOIN estimation_procedure ep on ep.id = i2.value WHERE t.task_id = i.task_id AND i.input = 'source_data' AND t.ttid = tt.ttid AND d.did = i.value" . ($id ? ' and t.task_id=' . $id : ''));
        if ($tasks){
            if($id)
              $targets = $this->fetch_classes($tasks[0]->did);
            else
              $targets = $this->fetch_classes();
            foreach ($tasks as $v) {
              $index[$v->task_id]['task_id'] = $v->task_id;
              $index[$v->task_id]['visibility'] = ((strtotime($v->embargo_end_date) < time()) ? 'public' : 'private');
              $index[$v->task_id]['tasktype']['name'] = $v->ttname;
              $index[$v->task_id]['source_data']['data_id'] = $v->did;
              $index[$v->task_id]['source_data']['name'] = $v->dname;
              $index[$v->task_id]['estimation_procedure']['name'] = $v->epname;

              if($v->ttname == "Supervised Classification"){
                if(array_key_exists($v->did,$targets)){ //check whether the task is valid (uses an existing dataset)
                  $index[$v->task_id]['target_values'] = $targets[$v->did]['target_values'];
                }
              }
            }
          }
        return $index;
    }

    private function fetch_classes($id = false) {
        $index = array();
        $data = $this->db->query("SELECT did, data_type, ClassDistribution FROM data_feature WHERE is_target='true'" . ($id ? ' and did=' . $id : ''));
        if ($data)
            foreach ($data as $v) {
              if ($v->data_type == "nominal") {
                  $distr = json_decode($v->ClassDistribution);
                  if ($distr) {
                      $index[$v->did]['target_values'] = $distr[0];
                  }
              }
            }
        return $index;
    }

    private function fetch_runfiles($min, $max) {
        $index = array();
        $runfiles = $this->db->query('SELECT source, field, name, format, file_id from runfile where source >= ' . $min . ' and source < ' . $max);
        if ($runfiles)
          foreach ($runfiles as $r) {
              $index[$r->source][$r->field]['url'] = BASE_URL . 'data/download/' . $r->file_id . '/' . $r->name;
              $index[$r->source][$r->field]['format'] = $r->format;
          }
        return $index;
    }

    function roundnum($i) {
        if (is_numeric($i))
            return round($i, 4);
        else
            return $i;
    }

    private function fetch_evaluations($min, $max, $include_folds=True) {
        $index = array();
  if($include_folds){
        $folddata = $this->db->query('SELECT e.source, m.name AS function, e.fold, e.`repeat`, e.value FROM evaluation_fold e, math_function m WHERE e.function_id = m.id AND source >= ' . $min . ' and source < ' . $max);
  $allfolds = array();

        if ($folddata) {
            $curr_src = array();
            $folds = array();
            $curr_fold = array();
            $rp = 0;
            $src = 0;
            $fct = "";
            foreach ($folddata as $f) {
                if ($f->source != $src) {
                    if (!empty($curr_fold))
                        $folds[] = $curr_fold;
                    if (!empty($folds))
                        $curr_src[$fct] = $folds;
                    if (!empty($curr_src))
                        $allfolds[$src] = $curr_src;
                    $src = $f->source;
                    $curr_src = array();
                    $folds = array();
                    $curr_fold = array();
                    $fct = $f->function;
                    $rp = $f->repeat;
                }
                elseif ($f->function != $fct) {
                    if (!empty($curr_fold))
                        $folds[] = $curr_fold;
                    if (!empty($folds))
                        $curr_src[$fct] = $folds;
                    $folds = array();
                    $curr_fold = array();
                    $fct = $f->function;
                    $rp = $f->repeat;
                }
                elseif ($f->repeat != $rp) {
                    if (!empty($curr_fold))
                        $folds[] = $curr_fold;
                    $rp = $f->repeat;
                    $curr_fold = array();
                }
                if ($f->value)
                    $curr_fold[] = round($f->value, 4);
            }
            $folds[] = $curr_fold;
            $curr_src[$fct] = $folds;
            $allfolds[$src] = $curr_src;
        }
  }
        $evals = $this->db->query('SELECT e.source, m.name AS `function`, e.value, e.stdev, e.array_data FROM evaluation e, math_function m WHERE e.function_id = m.id AND source >= ' . $min . ' and source < ' . $max);
        if ($evals) {
            foreach ($evals as $r) {
                $neweval = array(
                    'evaluation_measure' => $r->function
                );
                if ($r->value) {
                    $neweval['value'] = (is_numeric($r->value) ? round($r->value, 4) : $r->value);
                }
                if ($r->stdev) {
                    $neweval['stdev'] = round($r->stdev, 4);
                }
                if ($r->array_data) {
                    $arrayd = str_replace('?', 'null', $r->array_data);
                    if (json_decode($arrayd)) {
                        $arrayd = array_map(array($this, 'roundnum'), json_decode($arrayd));
                        $neweval['array_data'] = $arrayd;
                    } else {
                        $neweval['data'] = $arrayd;
                    }
                }
                if (array_key_exists($r->source, $allfolds) and array_key_exists($r->function, $allfolds[$r->source]))
                    $neweval['per_fold'] = $allfolds[$r->source][$r->function];
                $index[$r->source][] = $neweval;
            }
        }
        return $index;
    }

    //update task, dataset, flow to make sure that their indexed run counts are up to date? Only needed for sorting on number of runs.
    private function update_runcounts($run) {
        $runparams['index'] = 'run';
    $runparams['type'] = 'run';
        $runparams['body']['query']['match']['run_task.task_id'] = $run->task_id;
        $result = $this->client->search($runparams);
        $runcount = $this->checkNumeric($result['hits']['total']['value']);

        $params['index'] = 'task';
        $params['id'] = $run->task_id;
        $params['body'] = array('doc' => array('runs' => $runcount));
        $this->client->update($params);

        $runparams = array();
        $runparams['index'] = 'run';
        $runparams['type'] = 'run';
        $runparams['body']['query']['match']['run_flow.flow_id'] = $run->implementation_id;
        $result = $this->client->search($runparams);
        $runcount = $this->checkNumeric($result['hits']['total']['value']);

        $params['index'] = 'flow';
        $params['id'] = $run->implementation_id;
        $params['body'] = array('doc' => array('runs' => $runcount));
        $this->client->update($params);
    }

    //update tags for given type and id
    public function update_tags($type, $id) {
        $tagtable = $this->CI->Run_tag;
        if($type == 'flow')
          $tagtable = $this->CI->Implementation_tag;
        elseif($type == 'data')
          $tagtable = $this->CI->Dataset_tag;
        elseif($type == 'task')
          $tagtable = $this->CI->Task_tag;

        $ts = array();
        $tags = $tagtable->getAssociativeArray('tag', 'uploader', 'id = ' . $id);
        if ($tags != false) {
            foreach ($tags as $t => $u) {
                $ts[] = array(
                    'tag' => $t,
                    'uploader' => $u);
            }
        }

        $params['index'] = $type;
        $params['id'] = $id;
        $params['body'] = array('doc' => array('tags' => $ts));
        $this->client->update($params);
    }

    //update topics for given type and id
    public function update_topics($id) {
        $topictable = $this->CI->Dataset_topic;
        $ts = array();
        $topics = $topictable->getAssociativeArray('topic', 'uploader', 'id = ' . $id);
        if ($topics != false) {
            foreach ($topics as $t => $u) {
                $ts[] = array(
                    'topic' => $t,
                    'uploader' => $u);
            }
        }

        $params['index'] = 'data';
        $params['id'] = $id;
        $params['body'] = array('doc' => array('topics' => $ts));
        $this->client->update($params);
    }

    private function index_single_run($id) {

        $params['index'] = 'run';
        $params['id'] = $id;

        $run = $this->db->query('SELECT rid, uploader, setup, implementation_id, task_id, start_time FROM run r, algorithm_setup s where s.sid=r.setup and rid=' . $id);
        if (!$run)
            return 'Error: run ' . $id . ' is unknown';

        $setups = $this->fetch_setups($run[0]->setup);
        $tasks = $this->fetch_tasks($run[0]->task_id);
        $runfiles = $this->fetch_runfiles($id, $id + 1);
        $evals = $this->fetch_evaluations($id, $id + 1);
        $params['body'] = $this->build_run($run[0], $setups, $tasks, $runfiles, $evals);
        //echo json_encode($params);
        $responses = $this->client->index($params);
        $this->update_runcounts($run[0]);

        return 'Successfully indexed run '. $id;
    }

    public function index_run($id, $start_id = 0, $altmetrics=True, $verbosity=0) {
        if ($id)
            return $this->index_single_run($id);

        $params['index'] = 'run';
  $setups = array();
        $tasks = array();

  $runmaxquery = $this->db->query('SELECT max(rid) as maxrun from run');
        $runcountquery = $this->db->query('SELECT count(rid) as runcount from run');
        $runmax = intval($runmaxquery[0]->maxrun);
        $runcount = intval($runcountquery[0]->runcount);

        $rid = $start_id;
        $submitted = 0;
        $incr = 100;
        if ($verbosity) {
          echo "Processing run ";
        }
        while ($rid < $runmax) {
          if ($verbosity) {
            echo str_pad($rid, 9, ' ', STR_PAD_RIGHT);
          }
          set_time_limit(6000);
          $runs = null;
          $runfiles = null;
          $evals = null;
          $params['body'] = array();
          $runs = $this->db->query('SELECT rid, uploader, setup, implementation_id, task_id, start_time, re.error, error_message, run_details FROM run r,run_evaluated re, algorithm_setup s where r.rid=re.run_id and s.sid=r.setup and rid>=' . $rid . ' and rid<' . ($rid + $incr));
          if($runs){
            $runfiles = $this->fetch_runfiles($rid, $rid + $incr);
            $evals = $this->fetch_evaluations($rid, $rid + $incr);

            foreach ($runs as $r) {
              try {
                $params['body'][] = array(
                  'index' => array(
                     '_id' => $r->rid
                  )
                );
              $params['body'][] = $this->build_run($r, $setups, $tasks, $runfiles, $evals, $altmetrics);
              } catch (Exception $e) {
                if ($verbosity) {
                  echo $e->getMessage();
                  return $e->getMessage();
                }
              }
              $responses = $this->client->bulk($params);
              $submitted += sizeof($responses['items']);
              if ($verbosity) {
                #echo "-  completed ".str_pad($submitted, 9, ' ', STR_PAD_RIGHT);
                #echo "\033[31D";
              }
           }
         } elseif($verbosity) {
           #echo "\033[9D";
         }
         $rid += $incr;
       }
       return 'Successfully indexed ' . $submitted . ' out of ' . $runcount . ' runs.';
    }

    private function build_run($r, $setups, $tasks, $runfiles, $evals, $altmetrics=True) {
        // build dictionary of setups and tasks to eliminate duplicate calls
        if(!array_key_exists($r->setup, $setups))
          $setups[$r->setup] = $this->fetch_setups($r->setup)[$r->setup];
        if(!array_key_exists($r->task_id, $tasks))
          $tasks[$r->task_id] = $this->fetch_tasks($r->task_id)[$r->task_id];
        if(!array_key_exists($r->task_id,$tasks) or !array_key_exists($r->implementation_id,$this->flow_names)){ // catch faulty runs
            return array();
        }
        $new_data = array(
            'run_id' => $r->rid,
            'uploader' => array_key_exists($r->uploader, $this->user_names) ? $this->user_names[$r->uploader] : 'Unknown',
            'uploader_id' => intval($r->uploader),
            'run_task' => $tasks[$r->task_id],
            'date' => $r->start_time,
            'run_flow' => array(
                'flow_id' => $r->implementation_id,
                'name' => $this->flow_names[$r->implementation_id],
                'parameters' => array_key_exists($r->setup, $setups) ? $setups[$r->setup] : array()
            ),
            'output_files' => array_key_exists($r->rid, $runfiles) ? $runfiles[$r->rid] : array(),
            'evaluations' => array_key_exists($r->rid, $evals) ? $evals[$r->rid] : array(),
            'error' => (isset($r->error) ? $r->error : ""),
            'error_message' => (isset($r->error_message) ? $r->error_message : ""),
            'run_details' => (isset($r->run_details) ? $r->run_details : ""),
            'visibility' => $tasks[$r->task_id]['visibility']
        );

        $new_data['tags'] = array();
        $new_data['collections'] = array();
        $studies = array();
        $tags = $this->CI->Run_tag->getAssociativeArray('tag', 'uploader', 'id = ' . $r->rid);
        if ($tags != false) {
            foreach ($tags as $t => $u) {
                $new_data['tags'][] = array(
                    'tag' => $t,
                    'uploader' => $u);
                if(substr( $t, 0, 6 ) === "study_"){
                    $study_id = substr($t, strpos($t, "_") + 1);
                    $studies[] = $study_id;
                    $new_data['collections'][] = array(
                        'type' => 'run',
                        'id' => $study_id);
                }
            }
        }

        $run_studies = $this->db->query("select distinct study_id from run_study where run_id=" . $r->rid);
        if ($run_studies != false) {
            foreach ($run_studies as $t) {
              if (!in_array($t->study_id, $studies)){
                 $new_data['tags'][] = array('tag' => 'study_' . $t->study_id, 'uploader' => '0');                    
                 $new_data['collections'][] = array(
                    'type' => 'run',
                    'id' => $t->study_id);
              }
            }
        }

        $new_data['nr_of_issues'] = 0;
        $new_data['nr_of_downvotes'] = 0;
        $new_data['nr_of_likes'] = 0;
        $new_data['nr_of_downloads'] = 0;
        $new_data['total_downloads'] = 0;
        $new_data['reach'] = 0;
        $new_data['reuse'] = 0;
        $new_data['impact_of_reuse'] = 0;
        $new_data['reach_of_reuse'] = 0;
        $new_data['impact'] = 0;
        if($altmetrics){
              $nr_of_downvotes = 0;
              $nr_of_issues = $this->CI->Downvote->getDownvotesByKnowledgePiece('r',$r->rid,1);
              if($nr_of_issues){
                  $new_data['nr_of_issues'] = count($nr_of_issues);
                  $nr_of_downvotes+=count($nr_of_issues);
                  $downvote_agrees = $this->CI->Downvote->getDownvotesByKnowledgePiece('r',$r->rid,0);
                  if($downvote_agrees){
                      $nr_of_downvotes+=count($downvote_agrees);
                  }
              }else{
                  $new_data['nr_of_issues'] = 0;
              }
              $new_data['nr_of_downvotes'] = $nr_of_downvotes;

              $ld_run = $this->CI->KnowledgePiece->getNumberOfLikesAndDownloadsOnUpload('r',$r->rid);
              $reach = 0;
              $nr_of_likes = 0;
              $nr_of_downloads = 0;
              $total_downloads = 0;
              if($ld_run){
                  foreach($ld_run as $ld){
                      if($ld->ldt=='l'){
                          $reach += $this->CI->Gamification->getReachFromParts($ld->count,0);
                          $nr_of_likes+=$ld->count;
                      }else if($ld->ldt=='d'){
                          $reach += $this->CI->Gamification->getReachFromParts(0,$ld->count);
                          $nr_of_downloads+=$ld->count;
                          $total_downloads+=$ld->sum;
                      }
                  }
              }
              $new_data['nr_of_likes'] = $nr_of_likes;
              $new_data['nr_of_downloads'] = $nr_of_downloads;
              $new_data['total_downloads'] = $total_downloads;
              $new_data['reach'] = $reach;
        }
        return $new_data;
    }

    public function index_task_type($id, $start_id = 0, $altmetrics=True, $verbosity=0) {

        $params['index'] = 'task_type';
        $types = $this->db->query('SELECT tt.ttid, tt.name, tt.description, count(task_id) as tasks, tt.creationDate as date FROM task_type tt, task t where tt.ttid=t.ttid' . ($id ? ' and tt.ttid=' . $id : '') . ' group by tt.ttid');

        if ($id and ! $types)
            return 'Error: task type ' . $id . ' is unknown';
        elseif (! $types)
            return 'Nothing to index';
        foreach ($types as $d) {
            $params['body'][] = array(
                'index' => array(
                    '_id' => $d->ttid
                )
            );

            $params['body'][] = $this->build_task_type($d);
        }

        $responses = $this->client->bulk($params);

        return 'Successfully indexed ' . sizeof($responses['items']) . ' out of ' . sizeof($types) . ' task types.';
    }

    private function build_task_type($d) {
        $new_data = array(
            'tt_id' => $d->ttid,
            'name' => $d->name,
            'description' => $d->description,
            'tasks' => $d->tasks,
            'visibility' => 'public',
            'date' => $d->date,
            'suggest' => array(
                'input' => array($d->name, $d->description . ' '),
                'weight' => 2
            ),
        );

        $inputs = $this->db->query('SELECT name, type, description, io, requirement FROM task_type_inout where ttid=' . $d->ttid);
        if ($inputs)
          foreach ($inputs as $i) {
              $new_data['input'][] = array(
                  'name' => $i->name,
                  'type' => $i->type,
                  'description' => $i->description,
                  'io' => $i->io,
                  'requirement' => $i->requirement
              );
          }
        return $new_data;
    }

    public function index_flow($id, $start_id = 0, $altmetrics=True, $verbosity=0) {

        $params['index'] = 'flow';
        $flows = $this->db->query('select i.*, count(rid) as runs from implementation i left join algorithm_setup s on (s.implementation_id=i.id) left join run r on (r.setup=s.sid)' . ($id ? ' where i.id=' . $id : '') . ' group by i.id');


        if ($id and ! $flows)
            return 'Error: flow ' . $id . ' is unknown';
        elseif (! $flows)
            return 'Nothing to index';
        foreach ($flows as $d) {
            $params['body'][] = array(
                'index' => array(
                    '_id' => $d->id
                )
            );

            $params['body'][] = $this->build_flow($d);
        }

        $responses = $this->client->bulk($params);

        if($responses['errors'] == True){
    foreach ($responses['items'] as $res){
    if(array_key_exists('error',$res['index'])){
      $err = $res['index']['error'];
      return 'ERROR for ID ' . $res['index']['_id'] . ' : Type:' . $err['type'] . ' Reason: ' . $err['reason'] . (array_key_exists('caused_by', $err) ? ' Caused by: ' . $err['caused_by']['reason'] : '');
      }
    }
  }

        return 'Successfully indexed ' . sizeof($responses['items']) . ' out of ' . sizeof($flows) . ' flows.';
    }

    private function build_flow($d) {
        $new_data = array(
            'flow_id' => $d->id,
            'name' => $d->name,
            'exact_name' => $d->name,
            'version' => $d->version,
            'external_version' => $d->external_version,
            'licence' => $d->licence,
            'description' => (strlen($d->description) > 0 ? $d->description : 'No description'),
            'full_description' => $d->fullDescription,
            'installation_notes' => $d->installationNotes,
            'uploader' => array_key_exists($d->uploader, $this->user_names) ? $this->user_names[$d->uploader] : 'Unknown',
            'uploader_id' => $d->uploader,
            'creator' => $d->creator,
            'contributor' => $d->contributor,
            'dependencies' => $d->dependencies,
            'date' => ($d->uploadDate == "0000-00-00 00:00:00" ? null : $d->uploadDate),
            'runs' => $this->checkNumeric($d->runs),
            'visibility' => $d->visibility,
            'suggest' => array(
                'input' => array(str_replace("weka.", "", $d->name), $d->description . ' '),
                'weight' => 5
            )
        );

        $new_data['qualities'] = array();
        $qualities = $this->CI->Algorithm_quality->getAssociativeArray('quality', 'value', 'implementation_id = ' . $d->id);
        if ($qualities != false)
            $new_data['qualities'] = array_map(array($this, 'checkNumeric'), $qualities);

        $new_data['tags'] = array();
        $studies = array();
        $tags = $this->CI->Implementation_tag->getAssociativeArray('tag', 'uploader', 'id = ' . $d->id);
        if ($tags != false) {
            foreach ($tags as $t => $u) {
                $new_data['tags'][] = array(
                    'tag' => $t,
                    'uploader' => $u);
                if(substr( $t, 0, 6 ) === "study_")
                  $studies[] = substr($t, strpos($t, "_") + 1);
            }
        }

        $run_studies = $this->db->query("select distinct study_id from run_study where run_id in (select rid from run where setup in (select sid from algorithm_setup where implementation_id=" . $d->id . "))");
        if ($run_studies != false) {
            foreach ($run_studies as $t) {
              if (!in_array($t->study_id, $studies)){
                 $new_data['tags'][] = array('tag' => 'study_' . $t->study_id, 'uploader' => '0');
              }
            }
        }

        $new_data['components'] = array();
        $components = $this->db->query('SELECT identifier, i.id, i.fullName, n.description FROM implementation_component c, implementation i, input n WHERE c.child = i.id and n.implementation_id = c.parent and n.name= c.identifier and c.parent=' . $d->id);
        if ($components != false) {
            foreach ($components as $p) {
                $com = array(
                    'identifier' => $p->identifier,
                    'id' => $p->id,
                    'name' => $p->fullName,
                    'description' => $p->description
                );
                $new_data['components'][] = $com;
            }
        }

        $new_data['parameters'] = array();
        $parameters = $this->db->query('select input.*, implementation.fullName from input, implementation where input.implementation_id = implementation.id AND implementation_id=' . $d->id);
        if ($parameters) {
            foreach ($parameters as $p) {
                // JvR: if we keep relying on the fullname, there should be a central convenience fn that concatenates the full name in an uniform manner.
                $parameter_fullname = $p->fullName . '_' . $p->name;
                $par = array(
                    'name' => $p->name,
                    'full_name' => $parameter_fullname,
                    'description' => $p->description,
                    'default_value' => $p->defaultValue,
                    'recommended_range' => $p->recommendedRange,
                    'data_type' => $p->dataType
                );
                $new_data['parameters'][] = $par;
            }
        }

        $nr_of_downvotes = 0;
        $nr_of_issues = $this->CI->Downvote->getDownvotesByKnowledgePiece('f',$d->id,1);
        if($nr_of_issues){
            $new_data['nr_of_issues'] = count($nr_of_issues);
            $nr_of_downvotes+=count($nr_of_issues);
            $downvote_agrees = $this->CI->Downvote->getDownvotesByKnowledgePiece('f',$d->id,0);
            if($downvote_agrees){
                $nr_of_downvotes+=count($downvote_agrees);
            }
        }else{
            $new_data['nr_of_issues'] = 0;
        }
        $new_data['nr_of_downvotes'] = $nr_of_downvotes;

        $ld_flow = $this->CI->KnowledgePiece->getNumberOfLikesAndDownloadsOnUpload('f',$d->id);
        $reach = 0;
        $nr_of_likes = 0;
        $nr_of_downloads = 0;
        $total_downloads = 0;
        if($ld_flow){
            foreach($ld_flow as $ld){
                if($ld->ldt=='l'){
                    $reach += $this->CI->Gamification->getReachFromParts($ld->count,0);
                    $nr_of_likes+=$ld->count;
                }else if($ld->ldt=='d'){
                    $reach += $this->CI->Gamification->getReachFromParts(0,$ld->count);
                    $nr_of_downloads+=$ld->count;
                    $total_downloads+=$ld->sum;
                }
            }
        }
        $new_data['nr_of_likes'] = $nr_of_likes;
        $new_data['nr_of_downloads'] = $nr_of_downloads;
        $new_data['total_downloads'] = $total_downloads;
        $new_data['reach'] = $reach;

        $impact_struct = $this->CI->Gamification->getImpact('f',$d->id,"2013-1-1",date("Y-m-d"));

        $new_data['reuse'] = $impact_struct['reuse'];

        $new_data['impact_of_reuse'] = floor($impact_struct['recursive_impact']);

        $new_data['reach_of_reuse'] = floor($impact_struct['reuse_reach']);

        $new_data['impact'] = floor($impact_struct['impact']);
        return $new_data;
    }

    public function index_measure($id, $start_id = 0, $altmetrics=True, $verbosity=0) {

        $params['index'] = 'measure';
        $procs = $this->db->query('SELECT e.*, t.description FROM estimation_procedure e, estimation_procedure_type t WHERE e.type=t.name' . ($id ? ' and e.id=' . $id : ''));
        if ($procs)
            foreach ($procs as $d) {
                $params['body'][] = array(
                    'index' => array(
                        '_id' => $d->id
                    )
                );

                $params['body'][] = $this->build_procedure($d);
            }

        $funcs = $this->db->query('SELECT * FROM math_function WHERE functionType="EvaluationFunction"' . ($id ? ' and name="' . $id . '"' : ''));
        if ($funcs)
            foreach ($funcs as $d) {
                $nid = str_replace("_", "-", $d->name);
                $params['body'][] = array(
                    'index' => array(
                        '_id' => $nid
                    )
                );

                $params['body'][] = $this->build_function($d);
            }

        $dataqs = $this->db->query('SELECT * FROM quality WHERE type="DataQuality"' . ($id ? ' and name="' . $id . '"' : ''));
        if ($dataqs)
            foreach ($dataqs as $d) {
                $nid = str_replace("_", "-", $d->name);
                $params['body'][] = array(
                    'index' => array(
                        '_id' => $nid
                    )
                );

                $params['body'][] = $this->build_dataq($d);
            }

        $flowqs = $this->db->query('SELECT * FROM quality WHERE type="AlgorithmQuality"' . ($id ? ' and name="' . $id . '"' : ''));
        if ($flowqs)
            foreach ($flowqs as $d) {
                $nid = str_replace("_", "-", $d->name);
                $params['body'][] = array(
                    'index' => array(
                        '_id' => $nid
                    )
                );

                $params['body'][] = $this->build_flowq($d);
            }

        if ($id and ! array_key_exists('body', $params))
            return "No measure found with id " . $id;

        $responses = $this->client->bulk($params);
        return 'Successfully indexed ' . sizeof($responses['items']) . ' out of ' . (($procs ? sizeof($procs) : 0) + ($funcs ? sizeof($funcs) : 0) + ($dataqs ? sizeof($dataqs) : 0) + ($flowqs ? sizeof($flowqs) : 0)) . ' measures (' . ($procs ? sizeof($procs) : 0) . ' procedures, ' . ($funcs ? sizeof($funcs) : 0) . ' functions, ' . ($dataqs ? sizeof($dataqs) : 0) . ' data qualities, ' . ($flowqs ? sizeof($flowqs) : 0) . ' flow qualities).';
    }

    private function build_procedure($d) {
        return array(
            'proc_id' => $d->id,
            'measure_type' => 'estimation_procedure',
            'task_type' => $d->ttid,
            'name' => $d->name,
            'description' => $d->description,
            'folds' => $d->folds,
            'repeats' => $d->repeats,
            'percentage' => $d->percentage,
            'stratified_sampling' => $d->stratified_sampling,
            'visibility' => 'public',
            'date' => $d->date,
            'suggest' => array(
                'input' => array($d->name, $d->description . ' '),
                'weight' => 2
            )
        );
    }

    private function build_function($d) {
        $id = str_replace("_", "-", $d->name);
        $desc = $d->description;
        $desc = str_replace("<math>","$$",$desc);
        $desc = str_replace("</math>","$$",$desc);

        return array(
            'eval_id' => $id,
            'measure_type' => 'evaluation_measure',
            'name' => $d->name,
            'description' => $desc,
            'code' => $d->source_code,
            'min' => $d->min,
            'max' => $d->max,
            'unit' => $d->unit,
            'higherIsBetter' => $d->higherIsBetter,
            'visibility' => 'public',
            'date' => $d->date,
            'suggest' => array(
                'input' => array($d->name, $d->description . ' '),
                'weight' => 2
            )
        );
    }

    private function build_dataq($d) {
        $id = str_replace("_", "-", $d->name);
        return array(
            'quality_id' => $id,
            'measure_type' => 'data_quality',
            'name' => $d->name,
            'description' => $d->description,
            'visibility' => 'public',
            'date' => $d->date,
            'suggest' => array(
                'input' => array($d->name, $d->description . ' '),
                'weight' => 2
            )
        );
    }

    private function build_flowq($d) {
        $id = str_replace("_", "-", $d->name);
        return array(
            'quality_id' => $id,
            'measure_type' => 'flow_quality',
            'name' => $d->name,
            'description' => $d->description,
            'visibility' => 'public',
            'date' => $d->date,
            'suggest' => array(
                'input' => array($d->name, $d->description . ' '),
                'weight' => 2
            )
        );
    }

    public function index_single_dataset($id) {

          $params['index'] = 'data';
        $status_sql_variable = 'IFNULL(`s`.`status`, \'' . $this->CI->config->item('default_dataset_status') . '\')';
        $datasets = $this->db->query('select d.*, ' . $status_sql_variable . ' AS `status`, count(rid) as runs, GROUP_CONCAT(dp.error) as error_message from dataset d left join (SELECT `did`, MAX(`status`) AS `status` FROM `dataset_status` GROUP BY `did`) s ON s.did = d.did left join task_inputs t on (t.value=d.did and t.input="source_data") left join run r on (r.task_id=t.task_id) left join data_processed dp on (d.did=dp.did)' . ($id ? ' where d.did=' . $id : '') . ' group by d.did');

        if ($id and ! $datasets)
            return 'Error: data set ' . $id . ' is unknown';

        if ($datasets)
          foreach ($datasets as $d) {
              $params['body'][] = array(
                  'index' => array(
                      '_id' => $d->did
                  )
              );

              $params['body'][] = $this->build_data($d);
          }

        $responses = $this->client->bulk($params);

        if($responses['errors'] == True){
          $err = $responses['items'][0]['index']['error'];
          return 'ERROR: Type:' . $err['type'] . ' Reason: ' . $err['reason'] . (array_key_exists('caused_by', $err) ? ' Caused by: ' . $err['caused_by']['reason'] : '');
        }

        return 'Successfully indexed dataset '.$id;
    }

    public function index_data($id, $start_id = 0, $altmetrics=True, $verbosity=0) {
        if ($id)
            return $this->index_single_dataset($id);

        $params['index'] = 'data';
        $datamaxquery = $this->db->query('SELECT max(did) as maxdata from dataset');
        $datacountquery = $this->db->query('SELECT count(did) as datacount from dataset');
        $datamax = intval($datamaxquery[0]->maxdata);
        $datacount = intval($datacountquery[0]->datacount);

        $did = $start_id;
        $submitted = 0;
        $incr = 10;
        if ($verbosity) {
          echo "Processing dataset ";
        }
        while ($did < $datamax) {
            if ($verbosity) {
              echo $did." ";
            }
            set_time_limit(600);
            $datasets = null;
            $params['body'] = array();
            $valid_ids = array();
            $status_sql_variable = 'IFNULL(`s`.`status`, \'' . $this->CI->config->item('default_dataset_status') . '\')';
            $datasets = $this->db->query('select d.*, ' . $status_sql_variable . 'AS `status`, count(rid) as runs, GROUP_CONCAT(dp.error) as error_message from dataset d left join (SELECT `did`, MAX(`status`) AS `status` FROM `dataset_status` GROUP BY `did`) s ON d.did = s.did left join task_inputs t on (t.value=d.did and t.input="source_data") left join run r on (r.task_id=t.task_id) left join data_processed dp on (d.did=dp.did) where d.did>=' . $did . ' and d.did<' . ($did + $incr) . ' group by d.did');
            if($datasets){
              foreach ($datasets as $d) {
                try {
                  $params['body'][] = array(
                      'index' => array(
                          '_id' => $d->did
                      )
                  );
                  $valid_ids[] = $d->did;
                  $params['body'][] = $this->build_data($d, $altmetrics);

                } catch (Exception $e) {
                    return $e->getMessage();
                }
              }

              $responses = $this->client->bulk($params);
              $submitted += sizeof($responses['items']);

               //clean up, just to be sure
              $params_del['index'] = 'data';
               foreach(array_diff(range($did,$did+$incr-1),$valid_ids) as $delid){
                   $params_del['id'] = $delid;
                   try{
                     $this->client->delete($params_del);
                   }
                   catch (Exception $e) {
                     if ($e->getCode() != 404){
                         echo($e);
                         die("unexpected exception");
                     }
                   }
               }
            }

            $did += $incr;
        }

        return 'Successfully indexed ' . $submitted . ' out of ' . $datacount . ' datasets.';
    }

    private function build_data($d, $altmetrics=True) {
        $description_record = $this->CI->Dataset_description->getWhereSingle('did =' . $d->did, 'version DESC');
        if(!$description_record){
           return 'Could not find description of dataset ' . $d->did;
        }
        $headless_description = trim(preg_replace('/\s+/', ' ', preg_replace('/^\*{2,}.*/m', '', $description_record->description)));
        $new_data = array(
            'data_id' => $d->did,
            'name' => $d->name,
            'exact_name' => $d->name,
            'version' => (float) $d->version,
            'version_label' => $d->version_label,
            'description' => $description_record->description,
            'format' => $d->format,
            'uploader' => array_key_exists($d->uploader, $this->user_names) ? $this->user_names[$d->uploader]: 'unknown',
            'uploader_id' => intval($d->uploader),
            'visibility' => $d->visibility,
            'creator' => $d->creator,
            'contributor' => $d->contributor,
            'date' => $d->upload_date,
            'update_comment' => $d->update_comment,
            'last_update' => $d->last_update,
            'licence' => $d->licence,
            'visibility' => $d->visibility,
            'status' => $d->status,
            'error_message' => $d->error_message,
            'url' => $d->url,
            'default_target_attribute' => $d->default_target_attribute,
            'row_id_attribute' => $d->row_id_attribute,
            'ignore_attribute' => $d->ignore_attribute,
            'runs' => $this->checkNumeric($d->runs),
            'suggest' => array(
                'input' => array($d->name, substr($headless_description, 0, 500) . ' '),
                'weight' => 5
            )
        );

        $new_data['qualities'] = array();
        $qualities = $this->CI->Data_quality->getQualitiesOrderedByPriority($d->did);
        if ($qualities != false)
          foreach($qualities as $q){
            $new_data['qualities'][$q->name] = $this->checkNumeric($q->value);
          }

        $new_data['tags'] = array();
        $studies = array();
        $tags = $this->CI->Dataset_tag->getAssociativeArray('tag', 'uploader', 'id = ' . $d->did);
        if ($tags != false) {
            foreach ($tags as $t => $u) {
                $new_data['tags'][] = array(
                    'tag' => $t,
                    'uploader' => $u);
                if(substr( $t, 0, 6 ) === "study_")
                  $studies[] = substr($t, strpos($t, "_") + 1);
            }
        }

        $topics = $this->CI->Dataset_topic->getAssociativeArray('topic', 'uploader', 'id = ' . $d->did);
        if ($topics != false) {
            foreach ($topics as $t => $u) {
                $new_data['topics'][] = array(
                    'topic' => $t,
                    'uploader' => $u);                
            }
        }
        // replace with study list in new indexer
        $new_studies = array();
        $task_studies = $this->db->query("select study_id from task_study where task_id in (select task_id from task_inputs where input='source_data' and value=" . $d->did . ")");
        if ($task_studies != false) {
            foreach ($task_studies as $t) { if (!in_array($t->study_id, $studies)){ $new_studies[] = $t->study_id; }}
        }
        $run_studies = $this->db->query("select distinct study_id from run_study where run_id in (select rid from run where task_id in (select task_id from task_inputs where input='source_data' and value=" . $d->did . "))");
        if ($run_studies != false) {
            foreach ($run_studies as $t) { if (!in_array($t->study_id, $studies)){ $new_studies[] = $t->study_id; }}
        }
        if ($new_studies) {
            foreach ($new_studies as $t) { $new_data['tags'][] = array('tag' => 'study_' . $t, 'uploader' => '0'); }
        }

        $new_data['features'] = array();
        $features = $this->db->query("SELECT name, `index`, data_type, is_target, is_row_identifier, is_ignore, NumberOfDistinctValues, NumberOfMissingValues, MinimumValue, MaximumValue, MeanValue, StandardDeviation, ClassDistribution FROM `data_feature` WHERE did=" . $d->did . " order by is_target limit 1000");
        if ($features != false) {
            foreach ($features as $f) {
                $feat = array(
                    'name' => $f->name,
                    'index' => $f->index,
                    'type' => $f->data_type,
                    'distinct' => $f->NumberOfDistinctValues,
                    'missing' => $f->NumberOfMissingValues
                );
                if ($f->is_target == "true")
                    $feat['target'] = "1";
                if ($f->is_row_identifier == "true")
                    $feat['identifier'] = "1";
                if ($f->is_ignore == "true")
                    $feat['ignore'] = "1";
                if ($f->data_type == "numeric") {
                    $feat['min'] = $f->MinimumValue;
                    $feat['max'] = $f->MaximumValue;
                    $feat['mean'] = $f->MeanValue;
                    $feat['stdev'] = $f->StandardDeviation;
                } elseif ($f->data_type == "nominal") {
                    $distr = json_decode($f->ClassDistribution);
                    if(is_array($distr))
                       $feat['distr'] = $this->array_map_recursive('strval',$distr);
                    else
                       $feat['distr'] = [];
                }
                $new_data['features'][] = $feat;
            }
        }
      $new_data['nr_of_issues'] = 0;
      $new_data['nr_of_downvotes'] = 0;
      $new_data['nr_of_likes'] = 0;
      $new_data['nr_of_downloads'] = 0;
      $new_data['total_downloads'] = 0;
      $new_data['reach'] = 0;
      $new_data['reuse'] = 0;
      $new_data['impact_of_reuse'] = 0;
      $new_data['reach_of_reuse'] = 0;
      $new_data['impact'] = 0;

      if($altmetrics){

        $nr_of_downvotes = 0;
        $nr_of_issues = $this->CI->Downvote->getDownvotesByKnowledgePiece('d',$d->did,1);
        if($nr_of_issues){
            $new_data['nr_of_issues'] = count($nr_of_issues);
            $nr_of_downvotes+=count($nr_of_issues);
            $downvote_agrees = $this->CI->Downvote->getDownvotesByKnowledgePiece('d',$d->did,0);
            if($downvote_agrees){
                $nr_of_downvotes+=count($downvote_agrees);
            }
        }else{
            $new_data['nr_of_issues'] = 0;
        }
        $new_data['nr_of_downvotes'] = $nr_of_downvotes;


        $ld_data = $this->CI->KnowledgePiece->getNumberOfLikesAndDownloadsOnUpload('d',$d->did);
        $reach = 0;
        $nr_of_likes = 0;
        $nr_of_downloads = 0;
        $total_downloads = 0;
        if($ld_data){
            foreach($ld_data as $ld){
                if($ld->ldt=='l'){
                    $reach += $this->CI->Gamification->getReachFromParts($ld->count,0);
                    $nr_of_likes+=$ld->count;
                }else if($ld->ldt=='d'){
                    $reach += $this->CI->Gamification->getReachFromParts(0,$ld->count);
                    $nr_of_downloads+=$ld->count;
                    $total_downloads+=$ld->sum;
                }
            }
        }
        $new_data['nr_of_likes'] = $nr_of_likes;
        $new_data['nr_of_downloads'] = $nr_of_downloads;
        $new_data['total_downloads'] = $total_downloads;
        $new_data['reach'] = $reach;

        $impact_struct = $this->CI->Gamification->getImpact('d',$d->did,"2013-1-1",date("Y-m-d"));

        $new_data['reuse'] = $impact_struct['reuse'];

        $new_data['impact_of_reuse'] = floor($impact_struct['recursive_impact']);

        $new_data['reach_of_reuse'] = floor($impact_struct['reuse_reach']);

        $new_data['impact'] = floor($impact_struct['impact']);
       }

        return $new_data;
    }

    public function checkNumeric($v) {
        if (is_integer($v))
            return intval($v);
        else if (is_numeric($v))
            return doubleval($v);
        else
            return $v;
    }

    public function array_map_recursive($callback, $array) {
       $func = function ($item) use (&$func, &$callback) {
         return is_array($item) ? array_map($func, $item) : call_user_func($callback, $item);
       };

       return array_map($func, $array);
    }

}
?>
