# Catatan Performa

## Short Code Generation
- Base62, 6 karakter: 56+ miliar kombinasi, risiko collision sangat kecil.
- Retry mechanism untuk collision, cek DB O(1).

## Database
- H2 in-memory untuk dev/testing: sangat cepat, tanpa I/O disk.
- H2 file-based untuk produksi: persisten, cocok untuk skala kecil-menengah.
- Bisa diganti PostgreSQL/MySQL untuk skala besar.

## API
- Semua endpoint stateless dan cepat (1 transaksi DB per request).
- Tracking klik: update DB atomik + insert analytics.

## Web UI
- File statis, JS minimal, CSS responsif.
- Tidak ada server-side rendering, load sangat cepat.

## Skalabilitas
- Stateless app, bisa horizontal scaling (dengan DB eksternal).
- Untuk traffic tinggi: gunakan DB terdistribusi, cache, CDN untuk static.

## Bottleneck
- Collision short code: sangat jarang, retry logic sudah ada.
- Analytics klik: sudah di-index per URL, bisa di-aggregate untuk reporting.

## Monitoring
- Endpoint health check untuk liveness/readiness.
- Logging via Logback, bisa integrasi Sentry/Prometheus.

---

## Kontak Developer

- Email: hi@0xrelogic.my.id
- Telegram: [@relogic](https://t.me/relogic)
- WhatsApp: +65 9095 7469