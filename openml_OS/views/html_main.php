<!doctype html>
<!--[if lt IE 7]> 
<html class="no-js lt-ie9 lt-ie8 lt-ie7" lang="en">
<![endif]-->
<!--[if IE 7]>    
<html class="no-js lt-ie9 lt-ie8" lang="en">
<![endif]-->
<!--[if IE 8]>    
<html class="no-js lt-ie9" lang="en">
<![endif]-->
<!--[if gt IE 8]><!--> 

<html class="no-js" lang="en" xmlns:og="http://ogp.me/ns#">
    <!--<![endif]-->
    <head>
        <base href="<?php echo BASE_URL; ?>" />
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <meta name="viewport" content="width=device-width">
        <title>OpenML</title>
        <meta name="description" content="OpenML: exploring machine learning better, together. An open science platform for machine learning.">
	<link href="http://www.openml.org/img/expdblogo2.png" rel="image_src" />
        <meta name="author" content="Joaquin Vanschoren">
        <meta property="og:title" content="OpenML"/>
        <meta property="og:url" content="http://www.openml.org"/>
        <meta property="og:image" content="http://www.openml.org/img/expdblogo2.png"/>
        <meta property="og:site_name" content="OpenML: exploring machine learning better, together."/>
        <meta property="og:description" content="OpenML: exploring machine learning better, together. An open science platform for machine learning."/>
        <meta property="og:type" content="Science"/>
        <meta name="viewport" content="width=device-width">
        <link rel="stylesheet" href="css/bootstrap.min.css">
        <link rel="stylesheet" href="css/expdb.css">
        <link rel="stylesheet" href="css/share.css">
        <link rel="stylesheet" href="css/docs.css">
        <link rel="shortcut icon" href="img/favicon.ico">
        <link href="//netdna.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.css" rel="stylesheet">
	<link href='http://fonts.googleapis.com/css?family=Roboto:400,100,300,500,700' rel='stylesheet' type='text/css'>
    </head>
    <body>
            <div class="modal-body">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	    <?php 
		if(false !== strpos($_SERVER['REQUEST_URI'],'/r/')){
			o('run'); 
		}
		elseif(false !== strpos($_SERVER['REQUEST_URI'],'/t/')){
			o('task'); 
		}

	    ?>
	    </div>            <!-- /modal-body -->
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>            <!-- /modal-footer --> 
        <script src="js/libs/bootstrap.min.js"></script>
        <script src="js/plugins.js"></script>
        <script src="js/application.js"></script>
     </body>
</html>

