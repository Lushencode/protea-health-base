<?php
declare(strict_types=1);
require dirname(__DIR__) . '/src/bootstrap.php';

Auth::requireAdmin();
Http::method('GET');

$patientId = null;
if (isset($_GET['patient_id'])) {
    $patientId = Http::toId($_GET['patient_id']);
    if ($patientId === null) Http::message(400, 'patient_id must be a positive number');
}
[$limit, $offset] = Http::paging();

// Counts are pre-aggregated once, instead of two subqueries per row.
$sql = "SELECT rv.view_id, rv.patient_id,
               CONCAT(p.name, ' ', p.surname) AS patient_name,
               rv.resource_id, rv.viewed_at,
               COALESCE(qc.cnt, 0) AS questions_asked,
               COALESCE(ac.cnt, 0) AS answers_received
        FROM Resource_View rv
        LEFT JOIN Patient p ON p.id = rv.patient_id
        LEFT JOIN (SELECT patient_id, COUNT(*) AS cnt
                   FROM Patient_Question GROUP BY patient_id) qc
               ON qc.patient_id = rv.patient_id
        LEFT JOIN (SELECT q.patient_id, COUNT(*) AS cnt
                   FROM Question_Answer a
                   JOIN Patient_Question q ON q.question_id = a.question_id
                   GROUP BY q.patient_id) ac
               ON ac.patient_id = rv.patient_id "
     . ($patientId !== null ? "WHERE rv.patient_id = :pid " : "")
     . "ORDER BY rv.viewed_at DESC LIMIT :limit OFFSET :offset";

$st = Database::get()->prepare($sql);
if ($patientId !== null) $st->bindValue(':pid', $patientId, PDO::PARAM_INT);
$st->bindValue(':limit', $limit, PDO::PARAM_INT);
$st->bindValue(':offset', $offset, PDO::PARAM_INT);
$st->execute();
Http::json(200, $st->fetchAll());
