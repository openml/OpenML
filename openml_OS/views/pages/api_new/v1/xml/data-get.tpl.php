<oml:data_set_description xmlns:oml="http://openml.org/openml">
  <oml:id><?php echo htmlspecialchars($did); ?></oml:id>
  <oml:name><?php echo htmlspecialchars($name); ?></oml:name>
  <oml:version><?php echo htmlspecialchars($version); ?></oml:version>
  <oml:description><?php echo htmlspecialchars($description); ?></oml:description>
  <oml:format><?php echo htmlspecialchars($format); ?></oml:format>
  <?php if( is_array( $creator ) ) foreach( $creator as $c ): ?><oml:creator><?php echo htmlspecialchars($c); ?></oml:creator><?php endforeach; ?>
	<?php if( is_array( $contributor ) ) foreach( $contributor as $c ): ?><oml:contributor><?php echo htmlspecialchars($c); ?></oml:contributor><?php endforeach; ?>
	<?php if ($collection_date != null): ?> <oml:collection_date><?php echo htmlspecialchars($collection_date); ?></oml:collection_date><?php endif; ?>
	<oml:upload_date><?php echo htmlspecialchars(dateXml($upload_date)); ?></oml:upload_date>
  <?php if ($language != null): ?><oml:language><?php echo htmlspecialchars($language); ?></oml:language><?php endif; ?>
  <?php if ($licence != null): ?><oml:licence><?php echo htmlspecialchars($licence); ?></oml:licence><?php endif; ?>
  <oml:url><?php echo htmlspecialchars($url); ?></oml:url>
  <?php if ($file_id != null): ?><oml:file_id><?php echo $file_id; ?></oml:file_id><?php endif; ?>
  <?php if ($default_target_attribute != null): ?><oml:default_target_attribute><?php echo htmlspecialchars($default_target_attribute); ?></oml:default_target_attribute><?php endif; ?>
  <?php if ($row_id_attribute != null): ?><oml:row_id_attribute><?php echo htmlspecialchars($row_id_attribute); ?></oml:row_id_attribute><?php endif; ?>
  <?php if( is_array( $ignore_attribute ) ) foreach( $ignore_attribute as $i ): ?><oml:ignore_attribute><?php echo htmlspecialchars($i); ?></oml:ignore_attribute><?php endforeach; ?>
  <?php if ($version_label != null): ?><oml:version_label><?php echo htmlspecialchars($version_label); ?></oml:version_label><?php endif; ?>
  <?php if ($citation != null): ?><oml:citation><?php echo htmlspecialchars($citation); ?></oml:citation><?php endif; ?>
  <?php if( is_array( $tag ) ) foreach( $tag as $t ): ?><oml:tag><?php echo htmlspecialchars($t); ?></oml:tag><?php endforeach; ?>
  <?php if ($visibility != null): ?><oml:visibility><?php echo htmlspecialchars($visibility); ?></oml:visibility><?php endif; ?>
  <?php if ($original_data_url != null): ?><oml:original_data_url><?php echo htmlspecialchars($original_data_url); ?></oml:original_data_url><?php endif; ?>
  <?php if ($paper_url != null): ?><oml:paper_url><?php echo htmlspecialchars($paper_url); ?></oml:paper_url><?php endif; ?>
  <oml:status><?php echo htmlspecialchars($status); ?></oml:status>
  <?php if ($processing_date != null): ?><oml:processing_date><?php echo htmlspecialchars($processing_date); ?></oml:processing_date><?php endif; ?>
  <?php if ($error != null): ?><oml:error><?php echo htmlspecialchars($error); ?></oml:error><?php endif; ?>
  <?php if ($warning != null): ?><oml:warning><?php echo htmlspecialchars($warning); ?></oml:warning><?php endif; ?>
  <oml:md5_checksum><?php echo htmlspecialchars($md5_checksum); ?></oml:md5_checksum>
</oml:data_set_description>
