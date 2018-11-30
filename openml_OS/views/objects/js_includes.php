
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
