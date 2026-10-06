<?php

header("Content-Type: application/json");

require_once "db.php";

$patient_id = $_GET["patient_id"] ?? "";

if (empty($patient_id)) {

    echo json_encode([
        "success" => false,
        "message" => "Patient ID is required",
        "medications" => []
    ]);

    exit;
}

try {

    $stmt = $conn->prepare("
        SELECT
            p.id AS prescription_id,
            p.patient_id,
            p.doctor_id,
            p.medication_id,

            pm.medication_name,

            p.quantity,
            p.dosage,
            p.purpose,
            p.scheduled_time,
            p.remaining_doses,
            p.last_taken_date,
            p.date_prescription,

            pm.price,
            pm.availability,
            pm.pharmacy_id

        FROM Prescriptions p

        INNER JOIN Pharmacy_Medication pm
            ON p.medication_id = pm.id

        WHERE p.patient_id = ?

        ORDER BY p.scheduled_time ASC
    ");

    $stmt->execute([$patient_id]);

    $medications =
        $stmt->fetchAll(PDO::FETCH_ASSOC);


    echo json_encode([
        "success" => true,
        "message" => "Medications retrieved successfully",
        "medications" => $medications
    ]);

} catch (PDOException $e) {

    echo json_encode([
        "success" => false,
        "message" => "Unable to retrieve medications",
        "medications" => []
    ]);
}
?>