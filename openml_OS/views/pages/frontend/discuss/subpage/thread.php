<div class="searchresult panel">
  <div class="itemheadfull">
    <i><img src="<?php echo htmlentities( authorImage( $this->author->image ) );?>" width="40" height="40" class="img-circle" /></i>
    <?php echo user_display_text( $this->author ); ?>
    <span><i class="fa fa-fw fa-clock-o"></i> <?php echo get_timeago(strtotime(str_replace('T',' ',$this->thread->post_date)));?></span>
  </div>
  <div class="discuss_postmessage" style="margin-bottom:40px;">
    <h2 style="margin-top:0px;"><?php echo $this->thread->title; ?></h2>
		<?php echo nl2br( stripslashes( $this->thread->body ) );?>
  </div>
  <div id="disqus_thread"></div>
</div>

<script type="text/javascript">

  var disqus_shortname = '<?php echo DISQUS_USERNAME; ?>';
  <?php if( $_SERVER['SERVER_NAME'] == 'localhost' ) echo 'var disqus_developer = 1;';?>

  (function() {
    var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true;
    dsq.src = 'http://' + disqus_shortname + '.disqus.com/embed.js';
    (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);
  })();
</script>
<noscript>Please enable JavaScript to view the <a href="http://disqus.com/?ref_noscript">comments powered by Disqus.</a></noscript>
