<?php
declare(strict_types=1);
require dirname(__DIR__) . '/src/bootstrap.php';

Auth::requireAdmin();
$method = Http::method('GET', 'POST');

// Whitelist: user input never reaches SQL as a table name
const TABLES = ['patient' => 'Patient', 'doctor' => 'Doctor', 'pharmacist' => 'Pharmacist'];
$table = TABLES[strtolower(Http::param('role') ?? '')] ?? null;
$pdo = Database::get();

if ($method === 'GET') {
    if ($table === null) Http::message(400, 'role must be patient, doctor or pharmacist');
    [$limit, $offset] = Http::paging();
    $st = $pdo->prepare(
        "SELECT id, name, surname, email, access_status, created_at
         FROM `$table` ORDER BY created_at DESC LIMIT :limit OFFSET :offset"
    );
    $st->bindValue(':limit', $limit, PDO::PARAM_INT);
    $st->bindValue(':offset', $offset, PDO::PARAM_INT);
    $st->execute();
    Http::json(200, $st->fetchAll());
}

// POST: allow / block
$action = strtolower(Http::param('action') ?? '');
$idRaw  = Http::param('id');
if ($table === null || $action === '' || $idRaw === null) {
    Http::message(400, 'role, id and action are required');
}
$status = match ($action) {
    'allow' => 'ALLOWED',
    'block' => 'BLOCKED',
    default => Http::message(400, 'action must be allow or block'),
};
$id = Http::toId($idRaw);
if ($id === null) Http::message(400, 'id must be a positive number');

$st = $pdo->prepare("UPDATE `$table` SET access_status = ? WHERE id = ?");
$st->execute([$status, $id]);

// rowCount() is 0 when the status is already the same, so check existence too
if ($st->rowCount() === 0) {
    $chk = $pdo->prepare("SELECT 1 FROM `$table` WHERE id = ?");
    $chk->execute([$id]);
    if (!$chk->fetchColumn()) Http::message(404, 'User not found');
}
Http::message(200, "User set to $status");
