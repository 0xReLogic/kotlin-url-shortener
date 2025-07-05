# Dokumentasi API Pemendek URL

## Base URL

- Development: `http://localhost:8080`
- Produksi: `${BASE_URL}`

---

## Endpoint

### 1. Pemendekan URL

**POST** `/api/shorten`

**Request:**
```json
{
  "url": "https://contoh.com/url-panjang",
  "customAlias": "opsional"
}
```

**Response:**
```json
{
  "shortUrl": "http://localhost:8080/abc123",
  "shortCode": "abc123"
}
```

**Error:**
- 400: URL atau alias tidak valid
- 409: Alias sudah dipakai

---

### 2. Redirect

**GET** `/{shortCode}`

- Redirect ke original URL (HTTP 301)
- 404 jika tidak ditemukan, 410 jika expired

---

### 3. Statistik URL

**GET** `/api/stats/{shortCode}`

**Response:**
```json
{
  "originalUrl": "https://contoh.com/url-panjang",
  "clickCount": 42,
  "createdAt": "2025-07-05T07:00:00Z",
  "isActive": true
}
```

- 404 jika tidak ditemukan

---

### 4. List URL (Admin)

**GET** `/api/urls`

- Mengembalikan list semua URL (bisa ditambah pagination/search)

---

## Contoh Error Response

```json
{
  "error": "Short URL tidak ditemukan"
}
```

---

## Kontak Developer

- Email: hi@0xrelogic.my.id
- Telegram: [@relogic](https://t.me/relogic)
- WhatsApp: +65 9095 7469

---

## Lihat juga

- [Instruksi Setup](../README.md)
- [Keputusan Arsitektur](ARCHITECTURE.md)
- [Catatan Performa](PERFORMANCE.md)