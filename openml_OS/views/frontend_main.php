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
          o('login');
        ?>



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
