<?php

header("Content-Type: application/json");

require_once "db.php";

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    echo json_encode([
        "success" => false,
        "message" => "Invalid request method"
    ]);
    exit;
}

$id = $_POST["id"] ?? "";
$role = $_POST["role"] ?? "";

if (empty($id) || empty($role)) {
    echo json_encode([
        "success" => false,
        "message" => "ID and role are required"
    ]);
    exit;
}

try {

    switch (strtolower($role)) {

        // -------------------------
        // PATIENT
        // -------------------------
        case "patient":

            $stmt = $conn->prepare("
                SELECT
                    id,
                    name,
                    surname,
                    age,
                    email,
                    phone,
                    gender,
                    home_address,
                    emergency_contact_name,
                    emergency_contact_number,
                    emergency_contact_relationship,
                    allergies,
                    medical_conditions,
                    risk_level
                FROM Patient
                WHERE id = ?
            ");

            $stmt->execute([$id]);

            break;


        // -------------------------
        // DOCTOR
        // -------------------------
        case "doctor":

            $stmt = $conn->prepare("
                SELECT
                    id,
                    name,
                    surname,
                    email,
                    specialization,
                    location,
                    clinic_name,
                    years_in_professional,
                    consultation_price,
                    certification,
                    verification
                FROM Doctor
                WHERE id = ?
            ");

            $stmt->execute([$id]);

            break;


        // -------------------------
        // PHARMACIST
        // -------------------------
        case "pharmacist":

            $stmt = $conn->prepare("
                SELECT
                    id,
                    name,
                    surname,
                    email,
                    pharmacy_id,
                    phone,
                    pharmacy_location,
                    verification,
                    registration_code,
                    work_experience_years
                FROM Pharmacist
                WHERE id = ?
            ");

            $stmt->execute([$id]);

            break;


        // -------------------------
        // PHARMACY
        // -------------------------
        case "pharmacy":

            $stmt = $conn->prepare("
                SELECT
                    id,
                    name,
                    email,
                    location,
                    open_hour,
                    close_hours,
                    details
                FROM Pharmacies
                WHERE id = ?
            ");

            $stmt->execute([$id]);

            break;


        default:

            echo json_encode([
                "success" => false,
                "message" => "Invalid role"
            ]);

            exit;
    }

    $user = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($user) {

        echo json_encode([
            "success" => true,
            "message" => "Profile retrieved successfully",
            "user" => $user
        ]);

    } else {

        echo json_encode([
            "success" => false,
            "message" => "User not found"
        ]);
    }

} catch (PDOException $e) {

    echo json_encode([
        "success" => false,
        "message" => "Database error"
    ]);
}
?>