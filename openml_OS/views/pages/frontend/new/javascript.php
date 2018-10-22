<?php if($this->newtype=='task') { ?>

  function expdbDatasets() {
  	return <?php echo array_to_js_array( $this->datasets ); ?>;
  }

  function expdbDatasetVersion() {
  	return <?php echo array_to_js_array( $this->datasetVersion ); ?>;
  }

  function expdbDatasetVersionOriginal() {
  	return <?php echo array_to_js_array( $this->datasetVersionOriginal ); ?>;
  }

  function expdbDatasetIDs() {
  	return <?php echo array_to_js_array( $this->datasetIds ); ?>;
  }

  function expdbEvaluationMetrics() {
  	return <?php echo array_to_js_array( $this->evaluationMetrics ); ?>;
  }

  function expdbAlgorithms() {
  	return <?php echo array_to_js_array( $this->algorithms ); ?>;
  }

  function expdbImplementations() {
  	return <?php echo array_to_js_array( $this->implementations ); ?>;
  }

  function expdbTaskTypes() {
  	return <?php echo array_to_js_array( $this->taskTypes ); ?>;
  }

$(document).ready( function() {
  //check for change on the categories menu
  $('#form-task-type-tabs li a').eq($('#selectTaskType option:selected').attr('name')).tab('show');

  $('#selectTaskType').change(function() {
    console.log('hello');

    var id = $(this).val();
    console.log(id);
    $('a[href="#task-type-desc-'+id+'"]').tab('show');
    $('a[href="#task-type-'+id+'"]').tab('show');
  });

  <?php
    foreach( $this->task_types as $tt ):
      foreach( $tt->in as $io ):
        $template_search = json_decode( $io->template_search );
        $id = 'input_' . $tt->ttid . '-' . $io->name;
        if( $template_search && property_exists( $template_search, 'autocomplete' )  ):
          if( $template_search->autocomplete == 'commaSeparated' ):
            echo "makeCommaSeperatedAutoComplete( '#$id', $template_search->datasource );\n";
          else: // plain
            echo "makeAutoComplete( '#$id', $template_search->datasource );\n";
          endif;
        endif;
      endforeach;
    endforeach;
  ?>
});
<?php } ?>

/// SHARING DATA

$(document).ready(function() {
	$('.pop').popover();
	$('.selectpicker').selectpicker();
});

function prepareDatasetDescriptionXML(form, options) {
	var fields =  ['name','description','format','creator','contributor','collection_date','licence','default_target_attribute','row_id_attribute','version_label','citation','visibility','original_data_url','paper_url'];
	var implode = [false,false,false,true,true,false,false,false,false,false,false,false,false,false,false];

	var xml_header = '<oml:data_set_description xmlns:oml="http://openml.org/openml">'+"\n";
	var xml_footer = '</oml:data_set_description>'+"\n";
	var xml_content = prepareDescriptionXML('dataset',fields,implode);

	$('#generated_input_dataset_description').val(xml_header+xml_content+xml_footer);
}


function prepareDescriptionXML(type,fields,implode) {
	var xml_content = '';
	for(i = 0; i < fields.length; i+=1) {
		field = fields[i];
		field_value = $('#input_'+type+'_'+field).val().trim();
		if(field_value != '') {
			if(implode[i] == false) {
				xml_content += "\t"+'<oml:'+field+'>'+field_value+'</oml:'+field+'>'+"\n";
			} else {
				xml_current = field_value.split(',');
				$.each(xml_current, function() {
					xml_content += "\t"+'<oml:'+field+'>'+this.trim()+'</oml:'+field+'>'+"\n";
				});
			}
		}
	}

	return xml_content;
}

$(function() {

  $("#addbutton").click(function() {
    var eventXml = XmlCreate("<event/>");
    var $event   = $(eventXml);

    $event.attr("title", $("#titlefield").val());
    $event.attr("start", [$("#bmonth").val(), $("#bday").val(), $("#byear").val()].join(" "));

    if (parseInt($("#eyear").val()) > 0) {
      $event.attr("end", [$("#emonth").val(), $("#eday").val(), $("#eyear").val()].join(" "));
      $event.attr("isDuration", "true");
    } else {
      $event.attr("isDuration", "false");
    }

    $event.text( tinyMCE.activeEditor.getContent() );

    $("#outputtext").val( XmlSerialize(eventXml) );
  });

});

// helper function to create an XML DOM Document
function XmlCreate(xmlString) {
  var x;
  if (typeof DOMParser === "function") {
    var p = new DOMParser();
    x = p.parseFromString(xmlString,"text/xml");
  } else {
    x = new ActiveXObject("Microsoft.XMLDOM");
    x.async = false;
    x.loadXML(xmlString);
  }
  return x.documentElement;
}

// helper function to turn an XML DOM Document into a string
function XmlSerialize(xml) {
  var s;
  if (typeof XMLSerializer === "function") {
    var x = new XMLSerializer();
    s = x.serializeToString(xml);
  } else {
    s = xml.xml;
  }
  return s
}

function datasetFormSubmitted(responseText,statusText,xhr,formElement) {
	var errorCodes = new Array();
	errorCodes[131] = 'Please make sure that all mandatory (red) fields are filled in. Don\'t use spaces in name or version fields. (Error 131) ';
	errorCodes[135] = 'Please make sure that all mandatory (red) fields are filled in. Don\'t use spaces in name or version fields. (Error 135) ';
	errorCodes[137] = 'Please login first.';
	errorCodes[138] = 'Please login first.';
	formSubmitted(responseText,statusText,xhr,formElement,'Dataset',errorCodes);
}


function formSubmitted(responseText,statusText,xhr,formElement,type,errorCodes) {
	var message = '';
	var status = '';
	if($(responseText).find('id, oml\\:id').text().length) {
		message = type + ' uploaded successfully. <a href="d/' + $(responseText).find('id, oml\\:id').text() + '">View online.</a>';
		status = 'alert-success';
	} else {
		var errorcode = $(responseText).find('code, oml\\:code').text();
		var errormessage = $(responseText).find('message, oml\\:message').text();
		status = 'alert-warning';
		if(errorcode in errorCodes) {
			message = errorCodes[errorcode];
		} else {
			message = 'Errorcode ' + errorcode + ': ' + errormessage;
		}
	}
	$('#response'+type+'Txt').removeClass();
	$('#response'+type+'Txt').addClass('alert');
	$('#response'+type+'Txt').addClass(status);
	$('#response'+type+'Txt').html(message);
}

// New Data
	$(document)
		.on('change', '.btn-file :file', function() {
			var input = $(this),
			numFiles = input.get(0).files ? input.get(0).files.length : 1,
			label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
			input.trigger('fileselect', [numFiles, label]);
	});

	$(document).ready( function() {
		$('.btn-file :file').on('fileselect', function(event, numFiles, label) {

			var input = $(this).parents('.input-group').find(':text'),
				log = numFiles > 1 ? numFiles + ' files selected' : label;

			if( input.length ) {
				input.val(log);
			} else {
				if( log ) alert(log);
			}

		});
	});

  $(function() {
      $('#licence').change(function(){
          $('.licences').hide();
          $('#' + $(this).val()).show();
      });
  });

  $('#name').bind('input', function() {
      var cname = $(this).val();
      if(cname.length > 0 && cname.split(" ").length == 1){
         $(this).parent().removeClass('has-error');
         $(this).parent().addClass('has-success');
      } else {
         $(this).parent().removeClass('has-success');
         $(this).parent().addClass('has-error');
      }
  });
  $('#study_tag').bind('input', function() {
      var cname = $(this).val();
      if(cname.length > 0 && cname.split(" ").length == 1 && !cname.startsWith('s:')){
         $(this).parent().removeClass('has-error');
         $(this).parent().addClass('has-success');
      } else {
         $(this).parent().removeClass('has-success');
         $(this).parent().addClass('has-error');
      }
  });
  $('#description').bind('input', function() {
      var cname = $(this).val();
      if(cname.length > 0){
         $(this).parent().removeClass('has-error');
         $(this).parent().addClass('has-success');
      } else {
         $(this).parent().removeClass('has-success');
         $(this).parent().addClass('has-error');
      }
  });
  $('#format').bind('input', function() {
      var cname = $(this).val();
      if(cname.length > 0){
         $(this).parent().removeClass('has-error');
         $(this).parent().addClass('has-success');
      } else {
         $(this).parent().removeClass('has-success');
         $(this).parent().addClass('has-error');
      }
  });
  $('#study_title').bind('input', function() {
      var cname = $(this).val();
      if(cname.length > 0){
         $(this).parent().removeClass('has-error');
         $(this).parent().addClass('has-success');
      } else {
         $(this).parent().removeClass('has-success');
         $(this).parent().addClass('has-error');
      }
  });
  $('#study_alias').bind('input', function() {
      var cname = $(this).val();
      if(cname.length > 0){
         $(this).parent().removeClass('has-error');
         $(this).parent().addClass('has-success');
      } else {
         $(this).parent().removeClass('has-success');
         $(this).parent().addClass('has-error');
      }
  });

// New flow
var $input = $("#addparameter").children();

$(".addparam").on("click", function(){
   var $newField = $input.clone();
   // change what you need to do with the field here.
   $("#parameterbox").append($newField);
   $("#parname").attr({value: '', placeholder: 'Name (required)'});
   $("#parinfo").attr({value: '', placeholder: 'Description (required)'});
   $("#pardatatype").attr({value: 'Integer', placeholder: 'Data type'});
   $("#pardefault").attr({value: '', placeholder: 'Default value'});
   $("#parrange").attr({value: '', placeholder: 'Recommended range'});
});

// New task
<?php foreach( $this->task_types as $tt ):
        foreach( $tt->in as $io ):
          $id = 'input_' . $tt->ttid . '-' . $io->name;
          $template_search = json_decode( $io->template_search );
          if( $template_search && property_exists( $template_search, 'type' ) && $template_search->type == 'select' ): // make a dropdown
          if( $template_search->table == 'estimation_procedure' ): ?>

          $( "#dropdown_input_<?php echo $io->ttid;?>_<?php echo $io->name;?>" ).change(function () {

            var field = "<?php echo 'input_' . $tt->ttid . '-custom_testset'; ?>";
            $( "#dropdown_input_<?php echo $io->ttid;?>_<?php echo $io->name;?> option:selected" ).each(function() {
              if( $( this ).data('dbfield_custom_testset') == true ) {
                $('#'+field).val('');
                $('#'+field).prop('disabled', false);
                $('#'+field+'_formgroup').css("display", "block");
              } else {
                $('#'+field).val('');
                $('#'+field).prop('disabled', true);
                $('#'+field+'_formgroup').css("display", "none");
              }
            });
          }).change();
<?php endif; endif; endforeach; endforeach;?>
