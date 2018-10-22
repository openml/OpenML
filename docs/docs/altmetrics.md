To encourage open science, OpenML now includes a score system to track and reward scientific activity, reach and impact, and in the future will include further gamification features such as badges. Because the system is still experimental and very much in development, the details are subject to change. Below, the score system is described in more detailed followed by our rationale for this system for those interested. If anything is unclear or you have any feedback of the system do not hesitate to let us know.

## The scores

All scores are awarded to users and involve datasets, flows, tasks and runs, or knowledge pieces in short.
<div class="row">
    <div class="col-sm-4">
        <h4 class="activity">Activity <i class="fa fa-heartbeat"></i></h4>
        <p>Activity score is awarded to users for contributing to the knowledge base of OpenML. This includes uploading knowledge pieces, leaving likes and downloading new knowledge pieces. Uploads are rewarded strongest, with 3 activity, followed by likes, with 2 activity, and downloads are rewarded the least, with 1 activity.</p>
    </div>
    <div class="col-sm-4">
        <h4 class="reach">Reach <i class="fa fa-rss"></i></h4>
        <p>Reach score is awarded to knowledge pieces and by extension their uploaders for the expressed interest of other users. It is increased by 2 for every user that leaves a like on a knowledge piece and increased by 1 for every user that downloads it for the first time.</p>
    </div>
    <div class="col-sm-4">
        <h4 class="impact">Impact <i class="fa fa-bolt"></i></h4>
        <p>Impact score is awarded to knowledge pieces and by extension their uploaders for the reuse of these knowledge pieces. A dataset is reused if when it is used as input in a task while flows and tasks are reused in runs. 1 Impact is awarded for every reuse by a user that is not the uploader. Impact of a reused knowledge piece is further increased by half of the acquired reach and half of the acquired impact of a reuse, usually rounded down. So the impact of a dataset that is used in a single task with reach 10 and impact 5, is 8 (&lfloor;1+0.5*10+0.5*5 &rfloor;).</p>
    </div>
</div>

## The rationale

One of OpenML's core ideas is to create an open science environment for sharing and exploration of knowledge while getting credit for your work. The <span class="activity">activity</span> score serves the encouragement of sharing and exploration. <span class="reach">Reach</span> makes exploration easier (by finding well liked, and/or often downloaded knowledge pieces), while also providing a form of credit to the user. <span class="impact">Impact</span> is another form of credit that is closer in concept to citation scores.

## Where to find it
The number of likes and downloads as well as the reach and impact of knowledge pieces can be found on the top of their respective pages, for example the <a href='https://www.openml.org/d/61' target="_blank">Iris data set</a>. In the top right you will also find the new Like button next to the already familiar download button.

When searching for knowledge pieces on <a href='https://www.openml.org/search' target="_blank">the search page</a>, you will now be able to see the statistics mentioned above as well. In addition you can sort the search results on their downloads, likes, reach or impact.

On user profiles you will find all statistics relevant to that user, as well as graphs of their progress on the three scores.

## Badges
Badges are intended to provide discrete goals for users to aim for. They are only in a conceptual phase, depending on the community's reaction they will be further developed.

The badges a user has acquired can be found on their user profile below the score graphs. The currently implemented badges are:

<dt>
<dd><b>Clockwork Scientist <img src='../img/clockwork_scientist_1.svg' style="width:48px;height:48px;"></b></dd> For being active every day for a period of time.
<dd><b>Team Player <img src='../img/team_player_1.svg' style="width:48px;height:48px;"></b></dd> For collaborating with other users; reusing a knowledge piece of someone who has reused a knowledge piece of yours.
<dd><b>Good News Everyone <img src='../img/good_news_everyone_1.svg' style="width:48px;height:48px;"></b></dd> For achieving a high reach on singular knowledge piece you uploaded.
</dt>

## Downvotes
Although not part of the scores, downvotes have also been introduced. They are intended to indicate a flaw of a data set, flow, task or run that can be fixed, for example a missing description.

If you want to indicate something is wrong with a knowledge piece, click the number of issues statistic at the top the page. A panel will open where you either agree with an already raised issue anonymously or submit your own issue (not anonymously).

You can also sort search results by the number of downvotes, or issues on <a href='https://www.openml.org/search' target='_blank'>the search page</a>.

## Opting out
If you really do not like the altmetrics you can opt-out by changing the setting on your profile. This hides your scores and badges from other users and hides their scores and badges from you. You will still be able to see the number of likes, downloads and downvotes on knowledge pieces, and your likes, downloads and downvotes will still be counted.
