# UPSRTC Roadways - Digital Duty Portal V4.0 (Web Edition)

A high-performance, client-side Web Application designed for Uttar Pradesh State Road Transport Corporation (UPSRTC) Drivers and Conductors to manage daily duty logs, monthly reports, official PDF exports, and gateway links to corporate portals.

## 🚀 Key Features

- **Strict Staff Authentication**: Initial staff login gateway with CND number / Email / Mobile and Google Sign-in.
- **Operational Control Deck (2-Column Grid)**:
  - 🚌 **FEED DUTY** (Cyan theme): Bus number, routes, timings, opening/closing KM, passengers, seat load factor % calculation, income collection, and diesel expenses.
  - 📊 **SHOW DATA** (Yellow theme): Month-by-month duty logs, KPI performance cards, real-time recalculations, CSV download, and **Official Printable PDF Crew Duty Sheet**.
  - 💳 **PAY SLIP** (Blue theme): Direct link to the official UPSRTC Employee Payroll Portal (`payroll.mectoi.in`).
  - 🏛️ **PF PORTAL** (Green theme): Direct link to EPFO Unified Member Passbook portal.
  - ⚠️ **CHALLAN** (Red theme): Direct gateway to the e-Challan Parivahan system.
  - 👥 **MANAV SAMPADA** (Purple theme): Gateway to Uttar Pradesh eHRMS / Manav Sampada.
- **Profile & Photo Management**: Upload custom profile photos directly from camera/gallery into local storage, update depot and designations, and manage passwords.
- **Interactive Calendar Date Picker**: Quick buttons for Today, Yesterday, or full date picker dialog.
- **Admin Control Console**: Manage portal endpoint links, view employee records, and export/import full database JSON backups.
- **100% Client-Side**: No Node.js server or backend database required for hosting; runs completely on client browser with `localStorage` persistence.

---

## 📂 Project Structure

```text
upsrtc-duty-portal/
├── dist/                          # Ready-to-deploy static build for Hostinger
│   ├── .htaccess                  # Apache / LiteSpeed URL rewrite rules & security headers
│   └── index.html                 # Complete standalone production single-page application
├── web/
│   ├── .htaccess                  # Hostinger configuration
│   ├── index.html                 # Production standalone web application
│   ├── package.json               # Modern Vite + React project configuration
│   ├── vite.config.js             # Configured with base: './' for subfolder/root deployment
│   ├── README.md                  # This documentation file
│   └── src/                       # Modular source code
```

---

## 🌐 Deploying to Hostinger (2 Simple Methods)

### Method 1: Instant Drag-and-Drop (Recommended - Takes 60 Seconds)

1. Log in to your **Hostinger hPanel** (`hpanel.hostinger.com`).
2. Go to **Websites** → Select your domain → Click on **File Manager**.
3. Open the `public_html` directory.
4. Upload all files from the `dist/` (or `web/`) folder:
   - `index.html`
   - `.htaccess`
5. That's it! Visit your domain in any browser (e.g. `https://yourdomain.com`). Your UPSRTC Duty Portal is live instantly!

### Method 2: GitHub Repository & Git Deployment on Hostinger

1. Initialize git and commit the repository to GitHub:
   ```bash
   git init
   git add .
   git commit -m "Initial commit of UPSRTC Duty Portal Web App"
   git branch -M main
   git remote add origin https://github.com/your-username/upsrtc-duty-portal.git
   git push -u origin main
   ```
2. In Hostinger hPanel:
   - Go to **Advanced** → **Git**.
   - Paste your GitHub repository URL.
   - Set the Branch to `main`.
   - Set the Install directory to `/public_html` (or choose the `dist` subfolder).
   - Click **Deploy**.
   - Enable **Auto Deployment** so every time you push to GitHub, Hostinger updates automatically!

---

## ⚙️ Local Development (Optional)

If you wish to run with Vite:
```bash
cd web
npm install
npm run dev
```
To create a fresh production build:
```bash
npm run build
```
The output will be placed in `web/dist/` with relative paths (`./`) ready for any Apache or LiteSpeed server.

---

## 👨‍✈️ Default Demo Logins

| Role | Identifier / Employee ID | Password |
|---|---|---|
| **Conductor (Default)** | `CND 1901` | `123456` |
| **Admin (HQ)** | `ADMIN001` | `admin123` |
