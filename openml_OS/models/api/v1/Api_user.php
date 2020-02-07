<?php
class Api_user extends MY_Api_Model {

  protected $version = 'v1';

  function __construct() {
    parent::__construct();

    // load models
    $this->load->model('Author');
    $this->load->model('Dataset');
    $this->load->model('Implementation');
    $this->load->model('Run');
  }

  function bootstrap($format, $segments, $request_type, $user_id) {
    $this->outputFormat = $format;

  # http://test.openml.org/api/v1/user/list/uploader/1,2

    /**
     *@OA\Get(
     *	path="/user/list",
     *	tags={"user"},
     *	summary="List all users by user id",
     *	description="Returns an array with all user ids and names.",
     *	@OA\Parameter(
     *		name="api_key",
     *		in="query",
     *		type="string",
     *		description="API key to authenticate the user",
     *		required="false",
     *	),
     *	@OA\Response(
     *		response=200,
     *		description="A list of users",
     *		@OA\JsonContent(
     *			ref="#/components/schemas/UserList",
     *			example={
     *			  "users":{
     *			    "user":[
     *			      {
     *			        "id":"1",
     *			        "username":"janvanrijn@gmail.com"},
     *			      {
     *			        "id":"2",
     *			        "username":"joaquin.vanschoren@gmail.com"}
     *			      ]
     *			  }
     *			}
     *		),
     *	),
     *)
     */
    if (count($segments) >= 1 && $segments[0] == 'list') {
      array_shift($segments);
      $this->username_list($segments);
      return;
    }

    /*$getpost = array('get','post');

    if (count($segments) == 1 && is_numeric($segments[0]) && $request_type == 'delete') {
      $this->user_delete($segments[0]);
      return;
    }*/

    $this->returnError( 100, $this->version );
  }


  /*private function user_delete() {

    if( $this->user_has_admin_rights == false ) {
      $this->returnError( 104, $this->version );
      return;
    }

    $user = $this->Author->getById( $this->input->post( 'user_id' ) );
    if( $user == false ) {
      $this->returnError( 463, $this->version );
      return;
    }

    $datasets = $this->Dataset->getWhereSingle( 'uploader = ' . $user->id );

    if( $datasets ) {
      $this->returnError( 464, $this->version );
      return;
    }

    $flows = $this->Implementation->getWhereSingle( 'uploader = ' . $user->id );
    if( $flows ) {
      $this->returnError( 464, $this->version );
      return;
    }
    $runs = $this->Run->getWhereSingle( 'uploader = ' . $user->id );
    if( $runs ) {
      $this->returnError( 464, $this->version );
      return;
    }

    $result = $this->ion_auth->delete_user( $user->id );
    if( !$result ) {
      $this->returnError( 465, $this->version );
      return;
    }
    
    try {
      $this->elasticsearch->delete('user', $this->user_id);
    } catch (Exception $e) {
      $this->returnError(105, $this->version, $this->openmlGeneralErrorCode, get_class() . '.' . __FUNCTION__ . ':' . $e->getMessage());
      return;
    }
    
    $this->_xmlContents( 'user-delete', array( 'user' => $user ) );
  } */


  private function username_list($segs) {
    # pass uploader list to get username list
    $legal_filters = array('user_id');
    $query_string = array();
    for ($i = 0; $i < count($segs); $i += 2) {
      $query_string[$segs[$i]] = urldecode($segs[$i+1]);
      if (in_array($segs[$i], $legal_filters) == false) {
        $this->returnError(370, $this->version, $this->openmlGeneralErrorCode, 'Legal filter operators: ' . implode(',', $legal_filters) .'. Found illegal filter: ' . $segs[$i]);
        return;
      }
    }
    $user_id = element('user_id', $query_string);
    $users = $this->Author->getWhere('`id` IN (' . $user_id . ')');
    $this->xmlContents('user-name', $this->version, array('users' => $users));
  }

}



?>
