<div class="container-fluid topborder">
  <div class="row">
   <div class="col-xs-12 col-sm-4 col-sm-offset-4 openmlsectioninfo">
     <div class="col-xs-12 col-md-10 col-md-offset-1" id="mainpanel">
       <div class="panel">
     <h1>Forgot password</h1>
    <?php echo form_open('password_forgot');?>
    <fieldset>
      <div class="form-group">
        <label for="email">Email adress:</label>
        <?php echo form_input($this->emailField);?>
      </div>
      <div class="form-group"><?php echo form_submit('submit', 'Submit');?></div>
    </fieldset>
    <?php echo form_close();?>
  </div>
</div>
  </div>
</div>
</div>
