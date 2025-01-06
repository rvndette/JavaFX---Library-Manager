# Library Manager - Java

Library Manager adalah aplikasi manajemen perpustakaan yang dikembangkan menggunakan bahasa pemrograman Java dengan antarmuka pengguna berbasis *GUI* yang dibangun menggunakan Scene Builder. Aplikasi ini menggunakan MySQL sebagai basis data untuk menyimpan informasi buku dan anggota perpustakaan.

## Fitur

- **Login Admin**: Admin dapat mengelola buku dan anggota perpustakaan, termasuk menambah, menghapus, dan mencari buku, serta menambahkan user baru.
- **Login User**: Pengguna dapat melihat daftar buku, mencari buku, dan melakukan peminjaman.
- Admin dapat menambah user baru (dengan peran "user").
- Menambah buku baru (admin).
- Menghapus buku (admin).
- Mencari buku berdasarkan judul atau pengarang.
- Menampilkan daftar buku.
- Pengelolaan data anggota perpustakaan (admin).

## Prasyarat

Pastikan Anda telah menginstal:
- **JDK (Java Development Kit)**: Untuk menjalankan dan mengembangkan aplikasi Java.
- **Scene Builder**: Untuk merancang antarmuka pengguna berbasis GUI.
- **MySQL Database**: Untuk menyimpan data buku dan anggota perpustakaan.
- **IDE**: Seperti IntelliJ IDEA atau Eclipse, untuk mengembangkan aplikasi.

## Cara Memulai

1. **Clone Repository**
   ```bash
   git clone https://github.com/username/library-manager-java.git
   cd library-manager-java

## Mengkonfigurasi Koneksi MySQL
Pastikan Anda mengonfigurasi koneksi ke database di aplikasi. Edit file konfigurasi Database.java untuk menyesuaikan informasi koneksi MySQL.

## Kompilasi dan Jalankan Aplikasi
Membangun dan menjalankan aplikasi: Jika Anda menggunakan Maven, jalankan:

bash
Copy code
mvn clean install
mvn javafx:run
Menggunakan IDE: Anda dapat langsung menjalankan aplikasi dari IDE yang Anda gunakan setelah memastikan dependensi sudah diinstal.

## Antarmuka Pengguna
Aplikasi ini menggunakan Scene Builder untuk merancang antarmuka pengguna. Anda dapat membuka file FXML di Scene Builder untuk memodifikasi tampilan sesuai kebutuhan.

Fungsi Utama
Login Admin & User: Mengelola akses untuk admin dan user.
CRUD Buku: Menambah, menghapus, dan mengedit data buku.
Pencarian Buku: Mencari buku berdasarkan judul atau pengarang.
Menampilkan Buku: Menampilkan daftar buku yang ada di perpustakaan.
Menambah User Baru: Admin dapat menambah pengguna baru dengan peran "user".

## Developer
Putri Nayla Sabri
Email: putrinaylasabrii@gmail.com
