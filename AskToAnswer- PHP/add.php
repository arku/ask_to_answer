<?php

		include("dbconnect.php");

		$gid=$_GET['group'];
		$user=$_GET['uid'];

		mysqli_query($con,"insert into members(uid,gid) values('$user',$gid)");



	$result=mysqli_query($con,"select count(*) from members where uid='$user' and gid='$gid' ");
	$result=mysqli_fetch_array($result);

	$result=$result[0];

	echo json_encode($result);

?>
