$(function(){
	$('.cqvip-jianceorgan-paperreportpart').click(function(){
		var partid=$(this).attr('partid');
		var parthtml=$('#report_part_'+partid).html();
		$('.cqvip-jianceorgan-paperreportpartpanel').html(parthtml);
	});
});