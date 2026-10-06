<?php

header("Content-Type: application/json");

require_once "db.php";

try {

    $stmt = $conn->prepare("
        SELECT
            id,
            name,
            location,
            open_hour,
            close_hours,
            details,
            offers_delivery,
            delivery_fee
        FROM Pharmacies
        ORDER BY name ASC
    ");

    $stmt->execute();

    $pharmacies = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode([
        "success" => true,
        "message" => "Pharmacies retrieved successfully",
        "pharmacies" => $pharmacies
    ]);

} catch (PDOException $e) {

    echo json_encode([
        "success" => false,
        "message" => "Unable to retrieve pharmacies"
    ]);
}
?>