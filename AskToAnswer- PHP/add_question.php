<?php
	
	include("dbconnect.php");

	$text=$_GET['text'];
	$group=$_GET['group'];
	
	

	mysqli_query($con,"insert into question(text,groupid) values ('$text','$group') ");

	$result=mysqli_query($con,"select count(*) from question where text='$text' ");
	$result=mysqli_fetch_array($result);

	$result=$result[0];

	echo json_encode($result);

?>