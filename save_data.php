<?php
/**
 * UPSRTC DUTY MANAGEMENT PORTAL - SECURE BACKEND API (save_data.php)
 * Hostinger Apache / PHP / MySQL Integration Endpoint
 */

header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS, DELETE');
header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
header('Content-Type: application/json; charset=UTF-8');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    echo json_encode(['status' => 'success', 'message' => 'Preflight OK']);
    exit;
}

// =========================================================================
// HOSTINGER DATABASE CONFIGURATION PLACEHOLDERS
// Found in Hostinger hPanel -> Databases -> MySQL Databases
// =========================================================================
define('DB_HOST', 'localhost');                  // Usually 'localhost' on Hostinger
define('DB_USER', 'u123456789_upsrtc_user');     // Replace with your Hostinger DB Username
define('DB_PASS', 'Your_Secure_DB_Password_123'); // Replace with your Hostinger DB Password
define('DB_NAME', 'u123456789_upsrtc_db');       // Replace with your Hostinger DB Name
define('DB_PORT', 3306);

function sendJsonResponse($status, $message, $extra = [], $statusCode = 200) {
    http_response_code($statusCode);
    $payload = array_merge([
        'status'    => $status,
        'message'   => $message,
        'timestamp' => date('Y-m-d H:i:s')
    ], $extra);
    echo json_encode($payload, JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT);
    exit;
}

// -------------------------------------------------------------------------
// Establish MySQLi Connection
// -------------------------------------------------------------------------
mysqli_report(MYSQLI_REPORT_OFF);
$mysqli = @new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME, DB_PORT);

if ($mysqli->connect_errno) {
    sendJsonResponse(
        'error',
        'Database connection failed: ' . $mysqli->connect_error,
        ['code' => $mysqli->connect_errno],
        500
    );
}

$mysqli->set_charset('utf8mb4');

// -------------------------------------------------------------------------
// Parse JSON Payload
// -------------------------------------------------------------------------
$rawInput = file_get_contents('php://input');
$requestData = [];

if (!empty($rawInput)) {
    $decoded = json_decode($rawInput, true);
    if (json_last_error() === JSON_ERROR_NONE && is_array($decoded)) {
        $requestData = $decoded;
    }
}
if (empty($requestData)) {
    $requestData = $_POST;
}

$action = $_GET['action'] ?? ($requestData['action'] ?? 'save_duty');

// -------------------------------------------------------------------------
// Action Router
// -------------------------------------------------------------------------
try {
    switch ($action) {
        case 'test':
            $res = $mysqli->query("SELECT DATABASE() AS db, VERSION() AS version");
            $info = $res ? $res->fetch_assoc() : [];
            sendJsonResponse('success', 'Hostinger MySQL Database Connected Successfully!', [
                'database' => $info['db'] ?? DB_NAME,
                'mysql_version' => $info['version'] ?? 'Unknown'
            ]);
            break;

        case 'save_duty':
            if (empty($requestData['dutyDate']) || empty($requestData['busNumber'])) {
                sendJsonResponse('error', 'Validation failed: dutyDate and busNumber are required.', [], 400);
            }

            $id = !empty($requestData['id']) ? trim($requestData['id']) : 'duty_' . round(microtime(true) * 1000);
            $employeeId = !empty($requestData['employeeId']) ? trim($requestData['employeeId']) : 'CND 1901';
            $userId = !empty($requestData['userId']) ? trim($requestData['userId']) : 'u1';
            $dutyDate = trim($requestData['dutyDate']);
            $dutyNumber = !empty($requestData['dutyNumber']) ? trim($requestData['dutyNumber']) : '01';
            $shift = !empty($requestData['shift']) ? trim($requestData['shift']) : 'Morning';
            $busNumber = trim($requestData['busNumber']);
            $routeFrom = trim($requestData['routeFrom'] ?? '');
            $routeTo = trim($requestData['routeTo'] ?? '');
            $departureTime = trim($requestData['departureTime'] ?? '');
            $arrivalTime = trim($requestData['arrivalTime'] ?? '');
            $openingKm = floatval($requestData['openingKm'] ?? 0);
            $closingKm = floatval($requestData['closingKm'] ?? 0);
            $totalKm = floatval($requestData['totalKm'] ?? max(0, $closingKm - $openingKm));
            $passengerCount = intval($requestData['passengerCount'] ?? 0);
            $totalSeats = intval($requestData['totalSeats'] ?? 52);
            $loadFactor = floatval($requestData['loadFactor'] ?? 0);
            $income = floatval($requestData['income'] ?? 0);
            $expenses = floatval($requestData['expenses'] ?? 0);
            $remarks = trim($requestData['remarks'] ?? '');

            // Prepared Statement for SQL Injection Security
            $query = "INSERT INTO `duties` (
                        `id`, `employee_id`, `user_id`, `duty_date`, `duty_number`,
                        `shift`, `bus_number`, `route_from`, `route_to`, `departure_time`,
                        `arrival_time`, `opening_km`, `closing_km`, `total_km`, `passenger_count`,
                        `total_seats`, `load_factor`, `income`, `expenses`, `remarks`
                      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                      ON DUPLICATE KEY UPDATE
                        `duty_date` = VALUES(`duty_date`),
                        `shift` = VALUES(`shift`),
                        `bus_number` = VALUES(`bus_number`),
                        `route_from` = VALUES(`route_from`),
                        `route_to` = VALUES(`route_to`),
                        `departure_time` = VALUES(`departure_time`),
                        `arrival_time` = VALUES(`arrival_time`),
                        `opening_km` = VALUES(`opening_km`),
                        `closing_km` = VALUES(`closing_km`),
                        `total_km` = VALUES(`total_km`),
                        `passenger_count` = VALUES(`passenger_count`),
                        `load_factor` = VALUES(`load_factor`),
                        `income` = VALUES(`income`),
                        `expenses` = VALUES(`expenses`),
                        `remarks` = VALUES(`remarks`),
                        `updated_at` = CURRENT_TIMESTAMP";

            $stmt = $mysqli->prepare($query);
            if (!$stmt) {
                sendJsonResponse('error', 'Query preparation error: ' . $mysqli->error, [], 500);
            }

            $stmt->bind_param(
                'sssssssssssdddiiddds',
                $id, $employeeId, $userId, $dutyDate, $dutyNumber,
                $shift, $busNumber, $routeFrom, $routeTo, $departureTime,
                $arrivalTime, $openingKm, $closingKm, $totalKm, $passengerCount,
                $totalSeats, $loadFactor, $income, $expenses, $remarks
            );

            if (!$stmt->execute()) {
                $err = $stmt->error;
                $stmt->close();
                sendJsonResponse('error', 'Execution error: ' . $err, [], 500);
            }

            $affected = $stmt->affected_rows;
            $stmt->close();

            sendJsonResponse('success', 'Duty record saved successfully into Hostinger MySQL.', [
                'id' => $id,
                'dutyDate' => $dutyDate,
                'busNumber' => $busNumber,
                'income' => $income,
                'totalKm' => $totalKm
            ]);
            break;

        case 'delete_duty':
            if (empty($requestData['id'])) {
                sendJsonResponse('error', 'Missing duty ID.', [], 400);
            }
            $stmt = $mysqli->prepare("DELETE FROM `duties` WHERE `id` = ?");
            $stmt->bind_param('s', $requestData['id']);
            $stmt->execute();
            $stmt->close();
            sendJsonResponse('success', 'Duty deleted successfully.');
            break;

        default:
            sendJsonResponse('error', 'Invalid action: ' . htmlspecialchars($action), [], 400);
            break;
    }
} catch (Throwable $t) {
    sendJsonResponse('error', 'Server error: ' . $t->getMessage(), [], 500);
} finally {
    if ($mysqli) {
        $mysqli->close();
    }
}
