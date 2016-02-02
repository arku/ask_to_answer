<?php
	
	include("dbconnect.php");

	$email=$_GET['email'];
	$name=$_GET['name'];
	$password=$_GET['password'];
	

	mysqli_query($con,"insert into user(email,name,password) values ('$email','$name','$password') ");

	$result=mysqli_query($con,"select count(*) from user where email='$email' ");
	$result=mysqli_fetch_array($result);

        
	$result=$result[0];
        if($result>0)
        {
           $data=array('reply'=>"yes");
        }
        else
        {
            $data=array('reply'=>"no");
         }


	echo json_encode($data);

?>