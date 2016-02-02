<?php
	
	include("dbconnect.php");

	$text=$_GET['text'];

	mysqli_query($con,"delete from answer where text='$text' ");

	$result=mysqli_query($con,"select count(*) from answer where text='$text' ");
	$result=mysqli_fetch_array($result);

	$result=$result[0];

	echo json_encode($result);


?>