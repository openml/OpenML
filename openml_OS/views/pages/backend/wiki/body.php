<div class="container-fluid topborder endless guidecontainer openmlsectioninfo">
  <div class="col-xs-12 col-md-10 col-md-offset-1 guidesection" id="mainpanel">

    <div class="tabbed-submenu" style="margin-top:10px;">
    <ul class="nav nav-pills pull-right">
      <?php foreach( $this->directories as $d ): ?>
        <li><a onclick="highlight()" id="detail-btn" class="btn btn-default btn-raised btn-info" href="backend/page/<?php echo $d; ?>"><?php echo str_replace("/","",text_neat_ucwords($d)); ?></a></li>
      <?php endforeach; ?>
    </ul>
    </div>

	<?php if($this->messages) { ?>
	<div class="alert alert-success" role="alert">
	<?php foreach( $this->messages as $m ):
                  echo $m.' <br>';
              endforeach; ?><br />
	</div>
	<?php } ?>
<div class="col-lg-12">
	<h1>Wiki manager</h1>
	<h3>Export database to wiki</h2>
	<p>Allows you to move descriptions from the database into the wiki manually. Throws error when attempting to overwrite an existing page. If you really want to overwrite it, first remove the wiki page from the (local) git repo (git rm).</p>
	<form method="post" action="">
		<input type="text" name="id" placeholder="Dataset id (or 'all' for all)"/>
		<input class="btn btn-primary" type="submit" value="Export to wiki"/>
        </form>
	<form method="post" action="">
		<input type="text" name="flow-id" placeholder="Flow id (or 'all' for all)"/>
		<input class="btn btn-primary" type="submit" value="Export to wiki"/>
        </form>

	<h3>Import wiki into database</h2>
	<p>Allows you to move descriptions from the wiki into the database manually. Will overwrite the description in the database.</p>
	<form method="post" action="">
		<input type="text" name="wiki-id" placeholder="Dataset id (or 'all' for all)"/>
		<input class="btn btn-primary" type="submit" value="Import into database"/>
        </form>
</div>

  </div>
</div>
