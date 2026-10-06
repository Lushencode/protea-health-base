<?php

header("Content-Type: application/json");

require_once "db.php";

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    echo json_encode([
        "success" => false,
        "message" => "Only POST requests are allowed"
    ]);
    exit;
}

$patientId = trim($_POST["patient_id"] ?? "");
$pharmacyId = trim($_POST["pharmacy_id"] ?? "");
$medicationName = trim($_POST["medication_name"] ?? "");
$quantity = trim($_POST["quantity"] ?? "");
$amount = trim($_POST["amount"] ?? "");
$fulfillmentMethod = strtolower(
    trim($_POST["fulfillment_method"] ?? "")
);
$deliveryAddress = trim(
    $_POST["delivery_address"] ?? ""
);
$deliveryFee = trim(
    $_POST["delivery_fee"] ?? "0"
);


// ---------------------------------------------------------
// VALIDATION
// ---------------------------------------------------------

if (
    $patientId === "" ||
    $pharmacyId === "" ||
    $medicationName === "" ||
    $quantity === "" ||
    $amount === "" ||
    $fulfillmentMethod === ""
) {

    echo json_encode([
        "success" => false,
        "message" => "Missing required order information"
    ]);

    exit;
}


if (!in_array(
    $fulfillmentMethod,
    ["collection", "delivery"],
    true
)) {

    echo json_encode([
        "success" => false,
        "message" => "Invalid fulfillment method"
    ]);

    exit;
}


if (
    !is_numeric($patientId) ||
    !is_numeric($pharmacyId) ||
    !is_numeric($quantity) ||
    !is_numeric($amount) ||
    !is_numeric($deliveryFee)
) {

    echo json_encode([
        "success" => false,
        "message" => "Invalid numeric order information"
    ]);

    exit;
}


$patientId = (int)$patientId;
$pharmacyId = (int)$pharmacyId;
$quantity = (int)$quantity;
$amount = (float)$amount;
$deliveryFee = (float)$deliveryFee;


if ($quantity <= 0) {

    echo json_encode([
        "success" => false,
        "message" => "Quantity must be greater than zero"
    ]);

    exit;
}


if ($amount <= 0) {

    echo json_encode([
        "success" => false,
        "message" => "Order amount must be greater than zero"
    ]);

    exit;
}


// ---------------------------------------------------------
// DELIVERY VALIDATION
// ---------------------------------------------------------

if ($fulfillmentMethod === "delivery") {

    if ($deliveryAddress === "") {

        echo json_encode([
            "success" => false,
            "message" => "Delivery address is required"
        ]);

        exit;
    }

} else {

    $deliveryAddress = null;
    $deliveryFee = 0.00;
}


// ---------------------------------------------------------
// INSERT ORDER
// ---------------------------------------------------------

try {

    $stmt = $conn->prepare("
        INSERT INTO Orders (
            patient_id,
            pharmacy_id,
            medication_name,
            quantity,
            amount,
            fulfillment_method,
            delivery_address,
            delivery_fee,
            status
        )
        VALUES (
            ?,
            ?,
            ?,
            ?,
            ?,
            ?,
            ?,
            ?,
            'confirmed'
        )
    ");

    $stmt->execute([
        $patientId,
        $pharmacyId,
        $medicationName,
        $quantity,
        $amount,
        $fulfillmentMethod,
        $deliveryAddress,
        $deliveryFee
    ]);


    $orderId =
        $conn->lastInsertId();


    echo json_encode([
        "success" => true,
        "message" => "Order placed successfully",
        "order_id" => (int)$orderId
    ]);


} catch (PDOException $e) {

    echo json_encode([
        "success" => false,
        "message" => "Unable to place order"
    ]);
}
?>