<div class="container-fluid topborder endless openmlsectioninfo">
  <div class="col-xs-12" id="mainpanel">

    <div class="tab-content">

      <?php
    	if (!isset($this->task)){ ?>
                 <div class="tab-content">
                  <h3><i class="fa fa-warning"></i> This is not the task you are looking for</h3>
                  <p>Sorry, this task does not seem to exist (anymore).</p>
                </div>
          <?php
        } else { ?>

          <ul class="hotlinks">
                      <?php if ($this->ion_auth->logged_in()) {?>
                              <li>
                                  <?php
                                  if ($this->activeuserlike) {
                                      echo '<a id="likebutton" class="loginfirst btn btn-link" onclick="doLike(true)" title="Click to unlike"><i id="likeicon" class="fa fa-heart fa-2x"></i></a>';
                                  } else {
                                      echo '<a id="likebutton" class="loginfirst btn btn-link" onclick="doLike(false)" title="Click to like"> <i id="likeicon" class="fa fa-heart-o fa-2x"></i></a>';
                                  }}
                                  ?>
                              </li>
      				                <li><a class="btn btn-link" onclick="doDownload()" href="<?php echo $this->sourcedata_url; ?>"><i class="fa fa-cloud-download fa-2x"></i></a></li>
      				                <li><a class="btn btn-link" onclick="doDownload()" href="<?php echo $_SERVER['REQUEST_URI']; ?>/json">
      				                                  <span class="fa-stack fa-stack-icon fa-2x">
      				                                      <i class="fa fa-file-o fa-stack-1x"></i>
      				                                      <strong class="fa-stack-1x file-text">JSON</strong>
      				                                  </span>
      				                                </a></li>
      												<li><a class="btn btn-link" onclick="doDownload()" href="api/v1/task/<?php echo $this->id; ?>">
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
          </ul>
          <div id="subtitle"><?php echo $this->task['tasktype']['name']; ?> on <?php echo $this->task['source_data']['name']; ?></div>
          <h1><i class="fa fa-trophy"></i> <?php echo $this->task['tasktype']['name']; ?> on <?php echo $this->task['source_data']['name']; ?></h1>
          <div class="datainfo">
                      <i class="fa fa-trophy"></i> Task <?php echo $this->task_id; ?>
                      <i class="fa fa-flag"></i> <a href="tt/<?php echo $this->task['tasktype']['tt_id'];?>"><?php echo $this->task['tasktype']['name']; ?></a>
                      <i class="fa fa-database"></i> <a href="d/<?php echo $this->task['source_data']['data_id'];?>"><?php echo $this->task['source_data']['name']; ?></a>
                      <i class="fa fa-star"></i> <a href="#taskruns" data-toggle="tab"><?php echo $this->task['runs']; ?> runs submitted</a>
                      <br>
                      <i class="fa fa-heart"></i> <span id="likecount"><?php if(array_key_exists('nr_of_likes',$this->task)): if($this->task['nr_of_likes']!=null): $nr_l = $this->task['nr_of_likes']; else: $nr_l=0; endif; else: $nr_l=0; endif; echo $nr_l.' likes'; ?></span>
                      <i class="fa fa-cloud-download"></i><span id="downloadcount"><?php if(array_key_exists('nr_of_downloads',$this->task)): if($this->task['nr_of_downloads']!=null): $nr_d = $this->task['nr_of_downloads']; else: $nr_d = 0; endif; else: $nr_d = 0; endif; echo 'downloaded by '.$nr_d.' people'; ?>
      								<?php if(array_key_exists('total_downloads',$this->task)): if($this->task['total_downloads']!=null): $nr_d = $this->task['total_downloads']; endif; endif; echo ', '.$nr_d.' total downloads'; ?></span>
                      <i class="fa fa-warning task" data-toggle="collapse" data-target="#issues" title="Click to show/hide" style="cursor: pointer; cursor: hand;"></i><span id="nr_of_issues" data-toggle="collapse" data-target="#issues" title="Click to show/hide" style="cursor: pointer; cursor: hand;"><?php if(array_key_exists('nr_of_issues',$this->task)): if($this->task['nr_of_issues']!=null): $i = $this->task['nr_of_issues']; else: $i=0; endif; else: $i=0; endif; echo $i.' issues'; ?></span>
                      <!--<i class="fa fa-thumbs-down"></i><span id="downvotes"><?php if(array_key_exists('nr_of_downvotes',$this->task)): if($this->task['nr_of_downvotes']!=null): $d = $this->task['nr_of_downvotes']; else: $d=0; endif; else: $d=0; endif; echo $d.' downvotes'; ?></span>
      								-->
                      <?php if ($this->ion_auth->logged_in()) {
                              if ($this->ion_auth->user()->row()->gamification_visibility == 'show') {?>
                                  <span title="Reach is: 2x likes received + downloads received on this task."><i class="fa fa-rss reach"></i><span id="reach"><?php if(array_key_exists('reach',$this->task)): if($this->task['reach']!=null): $r = $this->task['reach']; else: $r=0; endif; else: $r=0; endif; echo $r.' reach'; ?></span></span>
                                  <span id="impact" title="Impact is: number or reuses of this task in runs + 0.5*reach of these runs"><i class="fa fa-bolt impact"></i><?php if(array_key_exists('impact',$this->task)): if($this->task['impact']!=null): $i = $this->task['impact']; else: $i=0; endif; else: $i=0; endif; echo $i.' impact'; ?></span>
                          <?php }?>
                     <?php }?>
      							 <form method="post" action="" enctype="multipart/form-data">
      								 <input type="hidden" name="deletetag" id="deletetag"/>
      								 <ul class="tags" id="taglist">
      									 <li class="tags">
      										 <i class="fa fa-fw fa-tags"></i>
      										 <?php if(array_key_exists('tags', $this->task)){
      													 foreach( $this->task['tags'] as $t) { ?>
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
                    //if ($this->ion_auth->user()->row()->id != $this->task['uploader_id']){
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
                  <?php //}

                    }} ?>
          </div>
          <div class="tabbed-submenu">
      		<ul class="nav nav-pills pull-right">
      			<li><a onclick="highlight()" id="detail-btn" class="btn btn-default btn-raised btn-info" data-toggle="tab" href="#detail"><i class="fa fa-fw fa-bar-chart"></i> Evaluations</a></li>
      			<li><a onclick="highlight()" id="people-btn" class="btn btn-default btn-raised" data-toggle="tab" href="#people"><i class="fa fa-fw fa-users"></i> People</a></li>
      			<li><a onclick="highlight()" id="runs-btn" class="btn btn-default btn-raised" data-toggle="tab" href="#taskruns"><i class="fa fa-fw fa-list"></i> Runs</a></li>
      			<li><a onclick="highlight()" id="add-btn" class="btn btn-default btn-raised" data-toggle="tab" href="#submit"><i class="fa fa-fw fa-plus"></i> Add results</a></li>
      		</ul>
          </div>

      		<div class="tab-pane active" id="detail">
      			<?php if( isset($this->task_id) ) { subpage('task_results'); } ?>
      		</div> <!-- end task tab -->
      		<div class="tab-pane" id="taskruns">
      			<?php if( isset($this->task_id) ) { subpage('task_runs'); } ?>
      		</div>
      		<div class="tab-pane" id="people">
      			<?php if( isset($this->task_id) ) { subpage('task_leaderboard'); } ?>
      		</div> <!-- end task tab -->
          <div class="tab-pane" id="submit">
      			<?php if( isset($this->task_id) ) { subpage('task'); } ?>
      		</div> <!-- end task tab -->

</div> <!-- end tabs content -->
</div> <!-- end container -->
<?php } ?>
