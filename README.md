# गणेशोत्सव मंडळ — T-Shirt Booking Website

A t-shirt booking site for your mandal. People fill a form (name, size,
phone), it's saved straight into an Excel file — no database. Only the
mandal's admin/karyakarta can log in and upload the t-shirt design photos.

- **Frontend:** React (Vite)
- **Backend:** Java Spring Boot
- **Storage:** a single `bookings.xlsx` file (via Apache POI) + an
  `images/` folder for uploaded designs

## Project structure

```
mandal-tshirt/
├── backend/     Spring Boot API (writes to Excel, handles image upload)
└── frontend/    React site (booking form + admin page)
```

## 1. Run the backend

Requirements: Java 17+ and Maven (or use the included `mvnw` if you add one).

```bash
cd backend
mvn spring-boot:run
```

This starts the API on **http://localhost:8080**. On first run it creates:
- `backend/data/bookings.xlsx` — every submitted booking becomes a row here
- `backend/data/images/` — where uploaded t-shirt design photos are stored

**Change the admin password** in `backend/src/main/resources/application.properties`:
```properties
app.admin.key=Ganpati@Bappa2026
```
This is the password your karyakartas will use on the `/admin` page to
upload designs and view bookings. Change it before you deploy.

## 2. Run the frontend

Requirements: Node.js 18+.

```bash
cd frontend
npm install
npm run dev
```

Opens on **http://localhost:5173**. It talks to the backend at
`http://localhost:8080` by default — change this via `VITE_API_URL` if you
deploy them on different domains (copy `.env.example` to `.env`).

## 3. Customize it for your mandal

Edit `frontend/src/config.js` — mandal name (Marathi + English), tagline,
address, phone number, Instagram link, and the t-shirt size chart. Nothing
else needs to change to make this "yours."

## 4. How booking data is stored

Every submission appends one row to `backend/data/bookings.xlsx`:

| Sr No | Full Name | T-Shirt Size | Phone Number | Booked On |
|---|---|---|---|---|

Open this file directly in Excel/Google Sheets any time, or use the
**"Excel डाउनलोड करा"** button on the `/admin` page to download the latest
copy. Back this file up regularly (copy it out of `backend/data/`) since it
is the only record of your bookings.

## 5. Admin access

Go to `/admin` on the site, enter the password from
`application.properties`. From there a karyakarta can:
- Upload / delete t-shirt design photos (shown on the public homepage)
- View every booking in a table
- Download the current `bookings.xlsx`

There's no separate database of admin accounts — it's a single shared
password for your committee, which is enough for a small mandal site with
a couple of trusted admins.

## 6. Deploying

- **Backend:** package with `mvn clean package`, run the resulting jar
  from `backend/target/` on any server with Java 17 (a small VPS is
  plenty). Make sure the `data/` folder is on persistent storage, not a
  container's ephemeral disk, or bookings will be lost on restart.
- **Frontend:** `npm run build` in `frontend/` produces a static `dist/`
  folder — host it anywhere (Netlify, Vercel, Nginx, etc.) and point
  `VITE_API_URL` at your backend's public URL.

## Notes on the design

The visual style (maroon + marigold + turmeric palette, the hand-drawn
toran garland at the top, the pinboard-style design gallery, the
receipt/token-style booking confirmation) was built specifically for a
Ganeshotsav mandal rather than from a generic template — edit
`frontend/src/index.css` and `frontend/src/config.js` freely to match your
own mandal's colors and branding.
