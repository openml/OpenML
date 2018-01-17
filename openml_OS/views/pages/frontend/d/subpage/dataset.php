    <?php if($this->blocked){
		o('no-access');
	  } else {
    ?>
    <ul class="hotlinks">
        <?php if ($this->ion_auth->logged_in()) {
            if ($this->ion_auth->user()->row()->id != $this->data['uploader_id']) {?>
                <li>
                        <?php if($this->activeuserlike){
                            echo '<a id="likebutton" class="loginfirst btn btn-link" onclick="doLike(true)" title="Click to unlike"><i id="likeicon" class="fa fa-heart fa-2x"></i></a>';
                        } else{
                            echo '<a id="likebutton" class="loginfirst btn btn-link" onclick="doLike(false)" title="Click to like"> <i id="likeicon" class="fa fa-heart-o fa-2x"></i></a>';
                        } ?>
                </li>
                <?php }} ?>
                <li><a class="btn btn-link" onclick="doDownload()" href="<?php echo $this->data['url']; ?>">
                                  <span class="fa-stack fa-stack-icon fa-2x">
                                      <i class="fa fa-cloud-download fa-stack-1x"></i>
                                      <strong class="fa-stack-1x file-text" style="margin-top: 1.8em;">ARFF</strong>
                                  </span>
                                </a></li>
                <li><a class="btn btn-link" onclick="doDownload()" href="<?php echo str_replace('download','get_csv',$this->data['url']); ?>">
                                  <span class="fa-stack fa-stack-icon fa-2x">
                                      <i class="fa fa-cloud-download fa-stack-1x"></i>
                                      <strong class="fa-stack-1x file-text" style="margin-top: 1.8em;">CSV</strong>
                                  </span>
                                </a></li>
                <li><a class="btn btn-link" onclick="doDownload()" href="<?php echo $_SERVER['REQUEST_URI']; ?>/json">
                                  <span class="fa-stack fa-stack-icon fa-2x">
                                      <i class="fa fa-file-o fa-stack-1x"></i>
                                      <strong class="fa-stack-1x file-text">JSON</strong>
                                  </span>
                                </a></li>
                <li><a class="btn btn-link" onclick="doDownload()" href="api/v1/data/<?php echo $this->id; ?>">
                                  <span class="fa-stack fa-stack-icon fa-2x">
                                      <i class="fa fa-file-o fa-stack-1x"></i>
                                      <strong class="fa-stack-1x file-text">XML</strong>
                                  </span>
                                </a></li>
                <li><a class="btn btn-link" onclick="doDownload()" href="<?php echo $_SERVER['REQUEST_URI']; ?>/rdf">
                                  <span class="fa-stack fa-stack-icon fa-2x">
                                      <i class="fa fa-file-o fa-stack-1x"></i>
                                      <strong class="fa-stack-1x file-text">RDF</strong>
                                  </span>
                                </a></li>
         <li>
             <div class="version" style="margin-bottom: -17px;">
		  <select class="selectpicker" data-width="auto" onchange="location = this.options[this.selectedIndex].value;">
                         <?php foreach ($this->versions as $v) { ?>
                             <option value="<?php echo 'd/' . $v['data_id']; ?>" <?php echo $v['version'] == $this->data['version'] ? 'selected' : ''; ?>>v. <?php echo $v['version']; ?></option>
			  <?php } ?>
			</select>
                 </div>
         </li>
    </ul>

    <h1 class="pull-left"><a href="d"><i class="fa fa-database"></i></a>
	     <?php echo $this->data['name']; ?>
    </h1>

    <div class="datainfo">
       <span class="label label-<?php echo ($this->data['status'] == 'active' ? 'success' : ($this->data['status'] == 'in_preparation' ? 'warning' : 'danger'))?>"><?php echo $this->data['status'];?></span>
       <i class="fa fa-table"></i> <?php echo (strtolower($this->data['format']) == 'arff' ? '<a href="http://weka.wikispaces.com/ARFF+%28developer+version%29" target="_blank">ARFF</a>' : $this->data['format']); ?>
       <?php if($this->data['licence']):?><i class="fa fa-cc"></i>
         <?php if(!array_key_exists($this->data['licence'],$this->licences)): echo $this->data['licence'];
               else: $l = $this->licences[$this->data['licence']]; echo '<a href="'.$l['url'].'">'.$l['name'].'</a>';
             endif; endif;?>
       <i class="fa fa-eye-slash"></i> Visibility: <?php echo strtolower($this->data['visibility']); ?>
       <i class="fa fa-cloud-upload"></i> Uploaded <?php echo dateNeat( $this->data['date']); ?> by <a href="u/<?php echo $this->data['uploader_id'] ?>"><?php echo $this->data['uploader'] ?></a>
       <?php if($this->is_owner): echo '<i class="fa fa-pencil-square-o"></i> <a href="d/'.$this->id.'/update">Edit</a>'; endif;?>
       <?php if ($this->ion_auth->logged_in()) { ?>
       <i class="fa fa-refresh"></i>
         <form method="post" action="" enctype="multipart/form-data" style="display:inline;">
           <input type="hidden" name="index_type" id="index_type" value="data" />
           <input type="hidden" name="index_id" id="index_id" value="<?php echo $this->id; ?>" />
           <input type="submit" class="btn-link datalink" value="Refresh" />
         </form>
       <?php } ?>
       <br>
       <i class="fa fa-heart"></i> <span id="likecount"><?php if(array_key_exists('nr_of_likes',$this->data)): if($this->data['nr_of_likes']!=null): $nr_l = $this->data['nr_of_likes']; else: $nr_l=0; endif; else: $nr_l=0; endif; echo $nr_l.' likes'; ?></span>
        <i class="fa fa-cloud-download"></i><span id="downloadcount">
      <?php if(array_key_exists('nr_of_downloads',$this->data)): if($this->data['nr_of_downloads']!=null): $nr_d = $this->data['nr_of_downloads']; else: $nr_d = 0; endif; echo 'downloaded by '.$nr_d.' people'; endif; ?>
      <?php if(array_key_exists('total_downloads',$this->data)): if($this->data['total_downloads']!=null): $nr_td = $this->data['total_downloads']; else: $nr_td = 0; endif; echo ', '.$nr_td.' total downloads'; endif; ?></span>
        <i class="fa fa-warning task" data-toggle="collapse" data-target="#issues" title="Click to show/hide" style="cursor: pointer; cursor: hand;"></i><span id="nr_of_issues" data-toggle="collapse" data-target="#issues" title="Click to show/hide" style="cursor: pointer; cursor: hand;"><?php if(array_key_exists('nr_of_issues',$this->data)): if($this->data['nr_of_issues']!=null): $i = $this->data['nr_of_issues']; else: $i=0; endif; else: $i=0; endif; echo $i.' issues'; ?></span>
        <i class="fa fa-thumbs-down"></i><span id="downvotes"><?php if(array_key_exists('nr_of_downvotes',$this->data)): if($this->data['nr_of_downvotes']!=null): $d = $this->data['nr_of_downvotes']; else: $d=0; endif; else: $d=0; endif; echo $d.' downvotes'; ?></span>
       <?php
        if ($this->ion_auth->logged_in()) {
            if($this->ion_auth->user()->row()->gamification_visibility=='show'){?>
                <span title="Reach is: 2x likes received + downloads received on this data set."><i class="fa fa-rss reach"></i><span id="reach"><?php if(array_key_exists('reach',$this->data)): if($this->data['reach']!=null): $r = $this->data['reach']; else: $r=0; endif; else: $r=0; endif; echo $r.' reach'; ?></span></span>
                <span id="impact" title="Impact is: number or reuses of this dataset in tasks + 0.5*reach of these tasks + 0.5*impact of these tasks"><i class="fa fa-bolt impact"></i><?php if(array_key_exists('impact',$this->data)): if($this->data['impact']!=null): $i = $this->data['impact']; else: $i=0; endif; else: $i=0; endif; echo $i.' impact'; ?></span>
            <?php }?>
        <?php }?>
        <?php if(array_key_exists('error_message',$this->data) and $this->data['error_message']){?>
          <br><a style="color:#ff5722;" role="button" data-toggle="collapse" href="#dataerror" aria-expanded="false" aria-controls="dataerror">
                <i class="fa fa-warning task"></i>Errors occured while analyzing this dataset.</a><br>

          <div class="collapse" id="dataerror">
            <div class="panel text-warning">
              <?php if(startsWith('error_message','keyword')){
                echo 'Dataset does not seem to be valid ARFF: ';}
                echo $this->data['error_message']; ?>
            </div>
          </div>
        <?php } ?>

      <form method="post" action="" enctype="multipart/form-data">
        <input type="hidden" name="deletetag" id="deletetag"/>
        <ul class="tags" id="taglist">
          <li class="tags">
            <i class="fa fa-fw fa-tags"></i>
            <?php if(array_key_exists('tags', $this->data)){
                  foreach( $this->data['tags'] as $t) { ?>
                <span class="label label-material-<?php echo $this->materialcolor; ?> tag"><?php echo $t['tag']; if($t['uploader']==$this->user_id or $this->ion_auth->is_admin()){ ?> <button class="deltag" type="submit" onclick="$('#deletetag').val('<?php echo $t['tag'];?>');" name="<?php echo $t['tag'];?>"><i class="fa fa-times"></i></button><?php } ?></span>
            <?php }} ?>
            <a class="" role="button" data-toggle="collapse" href="#addtagbox" aria-expanded="false" aria-controls="addtagbox">
              <i class="fa fa-fw fa-plus"></i>Add tag</a>
          </li>
        </ul>
      <div class="collapse" id="addtagbox">
        <div class="panel">
            <input type="text" class="form-control floating-label loginfirst" id="newtags" name="newtags" data-hint="Add a single new tag. Use underscores for spaces. Press enter when done."
             placeholder="Add tag">
        </div>
      </div>
      </form>
    </div>

    <div class="col-xs-12 panel collapse" id="issues">
        <table class="table table-striped" id="issues_content">
            <tr>
                <th>Issue</th>
                <th>#Downvotes for this reason</th>
                <th>By</th>
                <th></th>
            </tr>
            <?php
                foreach($this->downvotes as $downvote){
                    $id = $downvote['_source']['reason_id'];
                    echo '<tr>'
                    . '<td>'.$downvote['_source']['reason'].'</td>'
                    . '<td>'.$downvote['_source']['count'].'</td>'
                    . '<td><a href="u/'.$downvote['_source']['user_id'].'">User '.$downvote['_source']['user_id'].'</a></td>'
                    //. '<td><a id="downvotebutton-'.$id.'" class="loginfirst btn btn-link" onclick="doDownvote('.$id.')" title="Click to agree"> <i id="downvoteicon-'.$id.'" class="fa fa-thumbs-o-down"/></a></td>'
                    . '<td><a id="downvotebutton-'.$id.'" class="loginfirst btn btn-link" onclick="doDownvote('.$id.')" title="Click to agree"></a></td>'
                    . '</tr>';
                }
            ?>
        </table>
        <br>
        <br>
        <?php if ($this->ion_auth->logged_in()) {
              if ($this->ion_auth->user()->row()->id != $this->data['uploader_id']){
              $nodownvote = true;
              foreach($this->downvotes as $downvote){
                  if($downvote['_source']['user_id'] == $this->ion_auth->user()->row()->id){
                      $nodownvote = false;
                  }
              }
              if($nodownvote){
            ?>
                <form role="form" id="issueform">
                    <h5>Submit a new issue for this dataset</h5>
                    <div class="form-group">
                      <label for="Reason">Issue:</label>
                      <input type="text" class="form-control" id="reason">
                    </div>
                    <button type="submit" class="btn btn-default">Submit</button>
                    <div id="succes" class="text-center hidden">Issue Submitted!</div>
                    <div id="fail" class="text-center hidden">Can't submit issue </div>
                </form>
            <?php }}} ?>
    </div>

  <div class="col-xs-12 panel" onclick="showmore()">
    <div class="cardactions">
      <div class="wiki-buttons">
        <div class="pull-right" id="wiki-waiting">
          <i class="fa fa-spinner fa-pulse"></i> Loading wiki
        </div>
        <div class="pull-right" id="wiki-ready">
          <?php if(!$this->editing){ ?>
            <span style="font-size:10px;font-style:italic;color:#666">Help us complete this description <i class="fa fa-long-arrow-right"></i></span>
          <?php } ?>
          <a class="pull-right greenheader loginfirst" href="d/<?php echo $this->id; ?>/edit"><i class="fa fa-edit fa-lg"></i> Edit</a>
          <?php if ($this->show_history) { ?>
          <a class="pull-right" href="d/<?php echo $this->id; ?>/history"><i class="fa fa-clock-o fa-lg"></i> History</a>
          <?php } ?>
        </div>
      </div>
    </div>
    <div class="card-content">
     <div class="description <?php if($this->hidedescription) echo 'hideContent';?>">
	    <?php
        echo $this->wikiwrapper;
      ?>
     </div>
    </div>
  </div>


  <h3><?php echo (array_key_exists('NumberOfFeatures', $this->data['qualities']) ? $this->data['qualities']['NumberOfFeatures'] : '0'); ?> features</h3>
<?php
  if (!empty($this->data['features'])){ ?>
      <div class="cardtable">
			<div class="features <?php echo ($this->showallfeatures ? '' : 'hideFeatures'); ?>">
			<div class="table-responsive">
				<table class="table">
				<?php
        foreach( $this->data['features'] as $r ) {
				//get target values
					echo "<tr class='cardrow'><td>" . $r['name'] . ( array_key_exists('target',$r) ? ' <b>(target)</b>': '').( array_key_exists('identifier',$r) ? ' <b>(row identifier)</b>': '').( array_key_exists('ignore',$r) ? ' <b>(ignore)</b>': ''). "</td><td>" . $r['type'] . "</td><td>" . $r['distinct'] . " unique values<br> " . $r['missing'] . " missing</td><td class='feat-distribution'><div id='feat".$r['index']."' style='height: 90px; margin: auto; min-width: 300px; max-width: 50%;'></div></td></tr>";
				}
					?>
				</table>
			</div>
			</div>
    </div>

	<div class="show-more-features">
	<?php if(!$this->showallfeatures){ ?>
		<a class="cardaction" onclick="showmorefeats()"><i class="fa fa-chevron-down"></i> Show <?php echo ($this->data['qualities']['NumberOfFeatures']<100 ? 'all '.$this->data['qualities']['NumberOfFeatures'] : 'first 100'); ?> features</a>
	<?php } ?>
	</div>
        <div class="show-all-features">
	<?php if(isset($this->highFeatureCount) and $this->highFeatureCount){ ?>
		<a href="d/<?php echo $this->id; ?>?show=all">Show all <?php echo $this->data['qualities']['NumberOfFeatures']; ?> features.</a><br>This may take a while to load.
	<?php } ?>
	</div>

	<!-- features unavailable -->
	<?php } else {
			    echo '<p>Data features are not analyzed yet. Refresh the page in a few minutes.</p>';
	      } ?>



<?php
  echo '<h3>'.sizeof($this->data['qualities']).' properties</h3>';

  if (!empty($this->data['qualities'])){
  $qtable = ""; ?>
    <div class="properties <?php if($this->hidedescription) echo 'hideProperties'; ?>">
    <?php if($this->data['qualities']){
      foreach($this->data['qualities'] as $k => $v ) {
        ?>
      <div class="searchresult panel">
      <div class="itemhead">
      <a href="a/data-qualities/<?php echo str_replace("_", "-", $k); ?>" class="iconpurple">
      <i class="fa fa-fw fa-bar-chart"></i> <?php echo $k; ?></a>
      </div>
      <div class="dataproperty"><?php
        if(is_numeric($v)){
          echo round($v,2);
        } elseif($v=='true'){
          echo "<i class='fa fa-check fa-lg'></i>";
        } elseif($v=='false'){
          echo "<i class='fa fa-times fa-lg'></i>";
        } else{
          echo $v;
        } ?>
      </div>
      <div class="datadescription"><?php print_r($this->dataproperties[$k]['description']);?></div>
      </div>
      <?php }} ?> </div>
      <?php } else {
        echo '<p>Data properties are not analyzed yet. Refresh the page in a few minutes.</p>';
        } ?>

    <?php if (!empty($this->data['qualities'])){ ?>
    <div class="show-more-props">
      <a class="cardaction" onclick="showmoreprops()"><i class="fa fa-chevron-down"></i> Show all <?php echo sizeof($this->data['qualities']);?> properties</a>
    </div>
    <?php } ?>

		<h3><?php echo count($this->tasks); ?> tasks</h3>
		<?php foreach( $this->tasks as $q){?>
      <div class="searchresult panel">
        <div class="itemheadfull" <?php echo ($q['runs'] ? '' : 'style="opacity: 0.5"'); ?>>
          <i class="fa fa-trophy fa-lg" style="color:#fb8c00;"></i>
          <a href="t/<?php echo $q['task_id']; ?>"><?php echo $q['tasktype']['name'].' on '.$q['source_data']['name']; ?></a>
        </div>
        <div class="runStats statLine" <?php echo ($q['runs'] ? '' : 'style="opacity: 0.5"'); ?>>
          <?php
            echo '<b>'.($q['runs'] ? $q['runs'] : '0').' runs</b>';
            if(array_key_exists('estimation_procedure',$q)) echo ' - estimation_procedure: '.$q['estimation_procedure']['name'];
            if(array_key_exists('evaluation_measures',$q)) echo ' - evaluation_measure: '.$q['evaluation_measures'];
            if(array_key_exists('target_feature',$q)) echo ' - target_feature: '.$q['target_feature'];
            if(array_key_exists('cost_matrix',$q)) echo ' - cost matrix: '.$q['cost_matrix'];
            ?>
        </div>
      </div>
    <?php } ?>
  <a class="loginfirst btn btn-default btn-raised" href="new/task?data=<?php echo htmlentities($this->data['name'].'('.$this->data['version'].')');?>">Define a new task</a>

  <?php if($this->data['visibility'] != 'private'){ ?>
  <h3>Discussions</h3>
  <div class="panel disquspanel">
    <div id="disqus_thread">Loading discussions...</div>
  </div>

  <script type="text/javascript">
  var disqus_shortname = 'openml'; // forum name
	var disqus_category_id = '3353609'; // Data category
	var disqus_title = '<?php echo $this->data['name']; ?>'; // Data name
	var disqus_url = '<?php echo BASE_URL;?>d/<?php echo $this->id; ?>'; // Data url

        /* * * DON'T EDIT BELOW THIS LINE * * */
        (function() {
            var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true;
            dsq.src = '//' + disqus_shortname + '.disqus.com/embed.js';
            (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);
        })();
  </script>
  <?php } ?>
  <?php } ?>
