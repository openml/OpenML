<oml:reach-progress xmlns:oml="http://openml.org/openml">
    <?php foreach($results as $res){ ?>
        <oml:progresspart>
            <oml:reach><?php echo $res['reach']; ?></oml:reach>
            <oml:likes><?php echo $res['likes']; ?></oml:likes>
            <oml:downloads><?php echo $res['downloads']; ?></oml:downloads>
            <oml:date><?php echo $res['date']; ?></oml:date>
        </oml:progresspart>
    <?php }?>
</oml:reach-progress>