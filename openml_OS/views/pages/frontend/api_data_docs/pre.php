<?php

  $this->load_javascript = array('js/libs/jquery.slideto.min.js','js/libs/jquery.wiggle.min.js','js/libs/jquery.ba-bbq.min.js','js/libs/handlebars-2.0.0.js','js/libs/underscore-min.js','js/libs/backbone-min.js','js/libs/swagger-ui.js','js/libs/highlight.7.3.pack.js','js/libs/marked.js','js/libs/swagger-oauth.js');
  $this->load_css = array('css/standalone.css','css/api-explorer.css');

  $this->api_spec = BASE_URL ."downloads/swagger_data.json"; #default, should be latest version
  if(isset($this->subpage) and $this->subpage == 'v1'){
    $this->api_spec = BASE_URL ."downloads/swagger_data.json";
  }
?>
