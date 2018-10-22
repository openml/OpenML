<?php
    include 'profile_card.php';
?>

<table class="table panel usertable">
    <tbody>

<?php if ($this->ion_auth->logged_in()) {
      if ($this->ion_auth->user()->row()->gamification_visibility == 'show'  && $this->userinfo['gamification_visibility'] == 'show') {?>
        <tr>
            <td class="borderless"></td>
            <td class="borderless">Activity</td>
            <td class="borderless">Reach</td>
            <td class="borderless"></td>
            <td class="borderless">Impact</td>
        </tr>
<?php }} ?>
      <tr>
        <td class="mainvalue"><i class="fa fa-fw fa-database dataset" ></i> <a href="<?php echo BASE_URL .'u/' . $this->user_id . '/data';?>"> Data Sets</a></td>
        <td class="subvalue" title="Number of data sets uploaded"><?php if(in_array('datasets_uploaded', $this->userinfo)){echo $this->userinfo['datasets_uploaded'];}else{echo 0;}?> <i class="fa fa-cloud-upload"></i></td>
        <td class="subvalue" title="Number of downloads recieved on your data sets"><?php if(in_array('downloads_received_data', $this->userinfo)){echo $this->userinfo['downloads_received_data'];}else{echo 0;}?> <i class="fa fa-cloud-download"></i></td>
        <td class="subvalue" title="Number of likes recieved on your data sets"><?php if(in_array('likes_received_data', $this->userinfo)){echo $this->userinfo['likes_received_data'];}else{echo 0;}?> <i class="fa fa-heart"></i></td>
        <td class="subvalue" title="Number of runs done on your data sets"><?php if(in_array('runs_on_datasets', $this->userinfo)){echo $this->userinfo['runs_on_datasets'];}else{echo 0;}?> <i class="fa fa-refresh"></i><sub><i class="fa fa-star"></i></sub></td>
      </tr>
      <tr>
        <td class="mainvalue"><i class="fa fa-fw fa-cogs flow" ></i><a href="<?php echo BASE_URL .'u/' . $this->user_id . '/flows';?>"> Flows </a></td>
        <td class="subvalue" title="Number of flows uploaded"><?php if(in_array('flows_uploaded', $this->userinfo)){echo $this->userinfo['flows_uploaded'];}else{echo 0;}?> <i class="fa fa-cloud-upload"></i></td>
        <td class="subvalue" title="Number of downloads recieved on your flows"><?php if(in_array('downloads_received_flow', $this->userinfo)){echo $this->userinfo['downloads_received_flow'];}else{echo 0;}?> <i class="fa fa-cloud-download"></i></td>
        <td class="subvalue" title="Number of likes recieved on your flows"><?php if(in_array('likes_received_flow', $this->userinfo)){echo $this->userinfo['likes_received_flow'];}else{echo 0;}?> <i class="fa fa-heart"></i></td>
        <td class="subvalue" title="Number of runs done on your flows"><?php if(in_array('runs_on_flows', $this->userinfo)){echo $this->userinfo['runs_on_flows'];}else{echo 0;}?> <i class="fa fa-refresh"></i><sub><i class="fa fa-star"></i></sub></td>
      </tr>
      <tr>
        <td class="mainvalue"><i class="fa fa-fw fa-trophy task" ></i><a href="<?php echo BASE_URL .'u/' . $this->user_id . '/tasks';?>"> Tasks </a></td>
        <td class="subvalue" title="Number of tasks uploaded"><?php if(in_array('tasks_uploaded', $this->userinfo)){echo $this->userinfo['tasks_uploaded'];}else{echo 0;}?> <i class="fa fa-cloud-upload"></i></td>
        <td class="subvalue" title="Number of downloads recieved on your tasks"><?php if(in_array('downloads_received_task', $this->userinfo)){echo $this->userinfo['downloads_received_task'];}else{echo 0;}?> <i class="fa fa-cloud-download"></i></td>
        <td class="subvalue" title="Number of likes recieved on your tasks"><?php if(in_array('likes_received_task', $this->userinfo)){echo $this->userinfo['likes_received_task'];}else{echo 0;}?> <i class="fa fa-heart"></i></td>
        <td></td>
      </tr>
      <tr>
        <td class="mainvalue"><i class="fa fa-fw fa-star run" ></i><a href="<?php echo BASE_URL .'u/' . $this->user_id . '/runs';?>"> Runs </a></td>
        <td class="subvalue" title="Number of runs uploaded"><?php if(in_array('runs_uploaded', $this->userinfo)){echo $this->userinfo['runs_uploaded'];}else{echo 0;}?> <i class="fa fa-cloud-upload"></i></td>
        <td class="subvalue" title="Number of downloads recieved on your runs"><?php if(in_array('downloads_received_run', $this->userinfo)){echo $this->userinfo['downloads_received_run'];}else{echo 0;}?> <i class="fa fa-cloud-download"></i></td>
        <td class="subvalue" title="Number of likes recieved on your runs"><?php if(in_array('likes_received_run', $this->userinfo)){echo $this->userinfo['likes_received_run'];}else{echo 0;}?> <i class="fa fa-heart"></i></td>
        <td></td>
      </tr>
    </tbody>
  </table>


<?php if ($this->ion_auth->logged_in()) {
      if ($this->ion_auth->user()->row()->gamification_visibility == 'show'  && $this->userinfo['gamification_visibility'] == 'show') {?>
<div class="panel statpanel">
    <ul class="nav nav-pills activity">
        <li class="col-sm-4 mainvalue active" title="Activity is: 3x uploads done + 2x likes given + downloads done.">
            <a data-toggle="tab" onclick=redrawActivityChart("Activity")>
                <i class="fa fa-fw fa-heartbeat"></i>
                Activity:
                <span><?php if(in_array('activity', $this->userinfo)){ echo largeNumberFormat($this->userinfo['activity']);}else{echo 0;} ?>
            </a>
        </li>
        <li class="col-sm-2 mainvalue" title="The number of uploads you have done over the last year | the total number of uploads you have done">
            <a data-toggle="tab" onclick=redrawActivityChart("Uploads")>
                <span><?php if(in_array('nr_of_uploads', $this->userinfo)){ echo largeNumberFormat($this->userinfo['nr_of_uploads']);}else{echo 0;} ?> </span>
                <i class="fa fa-cloud-upload"></i>
            </a>
        </li>
        <li class="col-sm-2 mainvalue" title="The number of likes you have done over the last year | the total number of likes you have done">
            <a data-toggle="tab" onclick=redrawActivityChart("Likes")>
                <span><?php if(in_array('nr_of_likes', $this->userinfo)){ echo largeNumberFormat($this->userinfo['nr_of_likes']);}else{echo 0;} ?> </span>
                <i class="fa fa-heart"></i>
            </a>
        </li>
        <li class="col-sm-2 mainvalue" title="The number of things you have downloaded over the last year | the total number of things you have downloaded">
            <a data-toggle="tab" onclick=redrawActivityChart("Downloads")>
                <span><?php if(in_array('nr_of_downloads', $this->userinfo)){ echo largeNumberFormat($this->userinfo['nr_of_downloads']);}else{echo 0;} ?> </span>
                <i class="fa fa-cloud-download"></i>
            </a>
        </li>
        <li class="col-sm-1 pull-right">
            <a data-toggle="collapse" href="#Activity-chart">
                <i class="fa fa-2x fa-minus" id="activitytoggle" style="visibility: hidden"></i>
            </a>
        </li>
    </ul>
    <div class="col-sm-12 collapse" id="Activity-chart">
        <div id="activityplot" class="row">
            <i class="fa fa-spinner fa-pulse"></i> Loading activity data
        </div>
    </div>
</div>
<div class="panel statpanel">
   <ul class="nav nav-pills reach">
       <li class="col-sm-4 mainvalue active" title="Reach is: 2x likes received + downloads received.
           Given as reach over the last month | total reach">
           <a data-toggle="tab" onclick=redrawReachChart("Reach")>
               <i class="fa fa-fw fa-rss"></i>
               Reach:
               <span id="ReachAllTime" title="<?php if(in_array('reach', $this->userinfo)){echo $this->userinfo['reach'];}else{echo 0;}?>"><?php if(in_array('reach', $this->userinfo)){echo largeNumberFormat($this->userinfo['reach']);}else{echo 0;} ?></span>
           </a>
       </li>
        <li class="col-sm-2 mainvalue" title="The number of likes your uploads have gotten over the last year | the total number of likes you have gotten">
            <a data-toggle="tab" onclick=redrawReachChart("Likes")>
                <span id="LikesReceivedAllTime" title="<?php if(in_array('likes_received', $this->userinfo)){echo $this->userinfo['likes_received'];}else{echo 0;}?>"><?php if(in_array('likes_received', $this->userinfo)){ echo largeNumberFormat($this->userinfo['likes_received']);}else{echo 0;} ?></span>
                <i class="fa fa-heart"></i>
            </a>
        </li>
        <li class="col-sm-2 mainvalue" title="The number of distinct downloads of your uploads over the last year | the total number of distinct downloads of your uploads">
            <a data-toggle="tab" onclick=redrawReachChart("Downloads")>
                <span id="DownloadsReceivedAllTime" title="<?php if(in_array('downloads_received', $this->userinfo)){echo $this->userinfo['downloads_received'];}else{echo 0;}?>"><?php if(in_array('downloads_received', $this->userinfo)){ echo largeNumberFormat($this->userinfo['downloads_received']);}else{echo 0;} ?></span>
                <i class="fa fa-cloud-download"></i>
            </a>
        </li>
        <li class="col-sm-1 pull-right">
            <a data-toggle="collapse" href="#Reach-chart">
                <i class="fa fa-2x fa-minus" id="reachtoggle" style="visibility: hidden"></i>
            </a>
        </li>
    </ul>
    <div class="col-sm-12 collapse" id="Reach-chart">
        <div id="reachplot" class="row">
            <i class="fa fa-spinner fa-pulse"></i> Loading reach data
        </div>
    </div>
</div>
<div class="panel statpanel">
    <ul class="nav nav-pills impact">
        <li class="col-sm-4 mainvalue active" title="Impact is: number or reuses of your uploads + 0.5*reach of reuse of your uploads + 0.5*impact of reuse of your uploads. Given as your impact over the last month | your total impact">
            <a data-toggle="tab" onclick=redrawImpactChart("Impact")>
                <!--<i class="material-icons" style="font-size: 28px">flare</i>-->
                <i class="fa fa-fw fa-bolt"></i>
                Impact:
                <span id="ImpactAllTime" title="<?php if(in_array('impact', $this->userinfo)){echo $this->userinfo['impact'];}else{echo 0;}?>"><?php if(in_array('impact', $this->userinfo)){echo largeNumberFormat($this->userinfo['impact']);}else{echo 0;} ?></span>
            </a>
        </li>
        <li class="col-sm-2 mainvalue" title="The number of reuses of your uploads by other users over the last year | the total number of reuses of your uploads by other users">
            <a data-toggle="tab" onclick=redrawImpactChart("Reuse")>
                <span id="ReuseAllTime" title="<?php if(in_array('reuse', $this->userinfo)){echo $this->userinfo['reuse'];}else{echo 0;}?>"><?php if(in_array('reuse', $this->userinfo)){echo largeNumberFormat($this->userinfo['reuse']);}else{echo 0;} ?> </span>
                <i class="fa fa-fw fa-refresh"></i>
            </a>
        </li>
        <li class="col-sm-2 mainvalue" title="The Reach achieved by reuses of your uploads by other users over the last year | the total Reach of reuses of your uploads by other users">
            <a data-toggle="tab" onclick=redrawImpactChart("Reach_re")>
                <span id="ReachReuseAllTime" title="<?php if(in_array('reach_of_reuse', $this->userinfo)){echo $this->userinfo['reach_of_reuse'];}else{echo 0;}?>"><?php if(in_array('reach_of_reuse', $this->userinfo)){echo largeNumberFormat($this->userinfo['reach_of_reuse']);}else{echo 0;} ?> </span>
                <i class="fa fa-rss"></i><sub><i class="fa fa-fw fa-refresh"></i></sub>
            </a>
        </li>
        <li class="col-sm-2 mainvalue" title="The Impact achieved by reuses of your uploads by other users over the last year | the total Impact of reuses of your uploads by other users">
            <a data-toggle="tab" onclick=redrawImpactChart("Impact_re")>
                <span id="ImpactReuseAllTime" title="<?php if(in_array('impact_of_reuse', $this->userinfo)){echo $this->userinfo['impact_of_reuse'];}else{echo 0;}?>"><?php if(in_array('impact_of_reuse', $this->userinfo)){echo largeNumberFormat($this->userinfo['impact_of_reuse']);}else{echo 0;} ?> </span>
                <i class="fa fa-fw fa-bolt"></i><sub><i class="fa fa-fw fa-refresh"></i></sub>
            </a>
        </li>
        <li class="col-sm-1 pull-right">
            <a data-toggle="collapse" href="#Impact-chart">
                <i class="fa fa-2x fa-minus" id="impacttoggle" style="visibility: hidden"></i>
            </a>
        </li>
    </ul>
    <div class="col-sm-12 collapse" id="Impact-chart">
        <div id="impactplot" class="row">
            <i class="fa fa-spinner fa-pulse"></i> Loading impact data
        </div>
    </div>
</div>

<div class="col-sm-12">
    <h2>Badges</h2>
    <div id="badges">
        <i class="fa fa-spinner fa-pulse"></i>
    </div>
</div>
<?php }} ?>
