  <div class="col-sm-3 hidden-xs" style="padding-top:20px;">
  </div>

<div class="col-sm-9">
  <div class="col-sm-10">
    <div class="panel">
      <h2>Edit Profile</h2>
      <?php echo form_open_multipart("frontend/page/u/". $this->user_id);?>
      <fieldset>
        <div class="inputs">
            <?php echo form_input($this->emailField);?>
            <?php echo form_input($this->password_old);?>
            <?php echo form_input($this->password_new);?>
            <?php echo form_input($this->password_confirm);?>
            <?php echo form_input($this->first_name);?>
            <?php echo form_input($this->last_name);?>
            <?php echo form_input($this->affiliation);?>
            <?php echo form_input($this->country);?>
            <?php echo form_textarea($this->bio);?>
            <?php echo form_label('Do you want OpenML to show you your activity and impact (altmetrics)?', 'gamification_setting_label'); ?><br>
            <?php echo form_radio($this->optin_gamification); echo form_label(' Opt-in');?><br>
            <?php echo form_radio($this->optout_gamification); echo form_label(' Opt-out');?><br>
            <img src="<?php echo htmlentities( authorImage( $this->user->image ) ); ?>" width="80" class="img-circle" style="padding: 10px;" alt="<?php echo $this->user->first_name . ' ' . $this->user->last_name; ?>" /><br/>
            <?php echo form_input($this->image);?>
          </div>
          <div class="form-group"><?php echo form_submit('submit', 'Update', array(
            'class' => 'btn btn-default btn-material-green'));?> <a class="btn btn-default" href="u/<?php echo $this->user_id;?>">Cancel</a></div>
        </fieldset>
        <?php echo form_close();?>
   </div>
 </div>
</div>
