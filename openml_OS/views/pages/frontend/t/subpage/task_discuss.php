<?php
     foreach( $this->taskio as $r ):
	if($r['category'] != 'input') continue;
	if($r['type'] == 'Dataset'){
		$dataset = $r['dataset'];
		$dataset_id = $r['value'];
	}
     endforeach; ?>

		<?php if (!isset($this->record['task_id'])){
             echo "Sorry, this task is unknown.";
             die();
    } ?>

    <ul class="hotlinks">
		 <li><a href="<?php echo $_SERVER['REQUEST_URI']; ?>/json"><i class="fa fa-file-code-o fa-2x"></i></a><br>JSON</li>
		 <li><a href="api/v1/task/<?php echo $this->task_id;?>"><i class="fa fa-file-code-o fa-2x"></i></a><br>XML</li>
		</ul>
		<h1><i class="fa fa-trophy"></i> <?php echo $this->record['type_name']; ?> on <?php echo $dataset; ?></h1>
		<div class="datainfo">
<i class="fa fa-trophy"></i> Task <?php echo $this->task_id; ?> <i class="fa fa-flag"></i> <a href="t/type/<?php echo $this->record['type_id'];?>"><?php echo $this->record['type_name']; ?></a> <i class="fa fa-database"></i> <a href="d/<?php echo $dataset_id;?>"><?php echo $dataset; ?></a> <i class="fa fa-star"></i> <?php echo $this->record['runcount']; ?> runs submitted
</div>

    <h2>Discussions</h2>
    <div class="panel" id="disqus_thread">Loading discussions...</div>
    <script type="text/javascript">
        var disqus_shortname = 'openml'; // forum name
	var disqus_category_id = '3353607'; // Data category
	var disqus_title = '<?php echo $this->record['type_name']; ?> on <?php echo $dataset; ?>'; // Data name
	var disqus_url = 'http://openml.org/t/<?php echo $this->task_id; ?>'; // Data url

        /* * * DON'T EDIT BELOW THIS LINE * * */
        (function() {
            var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true;
            dsq.src = '//' + disqus_shortname + '.disqus.com/embed.js';
            (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);
        })();
    </script>
