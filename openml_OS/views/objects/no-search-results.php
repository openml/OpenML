<div class="noresult">
	<i class="fa fa-search fa-2x"></i><br><br>
	We could not find any <?php if($this->filtertype) {echo '<b>' . str_replace('data','data set',str_replace('_',' ',$this->filtertype)) . 's</b>';} else {echo 'resources';} ?> matching your query.<br />
	Widen your search or <a id="removefilters2"><i class="fa fa-lg fa-fw fa-trash-o"></i>remove all filters</a><br />
  <br /><br />

	<i class="fa fa-heart fa-2x" style="color: #d9534f;"></i><br><br>Start something great,

	<?php if($this->filtertype and $this->filtertype!='user'){
		echo '<a href="new/'. str_replace('_','',$this->filtertype) . '">';
		if($this->filtertype == 'data')
			echo 'upload new data</a>';
		else if($this->filtertype == 'flow' or $this->filtertype == 'measure')
			echo 'upload new flows</a>';
		else if($this->filtertype == 'run')
			echo 'submit new runs</a>';
		else if($this->filtertype == 'task')
			echo 'create new tasks</a>';
		else if($this->filtertype == 'task_type')
			echo 'create new task types</a>';
		else if($this->filtertype == 'task_type')
			echo 'create new task types</a>';
		}
		else if($this->filtertype=='user')
			echo 'invite people to join OpenML.';
		else
			echo 'upload new <a href="new/data">data</a>, <a href="new/flow">flows</a> and <a href="new/run">runs</a>.';
	?>
</div>
