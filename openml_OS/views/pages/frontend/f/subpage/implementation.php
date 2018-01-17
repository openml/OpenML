
<ul class="hotlinks">

<?php if ($this->ion_auth->logged_in()) {
    if ($this->ion_auth->user()->row()->id != $this->flow['uploader_id']) {?>
        <li>
                <?php
                if ($this->activeuserlike) {
                    echo '<a id="likebutton" class="loginfirst btn btn-link" onclick="doLike(true)" title="Click to unlike"><i id="likeicon" class="fa fa-heart fa-2x"></i></a>';
                } else {
                    echo '<a id="likebutton" class="loginfirst btn btn-link" onclick="doLike(false)" title="Click to like"> <i id="likeicon" class="fa fa-heart-o fa-2x"></i></a>';
                }
                ?>
        </li>
        <?php }} ?>
        <?php if(isset($this->flow_source_url)) { ?>
        <li><a class="btn btn-link" onclick="doDownload()" href="<?php echo $this->flow_source_url; ?>"><i class="fa fa-cloud-download fa-2x"></i></a></li>
        <?php } elseif(isset($this->flow_binary_url)) { ?>
        <li><a class="btn btn-link" onclick="doDownload()" href="<?php echo $this->flow_binary_url; ?>"><i class="fa fa-cloud-download fa-2x"></i></a></li>
        <?php } ?>
        <li><a class="btn btn-link" onclick="doDownload()" href="api/v1/json/flow/<?php echo $this->id; ?>">
                          <span class="fa-stack fa-stack-icon fa-2x">
                              <i class="fa fa-file-o fa-stack-1x"></i>
                              <strong class="fa-stack-1x file-text">JSON</strong>
                          </span>
                        </a></li>
        <li><a class="btn btn-link" onclick="doDownload()" href="api/v1/flow/<?php echo $this->id; ?>">
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

<li>   <div class="version" style="margin-bottom: -17px;">
  <select class="selectpicker" data-width="auto" onchange="location = this.options[this.selectedIndex].value;">
    <?php foreach( $this->versions as $k => $v ) { ?>
    <option value="<?php echo 'f/'.$k;?>" <?php echo $v == $this->flow['version'] ? 'selected' : '';?>>v. <?php echo $v; ?></option>
    <?php } ?>
  </select>
      </div></li>
</ul>

<h1 class="pull-left"><a href="f"><i class="fa fa-cogs"></i></a> <?php echo $this->displayName; ?></h1>

<div class="datainfo">
   <?php if($this->flow['licence']): ?><i class="fa fa-cc"></i> <?php $l = $this->licences[$this->flow['licence']]; echo '<a href="'.$l['url'].'">'.$l['name'].'</a>'; endif; ?>
   <i class="fa fa-eye-slash"></i> Visibility: <?php echo strtolower($this->flow['visibility']); ?>
   <i class="fa fa-cloud-upload"></i> Uploaded <?php echo dateNeat( $this->flow['date']); ?> by <a href="u/<?php echo $this->flow['uploader_id']; ?>"><?php echo $this->flow['uploader'];?></a>
   <?php if($this->flow['dependencies']): ?><i class="fa fa-sitemap"></i> <?php echo $this->flow['dependencies']; endif; ?>
   <i class="fa fa-star"></i><?php echo $this->flow['runs']; ?> runs
   <br>
       <i class="fa fa-heart"></i> <span id="likecount"><?php if(array_key_exists('nr_of_likes',$this->flow)): if($this->flow['nr_of_likes']!=null): $nr_l = $this->flow['nr_of_likes']; else: $nr_l=0; endif; else: $nr_l=0; endif; echo $nr_l.' likes'; ?></span>
       <i class="fa fa-cloud-download"></i><span id="downloadcount"><?php if(array_key_exists('nr_of_downloads',$this->flow)): if($this->flow['nr_of_downloads']!=null): $nr_d = $this->flow['nr_of_downloads']; else: $nr_d = 0; endif; else: $nr_d = 0; endif; echo 'downloaded by '.$nr_d.' people'; ?>
       <i class="fa fa-warning task" data-toggle="collapse" data-target="#issues" title="Click to show/hide" style="cursor: pointer; cursor: hand;"></i><span id="nr_of_issues" data-toggle="collapse" data-target="#issues" title="Click to show/hide" style="cursor: pointer; cursor: hand;"><?php if(array_key_exists('nr_of_issues',$this->flow)): if($this->flow['nr_of_issues']!=null): $i = $this->flow['nr_of_issues']; else: $i=0; endif; else: $i=0; endif; echo $i.' issues'; ?></span>
       <i class="fa fa-thumbs-down"></i><span id="downvotes"><?php if(array_key_exists('nr_of_downvotes',$this->flow)): if($this->flow['nr_of_downvotes']!=null): $d = $this->flow['nr_of_downvotes']; else: $d=0; endif; else: $d=0; endif; echo $d.' downvotes'; ?></span>
       <?php if(array_key_exists('total_downloads',$this->flow)): if($this->flow['total_downloads']!=null): $nr_d = $this->flow['total_downloads']; endif; endif; echo ', '.$nr_d.' total downloads'; ?></span>
       <?php
       if ($this->ion_auth->logged_in()) {
           if ($this->ion_auth->user()->row()->gamification_visibility == 'show') {?>
                <span title="Reach is: 2x likes received + downloads received on this flow."><i class="fa fa-rss reach"></i><span id="reach"><?php if(array_key_exists('reach',$this->flow)): if($this->flow['reach']!=null): $r = $this->flow['reach']; else: $r=0; endif; else: $r=0; endif; echo $r.' reach'; ?></span></span>
                <span id="impact" title="Impact is: number or reuses of this flow in runs + 0.5*reach of these runs"><i class="fa fa-bolt impact"></i><?php if(array_key_exists('impact',$this->flow)): if($this->flow['impact']!=null): $i = $this->flow['impact']; else: $i=0; endif; else: $i=0; endif; echo $i.' impact'; ?></span>
            <?php }?>
       <?php }?>
       <form method="post" action="" enctype="multipart/form-data">
         <input type="hidden" name="deletetag" id="deletetag"/>
         <ul class="tags" id="taglist">
           <li class="tags">
             <i class="fa fa-fw fa-tags"></i>
             <?php if(array_key_exists('tags', $this->flow)){
                   foreach( $this->flow['tags'] as $t) { ?>
                 <span class="label label-material-<?php echo $this->materialcolor; ?> tag"><?php echo $t['tag']; if($t['uploader']==$this->user_id){ ?> <button class="deltag" type="submit" onclick="$('#deletetag').val('<?php echo $t['tag'];?>');" name="<?php echo $t['tag'];?>"><i class="fa fa-times"></i></button><?php } ?></span>
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
              if ($this->ion_auth->user()->row()->id != $this->flow['uploader_id']){
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
        <a class="pull-right greenheader loginfirst" href="f/<?php echo $this->id; ?>/edit"><i class="fa fa-edit fa-lg"></i> Edit</a>
        <?php if ($this->show_history) { ?>
        <a class="pull-right" href="d/<?php echo $this->id; ?>/history"><i class="fa fa-clock-o fa-lg"></i> History</a>
        <?php } ?>
      </div>
    </div>
  </div>
  <div class="card-content">
   <div class="description <?php if($this->hidedescription) echo 'hideContent';?>">
    <?php
      $this->wikiwrapper = preg_replace('#<script(.*?)>(.*?)</script>#is', '', $this->wikiwrapper);
      echo $this->wikiwrapper;
    ?>
   </div>
  </div>
</div>

  <?php
	  if (is_array($this->flow['components']) and sizeof($this->flow['components'])>0){
     ?>
			<h2>Components</h2>
      <div class="panel tablepanel">
			<div class="table-responsive">
				<table class="table">
				<?php
					foreach( $this->flow['components'] as $c ){
						echo "<tr><td>" . $c['identifier'] . "</td><td><a href='f/" . $c['id'] . "'>" . $c['name'] . "</a></td><td>".$c['description']."</td></tr>";
					}
				?>
				</table>
			</div>
      </div> <!-- end col-md-6 -->
      <?php } /*endif components*/ ?>

			<h2>Parameters</h2>
      <div class="panel tablepanel">
			<div class="table-responsive">
				<table class="table">
				<?php
        if (is_array($this->flow['parameters']) and sizeof($this->flow['parameters'])>0){
				foreach( $this->flow['parameters'] as $r ) {
					echo "<tr><td>" . $r['name'] . "</td><td>" . $r['description'] . "</td><td>".(strlen($r['default_value'])>0 ? "default: " . $r['default_value'] : "").(strlen($r['recommended_range'])>0 ? "<br><div class='tip recommendedrange' data-toggle='tooltip' data-placement='left' title='Recommended range'><i class='fa fa-thumbs-o-up'></i> ". $r['recommended_range'] . "</div>" : "")."</td></tr>";}
        }
				?>
				</table>
			</div>
      </div> <!-- end col-md-6 -->

	  <div class="qualities col-xs-12">
		  <?php
      $qtable = "";
			if (sizeof($this->flow['qualities'])>0){
			echo '<h3>'.sizeof($this->flow['qualities']).' properties</h3>';

			foreach( $this->flow['qualities'] as $k => $v ) {
				$qtable .= "<tr><td><a href='a/flow-qualities/".cleanName($k)."'>" . $k . "</a></td><td>";
 					if(is_numeric($v)){ $qtable .= round($v,2); }
					else{ $qtable .= $v;}
				$qtable .= "</td></tr>";
			}} ?>
    		 </div><div class="col-xs-12">
		 <?php
			if(strlen($qtable)>1){
				echo "<a data-toggle='collapse' href='#algoquals'><i class='fa fa-caret-right'></i> Show more</a><div id='algoquals' class='collapse'><div class='table-responsive'><table class='table table-striped'>" . $qtable . "</table></div></div>";}
		 ?>
		 </div>

		<h3><div id="runcount">0</div> Runs</h3>
    <a class="btn btn-default pull-right" href="search?q=+run_flow.flow_id%3A<?php echo $this->id; ?>&type=run"><i class="fa fa-fw fa-list"></i> List all runs</a>
	    <?php
	      $taskparams['index'] = 'openml';
	      $taskparams['type']  = 'task_type';
	      $taskparams['body']['query']['match_all'] = (object)[];
	      $this->alltasks = $this->searchclient->search($taskparams)['hits']['hits'];
	    ?>
      <div style="float:right">
      Parameter:
          <select class="selectpicker" data-width="auto" onchange="selected_parameter = this.value; showData();">
            <option value="none " selected>none </option>
            <?php foreach($this->flow['parameters'] as $r): ?>
            <option value="<?php echo $r['full_name'];?>"><?php echo str_replace('_', ' ', $r['name'] );?></option>
            <?php endforeach; ?>
          </select>
      </div>
		<select class="selectpicker" id="typeselect" data-width="auto" onchange="current_task = this.value; showData();">
	    <?php foreach($this->alltasks as $h){?>
                <option value="<?php echo $h['_source']['name']; ?>" <?php echo ($h['_source']['name'] == $this->current_task) ? 'selected' : '';?>><?php echo $h['_source']['name']; ?></option>
	    <?php } ?>
		</select>
    <select class="selectpicker" data-width="auto" onchange="evaluation_measure = this.value; updateTableHeader(); showData();">
					<?php foreach($this->allmeasures as $m): ?>
					<option value="<?php echo $m;?>" <?php echo ($m == $this->current_measure) ? 'selected' : '';?>><?php echo str_replace('_', ' ', $m);?></option>
					<?php endforeach; ?>
		</select>

    <div class="col-xs-12 panel">
         <div id="code_result_visualize" class="panel-simple" style="width: 100%">Rendering chart <i class="fa fa-spinner fa-spin"></i></div>
    </div>

    <div class="col-xs-12 panel">
      <div class="table-responsive reflow-table">
         <div id="table-spinner">Rendering table <i class="fa fa-spinner fa-spin"></i></div>
         <table id="flowtable" class="display" width="100%" style='margin:0'></table>
      </div>
    </div>

		</div> <!-- end tab-runs -->

<h3>Discussions</h3>
<div class="panel disquspanel]">
    <div id="disqus_thread">Loading discussions...</div>
</div>
    <script type="text/javascript">
        var disqus_shortname = 'openml'; // forum name
	var disqus_category_id = '3353608'; // Data category
	var disqus_title = '<?php echo $this->displayName; ?>'; // Data name
	var disqus_url = '<?php echo BASE_URL.'f/'.$this->id; ?>'; // Data url

        /* * * DON'T EDIT BELOW THIS LINE * * */
        (function() {
            var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true;
            dsq.src = '//' + disqus_shortname + '.disqus.com/embed.js';
            (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);
        })();
    </script>
