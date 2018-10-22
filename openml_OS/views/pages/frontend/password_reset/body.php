<div class="container-fluid topborder">
  <div class="row">
   <div class="col-xs-12 col-sm-4 col-sm-offset-4 openmlsectioninfo">
    <h1>Reset password</h1>
    <?php echo form_open('password_reset?code='.$this->code);?>
    <fieldset>
      <div class="form-group">
        <label for="email">New password:</label>
        <?php echo form_input($this->new_password);?>
      </div>
      <div class="form-group">
        <label for="email">Confirm password:</label>
        <?php echo form_input($this->new_password_confirm);?>
      </div>
      
      <?php echo form_input($this->user_id);?>
	    <?php echo form_hidden($this->csrf); ?>
      
      <div class="form-group"><?php echo form_submit('submit', 'Submit');?></div>
    </fieldset>
    <?php echo form_close();?>
  </div>
</div>
</div>

