<oml:badges-info xmlns:oml="http://openml.org/openml">
    <?php foreach( $badges_info as $badge_info ): ?>
    <oml:badge-info>
      <oml:badge>
          <oml:badge-id><?php echo $badge_info->badge->id ?></oml:badge-id>
          <oml:badge-name><?php echo $badge_info->badge->name ?></oml:badge-name>
          <oml:badge-nrranks><?php echo $badge_info->badge->nrranks ?></oml:badge-nrranks>
          <oml:badge-descriptions>
            <?php foreach( $badge_info->badge->descriptions as $description ): ?>
            <oml:badge-description>
                <?php echo $description ?>                
            </oml:badge-description>
            <?php endforeach; ?>
          </oml:badge-descriptions>
          <oml:badge-images>
            <?php foreach( $badge_info->badge->images as $image ): ?>
            <oml:badge-image>
                <?php echo $image ?>                
            </oml:badge-image>
            <?php endforeach; ?>
          </oml:badge-images>
      </oml:badge>
      <oml:acquiredrank><?php echo $badge_info->acquiredrank; ?></oml:acquiredrank>
    </oml:badge-info>
    <?php endforeach; ?>
</oml:badges-info>
