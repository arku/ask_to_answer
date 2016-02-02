<?php
	
	include("dbconnect.php");

	$email=$_GET['email'];

	mysqli_query($con,"delete from user where email='$email' ");

	$result=mysqli_query($con,"select count(*) from user where email='$email' ");
	$result=mysqli_fetch_array($result);

	$result=$result[0];

	echo json_encode($result);


?>