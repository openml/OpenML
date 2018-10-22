<?php
/*! \page datiqOSHelper datiqOS Helper
 *  \brief Helper functions based on DatiqOS and Code Igniter as a base functions.
 *  \author Marcin Polak / Datiq
 *  \version 1.0 beta
 *  \date    01.02.2010
 *  \warning Still in development mode.
 *  \details Library built for an alternative to native Code Igniter functions and speed up production by intuitive short naming convention.
 \section sec An example section
  This page contains the subsections \ref subsection1 and \ref subsection2.
  For more info see page \ref page2.
  \subsection subsection1 The fThe first subsection
  Text.The first subsection

  \subsection subsection2 The second subsection
  More text.

 */

function parseData($val)
{
	if(!$val) return false;
	$val = str_replace('|qm\'|', '?', $val);
	return $val;
}
 
function u() { return uri_string(); }
/*! \relates DatiqOS
 * returning whole user friendly url
 * @param	string	$name	name of url param eg. www.datiq.com/controller_name/page/1/page_param2/ -> page and page_params.
 */

function gu( $name ) { $dq = &get_instance(); return parseData(element($name,$dq->query_string)); }

/**
 * Set URL ( su )
 *
 * Redirection
 *
 * @access	public
 * @param	string	$url	url for redirection eg. controllername/page_1/.
 * @return	string
 */
function su( $url ){ if($url) redirect($url); }

/**
 * URL Encode ( u_enc )
 *
 * encodes the string + extra functions. this function might grow up.
 *
 * @access	public
 * @param	string	$url	string  for url encode
 * @return	string
 * @
 */
function u_enc( $url ){ $url = str_replace('\\\'','',$url);return urlencode($url); } // url encode

/**
 * Friendly URL ( fu )
 *
 * encodes the string to user friendly
 *
 * @access	public
 * @param	string	$url	string to convert to user friendly
 * @return	string
 * @
 */

function fu( $url ){ return url_title(trim(strtr($url,'()[]`!@#$%^&*_+={}:;",.<>/?','***************************')));}
/**
 * Friendly URL ( fu )
 *
 * encodes the string to user friendly
 *
 * @access	public
 * @param	string	$url	string to convert to user friendly
 * @return	string
 * @
 */

function p() { return $_POST; } // return $_POST[] variable // secure // parsed by Code Igniter
function gp( $name ) { $dq = &get_instance(); return $dq->input->post($name); } // get POST variable
function gpu( $name, $default = false ) { 
	$dq = &get_instance(); 
	$value = parseData(isset($_POST[$name])?$dq->input->post($name):element($name,$dq->query_string)); 
	return $value?$value:$default;
} // get REQUEST variable

function sp( $name, $value ) { $_POST[$name] = $value; } // set POST variable
//pa - pages
function pn(){ $dq = &get_instance(); return $dq->page_name; }
// SESSIONS -> s - session id, gs - get session, ss
function sid(){ $dq = &get_instance(); return $dq->session->userdata('session_id'); } // get session id
function gs( $name ){ $dq = &get_instance(); return $dq->session->userdata($name); } // get session
function ss( $name, $value ){ $dq = &get_instance(); return $dq->session->set_userdata($name,$value); } // set session
//
function ip(){$dq = &get_instance(); return $dq->session->userdata('ip_address');}
//
function js($js){ return '<s'.'cript type="text/javascript">'.$js.'</'.'script>'; }
function ljs($js){ return '<s'.'cript src="'.$js.'" type="text/javascript"></'.'script>'; } //load js
//
function gconfig($n ){ $dq = &get_instance(); return $dq->config->item( $n ); }
function sconfig($n, $v){ $dq = &get_instance(); return $dq->config->item($n, $v); }
//
function isAjax() {
	return (isset($_SERVER['HTTP_X_REQUESTED_WITH']) && $_SERVER['HTTP_X_REQUESTED_WITH']=="XMLHttpRequest");
}
// default loader
function _dq_load($name, $region = 'body', $folder = '_exports', $toVariable = false )
{
	$dq = &get_instance();
	//echo APPPATH.$folder.'/'.$name.EXT;	
	if(file_exists(APPPATH.$folder.'/'.$name.EXT))
	{	
		$oldViewPath = $dq->load->_ci_view_path;
		$dq->load->_ci_view_path = APPPATH.$folder.'/';
		$r = $dq->load->view($name,'',$toVariable);
		$dq->load->_ci_view_path = $oldViewPath;
		$dq->_cache[$name][$region][$folder] = $r;
		return $r;
	}
	return false;
}

function load_cron($name)
{
	return _dq_load('_cron_'.$name, $name,'_crons');
}

function l($text)
{
	$dq = &get_instance();
	return $dq->lang->line($text)?$dq->lang->line($text):$text;
}

function sm($message) //set message
{
	$dq = &get_instance();
	return $dq->session->set_flashdata('message', $message);
}

function gm() //get message
{
	$dq = &get_instance();
	$message = $dq->session->flashdata('message');
	if(isset($dq->error_message) && $message)
	{
		$message.= '<BR />';
	}
	if(isset($dq->error_message))$message.=$dq->error_message;
	return $message;
}

function sMakeRandomString( $length, $type='' ) {
	if($type=='cap')$chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	elseif($type=='low')$chars = "abcdefghijklmnopqrstuvwxyz";
	elseif($type=='num')$chars = "0123456789";
	else $chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	
	$rand = '';
	for ($i = 1; $i <= $length; $i++) {
		$num = rand(0, strlen($chars));
		$rand .= substr($chars, $num, 1);
	}
	return $rand;
}

function dq_highlight_phrase($str, $phrases = array(), $start = '<strong class="searchedWord">', $end = '</strong>')
{
	foreach($phrases as $keyword):
		$str = highlight_phrase($str,$keyword, $start,  $end);
	endforeach;
	return $str;
}

function formatBytes($b,$p = null) {
    if($b <1024) return $b.' B';
    $units = array("B","kB","MB","GB","TB","PB","EB","ZB","YB");
    $c=0;
    if(!$p && $p !== 0) {
        foreach($units as $k => $u) {
            if(($b / pow(1024,$k)) >= 1) {
                $r["bytes"] = $b / pow(1024,$k);
                $r["units"] = $u;
                $c++;
            }
        }
        return number_format($r["bytes"],2) . " " . $r["units"];
    } else {
        return number_format($b / pow(1024,$p)) . " " . $units[$p];
    }
}
?>
