<div class="col-sm-3 hidden-xs" style="padding-top:20px;">
</div>

<div class="col-sm-9">
<div class="col-sm-10">
  <div class="panel">
    <h2>API Authentication</h2>
    <h4>API key</h4>
    <p><?php echo ($this->author && $this->author->session_hash ? $this->author->session_hash : 'No API key known.' );?></p>
    <form name="keyreset" method="post">
      <input type="submit" name="key-reset" class="btn btn-warning btn-raised" value="Reset API Key" />
    </form>

    <p style="color:#999"><i class="fa fa-fw fa-warning"></i>This key uniquely identifies you on OpenML. Keep it secret. Keep it safe.</p>

    <?php if ($this->ion_auth->in_group('members')) {?>
      <form id="keydegrade" method="post">
        <input type="submit" name="key-degrade" class="btn btn-warning btn-raised" value="Make read-only" />
      </form>
    <?php } else if ($this->ion_auth->in_group('readonly')) { ?>
      <form id="keyupgrade" method="post">
        <input type="submit" name="key-upgrade" class="btn btn-warning btn-raised" value="Make write key" />
      </form>
    <?php } ?>

    <?php if ($this->ion_auth->in_group('members')) {?>
      <p style="color:#999"><i class="fa fa-fw fa-warning"></i>This key can be used for read and write operations.</p>
    <?php } else if ($this->ion_auth->in_group('readonly')) { ?>
      <p style="color:#999"><i class="fa fa-fw fa-warning"></i>This key can be used for read operations only.</p>
    <?php } ?>

    <a class="btn btn-default" href="u/<?php echo $this->user_id;?>">Cancel</a>
 </div>
</div>
</div>
