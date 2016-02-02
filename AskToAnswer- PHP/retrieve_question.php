<?php

	include("dbconnect.php");
		
	$gid=$_GET['group'];

	$result=mysqli_query($con,"select id,text from  question where groupid='$gid'  ");


	$data=array();
	while($res=mysqli_fetch_array($result))
	{
		$array=array('id'=>$res['id'],'text'=>$res['text']);
		array_push($data,$array);	
	}

	echo json_encode($data);
?>
