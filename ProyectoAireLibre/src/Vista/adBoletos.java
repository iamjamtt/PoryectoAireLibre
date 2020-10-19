/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Vista;

import ConexionSQL.ConexionSQL;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author jamt_
 */
public class adBoletos extends javax.swing.JFrame {

    /**
     * Creates new form adBoletos
     */
    DefaultTableModel model;
    
    
    
    public adBoletos() {
        initComponents();
        this.setLocationRelativeTo(null);
        System.out.println("idFechaReserva >> " + abRutasTren.idFR);
        System.out.println("idCliente >> " + acDatosPasajero.indCli);
        System.out.println("FechaSalida >> " + abRutasTren.fechaSalida);
        System.out.println("FechaLlegada >> " + abRutasTren.horaLlegada);
        System.out.println("HoraSalida >> " + abRutasTren.horaSalida);
        System.out.println("PrecioTotal >> " + abRutasTren.precioTotal);
        if(abRutasTren.horaSalida==null){
            System.out.println("Hola");
        }else{
            ingresarFechaReserva();
        }
        cargar2();
        cargar3();
        cargarImagenQR();
        obtenerIdComprobante();
        consulta();
    }
    
    void ingresarFechaReserva(){
        String sql = "INSERT INTO Comprobante (pagoComprobante,fechaComprobante,horaSalidaTren,horaLlegadaTren,descripcionC,idFechaReserva,idCliente) VALUES (?,?,?,?,?,?,?)";
            try {
                PreparedStatement pst  = cn.prepareStatement(sql);
                
                pst.setString(1, ""+abRutasTren.precioTotal);
                
                Calendar fecha = new GregorianCalendar();
                int anio = fecha.get(Calendar.YEAR);
                int mes = fecha.get(Calendar.MONTH);
                int dia = fecha.get(Calendar.DAY_OF_MONTH);
                int hora = fecha.get(Calendar.HOUR_OF_DAY);
                int minuto = fecha.get(Calendar.MINUTE);
                int segundo = fecha.get(Calendar.SECOND);
                
                
                System.out.println("Fecha Actual: " + dia + "/" + (mes+1) + "/" + anio);
                System.out.printf("Hora Actual: %02d:%02d:%02d %n", hora, minuto, segundo);
                String fechaActual = anio + "-" + (mes+1) + "-" + dia;
                
                pst.setString(2, fechaActual);
                pst.setString(3, ""+abRutasTren.horaSalida);
                pst.setString(4, ""+abRutasTren.horaLlegada);
                pst.setString(5, "1");
                pst.setString(6, ""+abRutasTren.idFR);
                pst.setString(7, ""+acDatosPasajero.indCli);

                int n=pst.executeUpdate();
                if(n>0){
                JOptionPane.showMessageDialog(null, "Registro Guardado con Exito");
                }
            } catch (SQLException ex) {
                System.out.println("Error al ingresar datos Comprobante: " + ex);
            }
    }
    
    void cargar2(){
        String mostrar="SELECT c.idComprobante,c.pagoComprobante,c.fechaComprobante,c.horaSalidaTren,c.horaLlegadaTren,k.nombrecli,k.dni FROM Comprobante c INNER JOIN Cliente k ON c.idCliente=k.idCliente WHERE c.idComprobante = (SELECT MAX(c.idComprobante) FROM Comprobante c)";
        String []titulos={"NRO","CLIENTE","PAGO","FECHA","HORA SALIDA","HORA LLEGADA"};
        String []Registros=new String[6];
        model= new DefaultTableModel(null, titulos);
        
        try {
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(mostrar);
                while(rs.next())
                {
                    Registros[0]= rs.getString("idComprobante");
                    Registros[1]= rs.getString("nombrecli");
                    Registros[2]= rs.getString("pagoComprobante");
                    Registros[3]= rs.getString("fechaComprobante");
                    Registros[4]= rs.getString("horaSalidaTren");
                    Registros[5]= rs.getString("horaLlegadaTren");
                    model.addRow(Registros); 
                }
                tblBoleto.setModel(model);
                if(rs.next()){
                    txtNombre.setText(rs.getString("nombrecli"));
                    txtDni.setText(rs.getString("dni"));
                }
        } catch (SQLException ex) {
            System.out.println("Error en la tabla mostrar comprobante:: " + ex);
        }
    }
    
    void cargar3(){
        String mostrar="SELECT k.nombreCli,k.apellidoPaterno,k.apellidoMaterno,k.dni FROM Comprobante c INNER JOIN Cliente k ON c.idCliente=k.idCliente WHERE c.idComprobante = (SELECT MAX(c.idComprobante) FROM Comprobante c)";
        
        try {
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(mostrar);
                String NombreCompleto = "";
                if(rs.next()){
                    NombreCompleto = rs.getString("nombreCli") + " " + rs.getString("apellidoPaterno") + " " + rs.getString("apellidoMaterno");
                    txtNombre.setText(NombreCompleto);
                    txtDni.setText(rs.getString("dni"));
                }
        } catch (SQLException ex) {
            System.out.println("Error en la tabla mostrar comprobante:: " + ex);
        }
    }
    
    void cargarImagenQR(){
        try {
            String ConsultaSQL="UPDATE Comprobante SET imagenComprobante = (SELECT imagenComprobante.* from Openrowset(Bulk 'C:\\QR.png', Single_Blob)as imagenComprobante) where descripcionC = '1'";
            PreparedStatement pst = cn.prepareStatement(ConsultaSQL);
            pst.executeUpdate();
        } catch (Exception e) {
            System.out.println("ERROR actualizar imagen qr: "+e.getMessage());
        }
    }
    
    int idCompro;
    void obtenerIdComprobante(){
        String ConsultaSQL="SELECT idComprobante FROM Comprobante WHERE idComprobante = (SELECT MAX(idComprobante) FROM Comprobante)"; 
        
        try {
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(ConsultaSQL);
                while(rs.next())
                {
                    idCompro = rs.getInt("idComprobante");
                }
              
              System.out.println("id Comprobante <<>> " + idCompro);
        } catch (SQLException ex) {
            System.out.println("Error en obtener id Comprobante: " + ex);
        }
    }
    
    void consulta(){
        String sql="select imagenComprobante from Comprobante where idComprobante="+idCompro;
        SQLServerDataSource dss= new SQLServerDataSource();
        dss.setServerName("Localhost");
        dss.setDatabaseName("AireLibre");
        dss.setUser("sa");
        dss.setPassword("123");
        dss.setPortNumber(1433);
        try {
            Connection ccc=dss.getConnection();
            Statement stt= ccc.createStatement();
            ResultSet rss=stt.executeQuery(sql);
            while(rss.next()){
                Blob fotos=rss.getBlob(1);
                byte []datos=fotos.getBytes(1, (int) fotos.length());
                BufferedImage img=ImageIO.read(new ByteArrayInputStream(datos));
               
                Image images=img.getScaledInstance(txtFoto.getWidth(),txtFoto.getHeight(),Image.SCALE_SMOOTH);
                txtFoto.setIcon(new ImageIcon(images));
            }
        } catch (Exception e) {
            System.out.println("Error mostrar imagen QR: "+e.getMessage());
        }
        
    }
    
    
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBoleto = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JLabel();
        txtDni = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        txtFoto = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Boletos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N

        tblBoleto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblBoleto.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblBoleto);

        jPanel3.setBackground(new java.awt.Color(204, 204, 204));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 30)); // NOI18N
        jLabel1.setText("BOLETO");

        txtNombre.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtDni.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtFoto, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtFoto, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
        );

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Derechos Reservados - PeruRail");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(8, 8, 8)))
                        .addGap(38, 38, 38))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(txtDni, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(57, 57, 57))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(14, 14, 14))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDni, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jButton1.setBackground(new java.awt.Color(255, 102, 102));
        jButton1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton1.setText("SALIR");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        
        
        aaPrincipal principal = new aaPrincipal();
        principal.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(adBoletos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(adBoletos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(adBoletos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(adBoletos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new adBoletos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblBoleto;
    private javax.swing.JLabel txtDni;
    private javax.swing.JLabel txtFoto;
    private javax.swing.JLabel txtNombre;
    // End of variables declaration//GEN-END:variables
ConexionSQL cc = new ConexionSQL();
Connection cn= ConexionSQL.conexionn();
}
