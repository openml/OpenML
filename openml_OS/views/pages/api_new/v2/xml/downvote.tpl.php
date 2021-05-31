<oml:downvote xmlns:oml="http://openml.org/openml">
    <oml:user_id><?php echo $user_id; ?></oml:user_id>
    <oml:knowledge_type><?php echo $knowledge_type; ?></oml:knowledge_type>
    <oml:knowledge_id><?php echo $knowledge_id; ?></oml:knowledge_id>
    <oml:reason><?php echo $description; ?></oml:reason>
    <oml:reason_id><?php echo $reason_id; ?></oml:reason_id>
    <oml:original><?php echo $original; ?></oml:original>
    <?php if($count){ ?>
        <oml:count><?php echo $count; ?></oml:count>    
    <?php } ?>
</oml:downvote>