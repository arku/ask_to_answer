<?php

	include("dbconnect.php");
		
	$uid=$_GET['user_id'];

	$result=mysqli_query($con,"select name from groups where uid='$uid' ");


	$data=array();
	while($res=mysqli_fetch_array($result))
	{
		$array=array('name'=>$res['name']);
		array_push($data,$array);	
	}

	echo json_encode($data);
?>
