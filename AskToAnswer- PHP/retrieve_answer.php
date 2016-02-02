<?php

	include("dbconnect.php");
		
	$qid=$_GET['question'];

	$result=mysqli_query($con,"select text from  answer where question='$qid'  ");


	$data=array();
	while($res=mysqli_fetch_array($result))
	{
		$array=array('text'=>$res['text']);
		array_push($data,$array);	
	}

	echo json_encode($data);
?>
