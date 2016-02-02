<?php
	
	include("dbconnect.php");

	$email=$_GET['email'];
	$password=$_GET['password'];

	$data=0;

	$result=mysqli_query($con,"select count(*) from user where email='$email' ");
	$result=mysqli_fetch_array($result);
	if($result[0]>0)
	{
		$result=mysqli_query($con,"select password,id from user where email='$email' ");
		$result=mysqli_fetch_array($result);
		if($password==$result['password'])
		{
			$data=$result['id'];
		}
		else
		{
			$data=2;
		}
	}

	echo json_encode($data);


?>