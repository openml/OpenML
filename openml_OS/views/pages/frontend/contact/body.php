<div class="container-fluid topborder endless guidecontainer openmlsectioninfo">
  <div class="col-xs-12 col-md-10 col-md-offset-1 guidesection" id="mainpanel">

    <h1>Get in touch</h1>
    <p>OpenML is a community effort and we'd love to get you involved one way or another.</p>

    <h2 class="contact-header">Suggestions, ideas, feedback</h2>
    <p>Feel free to contact us directly via email or social media:</p>

     <ul class="openml-contact-menu">
       <li><a href="mailto:openmachinelearning@gmail.com" target="_blank"><i class="fa fa-envelope-o fa-fw fa-lg"></i> </a></li>
       <li><a href="https://groups.google.com/forum/#!forum/openml" target="_blank"><i class="fa fa-users fa-fw fa-lg"></i> </a></li>
       <li><a href="https://plus.google.com/communities/105075769838900568763" target="_blank"><i class="fa fa-google-plus fa-fw fa-lg"></i></a></li>
       <li><a href="https://www.facebook.com/openml" target="_blank"><i class="fa fa-facebook fa-fw fa-lg"></i></a></li>
       <li><a href="https://twitter.com/intent/tweet?screen_name=open_ml&text=%23openml.org" data-related="open_ml"><i class="fa fa-twitter fa-fw fa-lg"></i></a></li>
     </ul>


    <h2 class="contact-header">Report issues, feature request</h2>
    <p>OpenML is an open source project on GitHub. To get help quickly, please <a href='https://docs.openml.org/developers/'>post issues in our GitHub issue trackers</a></p>.

    <h2 class="contact-header">Get involved</h2>
    <p>Want to help us make machine learning better? <a href='https://docs.openml.org/Contributing/'>Check out how to get involved!</a></p>.

    <h2 id="team-core">Our Team</h2>
    <p>OpenML is a community effort, and <a href="https://github.com/openml/OpenML/wiki/How-to-contribute">everybody is welcome to contribute</a>. Below are some of the core contributors, but also check out <a href="https://github.com/openml/">our GitHub page</a>.<br />

    <?php
     if( $this->team != false ) {
        foreach( $this->team as $t ) { ?>
    			<div class="col-md-4 head">
    				<img src="<?php echo htmlentities( authorImage( $t->image ) );?>" class="img-circle" width="70" /><br/><br/>
    				<span class="membername"><a href="u/<?php echo $t->id;?>"><?php echo $t->first_name.' '.$t->last_name; ?></a></span><br>
    				<span class="memberline"><?php echo $t->bio; ?></span>
    			</div>
    <?php }}?>

    <h2 id="terms-data">Get involved</h2>
    <i class="fa fa-heart" style="color:red;"></i> We always <a href='https://github.com/openml/OpenML/wiki/How-to-contribute'>love to welcome new contributers</a>.

 </div> <!-- end col-10 -->


</div>
<!-- end container -->
