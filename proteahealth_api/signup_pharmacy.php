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
| Pharmacy information
|--------------------------------------------------------------------------
*/

$pharmacy_name = trim($_POST["pharmacy_name"] ?? "");
$pharmacy_location = trim($_POST["pharmacy_location"] ?? "");
$open_hour = trim($_POST["open_hour"] ?? "");
$close_hour = trim($_POST["close_hour"] ?? "");
$details = trim($_POST["details"] ?? "");


/*
|--------------------------------------------------------------------------
| Pharmacist information
|--------------------------------------------------------------------------
*/

$name = trim($_POST["name"] ?? "");
$surname = trim($_POST["surname"] ?? "");
$email = trim($_POST["email"] ?? "");
$phone = trim($_POST["phone"] ?? "");
$registration_code = trim($_POST["registration_code"] ?? "");
$vertification = trim($_POST["vertification"] ?? "");
$work_experience_years =
    trim($_POST["work_experience_years"] ?? "");

$password = $_POST["password"] ?? "";


/*
|--------------------------------------------------------------------------
| Validate required fields
|--------------------------------------------------------------------------
*/

if (
    empty($pharmacy_name) ||
    empty($pharmacy_location) ||
    empty($open_hour) ||
    empty($close_hour) ||
    empty($name) ||
    empty($surname) ||
    empty($email) ||
    empty($phone) ||
    empty($registration_code) ||
    empty($vertification) ||
    $work_experience_years === "" ||
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
| Validate work experience
|--------------------------------------------------------------------------
*/

$work_experience_years = (int)$work_experience_years;

if ($work_experience_years < 0) {
    echo json_encode([
        "success" => false,
        "message" => "Work experience cannot be negative"
    ]);
    exit;
}


/*
|--------------------------------------------------------------------------
| Validate opening and closing times
|--------------------------------------------------------------------------
*/

if ($open_hour === $close_hour) {
    echo json_encode([
        "success" => false,
        "message" => "Opening and closing times cannot be the same"
    ]);
    exit;
}


/*
|--------------------------------------------------------------------------
| Check duplicate pharmacist email
|--------------------------------------------------------------------------
*/

$checkSql = "
    SELECT id
    FROM Pharmacist
    WHERE email = ?
    LIMIT 1
";

$checkStmt = $conn->prepare($checkSql);
$checkStmt->execute([$email]);

if ($checkStmt->fetch()) {
    echo json_encode([
        "success" => false,
        "message" => "A pharmacist account with this email already exists"
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
| Start database transaction
|--------------------------------------------------------------------------
*/

try {

    $conn->beginTransaction();


    /*
    |--------------------------------------------------------------------------
    | Create pharmacy
    |--------------------------------------------------------------------------
    */

    $pharmacySql = "
        INSERT INTO Pharmacies
        (
            name,
            location,
            open_hour,
            close_hours,
            details
        )
        VALUES (?, ?, ?, ?, ?)
    ";

    $pharmacyStmt = $conn->prepare($pharmacySql);

    $pharmacyStmt->execute([
        $pharmacy_name,
        $pharmacy_location,
        $open_hour,
        $close_hour,
        $details
    ]);


    /*
    |--------------------------------------------------------------------------
    | Get newly created pharmacy ID
    |--------------------------------------------------------------------------
    */

    $pharmacy_id = $conn->lastInsertId();


    /*
    |--------------------------------------------------------------------------
    | Create pharmacist
    |--------------------------------------------------------------------------
    */

    $pharmacistSql = "
        INSERT INTO Pharmacist
        (
            name,
            surname,
            email,
            password_hash,
            pharmacy_id,
            phone,
            pharmacy_location,
            registration_code,
            work_experience_years
        )
        VALUES (?, ?, ?, ?, ?, ?, ? , ?, ?)
    ";

    $pharmacistStmt = $conn->prepare($pharmacistSql);

    $pharmacistStmt->execute([
        $name,
        $surname,
        $email,
        $password_hash,
        $pharmacy_id,
        $phone,
        $pharmacy_location,
        "pending",
         $registration_code,
        $work_experience_years
    ]);


    /*
    |--------------------------------------------------------------------------
    | Everything succeeded
    |--------------------------------------------------------------------------
    */

    $pharmacist_id = $conn->lastInsertId();

    $conn->commit();


    /*
    |--------------------------------------------------------------------------
    | Success response
    |--------------------------------------------------------------------------
    */

    echo json_encode([
        "success" => true,
        "message" => "Pharmacy and pharmacist registered successfully",
        "pharmacy_id" => $pharmacy_id,
        "pharmacist_id" => $pharmacist_id
    ]);

} catch (PDOException $e) {

    /*
    |--------------------------------------------------------------------------
    | Something failed - undo everything
    |--------------------------------------------------------------------------
    */

    if ($conn->inTransaction()) {
        $conn->rollBack();
    }

    echo json_encode([
        "success" => false,
        "message" => "Registration failed"
    ]);

}

?>