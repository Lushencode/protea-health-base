<?php
declare(strict_types=1);
require dirname(__DIR__) . '/src/bootstrap.php';

Auth::requireAdmin();
$method = Http::method('GET', 'POST');
$pdo = Database::get();

if ($method === 'GET') {
    [$limit, $offset] = Http::paging();
    // patient_id and name are hidden for anonymous questions so identity cannot leak
    $sql = "SELECT q.question_id,
                   IF(q.is_anonymous = 1, NULL, q.patient_id) AS patient_id,
                   IF(q.is_anonymous = 1, 'Anonymous', CONCAT(p.name, ' ', p.surname)) AS patient_name,
                   q.category_id, q.question_text, q.is_anonymous, q.status, q.created_at,
                   COALESCE(ac.cnt, 0) AS answer_count
            FROM Patient_Question q
            LEFT JOIN Patient p ON p.id = q.patient_id
            LEFT JOIN (SELECT question_id, COUNT(*) AS cnt
                       FROM Question_Answer GROUP BY question_id) ac
                   ON ac.question_id = q.question_id
            ORDER BY q.created_at DESC
            LIMIT :limit OFFSET :offset";
    $st = $pdo->prepare($sql);
    $st->bindValue(':limit', $limit, PDO::PARAM_INT);
    $st->bindValue(':offset', $offset, PDO::PARAM_INT);
    $st->execute();
    Http::json(200, $st->fetchAll());
}

// POST
if (strtolower(Http::param('action') ?? '') !== 'delete') {
    Http::message(400, 'action must be delete');
}
$id = Http::toId(Http::param('id'));
if ($id === null) Http::message(400, 'id must be a positive number');

$pdo->beginTransaction();
try {
    // Answers first so the foreign key does not block the delete
    $pdo->prepare('DELETE FROM Question_Answer WHERE question_id = ?')->execute([$id]);
    $del = $pdo->prepare('DELETE FROM Patient_Question WHERE question_id = ?');
    $del->execute([$id]);

    if ($del->rowCount() === 0) {
        $pdo->rollBack();
        Http::message(404, 'Question not found');
    }
    $pdo->commit();
} catch (Throwable $e) {
    if ($pdo->inTransaction()) $pdo->rollBack();
    throw $e;
}
Http::message(200, 'Question deleted');
