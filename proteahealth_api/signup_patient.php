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

$name = trim($_POST["name"] ?? "");
$surname = trim($_POST["surname"] ?? "");
$age = trim($_POST["age"] ?? "");
$email = trim($_POST["email"] ?? "");
$phone = trim($_POST["phone"] ?? "");
$gender = trim($_POST["gender"] ?? "");
$home_address = trim($_POST["home_address"] ?? "");

$emergency_contact_name =
    trim($_POST["emergency_contact_name"] ?? "");

$emergency_contact_number =
    trim($_POST["emergency_contact_number"] ?? "");

$emergency_contact_relationship =
    trim($_POST["emergency_contact_relationship"] ?? "");

/*
|--------------------------------------------------------------------------
| Medical information
|--------------------------------------------------------------------------
*/

$allergies =
    trim($_POST["allergies"] ?? "");

$medical_conditions =
    trim($_POST["medical_conditions"] ?? "");

$password = $_POST["password"] ?? "";


/*
|--------------------------------------------------------------------------
| Required fields
|--------------------------------------------------------------------------
*/

if (
    empty($name) ||
    empty($surname) ||
    empty($age) ||
    empty($email) ||
    empty($phone) ||
    empty($gender) ||
    empty($home_address) ||
    empty($emergency_contact_name) ||
    empty($emergency_contact_number) ||
    empty($emergency_contact_relationship) ||
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
| Validate age
|--------------------------------------------------------------------------
*/

$age = (int)$age;

if ($age <= 0 || $age > 150) {
    echo json_encode([
        "success" => false,
        "message" => "Please enter a valid age"
    ]);
    exit;
}


/*
|--------------------------------------------------------------------------
| Validate gender
|--------------------------------------------------------------------------
*/

$validGenders = [
    "male",
    "female",
    "other",
    "prefer_not_to_say"
];

if (!in_array($gender, $validGenders, true)) {
    echo json_encode([
        "success" => false,
        "message" => "Invalid gender selected"
    ]);
    exit;
}


/*
|--------------------------------------------------------------------------
| Check if email already exists
|--------------------------------------------------------------------------
*/

$checkSql =
    "SELECT id FROM Patient WHERE email = ? LIMIT 1";

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

$password_hash =
    password_hash($password, PASSWORD_DEFAULT);


/*
|--------------------------------------------------------------------------
| Insert patient
|--------------------------------------------------------------------------
*/

$sql = "
    INSERT INTO Patient
    (
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
        password_hash
    )
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
";

$stmt = $conn->prepare($sql);

$stmt->execute([
    $name,
    $surname,
    $age,
    $email,
    $phone,
    $gender,
    $home_address,
    $emergency_contact_name,
    $emergency_contact_number,
    $emergency_contact_relationship,
    $allergies,
    $medical_conditions,
    $password_hash
]);


/*
|--------------------------------------------------------------------------
| Success response
|--------------------------------------------------------------------------
*/

echo json_encode([
    "success" => true,
    "message" => "Patient registered successfully",
    "patient_id" => $conn->lastInsertId()
]);

?>