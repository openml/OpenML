<?php
function text_neat_ucwords($input) {
  return ucwords(str_replace('_',' ',$input));
}

function text_to_underscored($input) {
  return strtolower(str_replace(' ','_',safe($input)));
}

function punc2uc($input) { // punctuation to underscores
  return preg_replace('/[^a-zA-Z0-9]+/', '_', $input );
}

function safe( $unsafe ) {
  return preg_replace('/[^a-zA-Z0-9\s.,-_()]/', '', $unsafe );
}

function is_safe( $unsafe ) {
  return !preg_match('/[^a-zA-Z0-9\s.,-_()]/', $unsafe );
}

function is_natural_number($query) {
  return preg_match('/[0-9]+/', $query ) && !preg_match('/[^0-9]/', $query );
}

function is_cs_natural_numbers($query) { // is comma separated numeric
  $result = true;
  foreach(explode(',', $query) as $number) {
    $result &= is_natural_number($number);
  }
  return $result;
}

function is_natural_number_range( $query ) {
  return preg_match('/[0-9]+..[0-9]+/', $query ) && !preg_match('/[^0-9\s.]/', $query );
}

function is_json($string) {
  json_decode($string);
  return (json_last_error() == JSON_ERROR_NONE);
}

//function startsWith($haystack, $needle) {
//  // search backwards starting from haystack length characters from the end
//  return $needle === "" || strrpos($haystack, $needle, -strlen($haystack)) !== false;
//}


function range_string_to_array( $range_string ) {
  $result = array();  
  if( is_cs_natural_numbers( $range_string ) ) {
    $parts = explode( ',', $range_string );
    foreach( $parts as $part ) {
      if( is_natural_number_range( trim( $part ) ) ) {
        $range = explode( '..', $part );
        if( $range[0] < $range[1] ) {
          for( $i = trim($range[0]); $i <= trim($range[1]); ++$i ) {
            $result[] = $i;
          }
        }
      } elseif( is_natural_number( trim( $part ) ) ) {
        $result[] = trim($part);
      }
    }
    sort( $result, SORT_NUMERIC );
    $result = array_unique( $result, SORT_NUMERIC );
  }
  return $result;
}

function clean_cs_natural_numbers( $input, $input_delim = ',', $output_delim = ',' ) {
  $input = preg_replace('/[^0-9,]+/', '', $input);
  $data = explode($input_delim, $input);
  for( $i = count($data) - 1; $i >= 0; --$i ) {
    // important test: we want to filter out empty values (because of tailing comma)
    if( is_numeric( $data[$i] ) == false ) {
      unset( $data[$i] );
    }
  }
  return implode( $output_delim, $data );
}

function cutoff( $input, $length ) {
  if( strlen( $input ) < $length + 5 ) return $input;
  
  return substr( $input, 0, $length ) . ' ...';
}

function rand_string( $length, $type='' ) {
  $chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";  
  if( $type == 'C' ) {
    $chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
  } elseif( $type == 'N' ) {
    $chars = '0123456789';
  } elseif( $type == 'CN' ) {
    $chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
  }
  
  $str = '';
  
  $size = strlen( $chars );
  for( $i = 0; $i < $length; $i++ ) {
    $str .= $chars[ rand( 0, $size - 1 ) ];
  }

  return $str;
}

function arr2string( $array ) {
  if( is_array( $array ) ) {
    $res = array();
    foreach( $array as $item ) {
      $res[] = arr2string( $item );
    }
    return '['.implode( ',', $res ).']';
  } else {
    return $array;
  }
}

function startsWith($haystack, $needle) {
    // search backwards starting from haystack length characters from the end
    return $needle === "" || strrpos($haystack, $needle, -strlen($haystack)) !== FALSE;
}
function endsWith($haystack, $needle) {
    // search forward starting from end minus needle length characters
    return $needle === "" || (($temp = strlen($haystack) - strlen($needle)) >= 0 && strpos($haystack, $needle, $temp) !== FALSE);
}

?>
