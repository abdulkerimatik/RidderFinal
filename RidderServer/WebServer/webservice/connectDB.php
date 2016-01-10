<?php

try {
	$mongo = new MongoClient('mongodb://46.101.152.220:27017');
	$db = $mongo->selectDB('Ridder');
} catch(MongoConnectionException $e) {
	die('Baglanti Kurulamadi : ' . $e->getMessage());
}

?>