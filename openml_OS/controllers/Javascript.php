<?php
class Javascript extends CI_Controller {

	function __construct() {
    parent::__construct();

		$this->load->model('Dataset');
		$this->load->model('Math_function');
		$this->load->model('Implementation');
		$this->load->model('Task_type');

		$this->controller = strtolower(get_class ($this));

		if($this->uri->rsegment(3)!=''){
			$this->page = $this->uri->rsegment(3);
		} else {
			$this->page = 'home';
		}
	}

	public function index() {
		$this->page = gu('page') ? gu('page') : 'data';
		$this->page();
	}

	public function page() {
		$d = loadpage($this->page,FALSE,'pre');
		$this->load->view('javascript_main');
	}
}
?>
