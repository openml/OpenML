<oml:activity-progress xmlns:oml="http://openml.org/openml">
    <?php foreach($results as $res){ ?>
        <oml:progresspart>
            <oml:activity><?php echo $res['activity']; ?></oml:activity>
            <oml:uploads><?php echo $res['uploads']; ?></oml:uploads>
            <oml:likes><?php echo $res['likes']; ?></oml:likes>
            <oml:downloads><?php echo $res['downloads']; ?></oml:downloads>
            <oml:date><?php echo $res['date']; ?></oml:date>
        </oml:progresspart>
    <?php }?>
</oml:activity-progress>

