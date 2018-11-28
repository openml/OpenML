<?php
function formatTeaser($r){
	$teaser = '';
	if(array_key_exists('highlight',$r))
		$teaser = trim(preg_replace('/\s+/', ' ', preg_replace('/^\*{2,}.*?\n/', '', $r['highlight']['description'][0])));
	elseif(array_key_exists('description',$r['_source']))
		$teaser = truncate(trim(preg_replace('/\s+/',' ',preg_replace('/^\*{2,}.*/m', '', $r['_source']['description']))));
	if($teaser == '')
		$teaser = 'No data.';
	return strip_tags($teaser);
}

function truncate($string,$length=200,$append="&hellip;") {
  $string = trim($string);
	$string = str_replace('<br>','',$string);

  if(strlen($string) > $length) {
    $string = wordwrap($string, $length);
    $string = explode("\n",$string);
    $string = array_shift($string) . $append;
  }

  return $string;
}

?>
<?php if(!$this->dataonly) {?>
<div class="topselectors">
<?php if($this->filtertype and in_array($this->filtertype, array("data", "task", "study"))){ ?>
	<a type="button" class="btn btn-success loginfirst" style="float:right; margin-left:10px;" href="new/<?php echo $this->filtertype;?>">
		<i class="fa fa-plus"></i> Add new</a>
<?php } ?>
<?php if($this->filtertype and in_array($this->filtertype, array("data"))){ ?>
	<a type="button" class="btn btn-default" style="float:right; margin-left:10px;" href="
<?php
if($this->table) // toggle off
$att = addToGET(array( 'table' => false, 'listids' => false, 'size' => false));
else // toggle on
$att = addToGET(array( 'table' => '1', 'listids' => false, 'size' => $this->results['hits']['total']));
echo 'search?'.$att; ?>"><i class="fa <?php echo ($this->table ? 'fa-align-justify' : 'fa-table');?>"></i><?php echo ($this->table ? ' List' : ' Table');?></a>
<?php } ?>

<?php if($this->filtertype and in_array($this->filtertype, array("run", "task", "data", "flow"))){ ?>
	<a type="button" class="btn btn-default" style="float:right; margin-left:10px;" href="
<?php
if($this->listids) // toggle off
$att = addToGET(array( 'listids' => false, 'table' => false, 'size' => false));
else // toggle on
$att = addToGET(array( 'listids' => '1', 'table' => false, 'size' => $this->results['hits']['total']));
echo 'search?'.$att; ?>"><i class="fa <?php echo ($this->listids ? 'fa-align-justify' : 'fa-list-ol');?>"></i> ID's</a>
	<?php } ?>

<div class="dropdown pull-right">
 <?php if($this->filtertype and in_array($this->filtertype, array("task", "data", "flow", "task", "task_type", "run", "user", "measure", "study"))){ ?>
  <a data-toggle="dropdown" class="btn btn-default" href="#">Sort: <b><?php echo $this->curr_sort; ?></b> <i class="fa fa-caret-down"></i></a>
  <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
    <?php if($this->filtertype and in_array($this->filtertype, array("task", "data", "flow", "task_type"))){ ?>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'match', 'order' => 'desc')); ?>">Best match</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'runs', 'order' => 'desc')); ?>">Most runs</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'runs', 'order' => 'asc')); ?>">Fewest runs</a></li>
    <?php } ?>
    <?php if($this->filtertype and in_array($this->filtertype, array("data", "flow", "run", "task"))){ ?>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'nr_of_likes', 'order' => 'desc')); ?>">Most likes</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'nr_of_likes', 'order' => 'asc')); ?>">Fewest likes</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'nr_of_downloads', 'order' => 'desc')); ?>">Most downloads</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'nr_of_downloads', 'order' => 'asc')); ?>">Fewest downloads</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'reach', 'order' => 'desc')); ?>">Highest Reach</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'reach', 'order' => 'asc')); ?>">Lowest Reach</a></li>
    <?php } ?>
    <?php if($this->filtertype and in_array($this->filtertype, array("data", "flow", "task"))){ ?>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'impact', 'order' => 'desc')); ?>">Highest Impact</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'impact', 'order' => 'asc')); ?>">Lowest Impact</a></li>
    <?php } ?>
    <?php if($this->filtertype and in_array($this->filtertype, array("data", "flow", "run", "user"))){ ?>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'date', 'order' => 'desc')); ?>">Most recent</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'date', 'order' => 'asc')); ?>">Least recent</a></li>
    <?php } ?>
    <?php if($this->filtertype and in_array($this->filtertype, array("data"))){ ?>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'last_update', 'order' => 'desc')); ?>">Last update</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'qualities.NumberOfInstances', 'order' => 'desc')); ?>">Most instances</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'qualities.NumberOfInstances', 'order' => 'asc')); ?>">Fewest instances</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'qualities.NumberOfFeatures', 'order' => 'desc')); ?>">Most features</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'qualities.NumberOfFeatures', 'order' => 'asc')); ?>">Fewest features</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'qualities.NumberOfNumericFeatures', 'order' => 'desc')); ?>">Most numeric features</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'qualities.NumberOfNumericFeatures', 'order' => 'asc')); ?>">Fewest numeric features</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'qualities.NumberOfMissingValues', 'order' => 'desc')); ?>">Most missing values</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'qualities.NumberOfMissingValues', 'order' => 'asc')); ?>">Fewest missing values</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'qualities.NumberOfClasses', 'order' => 'desc')); ?>">Most classes</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'qualities.NumberOfClasses', 'order' => 'asc')); ?>">Fewest classes</a></li>
    <?php } ?>
    <?php if($this->filtertype and in_array($this->filtertype, array("user"))){ ?>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'nr_of_uploads', 'order' => 'desc')); ?>">Most uploads done</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'nr_of_uploads', 'order' => 'asc')); ?>">Fewest uploads done</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'nr_of_likes', 'order' => 'desc')); ?>">Most likes given</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'nr_of_likes', 'order' => 'asc')); ?>">Fewest likes given</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'nr_of_downloads', 'order' => 'desc')); ?>">Most downloads done</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'nr_of_downloads', 'order' => 'asc')); ?>">Fewest downloads done</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'activity', 'order' => 'desc')); ?>">Highest Activity</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'activity', 'order' => 'asc')); ?>">Lowest Activity</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'likes_received', 'order' => 'desc')); ?>">Most likes received</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'likes_received', 'order' => 'asc')); ?>">Fewest likes received</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'downloads_received', 'order' => 'desc')); ?>">Most downloads received</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'downloads_received', 'order' => 'asc')); ?>">Fewest downloads received</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'reach', 'order' => 'desc')); ?>">Highest Reach</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'reach', 'order' => 'asc')); ?>">Lowest Reach</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'impact', 'order' => 'desc')); ?>">Highest Impact</a></li>
        <li role="presentation"><a role="menuitem" tabindex="-1" href="<?php echo str_replace("index.php/","",$_SERVER['PHP_SELF']) . "?" . addToGET(array( 'sort' => 'impact', 'order' => 'asc')); ?>">Lowest Impact</a></li>
    <?php } ?>
  </ul>
 <?php } else { ?>
	 <a data-toggle="collapse" data-parent="#topaccordeon" data-target="#mainlist" class="btn btn-default">For search options, select a result type first</a>
 <?php } ?>
</div>

<div class="dropdown pull-right" role="tab" id="filterHead">
		<a data-toggle="collapse" class="btn btn-default" href="#filterBox" aria-expanded="true" aria-controls="filterBox"><i class="fa fa-align-justify fa-filter"></i> Filters</a>
</div>

<div class="searchstats"><?php echo $this->results['hits']['total'];?> results</div>
<?php if($this->filtertype and in_array($this->filtertype, array("data")) and (!array_key_exists('status',$this->filters) or $this->filters['status'] != 'all')){?>
						<p class="searchstatdetail">Only showing <a data-toggle="collapse" href="#filterBox" aria-expanded="true" aria-controls="filterBox"><?php echo (array_key_exists('status',$this->filters) ? $this->filters['status'] : 'active (verified)');?></a> datasets.</p>
<?php } ?>
</div>
<?php } ?>
<div id="filterBox" class="panel panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne">
	<div class="panel-body">
		<h5>Filter results by:</h5>
		<ul class="sidenav nav collapse in" id="filterlist">
			<?php if($this->filtertype) subpage($this->filtertype);
						else subpage("everything");?>
		</ul>
		<?php
			if($this->filtertype and $this->filtertype!='user' and $this->filtertype!='task_type') {?>
		<div class="row">
  	<div class="col-md-6">
			<button class="btn btn-default btn-material-<?php echo $this->materialcolor;?>" id="research">Search</button>
		</div>
		<div class="col-md-6">
			<ul class="sidenav nav">
			<li><a style="cursor:default;"><i class="fa fa-lg fa-fw fa-info-circle"></i>You can use 1..10, >10, <10</a>
					<a id="removefilters"><i class="fa fa-lg fa-fw fa-trash-o"></i>Remove all filters</a></li>
			</ul>
		</div>
		</div>
		<?php } ?>
	</div>
</div>
<?php
if( $this->results != false and $this->results['hits']['total'] > 0){ ?>
      <?php
      if($this->listids){?>
      <!-- copy id list -->
        <div class="panel-collapse <?php if(!array_key_exists("size",$_GET)) echo 'collapse';?>" id="collapseAllIDs">
          <div class="panel panel-body panel-default">
            <?php echo implode(', ', object_array_get_value( $this->results['hits']['hits'], '_id' ) ); ?>
          </div>
        </div>

    <?php } else if($this->table) {
	?>
	<div class="topmenu"></div>
	<table id="tableview" class="table table-striped table-bordered table-condensed dataTable no-footer responsive">
	<thead><tr>
    <?php foreach( $this->cols as $k => $v ) {
			echo '<th>'.$k.'</th>';
		}
	?>
	</tr></thead>
	<tbody></tbody>
	</table>
     <?php } else { ?>

	<div id="scrollingcontent">
	<div class="listitempage" id="itempage" data-url="<?php echo $this->ref_url . '?' . addToGET(array( 'type' => $this->filtertype, 'from' => $this->from, 'dataonly' => 0, 'q' => $this->terms)); ?>" data-next-url="<?php echo $this->ref_url . '?' . addToGET(array( 'type' => $this->filtertype, 'from' => $this->from+$this->size, 'dataonly' => 0, 'q' => $this->terms)); ?>" data-prev-url="<?php echo $this->ref_url . '?' . addToGET(array( 'type' => $this->filtertype, 'from' => max(0,$this->from-$this->size), 'dataonly' => 0, 'q' => $this->terms));?>">
		 <?php
      foreach( $this->results['hits']['hits'] as $r ) {
        $type = $r['_type'];
				$rs = $r['_source'];
	   ?>
	<div class="searchresult panel">
		<?php if ($this->ion_auth->logged_in() and (array_key_exists('uploader_id',$rs) and $this->user_id == $rs['uploader_id']) or $this->ion_auth->is_admin()){ ?>
			<div class="actionicon">
				<div class="delete_action" data-type="<?php echo $r['_type'];?>" data-id="<?php echo $r['_id'];?>" data-name="<?php echo (array_key_exists('name',$rs) ? $rs['name'].(array_key_exists('version',$rs) ? ' ('.$rs['version'].')' : ' ') : $r['_type'].' '.$r['_id']);?>"><i class="fa fa-2x fa-trash"></i></div>
			</div>
		<?php } ?>

		  <?php if ($this->curr_sort == 'last update'){
			echo '<div class="update_date">'.$rs['last_update'].'</div><a href="u/'.$rs['uploader_id'].'">'.$rs['uploader'].'</a> ';
			if($rs['update_comment'])
				echo 'updated '.$type.':<div class="update_comment">'.$rs['update_comment'].'</div><div class="search-result">';
			else
				echo 'uploaded '.$type.':<div class="search-result">';
			}
			?>


		   <?php if($type == 'run') { ?>
				<div class="itemheadfull">
				<i class="<?php echo $this->icons[$type];?>" style="color:<?php echo $this->colors[$type];?>"></i>
				<a href="r/<?php echo  $r['_id'] ?>"><?php echo $rs['uploader'] ?> <span><i class="fa fa-fw fa-clock-o"></i> <?php echo get_timeago(strtotime(str_replace('.000Z','',$rs['date'])));?></span><br>
					<span>ran flow</span> <?php echo shortenString($rs['run_flow']['name']); ?> <span>on task</span> <?php echo $rs['run_task']['tasktype']['name'];?> on data set <?php echo $rs['run_task']['source_data']['name']; ?></a>
				</div>
				<div class="runStats statLine">
				<?php
                                echo '<b><i class="fa fa-heart"></i>'.$rs['nr_of_likes'].' likes';
                                echo ' - <i class="fa fa-cloud-download"></i>'.$rs['nr_of_downloads'].' downloads';
                                echo ' - <i class="fa fa-rss"></i>'.$rs['reach'].' reach </b> - ';
				if(!array_key_exists('evaluations',$rs) or empty($rs['evaluations'])) {
					echo 'No evaluations yet (or not applicable).';

          if (array_key_exists('error_message',$rs) &&  $rs['error_message']) {
            echo '<span class="task"> Client side error: ' . $rs['error_message'] . '</span>';
          }
          if (array_key_exists('error',$rs) && $rs['error']) {
            echo '<span class="task">  Evaluation Engine Exception: ' . $rs['error'] . '</span>';
          }
				} else{
					$tn = "";
					$vals = array();
					foreach($rs['evaluations'] as $eval){
						if(array_key_exists('value', $eval))
							$vals[$eval['evaluation_measure']] = $eval['value']."";
					}
					if(array_key_exists('evaluation_measures',$rs['run_task'])){
						$tm = $rs['run_task']['evaluation_measures'];
						if(array_key_exists($tm,$vals))
							echo $tm.': '.$vals[$tm].', ';
					}
					foreach($vals as $k => $v){
						if(!isset($tm) or $k != $tm)
							echo $k.': '.$v.', ';
					}
				}
				?>
				</div>

		   <?php } elseif($type == 'user') { ?>
				<div class="itemheadhead">
					<?php
						$auth = $this->Author->getById($r['_id']);
						$authimg = "img/community/misc/anonymousMan.png";
						if ($auth)
							$authimg = htmlentities( authorImage( $auth->image ) );
					?>
					<i><img src="<?php echo $authimg; ?>" width="40" height="40" class="img-circle" /></i></div>
		   		<a href="u/<?php echo $r['_id']; ?>"><?php echo $rs['first_name'].' '.$rs['last_name']; ?></a>
					<div class="teaser"><?php echo $rs['bio']; ?> </div>
				  <div class="runStats statLine">
						<?php if($rs['company']) echo '<i class="fa fa-fw fa-institution"></i>'.$rs['company'];?>
						<?php if($rs['country']) echo '<i class="fa fa-fw fa-map-marker"></i>'.$rs['country'];?>
						<i class="fa fa-fw fa-clock-o"></i>Joined <?php echo substr($rs['date'],0,10); ?>
				</div>
                                <div class="runStats statLine">
                                    <b>
                                        <i class="fa fa-fw fa-cloud-upload"></i><?php echo $rs['nr_of_uploads'].' uploads'; ?>
                                        <?php if($rs['gamification_visibility']=='show'){ ?>
                                        <i class="fa fa-fw fa-heartbeat"></i><?php echo ''.$rs['activity'].' activity ';?>
                                        <i class="fa fa-fw fa-rss"></i><?php echo ''.$rs['reach'].' reach';?>
                                        <i class="fa fa-fw fa-bolt"></i><?php echo ''.$rs['impact'].' impact';?>
                                        <?php }?>
                                    </b>
                                </div>

		   <?php } elseif($type == 'data') { ?>
				<div class="itemhead">
				<i class="<?php echo $this->icons[$type];?>" style="color:<?php echo $this->colors[$type];?>"></i>
		   		<a href="d/<?php echo $r['_id']; ?>"><?php echo $rs['name'].' ('.$rs['version'].')'; ?></a></div>
				<div class="teaser"><?php echo formatTeaser($r); ?> </div>
				<div class="runStats statLine">
					<?php echo '<b><i class="fa fa-star"></i>'.$rs['runs'].' runs';
                                            echo '<i class="fa fa-heart"></i>'.$rs['nr_of_likes'].' likes';
                                            echo '<i class="fa fa-cloud-download"></i>'.$rs['nr_of_downloads'].' downloads';
                                            echo '<i class="fa fa-rss"></i>'.$rs['reach'].' reach';
                                            echo '<i class="fa fa-bolt"></i>'.$rs['impact'].' impact </b><br>';
					 	if(array_key_exists('qualities', $rs)){
								$q = $rs['qualities'];
					      if(array_key_exists('NumberOfInstances', $q))    echo ''.$q['NumberOfInstances'].' instances';
					      if(array_key_exists('NumberOfFeatures', $q))     echo ' - '.$q['NumberOfFeatures'].' features';
					      if(array_key_exists('NumberOfClasses', $q))      echo ' - '.$q['NumberOfClasses'].' classes';
					      if(array_key_exists('NumberOfMissingValues', $q))echo ' - '.$q['NumberOfMissingValues'].' missing values';
							}?>
				</div>
		   <?php } elseif($type == 'task_type') { ?>
				<div class="itemhead">
				<i class="<?php echo $this->icons[$type];?>" style="color:<?php echo $this->colors[$type];?>"></i>
		   		<a href="tt/<?php echo $r['_id']; ?>"><?php echo $rs['name']; ?></a></div>
				<div class="teaser"><?php echo formatTeaser($r); ?></div>
				<div class="runStats">
					<?php echo '<b>'.$rs['tasks'].' tasks</b>';?>
        </div>
				<?php } elseif($type == 'study') { ?>
 				<div class="itemhead">
 				<i class="<?php echo $this->icons[$type];?>" style="color:<?php echo $this->colors[$type];?>"></i>
 		   		<a href="<?php echo 's/'.$r['_id']; ?>"><?php echo $rs['name']; ?></a></div>
 				<div class="teaser">
 					<?php echo formatTeaser($r); ?>
 				</div>
 				<div class="runStats">
					<?php
								echo $rs['datasets_included'] . ' datasets, '. $rs['tasks_included'] . ' tasks, ' . $rs['flows_included'] . ' flows, ' . $rs['runs_included'] . ' runs';
					?>
 				</div>
		   <?php } elseif($type == 'measure') { ?>
				<div class="itemhead">
				<i class="<?php echo $this->icons[$type];?>" style="color:<?php echo $this->colors[$type];?>"></i>
		   		<a href="<?php echo $this->measures[$rs['measure_type']].'/'.$r['_id']; ?>"><?php echo $rs['name']; ?></a></div>
				<div class="teaser">
					<?php echo formatTeaser($r); ?>
				</div>
				<div class="runStats">
					<?php echo str_replace('_',' ',$rs['measure_type']); ?>
				</div>

		   <?php } elseif($type == 'task') { ?>
				<div class="itemheadfull">
				  <i class="<?php echo $this->icons[$type];?>" style="color:<?php echo $this->colors[$type];?>"></i>
		   		<a href="t/<?php echo $r['_id']; ?>"><?php echo $rs['tasktype']['name'].' on '.$rs['source_data']['name']; ?></a>
				</div>
				<div class="runStats statLine">
                                    <?php
                                        echo '<b><i class="fa fa-star"></i>' . (0+$rs['runs']) . ' runs';
                                        echo '<i class="fa fa-heart"></i>' . $rs['nr_of_likes'] . ' likes';
                                        echo '<i class="fa fa-download"></i>' . $rs['nr_of_downloads'] . ' downloads';
                                        echo '<i class="fa fa-rss"></i>' . $rs['reach'] . ' reach';
                                        echo '<i class="fa fa-bolt"></i>' . $rs['impact'] . ' impact </b><br>';
                                        $to_echo = '';
                                        foreach ($rs as $key => $value) {
                                            if ($key == 'task_id' or $key == 'suggest' or $key == 'source_data' or $key == 'visibility' or $key == 'data_splits' or $key == 'runs' or $key == 'tasktype' or $key == 'date' or $key == 'custom_testset' or $key == 'nr_of_downloads' or $key == 'total_downloads' or $key == 'reach' or $key == 'nr_of_likes' or $key == 'nr_of_issues' or $key == 'nr_of_downvotes' or $key == 'impact' or ! $value) {
                                                echo '';
                                            } elseif (is_array($value) and array_key_exists('name', $value)) {
                                                $to_echo.=$key . ' : ' . $value['name'] . ' - ';
                                            } elseif (!is_array($value)) {
                                                $to_echo.=$key . ' : ' . $value . ' - ';
                                            }
                                        }
                                        echo substr($to_echo,0,-3);
                                    ?>
				</div>

		   <?php } elseif($type == 'flow') { ?>
				<div class="itemhead">
				<i class="<?php echo $this->icons[$type];?>" style="color:<?php echo $this->colors[$type];?>"></i>
				<a href="f/<?php echo $r['_id']; ?>"><?php echo $rs['name'].' ('.$rs['version'].')'; ?></a></div>
				<div class="teaser"><?php echo formatTeaser($r); ?></div>
				<div class="runStats">
					<?php echo '<b><i class="fa fa-star"></i>'.$rs['runs'].' runs';
                                              echo '<i class="fa fa-heart"></i>'.$rs['nr_of_likes'].' likes';
                                              echo '<i class="fa fa-cloud-download"></i>'.$rs['nr_of_downloads'].' downloads';
                                                echo '<i class="fa fa-rss"></i>'.$rs['reach'].' reach';
                                                echo '<i class="fa fa-bolt"></i>'.$rs['impact'].' impact </b>';?>
        </div>
		   <?php } ?>

		   <?php if ($this->curr_sort == 'last update'){
				echo '</div>';
		   } ?>
			</div>
<?php }
?>

</div>
</div>
<p class="loadingmore" style="color:#666"></p>
<?php if(!$this->table and $this->results['hits']['total']/50>1){ ?>
<ul class="pagination" style="display:none;">
  <li><a href="<?php echo $_SERVER['PHP_SELF'] . "?" . addToGET(array( 'from' => 0)); ?>" style="color:#666">Jump back to page</a></li>
<?php for ($x=0; $x<min($this->from+(10*$this->size),$this->results['hits']['total']); $x+=$this->size) { ?>
  <li><a href="<?php echo $_SERVER['PHP_SELF'] . "?" . addToGET(array( 'from' => $x)); ?>"><?php echo floor($x/$this->size) +1; ?></a></li>
<?php } ?>
</ul>
<?php } else { ?>
	<ul class="pagination" style="margin-bottom:50px"></ul>
<?php }}
	} else {
		if( $this->terms != false ) {
			o('no-search-results');
		} else {
	    o('no-results');
	  }
	}?>
