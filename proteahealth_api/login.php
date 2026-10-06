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

$email = $_POST["email"] ?? "";
$password = $_POST["password"] ?? "";
$role = $_POST["role"] ?? "";

if (empty($email) || empty($password) || empty($role)) {
    echo json_encode([
        "success" => false,
        "message" => "Email, password and role are required"
    ]);
    exit;
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    echo json_encode([
        "success" => false,
        "message" => "Invalid email address"
    ]);
    exit;
}

/*
 * Decide which table to search
 */
switch ($role) {

    case "patient":
        $table = "Patient";
        break;

    case "doctor":
        $table = "Doctor";
        break;

    case "pharmacy":
        $table = "Pharmacist";
        break;

    default:
        echo json_encode([
            "success" => false,
            "message" => "Invalid user role"
        ]);
        exit;
    }
    
/*
 * Find user by email
 */
$sql = "SELECT * FROM $table WHERE email = ? LIMIT 1";

$stmt = $conn->prepare($sql);
$stmt->execute([$email]);

$user = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$user) {
    echo json_encode([
        "success" => false,
        "message" => "Invalid email or password"
    ]);
    exit;
}

/*
 * Verify password
 */
if (!password_verify($password, $user["password_hash"])) {
    echo json_encode([
        "success" => false,
        "message" => "Invalid email or password"
    ]);
    exit;
}

/*
 * Remove password from response
 */
unset($user["password_hash"]);

/*
 * Successful login
 */
echo json_encode([
    "success" => true,
    "message" => "Login successful",
    "role" => $role,
    "user" => $user
]);

?>
