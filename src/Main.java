import java.sql.*;
import java.util.Scanner;

public class Main {

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/toko";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.err.println("Driver JDBC tidak ditemukan!");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Scanner    sc   = new Scanner(System.in)) {

            System.out.print("Kode barang (max 10): ");
            String kode = sc.nextLine().trim();

            System.out.print("Nama barang: ");
            String nama = sc.nextLine().trim();

            System.out.print("Harga: ");
            int harga = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Stok: ");
            int stok  = Integer.parseInt(sc.nextLine().trim());
            insertBarang(conn, kode, nama, harga, stok);
            tampilView(conn);

        } catch (SQLException e) {
            System.err.println("Koneksi/SQL error: " + e.getMessage());
        }
    }

    private static void insertBarang(Connection conn, String kode, String nama,
                                     int harga, int stok) {
        String sql = "{ CALL insert_barang(?, ?, ?, ?) }";

        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, kode);
            cs.setString(2, nama);
            cs.setInt   (3, harga);
            cs.setInt   (4, stok);

            cs.executeUpdate();
            System.out.println("✔️  Data berhasil disimpan!");

        } catch (SQLException ex) {
            System.err.println("❌ Gagal insert: " + ex.getMessage());
        }
    }

    private static void tampilView(Connection conn) {
        String sql = "SELECT * FROM view_barang";

        try (Statement  st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.printf("%-10s %-25s %10s %6s %12s%n",
                    "Kode", "Nama", "Harga", "Stok", "TotalNilai");
            System.out.println("-----------------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-10s %-25s %10d %6d %12d%n",
                        rs.getString("kode"),
                        rs.getString("nama"),
                        rs.getInt   ("harga"),
                        rs.getInt   ("stok"),
                        rs.getInt   ("total_nilai"));
            }

        } catch (SQLException ex) {
            System.err.println("❌ Gagal menampilkan data: " + ex.getMessage());
        }
    }
}
