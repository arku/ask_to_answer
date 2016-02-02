<?php
	
	include("dbconnect.php");

	$name=$_GET['name'];

	mysqli_query($con,"delete from groups where name='$name' ");

	$result=mysqli_query($con,"select count(*) from groups where name='$name' ");
	$result=mysqli_fetch_array($result);

	$result=$result[0];

	echo json_encode($result);


?>