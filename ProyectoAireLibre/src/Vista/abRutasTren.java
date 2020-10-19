/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Vista;

import ConexionSQL.ConexionSQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author jamt_
 */
public class abRutasTren extends javax.swing.JFrame {

    /**
     * Creates new form abRutasTren
     */
    DefaultTableModel model;
    public static int idFR;
    public static String horaLlegada = null;
    public static String horaSalida = null;
    public static String fechaSalida = null;
    public static double precioTotal;
    int idAsiento;
    
    
    public abRutasTren() {
        initComponents();
        this.setLocationRelativeTo(null);
        cargarComboDestino();
    }

    void cargarComboDestino(){
        String SQL = "SELECT nombreDestino FROM Destino";
        try {
            PreparedStatement pst = cn.prepareStatement(SQL);
            ResultSet rs = pst.executeQuery();
            cboDestino.addItem("Destino");
            
            while(rs.next()){
                cboDestino.addItem(rs.getString("nombreDestino"));
            }
        } catch (Exception e) {
            System.out.println("Error en combo Destino: " + e);
        }
    }
    
    int idDestino;
    
    void cargarComboRutaTren(){
        idDestino = cboDestino.getSelectedIndex();
        String SQL = "SELECT r.descripcionRT,d.nombreDestino FROM RutaTren r INNER JOIN Destino d ON r.idDestino=d.idDestino WHERE d.idDestino="+idDestino;
        try {
            PreparedStatement pst = cn.prepareStatement(SQL);
            ResultSet rs = pst.executeQuery();
            cboRutas.removeAllItems();
            cboRutas.addItem("Ruta");
                        
            while(rs.next()){
                cboRutas.addItem(rs.getString("descripcionRT") + " >> " + rs.getString("nombreDestino"));
            }
        } catch (Exception e) {
            System.out.println("Error en combo Destino: " + e);
        }
    }
    
    void cargar2(){
        String mostrar="SELECT * FROM Tren WHERE estadoTren="+1+" AND estadoViaje="+1;
        String []titulos={"NRO","EMPRESA TREN","SALIDA","LLEGADA","PRECIO"};
        String []Registros=new String[5];
        model= new DefaultTableModel(null, titulos);
        
        try {
              Statement st = cn.createStatement();
              ResultSet rs = st.executeQuery(mostrar);
              while(rs.next())
              {
                  Registros[0]= rs.getString("idTren");
                  Registros[1]= rs.getString("empresa");
                  Registros[2]= rs.getString("horaSalidaInicio");
                  Registros[3]= rs.getString("horaLlegadaInicio");
                  Registros[4]= rs.getString("precioAdicionalTren");
                  model.addRow(Registros); 
              }
              tblBuscarHora.setModel(model);
        } catch (SQLException ex) {
            System.out.println("Error en la tabla buscar tren: " + ex);
        }
    }
    
    int idTren;
    
    void obtenerAsientoTren(){
        idTren=Integer.parseInt((String) tblBuscarHora.getValueAt(tblBuscarHora.getSelectedRow(),0));
        
        String ConsultaSQL="SELECT idAsientoTren FROM AsientoTren WHERE idAsientoTren=(SELECT MIN(idAsientoTren) FROM AsientoTren WHERE idTren="+idTren+" AND estadoAT="+1+")"; 
        
        try {
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(ConsultaSQL);
                while(rs.next())
                {
                    idAsiento = rs.getInt("idAsientoTren");
                }

                System.out.println("id AsientoTren >> " + idAsiento + " "+ idTren);
        } catch (SQLException ex) {
            System.out.println("Error en obtener Asiento Tren: " + ex);
        }
    }
    
    void modificarAsiento(){
        
        try {
            String ConsultaSQL="UPDATE AsientoTren SET estadoAT="+2+" WHERE idTren="+idTren+" AND idAsientoTren="+idAsiento;
            PreparedStatement pst = cn.prepareStatement(ConsultaSQL);
            pst.executeUpdate();
            
            System.out.println("idTren >> " + idTren);
        } catch (Exception e) {
            System.out.println("ERROR seleccionar datos: "+e.getMessage());
        }
    }
    
    void ingresarPreVenta(){
        String sql = "INSERT INTO PreVentaPasaje (cantidadPVP,fechaPVP) VALUES (?,?)";
            try {
                PreparedStatement pst  = cn.prepareStatement(sql);
                pst.setString(1, "1");
                
                int anio = dateFechaSalida.getCalendar().get(Calendar.YEAR);
                int dia = dateFechaSalida.getCalendar().get(Calendar.DAY_OF_MONTH);
                int mes = dateFechaSalida.getCalendar().get(Calendar.MARCH)+1;
                String fecha = anio+"-"+mes+"-"+dia;
                
                pst.setString(2, fecha);
                

                int n=pst.executeUpdate();
                if(n>0){
                JOptionPane.showMessageDialog(null, "Registro Guardado con Exito");
                }
            } catch (SQLException ex) {
                System.out.println("Error al ingresar datos PRE VENTA PASAJE: " + ex);
            }
    }
    
    int idPreVentPasaje;
    void obtenerPreVentaPasaje(){
        String ConsultaSQL="SELECT idPreVentaPasaje FROM PreVentaPasaje WHERE idPreVentaPasaje = (SELECT MAX(idPreVentaPasaje) FROM PreVentaPasaje)"; 
        
        try {
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(ConsultaSQL);
                while(rs.next())
                {
                    idPreVentPasaje = rs.getInt("idPreVentaPasaje");
                }
              
              System.out.println("id PreVentaPasaje <<>> " + idPreVentPasaje);
        } catch (SQLException ex) {
            System.out.println("Error en obtener Pre Venta Pasaje: " + ex);
        }
    }
    
    int idRTren;
    double preRuta;
    double preTren;
    void obtnerRutaTren(){
        idRTren = cboRutas.getSelectedIndex();
        
        if(idDestino==2){
            idRTren=4;
        }
        if(idDestino==3){
            idRTren=7;
        }
        if(idDestino==4){
            idRTren=8;
        }
        
        String ConsultaSQL="SELECT costoRT FROM RutaTren WHERE idRutaTren="+idRTren; 
        
        try {
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(ConsultaSQL);
                while(rs.next())
                {
                    preRuta = rs.getInt("costoRT");
                }
              
              System.out.println("Precio Ruta Tren <<>> " + preRuta);
        } catch (SQLException ex) {
            System.out.println("Error en obtener Precio Ruta: " + ex);
        }
        
        String ConsultaSQL2="SELECT precioAdicionalTren,horaSalidaInicio,horaLlegadaInicio FROM Tren WHERE idTren="+idTren; 
        
        try {
                Statement st2 = cn.createStatement();
                ResultSet rs2 = st2.executeQuery(ConsultaSQL2);
                while(rs2.next())
                {
                    preTren = rs2.getInt("precioAdicionalTren");
                    horaLlegada = rs2.getString("horaLlegadaInicio");
                    horaSalida = rs2.getString("horaSalidaInicio");
                }
              
                System.out.println("Precio Tren <<>> " + preTren);
                System.out.println("Hora Llegada <<>> " + horaLlegada);
                System.out.println("Hora Salida <<>> " + horaSalida);
        } catch (SQLException ex) {
            System.out.println("Error en obtener Precio Tren: " + ex);
        }
        
        precioTotal = preRuta + preTren;
        
        System.out.println("Precio Total a Pagar =>> " + precioTotal);
        
        System.out.println("idRutaTren => " + idRTren);
    }
    
    void ingresarFechaReserva(){
        String sql = "INSERT INTO FechaReserva (fechaReserva,cantidadReserva,idAsientoTren,idRutaTren,idPreVentaPasaje) VALUES (?,?,?,?,?)";
            try {
                PreparedStatement pst  = cn.prepareStatement(sql);
                
                int anio = dateFechaSalida.getCalendar().get(Calendar.YEAR);
                int dia = dateFechaSalida.getCalendar().get(Calendar.DAY_OF_MONTH);
                int mes = dateFechaSalida.getCalendar().get(Calendar.MARCH)+1;
                String fecha = anio+"-"+mes+"-"+dia;
                
                pst.setString(1, fecha);
                pst.setString(2, "1");
                pst.setString(3, ""+idAsiento);
                pst.setString(4, ""+idRTren);
                pst.setString(5, ""+idPreVentPasaje);
                

                int n=pst.executeUpdate();
                if(n>0){
                JOptionPane.showMessageDialog(null, "Registro Guardado con Exito");
                }
            } catch (SQLException ex) {
                System.out.println("Error al ingresar datos FECHA DE RESERVA: " + ex);
            }
    }
    
    void obtenerFechaReserva(){
        String ConsultaSQL="SELECT idFechaReserva FROM FechaReserva WHERE idFechaReserva = (SELECT MAX(idFechaReserva) FROM FechaReserva)"; 
        
        try {
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(ConsultaSQL);

                while(rs.next())
                {
                    idFR = rs.getInt("idFechaReserva");
                }

                System.out.println("id FechaReserva <<>> " + idFR);
        } catch (SQLException ex) {
            System.out.println("Error en obtener FECHA RESERVA: " + ex);
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
        jLabel1 = new javax.swing.JLabel();
        cboDestino = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        cboRutas = new javax.swing.JComboBox<>();
        dateFechaSalida = new com.toedter.calendar.JDateChooser();
        btnBuscarTren = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBuscarHora = new javax.swing.JTable();
        btnSeleccionar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(732, 700));
        setMinimumSize(new java.awt.Dimension(732, 700));
        setPreferredSize(new java.awt.Dimension(732, 700));
        setSize(new java.awt.Dimension(732, 700));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Seleccione las Opciones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/destino.png"))); // NOI18N

        cboDestino.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboDestino.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboDestinoActionPerformed(evt);
            }
        });

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/tren.png"))); // NOI18N

        cboRutas.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cboRutas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboRutasActionPerformed(evt);
            }
        });

        dateFechaSalida.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        btnBuscarTren.setBackground(new java.awt.Color(255, 204, 0));
        btnBuscarTren.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnBuscarTren.setText("BUSCAR TREN");
        btnBuscarTren.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarTrenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBuscarTren, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cboDestino, 0, 250, Short.MAX_VALUE)
                            .addComponent(cboRutas, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(dateFechaSalida, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cboDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(dateFechaSalida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(cboRutas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnBuscarTren, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(68, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnCancelar.setBackground(new java.awt.Color(255, 102, 102));
        btnCancelar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnCancelar.setText("CANCELAR");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        tblBuscarHora.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblBuscarHora.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "NRO", "EMPRESA", "SALIDA", "LLEGADA", "PRECIO"
            }
        ));
        jScrollPane1.setViewportView(tblBuscarHora);

        btnSeleccionar.setBackground(new java.awt.Color(255, 204, 0));
        btnSeleccionar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnSeleccionar.setText("SELECCIONAR");
        btnSeleccionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeleccionarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSeleccionar, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSeleccionar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        // TODO add your handling code here:
        aaPrincipal principal = new aaPrincipal();
        principal.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnSeleccionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeleccionarActionPerformed
        // TODO add your handling code here:
        ingresarPreVenta();
        obtenerPreVentaPasaje();
        obtenerAsientoTren();
        modificarAsiento();
        obtnerRutaTren();
        ingresarFechaReserva();
        obtenerFechaReserva();
        
        acDatosPasajero datPas = new acDatosPasajero();
        datPas.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnSeleccionarActionPerformed

    private void cboDestinoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboDestinoActionPerformed
        // TODO add your handling code here:
        cargarComboRutaTren();
    }//GEN-LAST:event_cboDestinoActionPerformed

    private void btnBuscarTrenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarTrenActionPerformed
        // TODO add your handling code here:
        cargar2();
    }//GEN-LAST:event_btnBuscarTrenActionPerformed

    private void cboRutasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboRutasActionPerformed
        // TODO add your handling code here:
        obtnerRutaTren();
    }//GEN-LAST:event_cboRutasActionPerformed

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
            java.util.logging.Logger.getLogger(abRutasTren.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(abRutasTren.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(abRutasTren.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(abRutasTren.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new abRutasTren().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarTren;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnSeleccionar;
    private javax.swing.JComboBox<String> cboDestino;
    private javax.swing.JComboBox<String> cboRutas;
    private com.toedter.calendar.JDateChooser dateFechaSalida;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblBuscarHora;
    // End of variables declaration//GEN-END:variables
ConexionSQL cc = new ConexionSQL();
Connection cn= ConexionSQL.conexionn();
}
