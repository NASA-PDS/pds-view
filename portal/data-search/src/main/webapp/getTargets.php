<?php
$servername = "localhost";
$dbname = "search";
$username = "registry";
$password = "kerAshyoc";

try {
  $conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);

  $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

   $rows = array();
   //$stmt = $conn->prepare("select distinct target from target_tools;");
   $stmt = $conn->prepare("select distinct target from target_tools;");
   $stmt->execute();

    $results=$stmt->fetchAll(PDO::FETCH_ASSOC);
    $json=json_encode($results);

    //$conn->close();

    echo $json;

  }
catch(PDOException $e)
  {
  echo "Connection failed: " . $e->getMessage();
  }


?>
