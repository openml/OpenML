<?php

function check_uploaded_file( $file, $image_restriction = false, &$message = NULL ) {
  if( $file == false ) {
    $message = 'File meta-info is missing. ';
    return false;
  } else if( is_array($file) == false ) {
    $message = 'File meta-info is no array. ';
    return false;
  } else if( $file['error'] > 0 ) { // php error generated
    $message = 'Upload Error ' . $file['error'] . ': ' . upload_error_message( $file['error'] );
    return false;
  } else if( ! file_exists( $file['tmp_name'] ) ) { // file doesn't exist
    $message = 'File not present at expected location. ';
    return false;
  } else if( $image_restriction && substr( $file['type'], 0, 5 ) != 'image' ) {
    $message = 'File should be an image; MIME-type does not confirm this. ';
    return false;
  }
  
  return true;
}

function upload_error_message( $code ) {
  switch($code) {
    case '0': return 'There is no error, the file uploaded with success.';
    case '1': return 'The uploaded file exceeds the upload_max_filesize directive in php.ini.';
    case '2': return 'The uploaded file exceeds the MAX_FILE_SIZE directive that was specified in the HTML form.';
    case '3': return 'The uploaded file was only partially uploaded.';
    case '4': return 'No file was uploaded.';
    case '5': return 'Undefined error code.';
    case '6': return 'Missing a temporary folder. Introduced in PHP 4.3.10 and PHP 5.0.3.';
    case '7': return 'Failed to write file to disk. Introduced in PHP 5.1.0.';
    default : return 'Undefined error code';
  }
}

function getAvailableName( $folder, $name ) {
  $name = explode( '.', $name );
  if( count( $name ) > 1 ) {
    $extension = end( $name );
    unset( $name[count($name)-1] );
  } else { $extension = 'arff'; }
  
  $name = implode( '.', $name );
  
  $newName = $name . '.' . $extension;
  for( $i = 1; file_exists( $folder . $newName ); $i++ ) {
    $newName = $name . '_' . $i . '.' . $extension;
  }
  return $newName;
}

function subdirectory($needle,$haystack) {
  $dirs = explode('/',$haystack);
  $found = false;
  $result = '';
  foreach($dirs as $d) {
    if($found == true) {
      $result .= '/' . $d;
    } elseif($d == $needle) {
      $found = true;
    }
  }
  if(!$found) return false;
  else return substr($result,1);
}

function prepend_to_file($to_prepend, $file_path) {
  $cache_new = $to_prepend;
  
  $handle = fopen($file_path, "r+");
  $len = strlen($cache_new);
  $final_len = filesize($file_path) + $len;
  $cache_old = fread($handle, $len);
  rewind($handle);
  
  for ($i = 1; ftell($handle) < $final_len; ++$i) {
    fwrite($handle, $cache_new);
    $cache_new = $cache_old;
    $cache_old = fread($handle, $len);
    fseek($handle, $i * $len);
  }
  return true;
}

function resize_image_squared( $image_path, $target_width ) {
  scale_image( $image_path, $target_width, $target_width ); // target_width suffices. squared image.  
  list($width,$height) = getimagesize( $image_path );
  crop_image( $image_path, min( $width, $height ), min( $width, $height ) );
}

function scale_image( $image_path, $target_width, $target_height ) {
  $ci = &get_instance();
  
  $config['source_image'] = $image_path;
  $config['image_library'] = 'gd2';
  $config['quality'] = '100%';
  $config['create_thumb'] = false;
  $config['maintain_ratio'] = true;
  $config['width'] = $target_width;
  $config['height'] = $target_height;
  
  $ci->load->library('image_lib');
  $ci->image_lib->initialize($config);
  $ci->image_lib->resize();
  $ci->image_lib->clear();
}

function crop_image( $image_path, $target_width, $target_height ) {
  $ci = &get_instance();
  list($width,$height) = getimagesize( $image_path );
  $config['source_image'] = $image_path;
  $config['image_library'] = 'gd2';
  $config['quality'] = '100%';
  $config['create_thumb'] = false;
  $config['maintain_ratio'] = false;
  $config['width'] = $target_width;
  $config['height'] = $target_height;
  $config['x_axis'] = (int) ($width < $height ? 0 : ($width - $height) / 2);
  $config['y_axis'] = (int) ($height < $width ? 0 : ($height - $width) / 2);
  
  $ci->load->library('image_lib');
  $ci->image_lib->initialize($config);
  $ci->image_lib->crop();
  $ci->image_lib->clear();
}

/**
 * Checks if a directory exists, and creates it if allowed. 
 **/
function create_dir( $directory ) {
  $ci =& get_instance();
  $creation_rights = $ci->config->item('content_directories_create');
  $creation_mode = $ci->config->item('content_directories_mode');
  
  if(! file_exists( $directory ) ) {
    if( $creation_rights ) {
      $res = true;
      $all_dirs = explode( '/', $directory );
      $total_path = '/';
      foreach( $all_dirs as $dir ) {
        if( $dir != '' ) {
          $total_path .= $dir . '/';
          if(! file_exists( $total_path ) ) {
            $res = $res && mkdir( $total_path, $creation_mode );
          }
        }
      }
      
      if($res) {
        return true;
      } else {
        // TODO: log it
        return false;
      }
    } else {
      return false;
    }
  } else {
    // directory already exists
    return true;
  }
}

function fileRecordToUrl( $file ) {
  return BASE_URL . 'data/download/' . $file->id . '/' . $file->filename_original;
}

function getextension($filename) {
  if (strpos($filename,'.')) {
    return strtolower(substr($filename,strpos($filename,'.')+1));
  } else {
    return false;
  }
}
  
function readfile_chunked($filename,$retbytes=true) {
  $chunksize = 1*(1024*1024); // how many bytes per chunk
  $buffer = '';
  $cnt =0;
  
  $handle = fopen($filename, 'rb');
  if ($handle === false) {
    return false;
  }
  while (!feof($handle)) {
    $buffer = fread($handle, $chunksize);
    echo $buffer;
    @ob_flush();
    flush();
    if ($retbytes) {
      $cnt += strlen($buffer);
    }
  }
  $status = fclose($handle);
  if ($retbytes && $status) {
    return $cnt; // return num. bytes delivered like readfile() does.
  }
  return $status;
}

?>
