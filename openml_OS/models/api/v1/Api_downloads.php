<?php

class Api_downloads extends MY_Api_Model {

    protected $version = 'v1';

    function __construct() {
        parent::__construct();

        // load models
        $this->load->model('Download');
        $this->load->model('KnowledgePiece');
    }

    function bootstrap($format, $segments, $request_type, $user_id) {
        $this->outputFormat = $format;
        $getpost = array('get', 'post');

        if ((count($segments) == 1 && $segments[0] == 'list') || (count($segments) == 3 && $segments[0] == 'any' && $segments[1] == 'any' && $segments[2] == 'any')) {
            $this->downloads_list();
            return;
        }

        if ((count($segments) == 1 && is_numeric($segments[0]))) {
            $this->downloads_of_user($segments[0]);
            return;
        }

        if (count($segments) == 3 && in_array($request_type, $getpost)) {
            $this->downloads_get($segments[0], $segments[1], $segments[2]);
            return;
        }

        if (count($segments) == 2 && is_numeric($segments[1]) && $request_type == 'delete') {
            $this->download_delete($segments[0], $segments[1]);
            return;
        }

        if (count($segments) == 2 && is_numeric($segments[1]) && $request_type == 'post') {
            $this->download_do($segments[0], $segments[1]);
            return;
        }

        $this->returnError(100, $this->version, 450, implode($segments));
    }

    private function downloads_list() {
        $downloads_res = $this->Download->get();
        if (is_array($downloads_res) == false) {
            //$this->returnError(801, $this->version);
            //return;
            $downloads_res = [];
        }

        $this->xmlContents('downloads', $this->version, array('downloads' => $downloads_res));
    }

    private function downloads_of_user($user_id) {
        $downloads_res = $this->Download->getDownloadsByUser($user_id);
        if (is_array($downloads_res) == false) {
            //$this->returnError(802, $this->version);
            //return;
            $downloads_res = [];
        }
        $this->xmlContents('downloads', $this->version, array('downloads' => $downloads_res));
    }

    private function downloads_get($user_id, $knowledge_type, $knowledge_id) {
        if ($user_id == 'any' && $knowledge_id == 'any') {
            $downloads_res = $this->Download->getDownloadsByType($knowledge_type);
        } else if ($user_id == 'any') {
            $downloads_res = $this->Download->getDownloadsByKnowledgePiece($knowledge_type, $knowledge_id);
        } else if ($knowledge_id == 'any') {
            $downloads_res = $this->Download->getDownloadsByUserAndType($user_id, $knowledge_type);
        } else {
            $downloads_res = $this->Download->getByIds($user_id, $knowledge_type, $knowledge_id);
        }
        if (is_array($downloads_res) == false || count($downloads_res) == 0) {
            //$this->returnError(803, $this->version);
            //return;            
            $downloads_res = [];
        }
        $this->xmlContents('downloads', $this->version, array('downloads' => $downloads_res));
    }

    private function download_delete($knowledge_type, $knowledge_id) {
        $knowledge_name = $knowledge_type === 'd' ? "data" : ($knowledge_type === 'f' ? "flow" : ($knowledge_type === 't' ? "task" : ($knowledge_type === 'r' ? "run" : "")));
        if ($knowledge_name === "") {
            $this->returnError(811, $this->version);
            return;
        } else {
            $download = $this->Download->getByIds($this->user_id, $knowledge_type, $knowledge_id);

            $download_id = $download[0]->did;
            if ($download == false) {
                $this->returnError(803, $this->version);
                return;
            }

            if ($download[0]->user_id != $this->user_id) {
                $this->returnError(821, $this->version);
                return;
            }

            $result = $this->Download->delete($download_id);

            if ($result == false) {
                $this->returnError(804, $this->version);
                return;
            }
            
            $this->elasticsearch->delete('download', $download_id);

            // update counters
            $this->elasticsearch->index($knowledge_name, $knowledge_id);
            $this->elasticsearch->index('user', $this->user_id);
            
            $this->xmlContents('download', $this->version, $download[0]);
        }
    }

    private function download_do($knowledge_type, $knowledge_id) {
        $knowledge_name = $knowledge_type === 'd' ? "data" : ($knowledge_type === 'f' ? "flow" : ($knowledge_type === 't' ? "task" : ($knowledge_type === 'r' ? "run" : "")));
        if($this->KnowledgePiece->getUploader($knowledge_type, $knowledge_id) == $this->user_id){
            $this->returnError(822, $this->version);
        }
        if ($knowledge_name === "") {
            $this->returnError(811, $this->version);
            return;
        } else {
            $did = $this->Download->insertOrIncrement($this->user_id, $knowledge_type, $knowledge_id);

            if (!$did) {
                $this->returnError(805, $this->version);
                return;
            }

            // update elastic search index.
            $this->elasticsearch->index('download', $did);


            // update counters
            $this->elasticsearch->index($knowledge_name, $knowledge_id);
            $this->elasticsearch->index('user', $this->user_id);

            $download = $this->Download->getById($did);
            // create
            $this->xmlContents('download', $this->version, $download);
        }
    }

}

?>
