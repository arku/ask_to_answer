<?php

	include("dbconnect.php");
		
	$gid=$_GET['group'];

	$result=mysqli_query($con,"select id,name from  user  where id in (select uid from members where gid='$gid') ");


	$data=array();
	while($res=mysqli_fetch_array($result))
	{
		$array=array('id'=>$res['id'],'name'=>$res['name']);
		array_push($data,$array);	
	}

	echo json_encode($data);
?>
