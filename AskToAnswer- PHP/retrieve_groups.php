<?php

	include("dbconnect.php");
		
	$uid=$_GET['user_id'];

	$result=mysqli_query($con,"select name from groups where uid<>'$uid' ");


	$data=array();
	while($res=mysqli_fetch_array($result))
	{
		$array=array('name'=>$res['name']);
		array_push($data,$array);	
	}

	$result=mysqli_query($con,"select distinct gid from members where uid<>'$uid' ");
	while($res=mysqli_fetch_array($result))
	{
		$gid=$res['gid'];
		$r=mysqli_query($con,"select name from groups where id='$gid' ");
		$r=mysqli_fetch_array($r);
		$r=$r['name'];

		$array=array('name'=>$r);
		array_push($data,$array);	

	}


	echo json_encode($data);
?>
