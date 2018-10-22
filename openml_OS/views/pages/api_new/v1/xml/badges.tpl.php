<oml:badges xmlns:oml="http://openml.org/openml">
    <?php foreach($badges as $badge){?>
        <oml:badge>
            <oml:id><?php echo $badge['id']; ?></oml:id>
            <oml:name><?php echo $badge['name']; ?></oml:name>
            <oml:rank><?php echo $badge['rank']; ?></oml:rank>
            <oml:description_current><?php echo $badge['description_current']; ?></oml:description_current>
            <oml:description_next><?php echo $badge['description_next']; ?></oml:description_next>
            <oml:image><?php echo $badge['image']; ?></oml:image>
        </oml:badge>
    <?php }?>
</oml:badges>