<oml:likes xmlns:oml="http://openml.org/openml">
  <?php foreach( $likes as $like ): ?>
  <oml:like>
    <oml:user_id><?php echo $like->user_id; ?></oml:user_id>
    <oml:knowledge_type><?php echo $like->knowledge_type; ?></oml:knowledge_type>
    <oml:knowledge_id><?php echo $like->knowledge_id; ?></oml:knowledge_id>
  </oml:like>
  <?php endforeach; ?>
</oml:likes>

