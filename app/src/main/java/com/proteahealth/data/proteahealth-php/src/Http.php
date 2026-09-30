<?php
declare(strict_types=1);

final class Http
{
    public static function json(int $status, mixed $data): never
    {
        http_response_code($status);
        echo json_encode($data, JSON_UNESCAPED_UNICODE | JSON_INVALID_UTF8_SUBSTITUTE);
        exit;
    }

    public static function message(int $status, string $msg): never
    {
        self::json($status, ['message' => $msg]);
    }

    /** Reject anything but the allowed methods (405 + Allow header). */
    public static function method(string ...$allowed): string
    {
        $m = $_SERVER['REQUEST_METHOD'] ?? 'GET';
        if (!in_array($m, $allowed, true)) {
            header('Allow: ' . implode(', ', $allowed));
            self::message(405, 'Method not allowed');
        }
        return $m;
    }

    /** Reads a parameter from the query string, form body or JSON body. */
    public static function param(string $key): ?string
    {
        static $json = null;
        if ($json === null) {
            $raw = file_get_contents('php://input');
            $json = ($raw !== '' && str_contains($_SERVER['CONTENT_TYPE'] ?? '', 'json'))
                ? (json_decode($raw, true) ?: []) : [];
        }
        $v = $_POST[$key] ?? $json[$key] ?? $_GET[$key] ?? null;
        return is_scalar($v) ? (string)$v : null;
    }

    /** Strict positive-integer parsing; returns null if invalid. */
    public static function toId(?string $v): ?int
    {
        if ($v === null || !ctype_digit($v)) return null;
        $n = (int)$v;
        return $n > 0 ? $n : null;
    }

    /** Pagination: ?limit= (default 200, max 1000) & ?offset= */
    public static function paging(): array
    {
        $limit  = Http::toId($_GET['limit'] ?? null) ?? 200;
        $offset = ctype_digit($_GET['offset'] ?? '') ? (int)$_GET['offset'] : 0;
        return [min($limit, 1000), $offset];
    }
}
