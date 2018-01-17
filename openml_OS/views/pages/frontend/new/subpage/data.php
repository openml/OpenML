<form method="post" action="" enctype="multipart/form-data">

<div class="panel">
	<h1><a href="d"><i class="fa fa-database"></i></a> Add data</h1>
        <div id="responseDatasetTxt" class="<?php echo $this->responsetype; ?>"><?php echo $this->response; ?></div>
	      <div class="row">
		<div class="col-sm-6">
		  <div class="form-group">
		    <label class="control-label" for="sourcefile">Data files</label>
				<input type="text" readonly="" class="form-control floating-label" placeholder="Browse..." style="margin-top:10px;">
				<input type="file" id="dataset" name="dataset" multiple="">
		    <div class="col-sm-12 input-info">Or URL (not both)</div>
		    <input type="text" class="form-control" name="url" placeholder="URL where the data is hosted (e.g. data repository)" value="" />
		  </div>
		  <div class="form-group">
		    <div class="row">
		    <div class="col-xs-6 form-group has-error" id="field_name">
		    <label class="control-label" for="name">Name</label>
		    <input type="text" class="form-control" name="name" id="name" placeholder="A good name (no spaces)" value="<?php echo $this->input->post('name'); ?>"/>
		    </div>
		    <div class="col-xs-6">
		    <label class="control-label" for="version_label">Version</label>
		    <input type="text" class="form-control" name="version_label" placeholder="Version number, id, date,..." value="<?php echo $this->input->post('version'); ?>"/>
		    </div>
		    </div>
		  </div>
		  <div class="form-group has-error">
		    <label class="control-label" for="description">Description</label>
		    <textarea class="form-control" name="description" id="description" rows="5" placeholder="Short description (can still be edited online). Use #tags to label it. Describe where the data originates from, and whether it was processed in any way." value=""><?php echo $this->input->post('description'); ?></textarea>
		  </div>
		  <div class="form-group">
	      <label class="control-label" for="format">Data format</label> (<a href="https://github.com/openml/OpenML/wiki/Data-Formats">see specifications</a>)
				<select class="form-control" id="format" name="format">
			  	<option <?php if($this->input->post('format') == 'ARFF') echo "selected"; ?> value="ARFF">ARFF</option>
			  	<option <?php if($this->input->post('format') == 'Sparse_ARFF') echo "selected"; ?> value="Sparse_ARFF">Sparse_ARFF</option>
				</select>
	          </div>
                </div>
   		<div class="col-sm-6">
		  <div class="form-group">
		    <label class="control-label" for="creator">Author(s)</label>
		    <input type="text" class="form-control" name="creator" placeholder="Firstname Lastname, Firstname Lastname,..." value="<?php echo $this->input->post('creator'); ?>" />
		  </div>

		  <div class="form-group">
		  <label class="control-label" for="licence">Licence - <a href="http://creativecommons.org/licenses/?lang=en" target="_blank">Learn more</a></label>
			  <select class="form-control" id="licence" name="licence">
			  <option value="Public">Publicly available</option>
			  <option value="CC_BY">Attribution (CC BY)</option>
			  <option value="CC_BY-SA">Attribution-ShareAlike (CC BY-SA)</option>
			  <option value="CC_BY-ND">Attribution-NoDerivs (CC BY-ND)</option>
			  <option value="CC_BY-NC">Attribution-NonCommercial (CC BY-NC)</option>
			  <option value="CC_BY-NC-SA">Attribution-NonCommercial-ShareAlike (CC BY-NC-SA)</option>
			  <option value="CC_BY-NC-ND">Attribution-NonCommercial-NoDerivs (CC BY-NC-ND)</option>
			  <option value="CC0">Public Domain (CC0)</option>
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
			    <label class="control-label" for="citation">Citation requests</label>
			    <textarea class="form-control" rows="4" name="citation"  placeholder="How to reference this data in future work (e.g., publication, DOI)." value="<?php echo $this->input->post('citation'); ?>"></textarea>
		  </div>
		  <div class="form-group">
		  <label class="control-label" for="visibility">Who can view this data <span class="label label-danger">Under development</span></label>
			  <select class="form-control" name="visibility">
			  <option value="public">Everyone</option>
			  <option value="friends">All my friends</option>
			  <option value="private">Only me</option>
			</select>
	          </div>
		</div>


            </div>
	    <div class="row">
	      <div class="col-sm-12">
		  <h2>Additional information</h2>
	      <div class="row">
		<div class="col-sm-6">
		  <div class="form-group">
		    <label class="control-label" for="default_target_attribute">Target attribute(s)</label>
		    <input type="text" class="form-control" name="default_target_attribute" placeholder="For predictive problems: name of the attribute that is typically used as the target feature of this dataset. Comma-separate if multiple values." value="<?php echo $this->input->post('default_target_attribute'); ?>" onblur=""/>
		  </div>
		  <div class="form-group">
		    <label class="control-label" for="row_id_attribute">Row ID Attribute</label>
		    <input type="text" class="form-control" name="row_id_attribute" placeholder="If present, the name of the feature keeping row id's." value="<?php echo $this->input->post('row_id_attribute'); ?>" onblur=""/>
                  </div>
		  <div class="form-group">
		    <label class="control-label" for="row_id_attribute">Ignore Attributes</label>
		    <input type="text" class="form-control" name="ignore_attributes" placeholder="If present, the names of attributes that should be ignored when modelling the data (e.g. identifiers, indices)." value="<?php echo $this->input->post('ignore_attributes'); ?>" onblur=""/>
		  </div>
		</div>
		<div class="col-sm-6">
		  <div class="form-group">
		    <label class="control-label" for="original_data_url">Original data URL</label>
		    <input type="text" class="form-control" name="original_data_url" placeholder="For derived data, the URL to the original data set. E.g., http://openml.org/d/1" value="<?php echo $this->input->post('original_data_url'); ?>"/>
		  </div>
		  <div class="form-group">
		    <label class="control-label" for="contributor">Acknowledgements, contributors</label>
		    <input type="text" class="form-control" name="contributor"
			placeholder="Thanks to..." value="<?php echo $this->input->post('contributor'); ?>" />
		  </div>
		  <div class="form-group">
		    <label class="control-label" for="paper_url">Paper/preprint</label>
		    <input type="text" class="form-control" name="paper_url" placeholder="URL to paper or preprint about this data." value="<?php echo $this->input->post('paper_url'); ?>" />
		  </div>
		  <div class="form-group">
		    <label class="control-label" for="collection_date">Collection date</label>
		    <input type="text" class="form-control" name="collection_date" placeholder="When was this data collected?" value="<?php echo $this->input->post('collection_date'); ?>" />
		  </div>
		</div>
              </div>
		</div>
              </div>

            </div>
	    <div class="row">
	      <div class="col-sm-12">
		  <div class="form-group">
		    <input class="btn btn-primary" type="submit" name="submit" value="Submit"/>
		  </div>


              </div>
           </div>
	</form>

        <p><i>By submitting, you allow OpenML to index the data and link it to uploaded results. All rights remain with the original author(s) of the data. You confirm that you have read and agreed to the <a href="https://www.openml.org/guide/terms">OpenML Honor Code and Terms of Use</a>.</i></p>
</div> <!-- end container -->
