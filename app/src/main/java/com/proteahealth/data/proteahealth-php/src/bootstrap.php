<?php
declare(strict_types=1);

require __DIR__ . '/Database.php';
require __DIR__ . '/Http.php';
require __DIR__ . '/Auth.php';

$GLOBALS['config'] = require dirname(__DIR__) . '/config.php';

ini_set('display_errors', '0');          // never show raw errors to clients
error_reporting(E_ALL);

header('Content-Type: application/json; charset=utf-8');
header('X-Content-Type-Options: nosniff');
header('X-Frame-Options: DENY');
header('Cache-Control: no-store');

set_exception_handler(function (Throwable $e): void {
    error_log('[ProteaHealth] ' . $e);   // full detail goes to the server log only
    $msg = $GLOBALS['config']['debug'] ? $e->getMessage() : 'Internal server error';
    Http::message(500, $msg);
});
