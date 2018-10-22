<?php

function generate_table( $keys, $data = false, $nullrecord = false ) {
	$string = "<thead>\n<tr>\n";
	foreach( $keys as $key => $value ) {
		$string .= "<th id='$key'>" . $value . "</th>\n";
	}
	$string .= "</tr>\n</thead>\n";
	
	if( $data != false ) {
		$string .= "<tbody>\n";
		foreach( $data as $d ) {
			$string .= "<tr>";
			foreach( $keys as $key => $value ) {
				$string .= "<td>" . ((property_exists($d,$key)) ? $d->{$key} : '') . "</td>";
			}
			$string .= "</tr>\n";
		}
		$string .= "</tbody>\n";
	} elseif( $nullrecord == true ) {
		$string .= "<tbody><tr>\n";
		$string .= '<td colspan="'.count($keys).'">Loading ... </td>';
		$string .= "</tr>\n</tbody>\n";
	}
	return $string;
}

function generate_headless_table( $keys, $data = false, $nullrecord = false ) {
        $string = "<tbody>\n";
	if( $data != false ) {
		foreach( $data as $d ) {
			$string .= "<tr>";
			foreach( $keys as $key => $value ) {
				$string .= "<td>" . $d->{$key} . $d->{$value} . "</td>";
			}
			$string .= "</tr>\n";
		}
		$string .= "</tbody></table>\n";
	} elseif( $nullrecord == true ) {
		$string .= "<tbody><tr>\n";
		$string .= '<td colspan="'.count($keys).'">Loading ... </td>';
		$string .= "</tr>\n</tbody>\n";
	}
	return $string;
}

function generate_table_one_record( $keys, $data = false ) {
	$string = "<thead>\n<tr><th>Name</th>\n<th>Value</th>\n</tr>\n</thead>\n";
	
	if( $data != false ) {
		$string .= "<tbody>\n";
		foreach( $keys as $key => $value ) {
			$string .= "<tr><td>".$value."</td><td>".$data->{$key}."</td></tr>\n";
		}
		$string .= "</tbody>\n";
	}
	return $string;
}

function simple_datatable($name,$css_picker) {
	$string = 'var '.$name.' = $("'.$css_picker.'").dataTable({"bPaginate": false,"bLengthChange": false,"bFilter": false,"bSort": false,"bInfo": false,"bAutoWidth": false});';
	//$string = 'var '.$name.' = $("'.$css_picker.'").dataTable({"sDom": \'T<"clear">lfrtip\',"oTableTools": {"sSwfPath": "http://localhost/openexpdb2/SWF/tableTools/copy_csv_xls_pdf.swf"}});';
	return $string;
}

function column_widths( $column_widths ) {
	$string = '';
	if(is_array($column_widths)) {
		$string .= '"aoColumns": ['."\n";
		for( $i = 0; $i < count($column_widths); $i++ ){
			$comma = ($i < count($column_widths) - 1) ? ',' : ''; // no tailing comma's, IE 
			$string .= '{ "sWidth": "'.$column_widths[$i].'%" }'.$comma."\n";
		}
		$string .= "],\n";
	} 
	return $string;
}
?>
