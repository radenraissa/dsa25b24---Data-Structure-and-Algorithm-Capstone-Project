<h1 align="left">DSA Github Project Guideline</h1>

###

<p align="left"># dsa25b24---Data-Structure-and-Algorithm-Capstone-Project
ES234317 - Data Structure and Algorithm : Group Capstone Project 
Group 23
1. 5026241089 - Fiorena Aisha Maharani
2. 5026241097 - Raden Muhammad Raissa Wirabuwana</p>

###

<h2 align="left">Smurf and Ladder</h2>

###

<p align="left">• Main<br>Berfungsi sebagai integrator utama project yang menampilkan menu awal untuk memilih game yang ingin dimainkan (Smurf & Ladder atau Pacman Maze).</p>

###

<p align="left">• GameGUI<br>Class utama gameplay Smurf & Ladder yang mengatur tampilan board, alur permainan, animasi, suara, serta interaksi pemain.</p>

###

<p align="left">• TurnManager<br>Mengatur urutan giliran pemain dalam setiap round permainan menggunakan struktur data Queue agar giliran pemain berjalan FIFO (First In First Out).</p>

###

<p align="left">• Player<br>Merepresentasikan entitas pemain yang menyimpan identitas, posisi, skor, dan riwayat langkah.</p>

###

<p align="left">• MovementManager<br>Mengelola urutan pergerakan pion saat animasi berlangsung dengan menggunakan struktur data Stack karena pergerakan diambil dari langkah terakhir ke langkah berikutnya secara terkontrol.</p>

###

<p align="left">• Dice<br>Mensimulasikan pelemparan dadu dengan nilai angka (1-6) dan warna (hijau/merah). Warna hijau pada dadu merepresentasikan maju dan merah yang berarti mundur, dengan probabilitas masing-masing sebesar 0,8 dan 0,2.</p>

###

<p align="left">• SoundManager<br>Mengelola seluruh audio dalam game, termasuk sound effect dadu, langkah pion, victory sound, dan theme song dengan format file berupa WAV.</p>

###

<p align="left">• GameStats<br>Menampilkan statistik permainan dan leaderboard berdasarkan skor yang diperoleh player menggunakan struktur data Priority Queue untuk mengurutkan pemain berdasarkan skor dan jumlah wins.</p>

###

<p align="left">• Board<br>Menampilkan game board yang berisi node, ukuran board, dan efek skor pada setiap posisi. Design board diimport dengan format PNG.</p>

###

<p align="left">• DijkstraAlgorithm<br>Class ini mengimplementasikan algoritma Dijkstra untuk mencari jalur terpendek pada papan permainan. Algoritma Dijkstra akan diaktifkan hanya ketika pemain berada di node prima.</p>

###

<p align="left">• CalibrationMode<br>Class ini digunakan khusus pada tahap pengembangan untuk mengatur posisi node pada board yang menghasilkan output berupa koordinat posisi masing-masing node secara berurutan. Class ini tidak digunakan saat game dijalankan oleh pemain.</p>

###

<h2 align="left">Pacman Maze</h2>

###

<p align="left">• PacmanSoundManager<br>Mengelola seluruh sound effect dan musik pada game Pacman Maze, yaitu Theme Song sebagai musik yang diputar selama game berlangsung, Move Sound yang diputar ketika Pacman mulai bergerak dan mencari jalur, dan Finish Sound sebagai penanda ketika Pacman berhasil mencapai tujuan.</p>

###

<p align="left">• PacmanMSTControl<br>Class ini berperan sebagai controller utama pada game Pacman Maze, yang bertugas mengintegrasikan seluruh komponen game seperti GamePanel sebagai area simulasi maze, SidePanel sebagai panel statistik dan monitoring algoritma, serta PacmanSoundManager untuk pengelolaan audio, sekaligus mengatur alur permainan mulai dari pemilihan algoritma (BFS, DFS, Dijkstra, dan A*), pengaturan mode tree dan weighted graph, pengendalian state game (start, running, finished), hingga navigasi kembali ke menu utama pada Main.java, sehingga seluruh logika kontrol game terpusat dalam satu class ini.</p>

###
