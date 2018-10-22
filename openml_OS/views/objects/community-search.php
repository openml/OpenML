<form class="form-inline" method="post" action="frontend/page/community_search">
  <input type="text" class="form-control" style="width: 50%; height: 30px; font-size: 13pt;" id="openmlsearch" name="searchterms" placeholder="Ask a question or search the discussions." value="<?php if( isset( $terms ) ) echo $terms; ?>" />
  <button class="btn btn-primary btn-small" type="submit" style="height: 30px; vertical-align:top; font-size: 8pt;"><i class="fa fa-search fa-lg"></i></button>
</form>

<script type="text/javascript">
document.getElementById('openmlsearch').focus()
</script>
