<?php
/**
 * ============================================================================
 * UPSRTC DUTY MANAGEMENT PORTAL - SECURE BACKEND API (save_data.php)
 * Hostinger Apache / PHP / MySQL Integration Endpoint
 * ============================================================================
 * 
 * Features:
 *  - MySQLi Prepared Statements (100% protection against SQL Injection)
 *  - CORS and Preflight Request Handling
 *  - Input sanitization and data type validation
 *  - Dynamic action routing (save_duty, get_duties, delete_duty, save_user, test)
 *  - Clean JSON responses: { "status": "success" | "error", "message": "...", ... }
 */

// -----------------------------------------------------------------------------
// 1. Cross-Origin Resource Sharing (CORS) & JSON Headers
// -----------------------------------------------------------------------------
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS, DELETE');
header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
header('Content-Type: application/json; charset=UTF-8');

// Respond immediately to HTTP OPTIONS preflight request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    echo json_encode(['status' => 'success', 'message' => 'Preflight OK']);
    exit;
}

// -----------------------------------------------------------------------------
// 2. Hostinger MySQL Database Configuration Placeholders
// -----------------------------------------------------------------------------
// NOTE FOR HOSTINGER DEPLOYMENT:
// Replace these placeholders with your actual MySQL database credentials
// created inside Hostinger hPanel -> Databases -> MySQL Databases.
define('DB_HOST', 'localhost');                  // Usually 'localhost' on Hostinger
define('DB_USER', 'u123456789_upsrtc_user');     // Replace with your Hostinger DB Username
define('DB_PASS', 'Your_Secure_DB_Password_123'); // Replace with your Hostinger DB Password
define('DB_NAME', 'u123456789_upsrtc_db');       // Replace with your Hostinger DB Name
define('DB_PORT', 3306);

// Helper function to return JSON response and exit
function sendJsonResponse($status, $message, $extra = [], $statusCode = 200) {
    http_response_code($statusCode);
    $payload = array_merge([
        'status'  => $status,
        'message' => $message,
        'timestamp' => date('Y-m-d H:i:s')
    ], $extra);
    echo json_encode($payload, JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT);
    exit;
}

// -----------------------------------------------------------------------------
// 3. Establish Secure Database Connection (MySQLi)
// -----------------------------------------------------------------------------
mysqli_report(MYSQLI_REPORT_OFF); // Disable fatal uncaught driver exceptions for clean JSON handling

$mysqli = @new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME, DB_PORT);

if ($mysqli->connect_errno) {
    // If running in development or before Hostinger credentials are configured:
    sendJsonResponse(
        'error',
        'Database connection failed. Please check Hostinger MySQL credentials in save_data.php. Error: ' . $mysqli->connect_error,
        ['code' => $mysqli->connect_errno],
        500
    );
}

// Set character set to utf8mb4 for unicode compatibility (Hindi routes, symbols, etc.)
$mysqli->set_charset('utf8mb4');

// -----------------------------------------------------------------------------
// 4. Parse Incoming JSON Request Payload
// -----------------------------------------------------------------------------
$rawInput = file_get_contents('php://input');
$requestData = [];

if (!empty($rawInput)) {
    $decoded = json_decode($rawInput, true);
    if (json_last_error() === JSON_ERROR_NONE && is_array($decoded)) {
        $requestData = $decoded;
    }
}

// Fallback to standard $_POST if not sent as application/json
if (empty($requestData)) {
    $requestData = $_POST;
}

// Merge query parameters for GET requests
$action = isset($_GET['action']) ? trim($_GET['action']) : (isset($requestData['action']) ? trim($requestData['action']) : 'save_duty');

// -----------------------------------------------------------------------------
// 5. Action Router
// -----------------------------------------------------------------------------
try {
    switch ($action) {
        case 'test':
            handleTestConnection($mysqli);
            break;

        case 'save_duty':
            handleSaveDuty($mysqli, $requestData);
            break;

        case 'get_duties':
            handleGetDuties($mysqli, array_merge($_GET, $requestData));
            break;

        case 'delete_duty':
            handleDeleteDuty($mysqli, $requestData);
            break;

        case 'save_user':
            handleSaveUser($mysqli, $requestData);
            break;

        case 'get_user':
            handleGetUser($mysqli, array_merge($_GET, $requestData));
            break;

        case 'sync_all':
            handleSyncAllDuties($mysqli, $requestData);
            break;

        default:
            // If data has duty fields like dutyDate/busNumber, treat as save_duty
            if (isset($requestData['dutyDate']) || isset($requestData['busNumber'])) {
                handleSaveDuty($mysqli, $requestData);
            } else {
                sendJsonResponse('error', 'Invalid action specified: ' . htmlspecialchars($action), [], 400);
            }
            break;
    }
} catch (Throwable $t) {
    sendJsonResponse('error', 'Server Error: ' . $t->getMessage(), [], 500);
} finally {
    if (isset($mysqli) && $mysqli instanceof mysqli) {
        $mysqli->close();
    }
}

// =============================================================================
// HANDLER FUNCTIONS USING MYSQLI PREPARED STATEMENTS
// =============================================================================

/**
 * Test Connection Endpoint
 */
function handleTestConnection($mysqli) {
    $res = $mysqli->query("SELECT DATABASE() AS db, VERSION() AS version, CURRENT_TIMESTAMP AS server_time");
    $info = $res ? $res->fetch_assoc() : [];
    sendJsonResponse('success', 'Hostinger MySQL Database Connected Successfully!', [
        'database' => $info['db'] ?? DB_NAME,
        'mysql_version' => $info['version'] ?? 'Unknown',
        'server_time' => $info['server_time'] ?? date('Y-m-d H:i:s')
    ]);
}

/**
 * Save / Insert / Update a Duty Record
 */
function handleSaveDuty($mysqli, $data) {
    // Validate required fields
    if (empty($data['dutyDate']) || empty($data['busNumber'])) {
        sendJsonResponse('error', 'Validation failed: dutyDate and busNumber are required fields.', [], 400);
    }

    // Extract and sanitize fields
    $id = !empty($data['id']) ? trim($data['id']) : 'duty_' . round(microtime(true) * 1000);
    $employeeId = !empty($data['employeeId']) ? trim($data['employeeId']) : 'CND 1901';
    $userId = !empty($data['userId']) ? trim($data['userId']) : 'u1';
    $dutyDate = trim($data['dutyDate']);
    $dutyNumber = !empty($data['dutyNumber']) ? trim($data['dutyNumber']) : '01';
    $shift = !empty($data['shift']) ? trim($data['shift']) : 'Morning';
    $busNumber = trim($data['busNumber']);
    $routeFrom = !empty($data['routeFrom']) ? trim($data['routeFrom']) : '';
    $routeTo = !empty($data['routeTo']) ? trim($data['routeTo']) : '';
    $departureTime = !empty($data['departureTime']) ? trim($data['departureTime']) : '';
    $arrivalTime = !empty($data['arrivalTime']) ? trim($data['arrivalTime']) : '';
    $openingKm = isset($data['openingKm']) ? floatval($data['openingKm']) : 0.0;
    $closingKm = isset($data['closingKm']) ? floatval($data['closingKm']) : 0.0;
    $totalKm = isset($data['totalKm']) ? floatval($data['totalKm']) : max(0, $closingKm - $openingKm);
    $passengerCount = isset($data['passengerCount']) ? intval($data['passengerCount']) : 0;
    $totalSeats = !empty($data['totalSeats']) ? intval($data['totalSeats']) : 52;
    $loadFactor = isset($data['loadFactor']) ? floatval($data['loadFactor']) : ($totalSeats > 0 ? round(($passengerCount / $totalSeats) * 100, 2) : 0.0);
    $income = isset($data['income']) ? floatval($data['income']) : 0.0;
    $expenses = isset($data['expenses']) ? floatval($data['expenses']) : 0.0;
    $remarks = isset($data['remarks']) ? trim($data['remarks']) : '';

    // Prepared Statement for MySQL INSERT ... ON DUPLICATE KEY UPDATE
    $query = "INSERT INTO `duties` (
                `id`, `employee_id`, `user_id`, `duty_date`, `duty_number`,
                `shift`, `bus_number`, `route_from`, `route_to`, `departure_time`,
                `arrival_time`, `opening_km`, `closing_km`, `total_km`, `passenger_count`,
                `total_seats`, `load_factor`, `income`, `expenses`, `remarks`
              ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
              ON DUPLICATE KEY UPDATE
                `duty_date` = VALUES(`duty_date`),
                `duty_number` = VALUES(`duty_number`),
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
                `total_seats` = VALUES(`total_seats`),
                `load_factor` = VALUES(`load_factor`),
                `income` = VALUES(`income`),
                `expenses` = VALUES(`expenses`),
                `remarks` = VALUES(`remarks`),
                `updated_at` = CURRENT_TIMESTAMP";

    $stmt = $mysqli->prepare($query);

    if (!$stmt) {
        sendJsonResponse('error', 'Prepare failed: ' . $mysqli->error, [], 500);
    }

    // Bind parameters:
    // s = string, d = double, i = integer
    // 1-11: s (id, empId, userId, date, dutyNum, shift, bus, from, to, dep, arr) -> 11 strings
    // 12-14: d (openKm, closeKm, totalKm) -> 3 doubles
    // 15-16: i (passengers, seats) -> 2 integers
    // 17-19: d (loadFactor, income, expenses) -> 3 doubles
    // 20: s (remarks) -> 1 string
    // Total types: sssssssssssdddiiddds (20 params)
    $stmt->bind_param(
        'sssssssssssdddiiddds',
        $id,
        $employeeId,
        $userId,
        $dutyDate,
        $dutyNumber,
        $shift,
        $busNumber,
        $routeFrom,
        $routeTo,
        $departureTime,
        $arrivalTime,
        $openingKm,
        $closingKm,
        $totalKm,
        $passengerCount,
        $totalSeats,
        $loadFactor,
        $income,
        $expenses,
        $remarks
    );

    if (!$stmt->execute()) {
        $err = $stmt->error;
        $stmt->close();
        sendJsonResponse('error', 'Failed to save duty record: ' . $err, [], 500);
    }

    $affected = $stmt->affected_rows;
    $stmt->close();

    sendJsonResponse('success', 'Duty record saved successfully into Hostinger MySQL database.', [
        'id' => $id,
        'dutyDate' => $dutyDate,
        'busNumber' => $busNumber,
        'income' => $income,
        'totalKm' => $totalKm,
        'affected_rows' => $affected
    ]);
}

/**
 * Fetch Duties (Filtered by Employee and/or Month)
 */
function handleGetDuties($mysqli, $params) {
    $employeeId = isset($params['employeeId']) ? trim($params['employeeId']) : '';
    $month = isset($params['month']) ? trim($params['month']) : ''; // Format: YYYY-MM

    $sql = "SELECT 
                `id`, 
                `employee_id` AS `employeeId`, 
                `user_id` AS `userId`, 
                `duty_date` AS `dutyDate`, 
                `duty_number` AS `dutyNumber`,
                `shift`, 
                `bus_number` AS `busNumber`, 
                `route_from` AS `routeFrom`, 
                `route_to` AS `routeTo`, 
                `departure_time` AS `departureTime`,
                `arrival_time` AS `arrivalTime`, 
                `opening_km` AS `openingKm`, 
                `closing_km` AS `closingKm`, 
                `total_km` AS `totalKm`, 
                `passenger_count` AS `passengerCount`,
                `total_seats` AS `totalSeats`, 
                `load_factor` AS `loadFactor`, 
                `income`, 
                `expenses`, 
                `remarks`,
                `created_at` AS `createdAt`
            FROM `duties` WHERE 1=1";

    $types = '';
    $bindParams = [];

    if (!empty($employeeId)) {
        $sql .= " AND `employee_id` = ?";
        $types .= 's';
        $bindParams[] = $employeeId;
    }

    if (!empty($month)) {
        $sql .= " AND `duty_date` LIKE ?";
        $types .= 's';
        $bindParams[] = $month . '%';
    }

    $sql .= " ORDER BY `duty_date` DESC, `id` DESC";

    $stmt = $mysqli->prepare($sql);
    if (!$stmt) {
        sendJsonResponse('error', 'Prepare failed: ' . $mysqli->error, [], 500);
    }

    if (!empty($bindParams)) {
        $stmt->bind_param($types, ...$bindParams);
    }

    $stmt->execute();
    $result = $stmt->get_result();
    $duties = [];

    while ($row = $result->fetch_assoc()) {
        // Cast numerical columns properly for client JSON
        $row['openingKm'] = floatval($row['openingKm']);
        $row['closingKm'] = floatval($row['closingKm']);
        $row['totalKm'] = floatval($row['totalKm']);
        $row['passengerCount'] = intval($row['passengerCount']);
        $row['totalSeats'] = intval($row['totalSeats']);
        $row['loadFactor'] = floatval($row['loadFactor']);
        $row['income'] = floatval($row['income']);
        $row['expenses'] = floatval($row['expenses']);
        $duties[] = $row;
    }

    $stmt->close();

    sendJsonResponse('success', 'Duties retrieved successfully.', [
        'count' => count($duties),
        'duties' => $duties
    ]);
}

/**
 * Delete a Duty Record by ID
 */
function handleDeleteDuty($mysqli, $data) {
    if (empty($data['id'])) {
        sendJsonResponse('error', 'Duty ID is required to delete record.', [], 400);
    }

    $id = trim($data['id']);
    $stmt = $mysqli->prepare("DELETE FROM `duties` WHERE `id` = ?");
    if (!$stmt) {
        sendJsonResponse('error', 'Prepare failed: ' . $mysqli->error, [], 500);
    }

    $stmt->bind_param('s', $id);
    $stmt->execute();
    $affected = $stmt->affected_rows;
    $stmt->close();

    if ($affected > 0) {
        sendJsonResponse('success', 'Duty record deleted successfully.', ['id' => $id]);
    } else {
        sendJsonResponse('error', 'No duty found with the specified ID or already deleted.', ['id' => $id], 404);
    }
}

/**
 * Save / Update Conductor Profile
 */
function handleSaveUser($mysqli, $data) {
    if (empty($data['employeeId']) || empty($data['fullName'])) {
        sendJsonResponse('error', 'employeeId and fullName are required to save profile.', [], 400);
    }

    $id = !empty($data['id']) ? trim($data['id']) : 'u_' . round(microtime(true) * 1000);
    $employeeId = trim($data['employeeId']);
    $fullName = trim($data['fullName']);
    $designation = !empty($data['designation']) ? trim($data['designation']) : 'Conductor';
    $userType = !empty($data['userType']) ? trim($data['userType']) : 'Conductor';
    $depot = !empty($data['depot']) ? trim($data['depot']) : 'Meerut Depot';
    $depotCode = !empty($data['depotCode']) ? trim($data['depotCode']) : 'MRT-01';
    $region = !empty($data['region']) ? trim($data['region']) : 'Meerut';
    $mobile = !empty($data['mobile']) ? trim($data['mobile']) : '';
    $email = !empty($data['email']) ? trim($data['email']) : '';
    $tokenNo = !empty($data['tokenNo']) ? trim($data['tokenNo']) : '';
    $photoUrl = !empty($data['photoUrl']) ? $data['photoUrl'] : null;

    $query = "INSERT INTO `users` (
                `id`, `employee_id`, `full_name`, `designation`, `user_type`,
                `depot`, `depot_code`, `region`, `mobile`, `email`, `token_no`, `photo_url`
              ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
              ON DUPLICATE KEY UPDATE
                `full_name` = VALUES(`full_name`),
                `designation` = VALUES(`designation`),
                `user_type` = VALUES(`user_type`),
                `depot` = VALUES(`depot`),
                `depot_code` = VALUES(`depot_code`),
                `region` = VALUES(`region`),
                `mobile` = VALUES(`mobile`),
                `email` = VALUES(`email`),
                `token_no` = VALUES(`token_no`),
                `photo_url` = VALUES(`photo_url`),
                `updated_at` = CURRENT_TIMESTAMP";

    $stmt = $mysqli->prepare($query);
    if (!$stmt) {
        sendJsonResponse('error', 'Prepare failed: ' . $mysqli->error, [], 500);
    }

    $stmt->bind_param(
        'ssssssssssss',
        $id,
        $employeeId,
        $fullName,
        $designation,
        $userType,
        $depot,
        $depotCode,
        $region,
        $mobile,
        $email,
        $tokenNo,
        $photoUrl
    );

    if (!$stmt->execute()) {
        $err = $stmt->error;
        $stmt->close();
        sendJsonResponse('error', 'Failed to save user profile: ' . $err, [], 500);
    }

    $stmt->close();
    sendJsonResponse('success', 'User profile updated successfully.', ['employeeId' => $employeeId]);
}

/**
 * Fetch Conductor Profile
 */
function handleGetUser($mysqli, $params) {
    $employeeId = !empty($params['employeeId']) ? trim($params['employeeId']) : 'CND 1901';

    $stmt = $mysqli->prepare("SELECT 
                                `id`, 
                                `employee_id` AS `employeeId`, 
                                `full_name` AS `fullName`, 
                                `designation`, 
                                `user_type` AS `userType`,
                                `depot`, 
                                `depot_code` AS `depotCode`, 
                                `region`, 
                                `mobile`, 
                                `email`, 
                                `token_no` AS `tokenNo`, 
                                `photo_url` AS `photoUrl` 
                              FROM `users` WHERE `employee_id` = ? LIMIT 1");
    if (!$stmt) {
        sendJsonResponse('error', 'Prepare failed: ' . $mysqli->error, [], 500);
    }

    $stmt->bind_param('s', $employeeId);
    $stmt->execute();
    $res = $stmt->get_result();
    $user = $res->fetch_assoc();
    $stmt->close();

    if ($user) {
        sendJsonResponse('success', 'User retrieved successfully.', ['user' => $user]);
    } else {
        sendJsonResponse('error', 'User not found.', [], 404);
    }
}

/**
 * Batch Sync All Duties from Client LocalStorage to Hostinger MySQL
 */
function handleSyncAllDuties($mysqli, $data) {
    $duties = $data['duties'] ?? [];
    if (!is_array($duties) || empty($duties)) {
        sendJsonResponse('error', 'No duties array supplied for batch synchronization.', [], 400);
    }

    $savedCount = 0;
    foreach ($duties as $d) {
        if (!empty($d['dutyDate']) && !empty($d['busNumber'])) {
            // Re-use logic safely
            $id = !empty($d['id']) ? trim($d['id']) : 'duty_' . round(microtime(true) * 1000) . '_' . $savedCount;
            $employeeId = !empty($d['employeeId']) ? trim($d['employeeId']) : 'CND 1901';
            $userId = !empty($d['userId']) ? trim($d['userId']) : 'u1';
            $dutyDate = trim($d['dutyDate']);
            $dutyNumber = !empty($d['dutyNumber']) ? trim($d['dutyNumber']) : '01';
            $shift = !empty($d['shift']) ? trim($d['shift']) : 'Morning';
            $busNumber = trim($d['busNumber']);
            $routeFrom = !empty($d['routeFrom']) ? trim($d['routeFrom']) : '';
            $routeTo = !empty($d['routeTo']) ? trim($d['routeTo']) : '';
            $departureTime = !empty($d['departureTime']) ? trim($d['departureTime']) : '';
            $arrivalTime = !empty($d['arrivalTime']) ? trim($d['arrivalTime']) : '';
            $openingKm = floatval($d['openingKm'] ?? 0);
            $closingKm = floatval($d['closingKm'] ?? 0);
            $totalKm = floatval($d['totalKm'] ?? max(0, $closingKm - $openingKm));
            $passengerCount = intval($d['passengerCount'] ?? 0);
            $totalSeats = intval($d['totalSeats'] ?? 52);
            $loadFactor = floatval($d['loadFactor'] ?? 0);
            $income = floatval($d['income'] ?? 0);
            $expenses = floatval($d['expenses'] ?? 0);
            $remarks = trim($d['remarks'] ?? '');

            $stmt = $mysqli->prepare("INSERT INTO `duties` (
                `id`, `employee_id`, `user_id`, `duty_date`, `duty_number`,
                `shift`, `bus_number`, `route_from`, `route_to`, `departure_time`,
                `arrival_time`, `opening_km`, `closing_km`, `total_km`, `passenger_count`,
                `total_seats`, `load_factor`, `income`, `expenses`, `remarks`
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE `income` = VALUES(`income`), `total_km` = VALUES(`total_km`)");

            if ($stmt) {
                $stmt->bind_param(
                    'sssssssssssdddiiddds',
                    $id, $employeeId, $userId, $dutyDate, $dutyNumber,
                    $shift, $busNumber, $routeFrom, $routeTo, $departureTime,
                    $arrivalTime, $openingKm, $closingKm, $totalKm, $passengerCount,
                    $totalSeats, $loadFactor, $income, $expenses, $remarks
                );
                if ($stmt->execute()) {
                    $savedCount++;
                }
                $stmt->close();
            }
        }
    }

    sendJsonResponse('success', "Batch sync completed. {$savedCount} duty records synchronized with Hostinger MySQL.", [
        'synchronized_count' => $savedCount
    ]);
}
