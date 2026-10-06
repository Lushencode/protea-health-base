<?php

header("Content-Type: application/json");

require_once "db.php";

$prescription_id = $_POST["prescription_id"] ?? "";
$patient_id = $_POST["patient_id"] ?? "";
$action = $_POST["action"] ?? "";

if (
    empty($prescription_id) ||
    empty($patient_id) ||
    empty($action)
) {
    echo json_encode([
        "success" => false,
        "message" => "Missing required information"
    ]);
    exit;
}

try {

    if ($action === "taken") {

        $stmt = $conn->prepare("
            UPDATE Prescriptions
            SET
                remaining_doses =
                    CASE
                        WHEN remaining_doses > 0
                        THEN remaining_doses - 1
                        ELSE 0
                    END,

                last_taken_date = CURDATE()

            WHERE id = ?
            AND patient_id = ?
            AND (
                last_taken_date IS NULL
                OR last_taken_date <> CURDATE()
            )
        ");

        $stmt->execute([
            $prescription_id,
            $patient_id
        ]);

        if ($stmt->rowCount() > 0) {

            echo json_encode([
                "success" => true,
                "message" => "Medication logged as taken"
            ]);

        } else {

            echo json_encode([
                "success" => false,
                "message" => "Medication already logged today"
            ]);
        }

    } elseif ($action === "undo") {

        $stmt = $conn->prepare("
            UPDATE Prescriptions
            SET
                remaining_doses =
                    CASE
                        WHEN remaining_doses < quantity
                        THEN remaining_doses + 1
                        ELSE quantity
                    END,

                last_taken_date = NULL

            WHERE id = ?
            AND patient_id = ?
            AND last_taken_date = CURDATE()
        ");

        $stmt->execute([
            $prescription_id,
            $patient_id
        ]);

        if ($stmt->rowCount() > 0) {

            echo json_encode([
                "success" => true,
                "message" => "Medication log undone"
            ]);

        } else {

            echo json_encode([
                "success" => false,
                "message" => "Nothing to undo"
            ]);
        }

    } else {

        echo json_encode([
            "success" => false,
            "message" => "Invalid action"
        ]);
    }

} catch (PDOException $e) {

    echo json_encode([
        "success" => false,
        "message" => "Unable to update medication"
    ]);
}
?>