<?php
$error_response = array(
  'error' => array(
    'code' => $code,
    'message' => $message
  )
);
if ($additional != null) {
  $error_response['error']['additional_information'] = $additional;
}
echo json_encode($error_response);
?>