<oml:data xmlns:oml="http://openml.org/openml">
  <?php foreach ($datasets as $data): ?>
  <oml:dataset>
    <oml:did><?php echo $data->did; ?></oml:did>
    <oml:name><?php echo $data->name; ?></oml:name>
    <oml:version><?php echo $data->version; ?></oml:version>
    <oml:uploader><?php echo $data->uploader; ?></oml:uploader>
    <oml:status><?php echo $data->status; ?></oml:status>
    <oml:format><?php echo $data->format; ?></oml:format>
    <?php if ($data->file_id != null): /* note that this is an optional field! */?>
    <oml:file_id><?php echo $data->file_id; ?></oml:file_id> 
    <?php endif; ?>
    <?php foreach ($data->qualities as $quality => $value): ?>
    <oml:quality name="<?php echo $quality; ?>"><?php echo $value; ?></oml:quality>
    <?php endforeach; ?>
  </oml:dataset>
  <?php endforeach; ?>
</oml:data>
