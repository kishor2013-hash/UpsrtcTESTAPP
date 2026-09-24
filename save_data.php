<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, GET, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");
header("Content-Type: application/json; charset=UTF-8");

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// आपकी फोटो के अनुसार डेटाबेस क्रेडेंशियल्स
$host = "localhost";
$db_user = "u481314904_kishor";
$db_pass = "Hariom@9452";
$db_name = "u481314904_upsrtc_db";

$conn = new mysqli($host, $db_user, $db_pass, $db_name);

if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Database connection failed: " . $conn->connect_error]);
    exit();
}

// टेबल्स अगर नहीं बनी हैं तो अपने आप बन जाएँगी
$conn->query("CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(50) UNIQUE,
    full_name VARCHAR(100),
    email VARCHAR(100),
    mobile VARCHAR(20),
    password VARCHAR(255),
    dob DATE,
    depot VARCHAR(100),
    depot_code VARCHAR(50),
    designation VARCHAR(100),
    employee_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)");

$conn->query("CREATE TABLE IF NOT EXISTS duties (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50),
    employee_id VARCHAR(50),
    duty_date DATE,
    duty_number VARCHAR(50),
    shift VARCHAR(50),
    bus_number VARCHAR(50),
    route_from VARCHAR(100),
    route_to VARCHAR(100),
    departure_time VARCHAR(20),
    arrival_time VARCHAR(20),
    opening_km FLOAT,
    closing_km FLOAT,
    total_km FLOAT,
    passengers INT,
    seats INT,
    load_factor FLOAT,
    income FLOAT,
    expenses FLOAT,
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)");

$input = json_decode(file_get_contents("php://input"), true);
$action = $input['action'] ?? '';

// 1. यूज़र रजिस्ट्रेशन
if ($action === 'register_user') {
    $empId = $conn->real_escape_string($input['employeeId'] ?? '');
    $name = $conn->real_escape_string($input['fullName'] ?? '');
    $email = $conn->real_escape_string($input['email'] ?? '');
    $pass = $conn->real_escape_string($input['password'] ?? '');
    $mobile = $conn->real_escape_string($input['mobile'] ?? '');
    $dob = !empty($input['dob']) ? $conn->real_escape_string($input['dob']) : '1990-01-01';
    $depot = $conn->real_escape_string($input['depot'] ?? '');
    $depotCode = $conn->real_escape_string($input['depotCode'] ?? '');
    $designation = $conn->real_escape_string($input['designation'] ?? '');
    $type = $conn->real_escape_string($input['employeeType'] ?? '');

    $sql = "INSERT INTO users (employee_id, full_name, email, password, mobile, dob, depot, depot_code, designation, employee_type) 
            VALUES ('$empId', '$name', '$email', '$pass', '$mobile', '$dob', '$depot', '$depotCode', '$designation', '$type')
            ON DUPLICATE KEY UPDATE full_name='$name', password='$pass', mobile='$mobile', depot='$depot'";

    if ($conn->query($sql) === TRUE) {
        echo json_encode(["status" => "success", "message" => "यूज़र आईडी और पासवर्ड सर्वर पर सेव हो गया"]);
    } else {
        echo json_encode(["status" => "error", "message" => "रजिस्ट्रेशन एरर: " . $conn->error]);
    }
}

// 2. यूज़र लॉगिन वेरिफिकेशन
else if ($action === 'login_user') {
    $ident = $conn->real_escape_string($input['identifier'] ?? '');
    $pass = $conn->real_escape_string($input['password'] ?? '');

    $sql = "SELECT * FROM users WHERE (employee_id='$ident' OR email='$ident' OR mobile='$ident') AND password='$pass' LIMIT 1";
    $res = $conn->query($sql);

    if ($res && $res->num_rows > 0) {
        $row = $res->fetch_assoc();
        echo json_encode([
            "status" => "success",
            "message" => "लॉगिन सफल",
            "user" => [
                "id" => 'u_' . $row['id'],
                "employeeId" => $row['employee_id'],
                "fullName" => $row['full_name'],
                "email" => $row['email'],
                "mobile" => $row['mobile'],
                "depot" => $row['depot'],
                "depotCode" => $row['depot_code'],
                "designation" => $row['designation'],
                "employeeType" => $row['employee_type'],
                "photoUrl" => ""
            ]
        ]);
    } else {
        echo json_encode(["status" => "error", "message" => "गलत आईडी या पासवर्ड"]);
    }
}

// 3. ड्यूटी डेटा सेव करना
else if ($action === 'save_duty') {
    $empId = $conn->real_escape_string($input['employeeId'] ?? '');
    $dutyDate = !empty($input['dutyDate']) ? $conn->real_escape_string($input['dutyDate']) : date('Y-m-d');
    $dutyNum = $conn->real_escape_string($input['dutyNumber'] ?? '');
    $shift = $conn->real_escape_string($input['shift'] ?? '');
    $bus = $conn->real_escape_string($input['busNumber'] ?? '');
    $from = $conn->real_escape_string($input['routeFrom'] ?? '');
    $to = $conn->real_escape_string($input['routeTo'] ?? '');
    $depTime = $conn->real_escape_string($input['departureTime'] ?? '');
    $arrTime = $conn->real_escape_string($input['arrivalTime'] ?? '');
    $openKm = floatval($input['openingKm'] ?? 0);
    $closeKm = floatval($input['closingKm'] ?? 0);
    $totalKm = floatval($input['totalKm'] ?? 0);
    $pax = intval($input['passengerCount'] ?? 0);
    $seats = intval($input['totalSeats'] ?? 52);
    $lf = floatval($input['loadFactor'] ?? 0);
    $income = floatval($input['income'] ?? 0);
    $exp = floatval($input['expenses'] ?? 0);
    $remarks = $conn->real_escape_string($input['remarks'] ?? '');

    $sql = "INSERT INTO duties (employee_id, duty_date, duty_number, shift, bus_number, route_from, route_to, departure_time, arrival_time, opening_km, closing_km, total_km, passengers, seats, load_factor, income, expenses, remarks) 
            VALUES ('$empId', '$dutyDate', '$dutyNum', '$shift', '$bus', '$from', '$to', '$depTime', '$arrTime', $openKm, $closeKm, $totalKm, $pax, $seats, $lf, $income, $exp, '$remarks')";

    if ($conn->query($sql) === TRUE) {
        echo json_encode(["status" => "success", "message" => "ड्यूटी सर्वर पर सुरक्षित हो गई"]);
    } else {
        echo json_encode(["status" => "error", "message" => "ड्यूटी सेव एरर: " . $conn->error]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "Invalid request"]);
}

$conn->close();
?>
