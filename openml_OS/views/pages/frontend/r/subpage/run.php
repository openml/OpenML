
  <ul class="hotlinks">
    <?php if ($this->ion_auth->logged_in()) {
        if ($this->ion_auth->user()->row()->id != $this->run['uploader_id']) {?>
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
            <li><a class="btn btn-link" onclick="doDownload()" href="api/v1/json/run/<?php echo $this->id; ?>">
                              <span class="fa-stack fa-stack-icon fa-2x">
                                  <i class="fa fa-file-o fa-stack-1x"></i>
                                  <strong class="fa-stack-1x file-text">JSON</strong>
                              </span>
                            </a></li>
            <li><a class="btn btn-link" onclick="doDownload()" href="api/v1/run/<?php echo $this->id; ?>">
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

  <h1>Run <?php echo $this->run_id; ?></h1>

  <div class="datainfo">
     <i class="fa fa-trophy"></i> <a href="t/<?php echo $this->run['run_task']['task_id']; ?>">Task <?php echo $this->run['run_task']['task_id']; ?> (<?php echo $this->run['run_task']['tasktype']['name']; ?>)</a> <i class="fa fa-database"></i> <a href="d/<?php echo $this->run['run_task']['source_data']['data_id']; ?>"><?php echo $this->run['run_task']['source_data']['name']; ?></a>
     <i class="fa fa-cloud-upload"></i> Uploaded <?php echo dateNeat($this->run['date']); ?> by <a href="u/<?php echo $this->run['uploader_id']; ?>"><?php echo $this->run['uploader']; ?></a>
    <br>
   <i class="fa fa-heart"></i> <span id="likecount"><?php if(array_key_exists('nr_of_likes',$this->run)): if($this->run['nr_of_likes']!=null): $nr_l = $this->run['nr_of_likes']; else: $nr_l=0; endif; else: $nr_l=0; endif; echo $nr_l.' likes'; ?></span>
   <i class="fa fa-cloud-download"></i><span id="downloadcount"><?php if(array_key_exists('nr_of_downloads',$this->run)): if($this->run['nr_of_downloads']!=null): $nr_d = $this->run['nr_of_downloads']; else: $nr_d = 0; endif; else: $nr_d = 0; endif; echo 'downloaded by '.$nr_d.' people'; ?>
   <i class="fa fa-warning task" data-toggle="collapse" data-target="#issues" title="Click to show/hide" style="cursor: pointer; cursor: hand;"></i><span id="nr_of_issues" data-toggle="collapse" data-target="#issues" title="Click to show/hide" style="cursor: pointer; cursor: hand;"><?php if(array_key_exists('nr_of_issues',$this->run)): if($this->run['nr_of_issues']!=null): $i = $this->run['nr_of_issues']; else: $i=0; endif; else: $i=0; endif; echo $i.' issues'; ?></span>
   <i class="fa fa-thumbs-down"></i><span id="downvotes"><?php if(array_key_exists('nr_of_downvotes',$this->run)): if($this->run['nr_of_downvotes']!=null): $d = $this->run['nr_of_downvotes']; else: $d=0; endif; else: $d=0; endif; echo $d.' downvotes'; ?>
   </span>
   <?php if ($this->ion_auth->logged_in()) {
            if ($this->ion_auth->user()->row()->gamification_visibility == 'show') {?>
                <span title="Reach is: 2x likes received + downloads received on this run."><i class="fa fa-rss reach"></i><span id="reach"><?php if(array_key_exists('reach',$this->run)): if($this->run['reach']!=null): $r = $this->run['reach']; else: $r=0; endif; else: $r=0; endif; echo $r.' reach'; ?></span></span>
        <?php }?>
   <?php }?>
   <?php if(array_key_exists('total_downloads',$this->run)): if($this->run['total_downloads']!=null): $nr_d = $this->run['total_downloads']; endif; endif; echo ', '.$nr_d.' total downloads'; ?></span>
   <?php
     if (array_key_exists('error_message',$this->run) &&  $this->run['error_message']) {
       echo '<br><span class="text-warning"><i class="fa fa-warning task"></i> Client side error: ' . $this->run['error_message'] . '</span>';
     }
     if (array_key_exists('error',$this->run) && $this->run['error']) {
       echo '<br><span class="text-warning"><i class="fa fa-warning task"></i> Evaluation Engine Exception: ' . $this->run['error'] . '</span>';
     }
   ?>
   <form method="post" action="" enctype="multipart/form-data">
     <input type="hidden" name="deletetag" id="deletetag"/>
     <ul class="tags" id="taglist">
       <li class="tags">
         <i class="fa fa-fw fa-tags"></i>
         <?php if(array_key_exists('tags', $this->run)){
               foreach( $this->run['tags'] as $t) { ?>
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
              if ($this->ion_auth->user()->row()->id != $this->run['uploader_id']){
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

  <h3 style="padding-top:0px;margin-top:-10px;">Flow</h3>
  <div class="cardtable">
    <div class='table-responsive'><table class='table'>
    <tr class="cardrow"><td><a href="f/<?php echo $this->run['run_flow']['flow_id']; ?>"><?php echo wordwrap($this->run['run_flow']['name'], 75, "\n", true); ?></a></td><td><?php echo $this->flow['description']; ?></td></tr>
    <?php foreach( $this->run['run_flow']['parameters'] as $p ): ?>
    <tr class="cardrow"><td><a class="pop" data-html="true" data-toggle="popover" data-placement="right" data-content="<?php if(array_key_exists($p['parameter'],$this->flow_parameters)) echo $this->flow_parameters[$p['parameter']]['description']; ?>"><?php echo $p['parameter']; ?></td><td><?php echo $p['value']; ?></td></tr>
    <?php endforeach; ?>
  </table></div>

  </div>

    <h3>Result files</h3>
    <div class="list-group">
    <?php foreach( $this->run['output_files'] as $k => $v ): ?>
      <div class="list-group-item">
          <a class="btn btn-fab btn-raised btn-material-red resultfile" href="<?php echo $v['url']; ?>">
              <i class="fa fa-cloud-download"></i>
          </a>
          <div class="row-content">
              <div class="least-content"><?php echo $v['format']; ?></div>
              <div class="list-group-item-heading"><?php echo ucfirst(str_replace("_"," ",$k)); ?></div>
              <p class="list-group-item-text"><?php echo $this->file_descriptions[strtolower($k)]; ?></p>
          </div>
      </div>
      <div class="list-group-separator"></div>
    <?php endforeach; ?>
  </div>

    <h3><?php echo count($this->run['evaluations']); ?> Evaluation measures</h3>
    <div class="cardtable">
    <div class='table-responsive'><table class='table' style="table-layout:fixed">
    <?php foreach( (array)$this->run['evaluations'] as $r ):
      if(count($r)<2)//if empty, skip
        continue;
      $perclass = false;
      if(in_array($r['evaluation_measure'], $this->binary_measures))
        $perclass = true;
      ?>
    <tr class="cardrow"><td><div class="col-md-3 evaltitle"><a href="a/evaluation-measures/<?php echo format_eval_link($r['evaluation_measure']); ?>"><b><?php echo format_eval_name($r['evaluation_measure']); ?></b></a></div><div class="col-md-9">
      <div class="list-group"><div class="list-group-item">
      <?php
        if(array_key_exists('value',$r))
          echo '<span class="mainvalue">'.$r['value'].'</span>';
        if(array_key_exists('stdev',$r) and $r['evaluation_measure'] != 'number_of_instances')
          echo ' &#177; '.$r['stdev'];
        if(array_key_exists('value',$r) and array_key_exists('array_data',$r))
          echo '</div><div class="list-group-separator-full"></div><div class="list-group-item">';
        if(array_key_exists('array_data',$r)){
          if($perclass)
            echo '<div class="item-title">Per class</div>';
          ?>
          <table class="table table-bordered table-condensed" style="width: auto; overflow-x: scroll" id="table_<?php echo $r['evaluation_measure']; ?>"></table>
        <?php }
        if(array_key_exists('per_fold',$r) and !empty($r['per_fold'])  and !empty($r['per_fold'][0])){
          echo '</div><div class="list-group-separator-full"></div><div class="list-group-item">';
          echo '<div class="item-title">Cross-validation details ('.$this->run['run_task']['estimation_procedure']['name'].')</div>';
          if($r['evaluation_measure'] == 'number_of_instances'){ ?>
              <table class="table table-bordered table-condensed" style="width: auto;" id="cvtable_<?php echo $r['evaluation_measure']; ?>"></table>
          <?php } else { ?>
              <div id="folds_<?php echo $r['evaluation_measure']; ?>" style="width: 100%;height:<?php echo 70+30*count($r['per_fold']);?>px"></div>
      <?php }}
      if(array_key_exists('data',$r)){
        echo $r['data'];
      }
      /* TODO: currently not implemented
      if($r['evaluation_measure'] == "area_under_roc_curve"){
        $charts = $this->Vipercharts->getWhere( 'run_id = ' . $this->run['run_id'] );

        if( $charts ) {
          ?>
          <div>
            <ul class="nav nav-tabs" role="tablist">
            <?php for( $i = 0; $i < count($charts); ++$i ): ?>
              <li class="<?php if($i == 0) echo 'active';?>"><a href="#roc-chart-<?php echo $charts[$i]->class; ?>" role="tab" data-toggle="tab"><?php echo $charts[$i]->{'class'}; ?></a></li>
            <?php endfor;?>
            </ul>
            <div class="tab-content">
            <?php for( $i = 0; $i < count($charts); ++$i ): ?>
              <div class="tab-pane<?php if($i == 0) echo ' active';?>" id="roc-chart-<?php echo $charts[$i]->class; ?>">
                <iframe src="<?php echo $this->config->item('api_vipercharts') . $charts[$i]->viper_id . '/'; ?>" width="300" height="250"></iframe>
              </div>
            <?php endfor; ?>
            </div>
          </div><?php
        }
      }*/
      ?>
    </div></div></td></tr>
    <?php endforeach; ?>
  </table></div></div>

  <h3>Discussions</h3>
  <div class="panel disquspanel">

    <div id="disqus_thread">Loading discussions...</div>
    <script type="text/javascript">
        var disqus_shortname = 'openml'; // forum name
	      var disqus_category_id = '3353606'; // Data category
	      var disqus_title = '<?php echo 'Run '.$this->run_id; ?>'; // Data name
	      var disqus_url = '<?php echo BASE_URL;?>r/<?php echo $this->run_id; ?>'; // Data url

        /* * * DON'T EDIT BELOW THIS LINE * * */
        (function() {
            var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true;
            dsq.src = '//' + disqus_shortname + '.disqus.com/embed.js';
            (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);
        })();
    </script>
  </div>
