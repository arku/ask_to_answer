<?php
	
	include("dbconnect.php");

	$text=$_GET['text'];
	$question=$_GET['question'];
	$group=$_GET['group'];
	
	

	mysqli_query($con,"insert into answer(text,groupid,question) values ('$text','$group','$question') ");

	$result=mysqli_query($con,"select count(*) from answer where text='$text' ");
	$result=mysqli_fetch_array($result);

	$result=$result[0];

	echo json_encode($result);

?>