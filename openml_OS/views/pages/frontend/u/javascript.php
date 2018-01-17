var activity = {
    startday:"",
    endday:"",
    nrdays:null,
    days:[],
    total:[],
    uploads:[],
    likes:[],
    downloads:[],
    totalscore:0,
    uploadscore:0,
    likescore:0,
    downloadscore:0
};

var reach = {
    startday:"",
    endday:"",
    nrdays:null,
    days:[],
    total:[],
    likes:[],
    downloads:[],
    totalscore:0,
    likescore:0,
    downloadscore:0
};

var impact = {
    startday:"",
    endday:"",
    nrdays:null,
    days:[],
    total:[],
    reuse:[],
    reach_reuse:[],
    recursive_impact:[],
    totalscore:0,
    reusescore:0,
    reachscore:0,
    impactscore:0
};


function redrawImpactChart(type){
    $("#Impact-chart").collapse('show');
    var values = [];
    if(type=="Impact"){
        values = impact.total;
    }else if(type=="Reuse"){
        values= impact.reuse;
        type ="Reuse";
    }else if(type=="Reach_re"){
        values = impact.reach_reuse;
        type ="Reach of reuse";
    }else if(type=="Impact_re"){
        values = impact.recursive_impact;
        type ="Impact of reuse";
    }
    if(values.length>0){
        $("#impactplot").empty();
        var chartOptions = {
            chart: {
                backgroundColor: null,
                height: 300
            },
            exporting: false,
            credits: false,
            title: {
                style: "font-size:100%",
                margin: 5,
                text: type+" gained from " + impact.startday + " to " + impact.endday
            },
            xAxis: {
                categories: impact.days
            },
            yAxis: {
                title: {
                    text: type
                },
                min: 0
            },
            legend: {enabled: false},
            series: [{
                    color: '#8E24AA',
                    name: type,
                    data: values
                }]
        };
        $("#impactplot").highcharts(chartOptions);
        $("#impacttoggle").css('visibility', 'visible');
        $("#impacttoggle").removeClass("fa-plus").addClass("fa-minus");
    }
}

function redrawReachChart(type){
    $("#Reach-chart").collapse('show');
    var values = [];
    if(type=="Reach"){
        values = reach.total;
    }else if(type=="Likes"){
        values = reach.likes;
    }else if(type=="Downloads"){
        values = reach.downloads;
    }
    if(values.length>0){
        $("#reachplot").empty();
        var chartOptions = {
            chart: {
                backgroundColor: null,
                height: 300
            },
            exporting: false,
            credits: false,
            title: {
                style: "font-size:100%",
                margin: 5,
                text: type+" gained from " + reach.startday + " to " + reach.endday
            },
            xAxis: {
                categories: reach.days
            },
            yAxis: {
                title: {
                    text: type
                },
                min: 0
            },
            legend: {enabled: false},
            series: [{
                    color: '#8E24AA',
                    name: type,
                    data: values
                }]
        };
        $("#reachplot").highcharts(chartOptions);
        $("#reachtoggle").css('visibility', 'visible');
        $("#reachtoggle").removeClass("fa-plus").addClass("fa-minus");
    }
}

function redrawActivityChart(type) {
    $("#Activity-chart").collapse('show');
    var values = [];
    var maxval = 0;
    if(type=="Activity"){
        values = activity.total;
        maxval = activity.totalscore+1;
    }else if(type=="Uploads"){
        values = activity.uploads;
        maxval = activity.uploadscore+1;
    }else if(type=="Likes"){
        values = activity.likes;
        maxval = activity.likescore+1;
    }else if(type=="Downloads"){
        values = activity.downloads;
        maxval = activity.downloadscore+1;
    }
    if(values.length>0){
        $("#activityplot").empty();
        var chartOptions = {
            chart: {
                type: 'heatmap',
                backgroundColor: null,
                height:  Math.max($('#Activity-chart').width()/7,200)
            },
            exporting: false,
            credits: false,
            title: {
                style: "font-size:100%",
                margin: 5,
                text: type+" from " + activity.startday + " to " + activity.endday
            },
            legend: {enabled: false},
            tooltip: false,
            xAxis: {
                visible: false,
                title: null,
                labels: {
                    enabled: false
                },
                tickLength: 0
            },
            yAxis: {
                categories: ['Sun', 'Sat', 'Fri', 'Thu', 'Wed', 'Tue', 'Mon'],
                gridLineWidth: 0,
                minorGridLineWidth: 0,
                labels: {
                    enabled: true
                },
                title: null,
                tickLength: 0
            },
            colorAxis: {
                type: 'logarithmic',
                min: 0.1,
                max: maxval,
                minColor: '#FFFFFF',
                maxColor: '#8E24AA'
            },
            tooltip: {
                formatter: function () {
                    return 'Amount of <span style="color:#8E24AA"> '+type+' </span> was <br><b>' +
                            Math.floor(this.point.value) + '</b> on ' + this.point.name + ' <br>';
                }
            },
            series: [{
                    turboThreshold: 100000,
                    name: type+' per day',
                    borderWidth: 1,
                    data: values,
                    dataLabels: {
                        enabled: false,
                        formatter: function () {
                            return this.point.name + '<br>' + Math.floor(this.point.value);
                        }
                    }
            }]
        };
        $("#activityplot").highcharts(chartOptions);
        $("#activitytoggle").css('visibility', 'visible');
        $("#activitytoggle").removeClass("fa-plus").addClass("fa-minus");
    }
}

$('#activitytoggle').click(function(){
    if($('#Activity-chart').hasClass("in")){
        $("#activitytoggle").removeClass("fa-minus").addClass("fa-plus");
    }else if($('#Activity-chart').hasClass("collapse")){
        $("#activitytoggle").removeClass("fa-plus").addClass("fa-minus");
    }
});

$('#reachtoggle').click(function(){
    if($('#Reach-chart').hasClass("in")){
        $("#reachtoggle").removeClass("fa-minus").addClass("fa-plus");
    }else if($('#Reach-chart').hasClass("collapse")){
        $("#reachtoggle").removeClass("fa-plus").addClass("fa-minus");
    }
});

$('#impacttoggle').click(function(){
    if($('#Impact-chart').hasClass("in")){
        $("#impacttoggle").removeClass("fa-minus").addClass("fa-plus");
    }else if($('#Reach-chart').hasClass("collapse")){
        $("#impacttoggle").removeClass("fa-plus").addClass("fa-minus");
    }
});

<?php
if ($this->ion_auth->logged_in()) {?>
getBadges();
$(function getActivity() {
    /*$("#ActivityThisYear").html('<i class="fa fa-spinner fa-pulse"/> |');
    $("#UploadsThisYear").html('<i class="fa fa-spinner fa-pulse"/> |');
    $("#LikesThisYear").html('<i class="fa fa-spinner fa-pulse"/> |');
    $("#DownloadsThisYear").html('<i class="fa fa-spinner fa-pulse"/> |');*/
    $.ajax({
        method:'GET',
        url:'<?php echo BASE_URL; ?>api_new/v1/json/gamification/activity/u/<?php echo $this->user_id; ?>/lastyear_perday',
        dataType:'json'
    }).done(function(resultdata){
        activity.startday = resultdata['activity-progress']['progresspart'][0]['date'];
        var istartday = 0;
        if(activity.startday.split(" ")[0]=="Monday"){
            istartday = 6;
        }else if(activity.startday.split(" ")[0]=="Tuesday"){
            istartday = 5;
        }else if(activity.startday.split(" ")[0]=="Wednesday"){
            istartday = 4;
        }else if(activity.startday.split(" ")[0]=="Thursday"){
            istartday = 3;
        }else if(activity.startday.split(" ")[0]=="Friday"){
            istartday = 2;
        }else if(activity.startday.split(" ")[0]=="Saturday"){
            istartday = 1;
        }
        console.log(istartday);
        $.each(resultdata['activity-progress']['progresspart'], function(i, item) {
            //console.log(item);
            activity.total.push({
                                x:Math.floor((i+istartday)/7),
                                y:(6-((i+istartday)%7)),
                                value: parseInt(item['activity']) + 0.000001,
                                name: item['date'].split(" ")[1]});
            activity.totalscore+= +item['activity'];
            activity.likes.push({
                                x:Math.floor((i+istartday)/7),
                                y:(6-((i+istartday)%7)),
                                value: parseInt(item['likes']) + 0.000001,
                                name: item['date'].split(" ")[1]});
            activity.likescore+= +item['likes'];
            activity.downloads.push({
                                x:Math.floor((i+istartday)/7),
                                y:(6-((i+istartday)%7)),
                                value: parseInt(item['downloads']) + 0.000001,
                                name: item['date'].split(" ")[1]});
            activity.downloadscore+= +item['downloads'];
            activity.uploads.push({
                                x:Math.floor((i+istartday)/7),
                                y:(6-((i+istartday)%7)),
                                value: parseInt(item['uploads']) + 0.000001,
                                name: item['date'].split(" ")[1]});
            activity.uploadscore+= +item['uploads'];
            activity.days.push(item['date'].split(" ")[1]);
        });
        activity.nrdays = activity.total.length;
        activity.endday = resultdata['activity-progress']['progresspart'][activity.nrdays-1]['date'];
        $("#ActivityThisYear").html(largeNumberFormat(activity.totalscore)+" |");
        $("#UploadsThisYear").html(largeNumberFormat(activity.uploadscore)+" |");
        $("#LikesThisYear").html(largeNumberFormat(activity.likescore)+" |");
        $("#DownloadsThisYear").html(largeNumberFormat(activity.downloadscore)+" |");
        redrawActivityChart('Activity');
    }).fail(function(resultdata, textStatus, errorThrown){
        console.log("Gamification API failed: "+textStatus+" ("+errorThrown+")");
    });

});

$(function getReach() {
    /*$("#ReachThisMonth").html('<i class="fa fa-spinner fa-pulse"/> |');
    $("#LikesReceivedThisMonth").html('<i class="fa fa-spinner fa-pulse"/> |');
    $("#DownloadsReceivedThisMonth").html('<i class="fa fa-spinner fa-pulse"/> |');*/
    $.ajax({
        method:'GET',
        url:'<?php echo BASE_URL ?>api_new/v1/json/gamification/reach/u/<?php echo $this->user_id ?>/lastyear_perday',
        dataType:'json'
    }).done(function(resultdata){
        reach.startday = resultdata['reach-progress']['progresspart'][0]['date'];
        $.each(resultdata['reach-progress']['progresspart'], function(i, item) {
            reach.total.push(reach.totalscore+parseInt(item['reach']));
            reach.totalscore+=parseInt(item['reach']);
            reach.likes.push(reach.likescore+parseInt(item['likes']));
            reach.likescore+=parseInt(item['likes']);
            reach.downloads.push(reach.downloadscore+parseInt(item['downloads']));
            reach.downloadscore+=parseInt(item['downloads']);
            reach.days.push(item['date'].split(" ")[1]);
        });
        reach.nrdays = reach.total.length;
        reach.endday = resultdata['reach-progress']['progresspart'][reach.nrdays-1]['date'];
        var alltimetotalscore = parseInt($("#ReachAllTime").attr("title"));
        var alltimelikescore = parseInt($("#LikesReceivedAllTime").attr("title"));
        var alltimedownloadscore = parseInt($("#DownloadsReceivedAllTime").attr("title"));
        for(var i=0; i<reach.nrdays; i++){
            reach.total[i]+=(alltimetotalscore-reach.totalscore);
            reach.likes[i]+=(alltimelikescore-reach.likescore);
            reach.downloads[i]+=(alltimedownloadscore-reach.downloadscore);
        }
        $("#ReachThisMonth").html(largeNumberFormat(reach.totalscore)+" |");
        $("#LikesReceivedThisMonth").html(largeNumberFormat(reach.likescore)+" |");
        $("#DownloadsReceivedThisMonth").html(largeNumberFormat(reach.downloadscore)+" |");
        redrawReachChart('Reach');
    }).fail(function(resultdata, textStatus, errorThrown){
        console.log("Gamification API failed: "+textStatus+" ("+errorThrown+")");
    });
});


$(function getImpact() {
    /*$("#ImpactThisMonth").html('<i class="fa fa-spinner fa-pulse"/> |');
    $("#ReuseThisMonth").html('<i class="fa fa-spinner fa-pulse"/> |');
    $("#ReachReuseThisMonth").html('<i class="fa fa-spinner fa-pulse"/> |');
    $("#ImpactReuseThisMonth").html('<i class="fa fa-spinner fa-pulse"/> |');*/
    $.ajax({
        method:'GET',
        url:'<?php echo BASE_URL ?>api_new/v1/json/gamification/impact/u/<?php echo $this->user_id ?>/lastyear_perday',
        dataType:'json'
    }).done(function(resultdata){
        impact.startday = resultdata['impact-progress']['progresspart'][0]['date'];
        $.each(resultdata['impact-progress']['progresspart'], function(i, item) {
            impact.total.push(impact.totalscore+parseInt(item['impact']));
            impact.totalscore+=parseInt(item['impact']);
            impact.reuse.push(impact.reusescore+parseInt(item['reuse']));
            impact.reusescore+=parseInt(item['reuse']);
            impact.reach_reuse.push(impact.reachscore+parseInt(item['reuse_reach']));
            impact.reachscore+=parseInt(item['reuse_reach']);
            impact.recursive_impact.push(impact.impactscore+parseInt(item['recursive_impact']));
            impact.impactscore+=parseInt(item['recursive_impact']);
            impact.days.push(item['date'].split(" ")[1]);
        });
        impact.nrdays = impact.total.length;
        impact.endday = resultdata['impact-progress']['progresspart'][impact.nrdays-1]['date'];
        var alltimetotalscore = parseInt($("#ImpactAllTime").attr("title"));
        var alltimereusescore = parseInt($("#ReuseAllTime").attr("title"));
        var alltimereachreusecore = parseInt($("#ReachReuseAllTime").attr("title"));
        var alltimeimpactreusecore = parseInt($("#ImpactReuseAllTime").attr("title"));
        for(var i=0; i<impact.nrdays; i++){
            impact.total[i]+=(alltimetotalscore-impact.totalscore);
            impact.reuse[i]+=(alltimereusescore-impact.reusescore);
            impact.reach_reuse[i]+=(alltimereachreusecore-impact.reachscore);
            impact.recursive_impact[i]+=(alltimeimpactreusecore-impact.impactscore);
        }
        $("#ImpactThisMonth").html(largeNumberFormat(impact.totalscore)+" |");
        $("#ReuseThisMonth").html(largeNumberFormat(impact.reusescore)+" |");
        $("#ReachReuseThisMonth").html(largeNumberFormat(impact.reachscore)+" |");
        $("#ImpactReuseThisMonth").html(largeNumberFormat(impact.impactscore)+" |");
        redrawImpactChart('Impact');
    }).fail(function(resultdata, textStatus, errorThrown){
        console.log("Gamification API failed: "+textStatus+" ("+errorThrown+")");
    });
});

function checkBadge(id){
    $.ajax({
            method:'GET',
            url:'<?php echo BASE_URL; ?>api_new/v1/json/badges/check/<?php echo $this->user_id; ?>/'+id
        }).done(function(resultdata){
            getBadges();
        }).fail(function(resultdata){
            console.log("Gamification API failed");
        });
}

function getBadges() {
    $.ajax({
        method:'GET',
        url:'<?php echo BASE_URL; ?>api_new/v1/json/badges/list/<?php echo $this->user_id; ?>'
    }).done(function(resultdata){
        var foundabadge = false;
        $('#badges').html("");
        $.each(resultdata['badges']['badge'], function(i,item){
            if(<?php echo $this->user_id; ?> == <?php echo $this->ion_auth->user()->row()->id; ?> || item['rank']>0){
                foundabadge = true;
                $('#badges').append('<div class="col-sm-3">'+
                                        '<img class="btn" src="'+item['image']+'" alt="'+item['name']+'" style="width:128px;height:128px;" onclick="checkBadge('+item['id']+')" title="Click to evaluate rank">'+
                                        '<br>'+
                                        '<h4 style="padding-top:5px">'+item['name']+'</h4>'+
                                        '<b>Current rank: </b>'+item['description_current']+
                                        '<br>'+
                                        '<b>Next rank: </b>'+item['description_next']+
                                    '</div>');
            }
        });
        if(!foundabadge){
            $('#badges').html('This user has no badges yet.');
        }
    }).fail(function(resultdata){
        console.log("Gamification API failed");
    });
}

function largeNumberFormat(number){
    if(number>1000000){
        return (number/1000000).toFixed(2)+"M";
    }else if(number>100000){
        return (number/1000).toFixed(0)+"K";
    }else if(number>10000){
        return (number/1000).toFixed(1)+"K";
    }else if(number>1000){
        return (number/1000).toFixed(2)+"K";
    }else{
        return number;
    }
}


$('#keyupgrade').submit(function() {
    var c = confirm("API key will be regenerated. ");
    return c; //you can just return c because it will be true or false
});



$('#keydegrade').submit(function() {
    var c = confirm("By doing this, API key can be used for read-operations only. Is this OK? ");
    return c; //you can just return c because it will be true or false
});

<?php } ?>
