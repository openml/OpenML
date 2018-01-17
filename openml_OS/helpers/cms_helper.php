<?php
/**
 *  @function loadpage
 *  @param page (string): the name of the page to be displayed, e.g., search
 *  @param display (boolean): whether the page needs to be outputted directly, 
 *     or returned in return value
 *  @param section (string): which part of the page needs to be loaded, e.g.,
 *    body,pre,post
 */
function loadpage($page = false,$display = FALSE,$section = 'body') {
  if(!$page) return false;
  $dq = &get_instance();
  
  $view = 'pages/'.$dq->controller.'/'.$page.'/'.$section.'.php';
  if(!file_exists(APPPATH.'/views/'.$view)) {
    return false;
  }
  
  $r = $dq->load->view($view,'' ,$display?FALSE:TRUE);
  return $r?$r:true;
}

function body(){$dq = &get_instance(); return isset($dq->page_body)?$dq->page_body:$dq->page_body = loadpage($dq->page);}
function menu(){$dq = &get_instance(); return isset($dq->menu)?$dq->menu:$dq->menu = loadpage($dq->page,FALSE,'menu');}
function script(){$dq = &get_instance(); return isset($dq->script)?$dq->script:$dq->script = loadpage($dq->page,FALSE,'javascript');}
function script_loc(){$dq = &get_instance(); return 'openml_OS/views/pages/frontend/'.$dq->page.'/'.'javascript.php';}

function home() {return BASE_URL;}

function o( $name, $toVariable = false ) { // beta
  $dq = &get_instance();
  $r = $dq->load->view( 'objects/'.$name, '', $toVariable );
  return $r;
}

function subpage( $subpage ) {
  $dq = &get_instance();
  $view = 'pages/'.$dq->controller.'/'.$dq->page.'/subpage/'.$subpage.'.php';
  if(!file_exists(APPPATH.'/views/'.$view)) { return false; }
  $r = $dq->load->view($view,'' ,false);
}

function searchview( $type ) {
  $dq = &get_instance();
  $view = 'pages/'.$dq->controller.'/'.'/search/'.$subpage.'.php';
  if(!file_exists(APPPATH.'/views/'.$view)) { return false; }
  $r = $dq->load->view($view,'' ,false);
}
?>
