<?php
$servername = "localhost";
$dbname = "search";
$username = "registry";
$password = "kerAshyoc";

if($_SERVER["REQUEST_METHOD"] == "POST") {
  $investigation = $_POST['investigation'];
  $type = $_POST['type'];

  $conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);

  $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

   $rows = array();

   if($type == "query"){
     $stmt = $conn->prepare("select investigation, title, description, url, rank from investigation_tools where investigation = :investigation and rank < 100 order by rank");
     $stmt->bindParam(':investigation', $investigation, PDO::PARAM_STR);
     $stmt->execute();
   }
   if($type == "additional"){
     $stmt = $conn->prepare("select investigation, title, description, url, rank from investigation_tools where investigation = :investigation and rank > 100 order by rank");
     $stmt->bindParam(':investigation', $investigation, PDO::PARAM_STR);
     $stmt->execute();
   }


    $results=$stmt->fetchAll(PDO::FETCH_ASSOC);
    $json=json_encode($results);

    //$conn->close();

    echo $json;

}

?>
