<html>
<body>
	<h1><?php echo sprintf(lang('email_forgot_password_heading'), $identity);?></h1>
	<p><?php echo sprintf(lang('email_forgot_password_subheading'), anchor('auth/reset_password/'. $forgotten_password_code, lang('email_forgot_password_link')));?></p>

    <p>(If the above link does not display correctly, please visit: <?php echo site_url('auth/reset_password/'. $forgotten_password_code);?>)</p>
    
    <p>Kind regards,</p>
    <p>The OpenML Team</p>
</body>
</html>