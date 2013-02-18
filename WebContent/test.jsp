<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title></title>
    
  <style type="text/css" title="currentStyle">
			@import "bootstrap/css/demo_page.css";
			@import "bootstrap/css/demo_table.css";
		</style>  
<script type="text/javascript"
            src="http://www.datatables.net/release-datatables/media/js/jquery.js"></script>
<script type="text/javascript" language="javascript" src="http://www.datatables.net/release-datatables/media/js/jquery.dataTables.js"></script>
<script type="text/javascript" charset="utf-8">
var oTable;
 
/* Formating function for row details */
function fnFormatDetails ( nTr )
{
    var aData = oTable.fnGetData( nTr );
    var sOut = '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">';
    sOut += '<tr><td>Rendering engine:</td><td>'+aData[2]+' '+aData[5]+'</td></tr>';
    sOut += '<tr><td>Link to source:</td><td>Could provide a link here</td></tr>';
    sOut += '<tr><td>Extra info:</td><td>And any further details here (images etc)</td></tr>';
    sOut += '</table>';
     
    return sOut;
}
 
$(document).ready(function() {
    oTable = $('#example').dataTable( {
        "bProcessing": true,
        "bServerSide": true,
        "sAjaxSource": "data.js",
        "aoColumns": [
            { "sClass": "center", "bSortable": false },
            null,
            null,
            null,
            { "sClass": "center" },
            { "sClass": "center" }
        ],
        "aaSorting": [[1, 'asc']]
    } );
     
    $('#example tbody td img').live( 'click', function () {
        var nTr = $(this).parents('tr')[0];
        if ( oTable.fnIsOpen(nTr) )
        {
            /* This row is already open - close it */
            this.src = "../examples_support/details_open.png";
            oTable.fnClose( nTr );
        }
        else
        {
            /* Open this row */
            this.src = "../examples_support/details_close.png";
            oTable.fnOpen( nTr, fnFormatDetails(nTr), 'details' );
        }
    } );
} );
</script>
</head>
<body>

<table cellpadding="0" cellspacing="0" border="0" class="display dataTable" id="example" aria-describedby="example_info">
	<thead>
		<tr role="row"><th width="4%" class="sorting_disabled center" role="columnheader" rowspan="1" colspan="1" style="width: 4px;" aria-label=""></th><th width="25%" class="sorting_asc" role="columnheader" tabindex="0" aria-controls="example" rowspan="1" colspan="1" style="width: 172px;" aria-label="Rendering engine: activate to sort column ascending">Rendering engine</th><th width="20%" class="sorting" role="columnheader" tabindex="0" aria-controls="example" rowspan="1" colspan="1" style="width: 132px;" aria-label="Browser: activate to sort column ascending">Browser</th><th width="25%" class="sorting" role="columnheader" tabindex="0" aria-controls="example" rowspan="1" colspan="1" style="width: 172px;" aria-label="Platform(s): activate to sort column ascending">Platform(s)</th><th width="16%" class="center sorting" role="columnheader" tabindex="0" aria-controls="example" rowspan="1" colspan="1" style="width: 100px;" aria-label="Engine version: activate to sort column ascending">Engine version</th><th width="10%" class="center sorting" role="columnheader" tabindex="0" aria-controls="example" rowspan="1" colspan="1" style="width: 52px;" aria-label="CSS grade: activate to sort column ascending">CSS grade</th></tr>
	</thead>
	
	<tfoot>
		<tr><th class="center" rowspan="1" colspan="1"></th><th rowspan="1" colspan="1">Rendering engine</th><th rowspan="1" colspan="1">Browser</th><th rowspan="1" colspan="1">Platform(s)</th><th class="center" rowspan="1" colspan="1">Engine version</th><th class="center" rowspan="1" colspan="1">CSS grade</th></tr>
	</tfoot>
<tbody role="alert" aria-live="polite" aria-relevant="all"><tr class="odd"><td class="center"><img src="../examples_support/details_open.png"></td><td class=" sorting_1">Other browsers</td><td class="">All others</td><td class="">-</td><td class="center">-</td><td class="center">U</td></tr><tr class="even"><td class="center"><img src="../examples_support/details_open.png"></td><td class=" sorting_1">Trident</td><td class="">AOL browser (AOL desktop)</td><td class="">Win XP</td><td class="center">6</td><td class="center">A</td></tr><tr class="odd"><td class="center"><img src="../examples_support/details_open.png"></td><td class=" sorting_1">Gecko</td><td class="">Camino 1.0</td><td class="">OSX.2+</td><td class="center">1.8</td><td class="center">A</td></tr><tr class="even"><td class="center"><img src="../examples_support/details_open.png"></td><td class=" sorting_1">Gecko</td><td class="">Camino 1.5</td><td class="">OSX.3+</td><td class="center">1.8</td><td class="center">A</td></tr><tr class="odd"><td class="center"><img src="../examples_support/details_open.png"></td><td class=" sorting_1">Misc</td><td class="">Dillo 0.8</td><td class="">Embedded devices</td><td class="center">-</td><td class="center">X</td></tr><tr class="even"><td class="center"><img src="../examples_support/details_open.png"></td><td class=" sorting_1">Gecko</td><td class="">Epiphany 2.20</td><td class="">Gnome</td><td class="center">1.8</td><td class="center">A</td></tr><tr class="odd"><td class="center"><img src="../examples_support/details_open.png"></td><td class=" sorting_1">Gecko</td><td class="">Firefox 1.0</td><td class="">Win 98+ / OSX.2+</td><td class="center">1.7</td><td class="center">A</td></tr><tr class="even"><td class="center"><img src="../examples_support/details_open.png"></td><td class=" sorting_1">Gecko</td><td class="">Firefox 1.5</td><td class="">Win 98+ / OSX.2+</td><td class="center">1.8</td><td class="center">A</td></tr><tr class="odd"><td class="center"><img src="../examples_support/details_open.png"></td><td class=" sorting_1">Gecko</td><td class="">Firefox 2.0</td><td class="">Win 98+ / OSX.2+</td><td class="center">1.8</td><td class="center">A</td></tr><tr class="even"><td class="center"><img src="../examples_support/details_open.png"></td><td class=" sorting_1">Gecko</td><td class="">Firefox 3.0</td><td class="">Win 2k+ / OSX.3+</td><td class="center">1.9</td><td class="center">A</td></tr></tbody></table>

        

</body>
