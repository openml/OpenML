<html>
<body>
	<h1><?php echo sprintf(lang('email_activate_heading'), $identity);?></h1>
	<p><?php echo sprintf(lang('email_activate_subheading'), anchor('auth/activate/'. $id .'/'. $activation, lang('email_activate_link')));?></p>

    <p>(If the above link does not display correctly, please visit: <?php site_url('auth/activate/'. $id .'/'. $activation);?>)</p>
    
    <p>Kind regards,</p>
    <p>The OpenML Team</p>
</body>
</html>