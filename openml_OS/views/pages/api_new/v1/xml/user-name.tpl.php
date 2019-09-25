<oml:users xmlns:oml="http://openml.org/openml">
    <?php foreach( $users as $u ): ?>
        <oml:user>
            <oml:id><?php echo $u->id ?></oml:id>
            <oml:username><?php echo $u->username ?></oml:username>
        </oml:user>
    <?php endforeach; ?>
</oml:users>
