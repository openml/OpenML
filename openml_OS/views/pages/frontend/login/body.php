<div id="login-dialog" class="modal fade" tabindex="-1">
 <div class="modal-dialog">
  <div class="modal-content">
   <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
    <h2 class="modal-title">Sign in</h2>
  </div>
  <div class="modal-body">
    <?php echo form_open("auth/login");?>
        <fieldset>
          <div class="inputs">
            <input type="hidden" name="location" value="<?php echo str_replace('OpenML/','',$_SERVER['REQUEST_URI']); ?>"/>
            <?php echo form_input($identity);?>
            <?php echo form_input($password);?>
          </div>
          <div class="form-group">
            <input class='btn btn-primary' type="submit" name="submitlogin" value="Sign in"  />
            <a href="register" class="btn pull-right">No account? Join OpenML</a>
            <a href="auth/forgot_password" class="btn pull-right">Forgot password</a>
          </div>
        </fieldset>
    <?php echo form_close();?>
  </div>
 </div>
</div>
</div>
