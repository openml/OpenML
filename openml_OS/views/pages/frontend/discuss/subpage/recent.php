<?php //o( 'community-search.php' );
?>

<div class="topselectors">
<a type="button" class="btn btn-default loginfirst" style="float:right; margin-left:10px;" href="#new" data-toggle="tab"><i class="fa fa-fw fa-edit"></i> New Topic</a>
<div class="searchstats"><?php echo 'Popular and recent'; ?></div>
</div>

<?php
    foreach ($this->popular as $thread) {
       $ttype = $this->category_code[$thread->category];
      ?>
      <div class="searchresult panel"><div class="itemheadfull">
       <?php if($ttype == 'general' and $t){ ?>
         <i><img src="<?php echo $thread->authimage;?>" width="30" height="30" class="img-circle" /></i>
         <a href="discuss/tid/<?php echo $thread->id; ?>"><?php echo stripslashes( teaser( $thread->title, 100 ) );?></a>
       <?php } else { ?>
         <i class="<?php echo $this->icons[$this->category_code[$thread->category]];?>"></i>
         <a href="<?php echo $thread->link; ?>"><?php echo $thread->title;?></a>
       <?php } ?>
        (<?php echo $thread->posts;?> comments)
       <!--<span><i class="fa fa-fw fa-clock-o"></i> <?php echo 'Created '.get_timeago(strtotime(str_replace('T',' ',$thread->createdAt)));?>-->
       <?php if($ttype == 'general' and $t){ echo ' by '.$thread->authname; } ?>
       </span>
     </div>
     <div class="discuss_postmessage">
       <?php echo $thread->message; ?>
     </div>
   </div>
    <?php }
  ?>


  <div class="topselectors">
  <div class="searchstats"><?php echo 'Recent posts'; ?></div>
  </div>

  <?php
      foreach ($this->posts as $post) { ?>
        <div class="searchresult panel" onclick="location.href='<?php echo $post->thread->link; ?>'"><div class="itemheadfull">
         <i><img src="<?php echo $post->author->avatar->small->permalink;?>" width="40" height="40" class="img-circle" /></i>
         <?php echo $post->author->username;?> posted in
         <b><?php echo $post->thread->title;?></b>
         (<?php echo $this->category_code[$post->thread->category];?>)
         <span><i class="fa fa-fw fa-clock-o"></i> <?php echo get_timeago(strtotime(str_replace('T',' ',$post->createdAt)));?></span>
         <div class="discuss_postmessage">
         <?php echo $post->message;?>
         </div>
       </div></div>
      <?php }
    ?>
