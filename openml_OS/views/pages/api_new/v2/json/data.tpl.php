{"data":{"dataset":[
  <?php $first = true;
        foreach ($datasets as $data):
          echo ($first ? "" : ",");
          $first = false; ?>
  {"did":<?php echo $data->did; ?>,
   "name":"<?php echo $data->name; ?>",
   "version":<?php echo $data->version; ?>,
   "status":"<?php echo $data->status; ?>",
   "format":"<?php echo $data->format; ?>",
   "md5_checksum":"<?php echo $data->md5_checksum; ?>",
   <?php if ($data->file_id != null): /* optional field! */?>
   "file_id": <?php echo $data->file_id; ?>,
   <?php endif; ?>
   "quality":[
    <?php $firstq = TRUE;
          foreach( $data->qualities as $quality => $value ):
            echo ($firstq ? "" : ",");
            $firstq = FALSE; ?>
    {"name":"<?php echo $quality; ?>",
     "value":"<?php echo $value; ?>"
    }
    <?php endforeach; ?>
    ]
    <?php if( property_exists( $data, 'tags' ) ): ?>
    ,"tags": [
      <?php $first_t = TRUE;
            foreach( $data->tags as $tag ):
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
