{"flows":{"flow":[
  <?php $first = TRUE;
        foreach( $implementations as $i ):
          echo ($first ? "" : ",");
          $first = FALSE; ?>
  {"id":<?php echo $i->id; ?>,
   "full_name":"<?php echo $i->fullName; ?>",
   "name":"<?php echo $i->name; ?>",
   "version":<?php echo $i->version; ?>,
   "external_version":"<?php echo $i->external_version; ?>",
   "uploader":<?php echo $i->uploader; ?>
	 <?php if( property_exists( $i, 'tags' ) ): ?>
	 ,"tags": [
		 <?php $first_t = TRUE;
					 foreach( $i->tags as $tag ):
					 echo ($first_t ? "" : ",");
					 $first_t = FALSE; ?>
		 "<?php echo $tag;?>"
	 <?php endforeach; ?>
	 ]
 	 <?php endif; ?>
  }
  <?php endforeach; ?>
  ]}
}
