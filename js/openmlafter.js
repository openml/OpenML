
<!-- User actions -->
$(document).ready(function() {
	$('.pop').popover();
	$('.selectpicker').selectpicker();
	let subtitle = document.getElementById('subtitle');
	if (subtitle != null) { document.title = document.title + ' ' + subtitle.innerHTML;}
});


      !function ($) {

        $(window).load(function(){

          // make code pretty
          window.prettyPrint && prettyPrint()

          // popover demo
          $("[data-toggle=popover]")
            .popover()
        });

      }(window.jQuery)

      $(window).load(function(){
        $('#popover').popover({
            trigger: 'click',
            placement: 'bottom',
            html: true,
            container: 'body',
            animation: 'false',
            content: function() { return $('#openmllinks').html(); }
        });
        $('#popover2').popover({
            trigger: 'click',
            placement: 'bottom',
            html: true,
            container: 'body',
            animation: 'false',
            content: $('#sociallinks')
        });
        $('#popover').on('shown.bs.popover', function () {
           $('.popover').css('left','inherit')
           $('.popover').css('right','10px')
           $('.arrow').css('left','inherit')
           $('.arrow').css('right','10px')
        });
        $('#popover2').on('shown.bs.popover', function () {
           $('.popover').css('left','inherit')
           $('.popover').css('right','10px')
           $('.arrow').css('left','inherit')
           $('.arrow').css('right','55px')
           $('#sociallinks').css('display','block')
        });
        $('#popover2').on('hide.bs.popover', function () {
           $('body').append($('#sociallinks'))
           $('#sociallinks').css('display','none')
        });
        $('.tip').tooltip();
        // This command is used to initialize some elements and make them work properly
        $.material.options.autofill = true;
        $.material.init();
      });

      $(document).click(function (e) {
          $('#popover').each(function () {
        if (!$(this).is(e.target) && $(this).has(e.target).length === 0 && $('.popover').has(e.target).length === 0) {
            if ($(this).data('bs.popover').tip().hasClass('in')) {
          $(this).popover('toggle');
            }
            return;
        }
          });
          $('#popover2').each(function () {
        if (!$(this).is(e.target) && $(this).has(e.target).length === 0 && $('.popover').has(e.target).length === 0) {
            if ($(this).data('bs.popover').tip().hasClass('in')) {
          $(this).popover('toggle');
            }
            return;
        }
          });
      });
      $('body').on('hidden.bs.modal', '.modal', function () {
        $(this).removeData('bs.modal');
      });
