<div class="row-fluid">
	<div class="topsearch">
		<!--<img width="35px" src="img/dots.png" style="margin:0px;"/>-->

		<form class="form-horizontal" method="post" action="search">
	<div class="form-group">
		<label class="col-md-2 control-label" for="openmlsearch">Search</label>
		<div class="col-md-10">
		  <input type="text" class="form-control" style="width: 80%; height: 30px; font-size: 13pt;" id="openmlsearch" name="searchterms" placeholder="" value="<?php if( $this->terms != false ) echo $this->terms; ?>" />
		  <button class="btn btn-primary btn-small" type="submit" style="height: 30px; vertical-align:middle; font-size: 8pt;"><span class="fa fa-search fa-lg"></span></button>
		</div>
		</div>
		</form>
	</div>
</div>
