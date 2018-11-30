<?php
if (session_status() === PHP_SESSION_NONE){session_start();}
?>

<!doctype html>
<!--[if lt IE 7]>
<html class="no-js lt-ie9 lt-ie8 lt-ie7" lang="en">
<![endif]-->
<!--[if IE 7]>
<html class="no-js lt-ie9 lt-ie8" lang="en">
<![endif]-->
<!--[if IE 8]>
<html class="no-js lt-ie9" lang="en">
<![endif]-->
<!--[if gt IE 8]><!-->

<html class="no-js" lang="en" xmlns:og="http://ogp.me/ns#">
    <!--<![endif]-->

    
    <body>
  <?php
      $req = array_slice($url, 1);
      if(sizeof($url)>2){
        $id=$url[2];
        if($url[1] == 'OpenML'){
          $ch = $url[2];
          $req = array_slice($url, 2);
            if(sizeof($url)>3){
                $id=$url[3];
            }
        }
      }
      if($ch == "")
        $ch = "home";
      $ch = explode('?',$ch)[0];
      $reqall = implode('/',$req);
      $req = explode('?',implode('/',$req))[0];
      if(strpos($ch, 'search') === 0){
        if(isset($this->filtertype) and $this->filtertype){
            $section = str_replace('_',' ',ucfirst($this->filtertype));
            if($section=='User')
              $section = 'People';
            $href = "search?type=".$this->filtertype;
          }
        else{
          $section = 'Search';
          $materialcolor = "blue";
        }
      }
      if($ch=='r' or $section=='Run'){
            $section = 'Run';
            $href = 'search?type=run';
            $materialcolor = "red";
          }
      elseif($ch=='d' or $section=='Data'){
            $section = 'Data';
            $href = 'search?type=data';
            $materialcolor = "green";
          }
      elseif($ch=='f' or $section=='Flow'){
            $section = 'Flow';
            $href = 'search?type=flow';
            $materialcolor = "blue";
          }
      elseif($ch=='t' or $section=='Task'){
            $section = 'Task';
            $href = 'search?type=task';
            $materialcolor = "orange";
          }
      elseif($ch=='tt' or $section=='Task type'){
            $section = 'Task type';
            $href = 'search?type=tasktype';
            $materialcolor = "deep-orange";
          }
      elseif($ch=='u' or $section=='People'){
            $section = 'People';
            $href = 'search?type=user';
            $materialcolor = "light-blue";
          }
      elseif($ch=='a' or $section=='Measure'){
            $section = 'Measure';
            $href = $ch;
            $materialcolor = "blue-grey";
          }
      elseif($ch=='s' or $section=='Study'){
            $section = 'Study';
            $href = 'search?type=study';
            $materialcolor = "deep-purple";
          }
      elseif(substr( $ch, 0, 5 ) === "guide"){
            $section = 'Guide';
            $href = $ch;
            $materialcolor = "green";
          }
      elseif(substr( $ch, 0, 4 ) === "cite"){
            $section = 'Citing';
            $href = $ch;
            $materialcolor = "red";
          }
      elseif(substr( $ch, 0, 7 ) === "contact"){
            $section = 'Contact';
            $href = $ch;
            $materialcolor = "green";
          }
      elseif($ch=='backend'){
            $section = 'Backend';
            $href = $ch;
            $materialcolor = "red";
          }
      elseif($ch=='query'){
            $section = 'Query';
            $href = $ch;
            $materialcolor = "blue";
          }
      elseif($ch=='register' or $ch=='profile' or $ch=='frontend' or $ch=='login'){
            $section = 'OpenML';
            $href = $ch;
          }

    $this->section = $section;
    $this->materialcolor = $materialcolor;
    $this->user = $this->ion_auth->user()->row();
    $this->image = array(
    	'name' => 'image',
    	'id' => 'image',
    	'type' => 'file',
    );
  ?>
        <div id="sectiontitle"><?php echo $section;?></div>
        <div class="navbar navbar-static-top navbar-fixed-top navbar-material-<?php echo $materialcolor;?>" id="openmlheader" style="margin-bottom: 0px;">
            <div class="navbar-inner">
              <div class="col-xs-6 col-sm-3 col-md-3">
              <div class="nav pull-left">
                <a class="navbar-brand menubutton"><i class="fa fa-bars fa-lg"></i></a>
              </div>
              <a class="navbar-brand" id="section-brand" href="home">OpenML</a>
              </div>
            <a class="openmlsoc openmlsocicon col-xs-2 hidden-sm hidden-md hidden-lg pull-left searchicon" onclick="showsearch()"><i class="fa fa-search fa-2x"></i></a>

       <div class="menuicons">
			<?php if ($this->ion_auth->logged_in()) {
        $authimg = "img/community/misc/anonymousMan.png";
         if ($this->user){ $authimg = htmlentities( authorImage( $this->user->image ) );}
        ?>
        <div class="nav pull-right openmlsocicons">
          <a href="#" class="dropdown-toggle openmlsoc openmlsocicon" data-toggle="dropdown" style="padding-top:12px;">
            <img src="<?php echo $authimg; ?>" width="35" height="35" class="img-circle" alt="<?php echo $this->user->first_name . ' ' . $this->user->last_name; ?>" /></a>
          <ul class="dropdown-menu">
              <li><a href="u/<?php echo $this->user->id;?>"><?php echo user_display_text(); ?></a></li>
              <li class="divider"></li>
              <li><a href="auth/logout">Sign off</a></li>
          </ul>
        </div>

  			<div class="nav pull-right openmlsocicons">
  			  <a href="#" class="dropdown-toggle openmlsoc openmlsocicon" data-toggle="dropdown"><i class="fa fa-plus fa-2x"></i></a>
  			  <ul class="dropdown-menu newmenu">
  			    <li><a href="new/data" class="icongreen"><i class="fa fa-fw fa-lg fa-database"></i> New data</a></li>
  		            <li class="divider"></li>
  			    <li><a href="new/task" class="iconyellow"><i class="fa fa-fw fa-lg fa-trophy"></i> New task</a></li>
  		            <li class="divider"></li>
  			    <!--<li><a href="new/flow" class="iconblue"><i class="fa fa-fw fa-lg fa-cogs"></i> New flow</a></li>
  		            <li class="divider"></li>-->
  			    <!--<li><a href="new/run" class="iconred"><i class="fa fa-fw fa-lg fa-star"></i> New run</a></li>
                  <li class="divider"></li>-->
            <li><a href="new/study" class="iconpurple"><i class="fa fa-fw fa-lg fa-flask"></i> New study</a></li>
  			  </ul>
  			</div>
        <div class="nav pull-right openmlsocicons">
          <a href="https://docs.openml.org" class="openmlsoc openmlsocicon"><i class="fa fa-leanpub fa-2x"></i></a>
        </div>
        <script>var logged_in = true;</script>
			<?php } else { ?>
        <script>var logged_in = false;</script>
			<div class="nav pull-right openmlsocicons">
                  <a href="https://docs.openml.org" class="btn btn-material-<?php echo $materialcolor;?>">Help</a>
                  <a class="btn btn-material-<?php echo $materialcolor;?>" data-toggle="modal" data-target="#login-dialog">Sign in</a>
      </div>
			<?php } ?>
      </div>

      <?php if($section != 'Guide') { ?>
      <div class="hidden-xs col-sm-6 col-md-6" id="menusearchframe">
      <form class="navbar-form" method="get" id="searchform" action="search">
        <input type="text" class="form-control col-lg-8" id="openmlsearch" name="q" placeholder="Search" onfocus="this.placeholder = 'Search datasets, flows, tasks, people,... (leave empty to see all)'" value="<?php if( isset( $this->terms ) ) echo htmlentities($this->terms); ?>" />
        <input type="hidden" name="type" value="<?php if(array_key_exists("type",$_GET)) echo htmlspecialchars(safe($_GET["type"]), ENT_QUOTES);
        elseif(false !== strpos($_SERVER['REQUEST_URI'],'/d')) echo 'data';
        elseif(false !== strpos($_SERVER['REQUEST_URI'],'/t')) echo 'task';
        elseif(false !== strpos($_SERVER['REQUEST_URI'],'/f')) echo 'flow';
        elseif(false !== strpos($_SERVER['REQUEST_URI'],'/r')) echo 'run';
        elseif(false !== strpos($_SERVER['REQUEST_URI'],'/a')) echo 'measure';
          ?>">
      <!-- <button class="btn btn-primary btn-small" type="submit" style="height: 30px; vertical-align:top; font-size: 8pt;"><i class="fa fa-search fa-lg"></i></button>-->
      </form>
       </div>
     <?php } ?>


                    <!--/.nav-collapse -->
            </div>
        </div>

        <?php
          o('login');
        ?>

        <div id="wrap">
            <div class="alertbox col-md-12">
            <!-- USER MESSAGE -->
            <noscript>
                <div class="alert alert-dismissible alert-error">
                  <button type="button" class="close" data-dismiss="alert">×</button>
                    JavaScript is required to properly view the contents of this page!
                </div>
            </noscript>
            <?php 
                  if($this->input->post('warningmessage')!==false and strlen($this->input->post('warningmessage')) > 0){
                    $this->message = $this->input->post('warningmessage');
                  }
                  if($this->message!==false and strlen($this->message) > 0): ?>
            <div class="alert alert-dismissible alert-warning">
                <button type="button" class="close" data-dismiss="alert">×</button>
                <?php echo $this->message; ?>
            </div>
            <?php endif; ?>
          </div>


          <div class="searchbarcontainer">
          <div class="searchbar" id="mainmenu" <?php if($section == "OpenML" or $section == "Guide" or $ch == "new" or $ch == "backend"){echo 'style="display:none"';}?>>
            <div class="sidebar-overlay">
            <div class="nav pull-left">
              <a class="navbar-brand menubutton"><i class="fa fa-bars fa-lg"></i></a>
            </div>
            <a class="navbar-brand" id="section-brand" href="<?php echo $href; ?>">OpenML</a>
            </div>
            <ul class="sidenav nav topchapter" id="topaccordeon">
              <li class="panel mainchapter">
                <a data-toggle="collapse" data-parent="#topaccordeon" data-target="#mainlist"> <b>Explore</b></a>
                <ul class="sidenav nav collapse in" id="mainlist">
                  <!--
                  <?php if (!$this->ion_auth->logged_in()){ ?>
                      <li <?php echo ($section == '' ?  'class="topactive"' : '');?>><a href="register" class="icongrayish"><i class="fa fa-fw fa-lg fa-child"></i> Join OpenML</a></li>
                  <?php } else { ?>
                      <li <?php echo ($section == '' ?  'class="topactive"' : '');?>><a href="u/<?php echo $this->user->id; ?>"><img src="<?php echo htmlentities( authorImage( $this->user->image ) ); ?>" width="25" height="25" class="img-circle" alt="<?php echo $this->user->first_name . ' ' . $this->user->last_name; ?>" /> <?php echo user_display_text(); ?></a></li>
                  <?php } ?> -->
                      <?php

                        $sections = ["Data", "Task", "Flow", "Run", "Study", "Task type", "Measure", "People"];
                        $types    = ["data", "task", "flow", "run", "study", "task_type", "measure", "user"];
                        $colors   = ["green", "yellow", "blue", "red", "purple", "orange", "bluegray", "blueacc"];
                        $icons    = ["database", "trophy", "cogs", "star", "flask", "flag-o", "bar-chart-o", "users"];

                        for( $i = 0; $i < count($sections); $i++ ) {
                          echo "<li" . ($section == $sections[$i] ? " class=\"topactive\"" : "") . ">";
                          echo "<a href=\"search?type=" . $types[$i] . (array_key_exists("q", $_GET) ? "&amp;q=" . htmlspecialchars(safe($_GET["q"]), ENT_QUOTES) : "") . "\" class=\"icon{$colors[$i]}\">";
                          echo "<i class=\"fa fa-fw fa-lg fa-{$icons[$i]}\"></i> $sections[$i]<span id=\"{$types[$i]}counter\" class=\"counter\"></span></a></li>" . PHP_EOL;
                        }

                      ?>
                </ul>
              </li>
                <li class="menu-cite <?php echo ($section == 'Guide' ?  'topactive' : '');?>"><a href="https://docs.openml.org" class="icongreen"><i class="fa fa-fw fa-lg fa-leanpub"></i> <b>Help</b></a></li>
                <li class="menu-cite"><a href="https://medium.com/open-machine-learning" class="iconyellow" target="_blank"><i class="fa fa-fw fa-lg fa-rss-square"></i> <b>Blog</b></a></li>
                <li class="menu-cite <?php echo ($section == 'Contact' ?  'topactive' : '');?>"><a href="contact" class="iconblue"><i class="fa fa-fw fa-lg fa-bullhorn"></i> <b>Contact</b></a></li>
                <li class="menu-cite <?php echo ($section == 'Citing' ?  'topactive' : '');?>"><a href="cite" class="iconred"><i class="fa fa-fw fa-lg fa-heart"></i> <b>Please cite us</b></a></li>
            </ul>
          </div>
        </div>

        <?php echo $body; ?>


        </div>
        <script type="text/javascript">
          var ES_URL = '<?php echo ES_URL; ?>';

          function downloadJSAtOnload() {
          //var element3= document.createElement("script");
          //element3.src = "js/libs/jquery.sharrre.js";
          //document.body.appendChild(element3);
          }

          !function(e,t,r){function n(){for(;d[0]&&"loaded"==d[0][f];)c=d.shift(),c[o]=!i.parentNode.insertBefore(c,i)}for(var s,a,c,d=[],i=e.scripts[0],o="onreadystatechange",f="readyState";s=r.shift();)a=e.createElement(t),"async"in i?(a.async=!1,e.head.appendChild(a)):i[f]?(d.push(a),a[o]=n):e.write("<"+t+' src="'+s+'" defer></'+t+">"),a.src=s}(document,"script",[
              'https:///ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js',
              'js/libs/modernizr-2.5.3-respond-1.1.0.min.js',
              '//code.jquery.com/ui/1.10.4/jquery-ui.min.js',
              'js/libs/elasticsearch.jquery.min.js',
              'js/libs/bootstrap.min.js',
              'js/libs/bootstrap-select.js',
              'js/material.min.js',
              'js/libs/jquery.form.js',
              'js/libs/perfect-scrollbar.min.js',
              'js/openml.js',
              <?php if( isset( $this->load_javascript ) ): foreach( $this->load_javascript as $j ):
                echo "'".$j."',"; endforeach; endif; ?>
              <?php if( isset( $this->id) || $ch == 'new'){
                echo "'frontend/js/".$req."',";
              } elseif( $ch == 'search'){
                echo "'frontend/js/".$reqall."',";
              } elseif( $ch == 'api'){
                echo "'frontend/js/api_docs',";
              } elseif( $ch != 'backend') {
                if ($ch == 'u' && $id) {
                    echo "'frontend/js/" . $ch . "/" . $id . "',";
                } else {
                    echo "'frontend/js/".$ch."',";
                }
              }?>
              'js/openmlafter.js'
            ])

          //download js that does not affect DOM and can be loaded after page is rendered
          if (window.addEventListener)
          window.addEventListener("load", downloadJSAtOnload, false);
          else if (window.attachEvent)
          window.attachEvent("onload", downloadJSAtOnload);
          else window.onload = downloadJSAtOnload;
        </script>
        <script>
          (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
          (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
          m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
          })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

          ga('require', 'linkid', 'linkid.js');
          ga('create', 'UA-40902346-1', 'openml.org');
          ga('send', 'pageview');
        </script>


    </body>
</html>
