/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ConexionSQL;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

/**
 *
 * @author jamt_
 */
public class ConexionSQL {
    public static Connection conexionn() {
        Connection conect = null;
        try {
            String controlador = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            String driver = "jdbc:sqlserver://localhost:1433;databaseName=AireLibre";
            String user = "sa";
            String pass = "123";
            try {
                Class.forName(controlador);
                conect = DriverManager.getConnection(driver, user, pass);
                if(conect!=null){
                    System.out.println("Conexion Ok");
                }
            } catch (Exception e) {
            System.out.println("Error Conexion: "+e.getMessage());
            }  
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Error "+e);
        }
        return conect;
    }
}
