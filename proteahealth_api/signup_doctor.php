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


/*
|--------------------------------------------------------------------------
| Get form data
|--------------------------------------------------------------------------
*/

$name = trim($_POST["name"] ?? "");
$surname = trim($_POST["surname"] ?? "");
$email = trim($_POST["email"] ?? "");
$specialization = trim($_POST["specialization"] ?? "");
$location = trim($_POST["location"] ?? "");
$clinic_name = trim($_POST["clinic_name"] ?? "");
$years_in_professional = trim($_POST["years_in_professional"] ?? "");
$consultation_price = trim($_POST["consultation_price"] ?? "");
$certification = trim($_POST["certification"] ?? "");
$password = $_POST["password"] ?? "";


/*
|--------------------------------------------------------------------------
| Required fields
|--------------------------------------------------------------------------
*/

if (
    empty($name) ||
    empty($surname) ||
    empty($email) ||
    empty($specialization) ||
    empty($location) ||
    empty($clinic_name) ||
    $years_in_professional === "" ||
    $consultation_price === "" ||
    empty($certification) ||
    empty($password)
) {
    echo json_encode([
        "success" => false,
        "message" => "Please complete all required fields"
    ]);
    exit;
}


/*
|--------------------------------------------------------------------------
| Validate email
|--------------------------------------------------------------------------
*/

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    echo json_encode([
        "success" => false,
        "message" => "Invalid email address"
    ]);
    exit;
}


/*
|--------------------------------------------------------------------------
| Validate years in profession
|--------------------------------------------------------------------------
*/

$years_in_professional = (int)$years_in_professional;

if ($years_in_professional < 0) {
    echo json_encode([
        "success" => false,
        "message" => "Years in profession cannot be negative"
    ]);
    exit;
}


/*
|--------------------------------------------------------------------------
| Validate consultation price
|--------------------------------------------------------------------------
*/

$consultation_price = (float)$consultation_price;

if ($consultation_price < 0) {
    echo json_encode([
        "success" => false,
        "message" => "Consultation price cannot be negative"
    ]);
    exit;
}


/*
|--------------------------------------------------------------------------
| Check duplicate email
|--------------------------------------------------------------------------
*/

$checkSql = "SELECT id FROM Doctor WHERE email = ? LIMIT 1";

$checkStmt = $conn->prepare($checkSql);
$checkStmt->execute([$email]);

if ($checkStmt->fetch()) {
    echo json_encode([
        "success" => false,
        "message" => "An account with this email already exists"
    ]);
    exit;
}


/*
|--------------------------------------------------------------------------
| Hash password
|--------------------------------------------------------------------------
*/

$password_hash = password_hash(
    $password,
    PASSWORD_DEFAULT
);


/*
|--------------------------------------------------------------------------
| Insert doctor
|--------------------------------------------------------------------------
*/

$sql = "
    INSERT INTO Doctor
    (
        name,
        surname,
        email,
        password_hash,
        specialization,
        location,
        clinic_name,
        years_in_professional,
        consultation_price,
        certification
    )
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
";

$stmt = $conn->prepare($sql);

$stmt->execute([
    $name,
    $surname,
    $email,
    $password_hash,
    $specialization,
    $location,
    $clinic_name,
    $years_in_professional,
    $consultation_price,
    $certification
]);


/*
|--------------------------------------------------------------------------
| Success
|--------------------------------------------------------------------------
*/

echo json_encode([
    "success" => true,
    "message" => "Doctor registered successfully",
    "doctor_id" => $conn->lastInsertId(),
    "verification" => "pending"
]);

?>