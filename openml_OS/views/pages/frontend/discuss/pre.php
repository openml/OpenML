<?php
$this->counter = 0;
//$this->threadsPerCategory = $this->Category->threadsPerCategory();
//$this->categories 		  = $this->Category->get();
$this->threads 			  = array();
$this->category_code = array('3200853' => 'general', '3353609' => 'data', '3353608' => 'flow', '3353607' => 'task', '3353606' => 'run', '3760343' => 'task_type');
$this->icons = array( 'general' => 'fa fa-circle-thin fa-lg', 'flow' => 'fa fa-cogs fa-lg', 'data' => 'fa fa-database fa-lg', 'run' => 'fa fa-star fa-lg', 'user' => 'fa fa-user fa-lg', 'task' => 'fa fa-trophy fa-lg', 'task_type' => 'fa fa-flask fa-lg', 'measure' => 'fa fa-bar-chart-o fa-lg');
$this->colors = array( 'general' => '#9c27b0', 'flow' => '#428bca', 'data' => '#3d8b3d', 'run' => '#d9534f', 'user' => '#e91e63', 'task' => '#fb8c00', 'task_type' => '#ff5722', 'measure' => '#9c27b0');
$this->activetab = 'recent';

//foreach($this->categories as $c) {
//	$this->threads[$c->id] = $this->Thread->getByCategoryId( $c->id, 0, 5, 'post_date DESC' );
//}

/// general topics
if(false !== strpos($_SERVER['REQUEST_URI'],'/tid/')){
	$this->thread = $this->Thread->getById( gu('tid') );
	if($this->thread){
		$this->activetab = 'thread';
		$this->author = $this->Author->getById( $this->thread->author_id );
	}
	//$this->category = $this->Category->getById( $this->thread->category_id );
}
if(false !== strpos($_SERVER['REQUEST_URI'],'new')){
	$this->activetab = 'new';
}

/// creating new topics
$this->title = array(
	'name' => 'title',
	'id' => 'title',
	'type' => 'text',
	'placeholder' => 'title or subject'
);

$this->body = array(
	'name' => 'body',
	'id' => 'body',
	'type' => 'text',
	'rows' => '5',
  'style'       => 'margin-top:20px;padding-top: 5px',
	'placeholder' => 'Question, comment, feature request,...'
);

/// disqus API
/// Get Popular

	$endpoint = 'https://disqus.com/api/3.0/threads/listPopular.json?api_secret='.urlencode(DISQUS_API_SECRET).'&forum=openml&interval=90d&limit=100';
	$ch = curl_init();
	// Disable SSL verification
	curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
	// Will return the response, if false it print the response
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
	// Set the url
	curl_setopt($ch, CURLOPT_URL, $endpoint);
	// Execute
	$data = curl_exec($ch);

	$results = json_decode($data);

	$category_code = array('3200853' => 'general', '3353609' => 'data', '3353608' => 'flow', '3353607' => 'task', '3353606' => 'run', '3760343' => 'task_type');
	$threads = $results->response;
	foreach ($threads as $thread) {
		$type = $category_code[$thread->category];
		$id = end(explode('/', $thread->link));
		$message = "Could not retrieve message";
		if($type == 'general'){
			$t = $this->Thread->getById( $tid );
			if($t){
				$thread->message = nl2br( stripslashes( $t->body ) );
				$thread->title = nl2br( stripslashes( $t->title ) );
				$auth = $this->Author->getById( $t->author_id );
				$thread->authname = user_display_text( $auth );
				$thread->authimage = htmlentities( authorImage( $auth->image ) );
			}
		}
		else{
			//get data from ES
			$p = array();
			$p['index'] = 'openml';
			$p['type'] = $type;
			$p['id'] = $id;
			$p['fields'] = 'description';
			try{
				$thread->message = substr(preg_replace('/\s+/',' ',$this->searchclient->get($p)['fields']['description'][0]),0,150).' ...';
			} catch (Exception $e) {}
		}
	}
	$this->popular = $threads;

  /// Get posts
	$endpoint = 'https://disqus.com/api/3.0/forums/listPosts.json?api_secret='.urlencode(DISQUS_API_SECRET).'&forum=openml&limit=100&related=thread';
	$ch = curl_init();
	// Disable SSL verification
	curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
	// Will return the response, if false it print the response
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
	// Set the url
	curl_setopt($ch, CURLOPT_URL, $endpoint);
	// Execute
	$data = curl_exec($ch);
	$this->posts = json_decode($data)->response;
?>
