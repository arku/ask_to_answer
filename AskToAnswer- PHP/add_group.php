<?php
	
	include("dbconnect.php");

	$name=$_GET['name'];

	$user=$_GET['uid'];
	
	mysqli_query($con,"insert into groups(name,uid) values ('$name','$user') ");

	$result=mysqli_query($con,"select count(*) from groups where name='$name' ");
	$result=mysqli_fetch_array($result);

	$result=$result[0];

	echo json_encode($result);

?>