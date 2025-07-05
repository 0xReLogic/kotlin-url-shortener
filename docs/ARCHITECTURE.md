# Keputusan Arsitektur

## Stack
- **Kotlin 1.9+**
- **Ktor 2.3+** (web framework)
- **Exposed ORM** (database)
- **H2** (DB dev/produksi, bisa diganti PostgreSQL)
- **Logback** (logging)
- **Ktor Test Utilities** (testing)
- **Docker** (containerization)

## Struktur
- `Application.kt`: Entry point Ktor, DI, config, routing
- `services/`: Logika bisnis, mudah di-test, tidak tergantung framework
- `db/`: Definisi tabel Exposed, DB factory, seed
- `models/`: Data class untuk API
- `routes/`: Definisi route Ktor
- `static/`: Web UI (HTML/CSS/JS)
- `test/`: Unit & integration test

## Keputusan Kunci
- **Short code Base62**: 6 karakter, 56+ miliar kombinasi, retry jika collision
- **Validasi**: Regex + URI parse, cegah re-shortening, sanitasi input
- **Click analytics**: Tabel terpisah untuk event timestamp
- **Config**: Semua via env var atau `application.conf` (dev/prod)
- **Error handling**: Sealed result type, HTTP status code
- **Graceful shutdown**: Ktor handle SIGTERM/SIGINT
- **Health check**: Endpoint `/health`
- **Docker**: Multi-stage, volume H2 persisten

## Ekstensibilitas
- Bisa ganti H2 ke PostgreSQL/MySQL hanya dengan config
- Bisa tambah autentikasi untuk endpoint admin
- Bisa tambah rate limiting, dashboard analytics, dsb

## Testability
- Semua logika bisnis di service, mudah di-unit test
- API di-test dengan Ktor test engine & in-memory DB

---

## Kontak Developer

- Email: hi@0xrelogic.my.id
- Telegram: [@relogic](https://t.me/relogic)
- WhatsApp: +65 9095 7469