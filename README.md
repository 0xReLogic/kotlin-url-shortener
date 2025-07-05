# Pemendek URL Kotlin Ktor

Aplikasi pemendek URL modern berbasis Kotlin, Ktor, Exposed ORM, dan H2. Tersedia REST API, web UI responsif, analytics, Docker, dan siap produksi.

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Build](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Kotlin](https://img.shields.io/badge/kotlin-1.9-blue)]()
[![Gradle](https://img.shields.io/badge/gradle-8.5-blue)]()
---

## Fitur

- Pemendekan URL dengan short code Base62 (6 karakter, 56+ miliar kombinasi)
- Dukungan custom alias
- Tracking klik & analytics
- REST API & web interface responsif
- Endpoint admin (list/search URL)
- Endpoint health check
- Graceful shutdown & siap monitoring error
- Docker & docker-compose untuk deployment mudah
- Database H2 (dev/prod), migrasi, seed, backup/restore

---

## Konfigurasi

Semua konfigurasi bisa diatur lewat environment variable atau `application.conf`:

- `PORT` - Port HTTP (default: 8080)
- `BASE_URL` - Base URL untuk short link (misal: http://localhost:8080)
- `DATABASE_URL` - JDBC URL untuk H2/PostgreSQL
- `DATABASE_DRIVER` - JDBC driver class (default: org.h2.Driver)
- `DATABASE_USER` - User DB (default: sa)
- `DATABASE_PASSWORD` - Password DB (default: kosong)
- `KTOR_ENV` - `dev` atau `prod` (default: dev)
- `LOG_LEVEL` - Level logging (default: INFO)

---

## Pengembangan Lokal

### Prasyarat

- JDK 17+
- Gradle
- Docker & Docker Compose (untuk dev containerized)

### Menjalankan Lokal

```sh
# Build & run dengan Gradle
./gradlew run

# Atau pakai Docker Compose
docker-compose up --build
```

Aplikasi akan berjalan di [http://localhost:8080](http://localhost:8080).

> **Catatan Penting**: Semua URL yang dipendekkan menggunakan alamat localhost, sehingga URL pendek hanya berfungsi pada komputer yang menjalankan aplikasi. Untuk penggunaan publik, aplikasi harus di-deploy ke server dengan nama domain.

---

## Struktur Routing

- `/` - Halaman utama untuk pemendek URL
- `/assets/*` - File statis (CSS, JavaScript, dll)
- `/api/shorten` - Endpoint API untuk mempendekkan URL
- `/api/stats/{shortCode}` - Endpoint API untuk statistik URL
- `/api/urls` - Endpoint admin untuk melihat semua URL
- `/health` - Endpoint health check
- `/{shortCode}` - Endpoint redirect untuk URL pendek

---

## Migrasi & Seed Database

- Skema otomatis dimigrasi via Exposed saat startup.
- Untuk seed data sample, panggil `SeedData.insertSampleUrls()` di main/REPL.
- Backup/restore H2 DB:
  ```sh
  ./scripts/h2-backup-restore.sh backup /data/shortener.mv.db /backup/shortener-backup.mv.db
  ./scripts/h2-backup-restore.sh restore /backup/shortener-backup.mv.db /data/shortener.mv.db
  ```

---

## Deployment

- Dockerfile multi-stage untuk image optimal.
- Endpoint health check: `/health`
- Graceful shutdown: otomatis oleh Ktor (SIGTERM/SIGINT)
- Monitoring error: bisa integrasi Sentry/log eksternal.

### Contoh Jalankan Produksi

```sh
docker build -t url-shortener .
docker run -d -p 80:8080 \
  -e BASE_URL=https://domainkamu.com \
  -e DATABASE_URL=jdbc:h2:/data/shortener;DB_CLOSE_DELAY=-1; \
  -v h2data:/data \
  url-shortener
```

---

## Pemecahan Masalah

### Masalah Umum

1. **URL pendek tidak berfungsi saat dibuka di browser lain atau perangkat lain**
   - Ini normal karena aplikasi menggunakan localhost. URL pendek hanya berfungsi pada komputer yang menjalankan aplikasi.
   - Solusi: Deploy aplikasi ke server publik dengan nama domain.

2. **Error "Address already in use" saat menjalankan aplikasi**
   - Port 8080 sudah digunakan oleh aplikasi lain.
   - Solusi: Matikan aplikasi yang menggunakan port tersebut atau ubah port di `application.conf`.

3. **File statis (CSS/JS) tidak termuat**
   - Pastikan aplikasi menggunakan konfigurasi static resources yang benar.
   - Solusi yang diterapkan: Pemisahan path static resources ke `/assets/` untuk menghindari konflik dengan rute shortcode.

---

## Struktur Project

- `src/main/kotlin/com/example/shortener/` - Source utama Kotlin
  - `Application.kt` - Entry point dan konfigurasi server
  - `routes/` - Definisi routing API
  - `models/` - Data class dan model
  - `services/` - Logika bisnis
  - `db/` - Konfigurasi database dan entitas
- `src/main/resources/static/` - Web UI (HTML/CSS/JS)
- `src/main/resources/application.conf` - Konfigurasi
- `Dockerfile`, `docker-compose.yml` - Containerization
- `scripts/` - Script backup/restore DB
- `docs/` - Dokumentasi API, arsitektur, performa
- `src/test/` - Unit & integration test

---

## Dokumentasi & Testing

- [Dokumentasi API](docs/API.md)
- [Keputusan Arsitektur](docs/ARCHITECTURE.md)
- [Catatan Performa](docs/PERFORMANCE.md)
- [Panduan Kontribusi](CONTRIBUTING.md)
- [Lisensi MIT](LICENSE)

---

## Developer

Dikembangkan oleh [0xrelogic](https://github.com/0xrelogic)

- Email: hi@0xrelogic.my.id
- Telegram: [@relogic](https://t.me/relogic)
- WhatsApp: +65 9095 7469

---

## Lisensi

MIT