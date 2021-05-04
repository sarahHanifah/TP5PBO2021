/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulgame;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author user
 */
public class dbConnection {
    public static Connection con;
    public static Statement stm;
    public static PreparedStatement pstm;
    private Menu menu;
    private Game game;
    
    
    public void connect(){//untuk membuka koneksi ke database
        try {
            String url ="jdbc:mysql://localhost/db_gamepbo";
            String user="root";
            String pass="";
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url,user,pass);
            stm = con.createStatement();
            System.out.println("koneksi berhasil;");
        } catch (Exception e) {
            System.err.println("koneksi gagal" +e.getMessage());
        }
    }
    
    public DefaultTableModel readTable(){
        
        DefaultTableModel dataTabel = null;
        try{
            Object[] column = {"No", "Username", "Score", "Waktu", "Score Akhir"};
            connect();
            dataTabel = new DefaultTableModel(null, column);
            String sql = "Select * from highscore order by Score DESC";
            ResultSet res = stm.executeQuery(sql);
            
            int no = 1;
            while(res.next()){
                Object[] hasil = new Object[5];
                hasil[0] = no;
                hasil[1] = res.getString("Username");
                hasil[2] = res.getString("Score");
                hasil[3] = res.getString("Waktu");
                hasil[4] = res.getString("ScoreAkhir");
                no++;
                dataTabel.addRow(hasil);
            }
        }catch(Exception e){
            System.err.println("Read gagal " +e.getMessage());
        }
        
        return dataTabel;
    }
    
    public void submit_data(String username, int score, int waktu, int score_akhir) {
        // menambahkan data ke db
        try
        {
            connect();
            String sql = "Select score from highscore where username = (?)";
            pstm = con.prepareStatement(sql);
            pstm.setString(1, username);
            ResultSet res = pstm.executeQuery();
            int value = 0;
            int score_sebelumnya = 0;
            while(res.next()){
                value++;
                score_sebelumnya = res.getInt(1);
            }
            if(value >= 1 && score_sebelumnya < score_akhir){
                sql = "Update highscore set score = (?), waktu = (?), ScoreAkhir = (?) where username = (?)";
                pstm = con.prepareStatement(sql);
                pstm.setInt(1, score);
                pstm.setInt(2, waktu);
                pstm.setInt(3, score_akhir);
                pstm.setString(4, username);
                pstm.executeUpdate();
            }
            else if(value == 0){
                sql = "Insert into highscore (username, score, waktu, scoreakhir) values (?, ?, ?, ?)";
                pstm = con.prepareStatement(sql);
                pstm.setString(1, username);
                pstm.setInt(2, score);
                pstm.setInt(3, waktu);
                pstm.setInt(4, score_akhir);
                pstm.executeUpdate();
            }
        }
        catch(Exception e)
        {
            System.err.println("Add data gagal " +e.getMessage());
        }
    }
}
