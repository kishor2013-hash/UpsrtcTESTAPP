<?php
// PHP 8.3 सुरक्षा और सख्त एरर हैंडलिंग
mysqli_report(MYSQLI_REPORT_OFF);

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, GET, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");
header("Content-Type: application/json; charset=UTF-8");

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

$host = "localhost";
$db_user = "u481314904_kishor";
$db_pass = "Hariom@9452";
$db_name = "u481314904_upsrtc_db";

try {
    $conn = @new mysqli($host, $db_user, $db_pass, $db_name);

    if ($conn->connect_error) {
        http_response_code(200);
        echo json_encode(["status" => "error", "message" => "Database connection failed: " . $conn->connect_error]);
        exit();
    }

    $conn->set_charset("utf8mb4");

    $rawInput = file_get_contents("php://input");
    $input = json_decode($rawInput, true);

    if (!$input) {
        echo json_encode(["status" => "error", "message" => "कोई डेटा प्राप्त नहीं हुआ"]);
        exit();
    }

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

        if ($conn->query($sql)) {
            echo json_encode(["status" => "success", "message" => "यूज़र सर्वर पर सेव हो गया"]);
        } else {
            echo json_encode(["status" => "error", "message" => "रजिस्ट्रेशन SQL एरर: " . $conn->error]);
        }
    }

    // 2. लॉगिन वेरिफिकेशन
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
                    "id" => 'u_' . $row['employee_id'],
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

    // 3. नई ड्यूटी सेव करना (eDuties / duty_count सहित)
    else if ($action === 'save_duty') {
        $dutyUniqueId = $conn->real_escape_string($input['id'] ?? ('duty_' . time()));
        $empId = $conn->real_escape_string($input['employeeId'] ?? '');
        $dutyDate = !empty($input['dutyDate']) ? $conn->real_escape_string($input['dutyDate']) : date('Y-m-d');
        $dutyCount = floatval($input['dutyCount'] ?? 1);
        $bus = $conn->real_escape_string($input['busNumber'] ?? '');
        $route = $conn->real_escape_string($input['route'] ?? '');
        $totalKm = floatval($input['totalKm'] ?? 0);
        $lf = floatval($input['loadFactor'] ?? 0);
        $income = floatval($input['income'] ?? 0);
        $remarks = $conn->real_escape_string($input['remarks'] ?? '');

        $sql = "INSERT INTO duties (duty_unique_id, employee_id, duty_date, duty_count, bus_number, route, total_km, load_factor, income, remarks) 
                VALUES ('$dutyUniqueId', '$empId', '$dutyDate', $dutyCount, '$bus', '$route', $totalKm, $lf, $income, '$remarks')";

        if ($conn->query($sql)) {
            echo json_encode(["status" => "success", "message" => "ड्यूटी सर्वर पर सेव हो गई"]);
        } else {
            echo json_encode(["status" => "error", "message" => "ड्यूटी सेव एरर: " . $conn->error]);
        }
    }

    // 4. सर्वर से ड्यूटी डिलीट करना
    else if ($action === 'delete_duty') {
        $dutyId = $conn->real_escape_string($input['dutyId'] ?? '');
        $empId = $conn->real_escape_string($input['employeeId'] ?? '');

        $sql = "DELETE FROM duties WHERE (duty_unique_id='$dutyId' OR id='$dutyId') AND employee_id='$empId'";
        if ($conn->query($sql)) {
            echo json_encode(["status" => "success", "message" => "ड्यूटी सर्वर से डिलीट कर दी गई"]);
        } else {
            echo json_encode(["status" => "error", "message" => "डिलीट फेल: " . $conn->error]);
        }
    }

    // 5. किसी खास यूज़र की ड्यूटियाँ लोड करना
    else if ($action === 'get_user_duties') {
        $empId = $conn->real_escape_string($input['employeeId'] ?? '');
        $sql = "SELECT * FROM duties WHERE employee_id='$empId' ORDER BY duty_date DESC";
        $res = $conn->query($sql);
        $duties = [];
        while ($row = $res->fetch_assoc()) {
            $duties[] = $row;
        }
        echo json_encode(["status" => "success", "duties" => $duties]);
    }

    $conn->close();

} catch (\Throwable $e) {
    http_response_code(200);
    echo json_encode(["status" => "error", "message" => "PHP एरर: " . $e->getMessage()]);
}
?>
