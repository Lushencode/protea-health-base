<?php

header("Content-Type: application/json");

require_once "db.php";

$medicationName = trim($_GET["medication_name"] ?? "");

if ($medicationName === "") {

    echo json_encode([
        "success" => false,
        "message" => "Medication name is required",
        "pharmacies" => []
    ]);

    exit;
}

try {

    $stmt = $conn->prepare("
        SELECT
            pm.id AS medication_id,
            pm.pharmacy_id,

            p.name AS pharmacy_name,
            p.location AS pharmacy_location,
            p.open_hour,
            p.close_hours,
            p.offers_delivery,
            p.delivery_fee,

            pm.medication_name,
            pm.price,
            pm.availability

        FROM Pharmacy_Medication pm

        INNER JOIN Pharmacies p
            ON p.id = pm.pharmacy_id

        WHERE pm.medication_name LIKE ?

        ORDER BY pm.price ASC
    ");

    $stmt->execute([
        "%" . $medicationName . "%"
    ]);

    $results = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode([
        "success" => true,
        "message" => "Medication prices retrieved successfully",
        "pharmacies" => $results
    ]);

} catch (PDOException $e) {

    echo json_encode([
        "success" => false,
        "message" => "Unable to retrieve medication prices",
        "pharmacies" => []
    ]);
}
?>