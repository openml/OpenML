{
    "<?php echo $xml_tag_name; ?>_tag_list": { "tag": [
        <?php 
        $first = true;
        foreach ($tags as $tag):
            if (!$first) {
                echo ","; 
            }
            $first = false;
            // Properly encode the tag as a JSON string
            echo json_encode($tag); 
        endforeach;
        ?>
    ]}
}