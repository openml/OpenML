<div class="header-panel">
  <div class="col-sm-3 hidden-xs" style="padding-top:20px;">
        <h5>You'll love OpenML</h5>
        <ul class="sidenav nav collapse in" id="filterlist">
          <li><a><i class="fa fa-fw fa-check" style="color:green;"></i>Scalable, online collaboration</a></li>
          <li><a><i class="fa fa-fw fa-check" style="color:green;"></i>Analyse data better, together</a></li>
          <li><a><i class="fa fa-fw fa-check" style="color:green;"></i>Share results in full detail</a></li>
          <li><a><i class="fa fa-fw fa-check" style="color:green;"></i>Automate experimentation</a></li>
          <li><a><i class="fa fa-fw fa-wrench" style="color:orange;"></i>Connect to other scientists</a></li>
          <li><a><i class="fa fa-fw fa-wrench" style="color:orange;"></i>Share data within teams</a></li>
          <li><a><i class="fa fa-fw fa-wrench" style="color:orange;"></i>Organize your work online</a></li>
          <li><a><i class="fa fa-fw fa-wrench" style="color:orange;"></i>Build trust, track your impact</a></li>
        </ul>
  </div>

<div class="pages col-sm-9">
  <div class="col-sm-10">
    <div class="well page" style="position: relative; z-index:1030 !important">
    <h2>Join OpenML</h2>
    <p style="text-align:right;"><i class="fa fa-warning" style="color:red;"></i> By joining, you agree to the <a href="https://docs.openml.org/terms/">Honor Code and Terms of Use</a>.</p>
    <?php echo form_open_multipart("frontend/page/register");?>
    <fieldset>
      <div class="inputs">
        <?php echo form_input($this->emailField);?>
        <?php echo form_input($this->password);?>
        <?php echo form_input($this->password_confirm);?>
        <?php echo form_input($this->first_name);?>
        <?php echo form_input($this->last_name);?>
        <?php echo form_input($this->company);?>
        <?php echo form_input($this->country);?>
        <?php echo form_input($this->bio);?>
        <input type="text" readonly="" class="form-control floating-label" placeholder="Upload picture...">
        <?php echo form_input($this->image);?>
      </div>
      <div class="form-group"><?php echo form_submit('submit', 'Join', array(
        'class' => 'btn btn-default btn-material-green'));?></div>
    </fieldset>
    <?php echo form_close();?>
    </div>
  </div>
</div>
