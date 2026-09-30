<?php
declare(strict_types=1);
require dirname(__DIR__) . '/src/bootstrap.php';

Auth::requireAdmin();
$method = Http::method('GET', 'POST');
$pdo = Database::get();

if ($method === 'GET') {
    [$limit, $offset] = Http::paging();
    $st = $pdo->prepare(
        "SELECT a.answer_id, a.question_id, a.provider_id, a.answer_text, a.answered_at,
                q.question_text
         FROM Question_Answer a
         LEFT JOIN Patient_Question q ON q.question_id = a.question_id
         ORDER BY a.answered_at DESC
         LIMIT :limit OFFSET :offset"
    );
    $st->bindValue(':limit', $limit, PDO::PARAM_INT);
    $st->bindValue(':offset', $offset, PDO::PARAM_INT);
    $st->execute();
    Http::json(200, $st->fetchAll());
}

if (strtolower(Http::param('action') ?? '') !== 'delete') {
    Http::message(400, 'action must be delete');
}
$id = Http::toId(Http::param('id'));
if ($id === null) Http::message(400, 'id must be a positive number');

$st = $pdo->prepare('DELETE FROM Question_Answer WHERE answer_id = ?');
$st->execute([$id]);
$st->rowCount() === 0
    ? Http::message(404, 'Answer not found')
    : Http::message(200, 'Answer deleted');
