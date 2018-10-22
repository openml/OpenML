<div class="panel">

	<h1><a href="d"><i class="fa fa-database"></i></a> Update <?php echo $this->data['name']; ?> (<?php echo $this->data['version']; ?>)</h1>
        <div id="responseDatasetTxt" class="<?php echo $this->responsetype; ?>"><?php echo $this->response; ?></div>
	<form method="post" action="" enctype="multipart/form-data">
        <input type="hidden" name="id" value="<?php echo $this->data['data_id']; ?>"/>
        <input type="hidden" name="name" value="<?php echo $this->data['name']; ?>"/>
        <input type="hidden" name="description" value="_"/>  <!-- will be ignored in update -->
        <input type="hidden" name="format" value="<?php echo $this->data['format']; ?>"/>
	      <div class="row">
		<div class="col-sm-6">
		  <h2>Core information</h2>
		  <div class="form-group">
		    <label class="control-label" for="sourcefile">Data files: <a href="<?php echo $this->data['url']; ?>">current file</a></label>
				<input type="text" readonly="" class="form-control floating-label" placeholder="Upload data file..." style="margin-top:10px;">
				<input type="file" id="dataset" name="dataset" multiple="">
		    <div class="col-sm-12 input-info">Or (not both)</div>
		    <input type="text" class="form-control" name="url" placeholder="URL where the data is hosted now (e.g. data repository)" value="<?php if(strpos(strtolower($this->data['url']), 'openml') == false) echo $this->data['url'];?>" />
		  </div>
		  <div class="form-group">
	            <label class="control-label" for="format">Version label</label>
		    <input type="text" class="form-control" name="version_label" id="format" placeholder="A version label for your reference" value="<?php echo $this->data['version_label']; ?>" onblur=""/>
	          </div>
		  <div class="form-group">
	            <label class="control-label" for="format">Data format</label>
		    <input type="text" class="form-control" name="format" id="format" placeholder="The data format (e.g. ARFF)" value="<?php echo $this->data['format']; ?>" onblur=""/>
	          </div>
		  <div class="form-group">
		  <label class="control-label" for="licence">Licence - <a href="http://creativecommons.org/licenses/?lang=en" target="_blank">Learn more</a></label>
			  <select class="form-control" id="licence" name="licence">
			  <option value="Public" <?php ($this->data['licence']=='Public' ? 'selected' : '') ?>>Publicly available</option>
			  <option value="CC_BY" <?php ($this->data['licence']=='CC_BY' ? 'selected' : '') ?>>Attribution (CC BY)</option>
			  <option value="CC_BY-SA" <?php ($this->data['licence']=='CC_BY-SA' ? 'selected' : '') ?>>Attribution-ShareAlike (CC BY-SA)</option>
			  <option value="CC_BY-ND" <?php ($this->data['licence']=='CC_BY-ND' ? 'selected' : '') ?>>Attribution-NoDerivs (CC BY-ND)</option>
			  <option value="CC_BY-NC" <?php ($this->data['licence']=='CC_BY-NC' ? 'selected' : '') ?>>Attribution-NonCommercial (CC BY-NC)</option>
			  <option value="CC_BY-NC-SA" <?php ($this->data['licence']=='CC_BY-NC-SA' ? 'selected' : '') ?>>Attribution-NonCommercial-ShareAlike (CC BY-NC-SA)</option>
			  <option value="CC_BY-NC-ND" <?php ($this->data['licence']=='CC_BY-NC-ND' ? 'selected' : '') ?>>Attribution-NonCommercial-NoDerivs (CC BY-NC-ND)</option>
			  <option value="CC0" <?php ($this->data['licence']=='CC0' ? 'selected' : '') ?>>Public Domain (CC0)</option>
			</select>
			<div id="Public" class="licences" style="display:block;">Mark a work that is free of known copyright restrictions. <a href="https://creativecommons.org/choose/mark/">More info</a></div>
			<div id="CC_BY" class="licences">Lets others distribute, remix, tweak, and build upon your work, even commercially, as long as they credit you for the original creation. <a href="http://creativecommons.org/licenses/by/4.0/" target="_blank">More info</a></div>
			<div id="CC_BY-SA" class="licences">Lets others remix, tweak, and build upon your work even for commercial purposes, as long as they credit you and license their new creations under the identical terms. <a href="http://creativecommons.org/licenses/by-sa/4.0/" target="_blank">More info</a></div>
			<div id="CC_BY-ND" class="licences">Allows for redistribution, commercial and non-commercial, as long as it is passed along unchanged and in whole, with credit to you. <a href="http://creativecommons.org/licenses/by-nd/4.0/" target="_blank">More info</a></div>
			<div id="CC_BY-NC" class="licences">Lets others remix, tweak, and build upon your work non-commercially, and although their new works must also acknowledge you and be non-commercial, they don’t have to license their derivative works on the same terms. <a href="http://creativecommons.org/licenses/by-nc/4.0" target="_blank">More info</a></div>
			<div id="CC_BY-NC-SA" class="licences">Lets others remix, tweak, and build upon your work non-commercially, as long as they credit you and license their new creations under the identical terms. <a href="http://creativecommons.org/licenses/by-nc-sa/4.0/" target="_blank">More info</a></div>
			<div id="CC_BY-NC-ND" class="licences">Allow others to download your works and share them with others as long as they credit you, but they can’t change them in any way or use them commercially. <a href="http://creativecommons.org/licenses/by-nc-nd/4.0" target="_blank">More info</a></div>
			<div id="CC0" class="licences">Waive all copyright and related rights. Others may freely build upon, enhance and reuse the works for any purposes without restriction under copyright or database law. <a href="http://creativecommons.org/about/cc0" target="_blank">More info</a></div>

	          </div>
		  <div class="form-group">
		  <label class="control-label" for="visibility">Who can view this data <span class="label label-danger">Under development</span></label>
			  <select class="form-control" name="visibility">
			  <option value="public" <?php ($this->data['visibility']=='public' ? 'selected' : '') ?>>Everyone</option>
			  <option value="friends" <?php ($this->data['visibility']=='friends' ? 'selected' : '') ?>>All my friends</option>
			  <option value="private" <?php ($this->data['visibility']=='private' ? 'selected' : '') ?>>Only me</option>
		  	  </select>
	          </div>
		</div>

	        <div class="col-sm-6">
		  <h2>Special features</h2>
		  <div class="form-group">
		    <label class="control-label" for="default_target_attribute">Target attribute(s) - for predictive tasks</label>
	            <select multiple class="form-control selectpicker" name="default_target_attribute">
		 	  <?php foreach( $this->data['features'] as $r ) {
				    echo '<option value="'.$r['name'].'" '.((array_key_exists('target',$r) and $r['target'] == '1') ? 'selected' : '').'>'.$r['name'].'</option>';} ?>
	  	    </select>
		  </div>
		  <div class="form-group">
		    <label class="control-label" for="row_id_attribute">Row Identifier - the name of the feature keeping row id's</label>
	            <select multiple class="form-control selectpicker" name="row_id_attribute">
		 	  <?php foreach( $this->data['features'] as $r ) {
				    echo '<option value="'.$r['name'].'" '.((array_key_exists('identifier',$r) and $r['identifier'] == '1') ? 'selected' : '').'>'.$r['name'].'</option>';} ?>
	  	    </select>
		  </div>
		  <div class="form-group">
		    <label class="control-label" for="ignore_attribute">Features to be ignored in predictive models - e.g. indices, identifiers,... </label>
	            <select multiple class="form-control selectpicker" name="ignore_attribute[]">
		 	  <?php foreach( $this->data['features'] as $r ) {
				    echo '<option value="'.$r['name'].'" '.((array_key_exists('ignore',$r) and $r['ignore'] == '1') ? 'selected' : '').'>'.$r['name'].'</option>';} ?>
	  	    </select>
		  </div>
		</div>
	</div>

	    <div class="row">
	      <div class="col-sm-12">
		  <h2>Submit</h2>
		  <div class="form-group has-error">
		    <input type="text" class="form-control" name="update_comment" id="comment" placeholder="State what changed and why this change was necessary" />
		  </div>

		  <div class="form-group">
		    <input class="btn btn-primary" type="submit" name="submit" value="Submit"/>
		  </div>


              </div>
	</form>
</div>
</div> <!-- end container -->
