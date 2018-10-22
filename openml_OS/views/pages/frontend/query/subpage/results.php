<div class="clearfix"></div>

<div class="row">
	<div class="tab-content col-md-12" id="resulstab_content" style="overflow: visible;">

		<div class="tab-pane" id="scatterplottab">
			<div id="topmenuScatter"></div>
			<p id="scattermain"></p>
		</div>

		<div class="tab-pane active" id="tabletab">


			<div class='topmenu'>
 <div class="btn-toolbar" style="margin: 0;">

              <div class="btn-group" style="float:right;">
<form class="form-inline pull-right" id="exportResultForm" method="post" action="frontend/result_output" style="float:right">
					<input type="hidden" name="name" id="exportResultName" />
					<input type="hidden" name="type" id="exportResultType" />
					<input type="hidden" name="data" id="exportResultData" />
<div class="btn-group" style="float:right">

<input type="text" name="name" value="MyFile.csv" class="col-md-4 input" style="height: 38px;width:160px;margin-right:-2px; border:none">

<a type="submit" class="btn btn-default dropdown-toggle" data-toggle="dropdown" href="#">Export <col-md-* class="caret"></col-md-*></a>
							<ul class="dropdown-menu">
								<li><a onclick="exportResult('csv');">CSV</a></li>
							</ul>
</div>
</form>
				<output></output>
              </div>
              <div class="btn-group" style="float:right">
				<button id="crosstabulateBtn" data-loading-text="Calculating..." autocomplete="off"  class="btn btn-default" onclick="toggleResultTables();" >
					Crosstabulate
				</button>
              </div>
            </div>
			</div>
			<div id='tableinfo'></div>
			<div style="clear: both;"></div>
			<div id="tablemain"></div>
		</div>

		<div class="tab-pane" id="linetab">
			<div id="topmenuLine"></div>
			<p id="linemain"></p>
		</div>
	</div>
</div>
