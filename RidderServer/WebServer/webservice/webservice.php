<?php

header('Content-Type: application/json');

if (($_GET['method'] == 'register')) {
	$deviceId = $_GET['deviceId'];
	
	include('connectDB.php');
	$table = $db->selectCollection('User');
	$cursor = $table->find(array('DeviceId' => $deviceId));
	$num_docs = $cursor->count();
	
	if($num_docs <= 0) {
		$response["users"] = array();
		
		$obj = array( 
		  "DeviceId" => $deviceId
	    );
	    $table->insert($obj);
		array_push($response["users"], $obj);
		
		$mongo->close();

		echo json_encode($response);
	}

} else if($_GET['method'] == 'categories') {
	
	$deviceId = $_GET['deviceId'];
	
	include('connectDB.php');
	$table = $db->selectCollection('Category');
	$cursor = $table->find();
	$num_docs = $cursor->count();

	if($num_docs > 0) {

		$response["categories"] = array();
		while($cursor->hasNext())
		{
			$obj = $cursor->getNext();
			array_push($response["categories"], $obj);
		}
		
		$mongo->close();

		echo json_encode($response);
	}
} else if($_GET['method'] == 'usercategories') {
	
	$deviceId = $_GET['deviceId'];
	
	include('connectDB.php');

	$table = $db->selectCollection('UserCategory');
	$cursor = $table->find(array("DeviceId" => $deviceId));
	$num_docs = $cursor->count();

	$tablecat = $db->selectCollection('Category');
	$cursorcat = $tablecat->find();
	$num_docscat = $cursorcat->count();
	
	$catnamearr = array();
	$catarr = array();
	$usercatarr = array();
	while($cursorcat->hasNext())
	{
		$obj = $cursorcat->getNext();
		array_push($catarr, (string)$obj["_id"]);
		array_push($catnamearr, $obj["Name"]);
	}
	while($cursor->hasNext())
	{
		$obj = $cursor->getNext();
		array_push($usercatarr, $obj["CategoryId"]);
	}
	
	$catarr_length = count($catarr);
	$usercatarr_length = count($usercatarr);
	
	$response["categories"] = array();
	
	for($k = 0; $k < $catarr_length; $k++) {
		$usersubscribed = false;
		if(in_array($catarr[$k], $usercatarr)) {
			$usersubscribed = true;
		}
		$response["categories"][$k] = array('categoryId' => $catarr[$k], 'categoryName' => $catnamearr[$k], 'userSubscribed' => $usersubscribed);
	}

	$mongo->close();

	echo json_encode($response);

} else if($_GET['method'] == 'entries') {
	
	$deviceId = $_GET['deviceId'];
	
	include('connectDB.php');
	
	$userentryarr = array();
	$userentrytable = $db->selectCollection('UserEntryView');
	$userentrytablecursor = $userentrytable->find(array('DeviceId' => $deviceId));
	while($userentrytablecursor->hasNext())
	{	
		$obj = $userentrytablecursor->getNext();
		array_push($userentryarr, new MongoId($obj['EntryId']));
	}
	
	$usercatarr = array();
	$usercattable = $db->selectCollection('UserCategory');
	$usercattablecursor = $usercattable->find(array('DeviceId' => $deviceId));
	while($usercattablecursor->hasNext())
	{	
		$obj = $usercattablecursor->getNext();
                if($obj['CategoryId'] != "")
		array_push($usercatarr, new MongoId($obj['CategoryId']));
	}
		
	$catarr = array();
	$cattable = $db->selectCollection('Category');
	$cattablecursor = $cattable->find(array('_id' => array('$in' => $usercatarr)));
	while($cattablecursor->hasNext())
	{	
		$obj = $cattablecursor->getNext();
		array_push($catarr, $obj['Name']);
	}
				
	$entrytable1 = $db->selectCollection('Entry');
	$cursor1 = $entrytable1->find(array(
	'_id' => array('$nin' => $userentryarr),
	'Category' => array('$in' => $catarr)
	));
	$num_docs1 = $cursor1->count();
	
	$skip = rand(0,$num_docs1);
	
	$entrytable = $db->selectCollection('Entry');
	$cursor = $entrytable->find(array(
	'_id' => array('$nin' => $userentryarr),
	'Category' => array('$in' => $catarr)
	))->skip($skip)->limit(1);
	
	$num_docs = $cursor->count();
	if($num_docs > 0) {
		$response["entries"] = array();
		while($cursor->hasNext())
		{	
			$obj = $cursor->getNext();
			array_push($response["entries"], $obj);
		}
	
		$mongo->close();

		echo json_encode($response);
	} 
} else if($_GET['method'] == 'savecategories') {
	
	$deviceId = $_GET['deviceId'];
	$categories = $_GET['categories'];
		
	$category_array = explode("-",$categories);
	$category_length = count($category_array);
		
	include('connectDB.php');
	
	$table = $db->selectCollection('UserCategory');
	$cursor = $table->find(array('DeviceId' => $deviceId));
	$num_docs = $cursor->count();

	if($num_docs > 0) { // varsa sil ve tekrar ekle
		$response["usercategories"] = array();

		$varolanisil = array('DeviceId' => $deviceId);
		$table->remove($varolanisil);
		
		$obj = array();
		for($i = 0; $i < $category_length; $i++) {
			$object = array( 
			  "DeviceId" => $deviceId,
			  "CategoryId" => $category_array[$i]
			);
			$table->insert($object);
			
			array_push($obj, $object);
		}
				
		array_push($response["usercategories"], $obj);
		
		$mongo->close();

		echo json_encode($response);
	} else { // yoksa yeni ekle
		$response["usercategories"] = array();
		$obj = array();
		
		for($i = 0; $i < $category_length; $i++) {
			$object = array( 
			  "DeviceId" => $deviceId,
			  "CategoryId" => $category_array[$i]
			);
			$table->insert($object);
			
			array_push($obj, $object);
		}
				
		array_push($response["usercategories"], $obj);
		
		$mongo->close();

		echo json_encode($response);
		
	}
} else if($_GET['method'] == 'markentry') {
	
	$deviceId = $_GET['deviceId'];
	$entryId = $_GET['entryId'];
	$interested = $_GET['interested'];
	$liked = false;
	
	if($interested == "true") {
		$liked = true;
	} else {
		$liked = false;
	}
	
	include('connectDB.php');
	
	$table = $db->selectCollection('UserEntryView');

	$response["userentryview"] = array();
	
	$obj = array( 
	  "DeviceId" => $deviceId,
	  "EntryId" => $entryId,
	  "DateViewed" => date("d/m/Y"),
	  "Liked" => $liked
	);
	
	$table->insert($obj);
	
	array_push($response["userentryview"], $obj);
	
	$table1 = $db->selectCollection('Entry');
	$cursor1 = $table1->find(array('_id' => new MongoId($entryId)));
	$num_docs1 = $cursor1->count();
				
	$categoryName = "";
	if($cursor1->hasNext())
	{	
		$obj = $cursor1->getNext();
		$categoryName = $obj['Category'];
	}
	
	$table2 = $db->selectCollection('UserStatistics');
	$cursor2 = $table2->find(/*array('$and' => */array('DeviceId' => $deviceId, 'CategoryName' => $categoryName)/*)*/);
	$num_docs2 = $cursor2->count();

	$userstatisticstable = $db->selectCollection('UserStatistics');
	
	if($num_docs2 > 0) {
		while($cursor2->hasNext())
		{	
			$obj = $cursor2->getNext();
			$score = $obj['Score'];
		}
		if($liked) {
			$score++;
		} else {
			$score--;
		}
			
		$userstatisticstable->update(array("CategoryName" => $categoryName), 
		array("DeviceId" => $deviceId,
		  "CategoryName" => $categoryName,
		  "Score" => $score,
		));
	}
	else {
		$score = 0;
		if($liked) {
			$score++;
		} else {
			$score--;
		}
		
		$obj = array( 
		  "DeviceId" => $deviceId,
		  "CategoryName" => $categoryName,
		  "Score" => $score,
		);
		
		$userstatisticstable->insert($obj);
	}
	
	$mongo->close();

	echo json_encode($response);
}


?>
