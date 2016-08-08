/*
 * PisaBostas.java
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import javax.swing.*;
import java.awt.event.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;
/**
 * @author  noemi
 */
public class PisaBostas extends javax.swing.JFrame implements ActionListener{
    
    /** Crea o Frame PisaBostas */
    public PisaBostas() {
        super("PisaBostas 1.0");
        //configuro como idioma por defecto o galego
        Locale localeGalicia=new Locale("gl","ES");
        bundle=ResourceBundle.getBundle("languages/textos",localeGalicia);
        novoXogo=true;
        fonteBotons = new Font("Courier New", Font.BOLD, 17);
        this.actualizarTimer = new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
                DateFormat df=new SimpleDateFormat("mm:ss");
                Date tempo;
                try {
                    tempo = df.parse(jtxtTimer.getText());
                    Calendar cal=Calendar.getInstance();
                    cal.setTime(tempo);
                    cal.add(Calendar.SECOND,1);
                    tempo=cal.getTime();
                    jtxtTimer.setText(df.format(tempo));
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            }
        };
        imxBosta=new ImageIcon(PisaBostas.class.getResource("/recursos/bosta2.png"));
        super.setIconImage(imxBosta.getImage());
        int delay=1000; //milisegundos
        temporizador=new Timer(delay,actualizarTimer);
        initComponents();
        ponPanel1();
        ponBotones();
        actualizarBotons(false);
        imxBostaOK=new ImageIcon(PisaBostas.class.getResource("/recursos/bosta2ok.png"));
        imxBostaErro=new ImageIcon(PisaBostas.class.getResource("/recursos/bosta2erro2.png"));
        CentrarFrame(this);
    }
    
    public final void CentrarFrame(javax.swing.JFrame objFrame){
        Dimension objDimension = Toolkit.getDefaultToolkit().getScreenSize();
        int iCoordX = (objDimension.width - objFrame.getWidth()) / 2;
        int iCoordY = (objDimension.height - objFrame.getHeight()) / 2;
        objFrame.setLocation(iCoordX, iCoordY); 
    } 
    /**
     * Pon os botóns coas bostas no frame. O número de bostas é aleatorio en cada partida
     * @return 
     */
    public int getNumBostas(){
        return numBostas;
    }
    public void setNumBostas(int numBostas){
        this.numBostas=numBostas;
    }
    public final void ponBotones() {
        for (int i=0; i < FILAS ;i++)
            for (int j=0; j < COLS; j++){
                botones[i][j] = new JButton("");
                botones[i][j].setFont(fonteBotons);        
                int ancho=Math.round(jPanel2.getWidth()/COLS);
                int alto=Math.round((jPanel2.getHeight()/FILAS));
                botones[i][j].setPreferredSize(new java.awt.Dimension(ancho,alto));
                botones[i][j].putClientProperty("idx1",i);
                botones[i][j].putClientProperty("idx2",j);
                botones[i][j].addMouseListener( new java.awt.event.MouseAdapter(){
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent e){
                            boolean bDerecho=false;
                            if (SwingUtilities.isRightMouseButton(e) && e.getClickCount()>=1){
                                bDerecho=true;
                            }
                            JButton jb = (JButton)e.getComponent();
                            Object m=jb.getClientProperty("idx1");
                            Object r=jb.getClientProperty("idx2");
                            if(jb.isEnabled()){
                                if ((bDerecho==true)&&(jb.getClientProperty("ocupado")==null)){
                                    int contB=Integer.parseInt(jtxtBostasDisponibles.getText());
                                    if ((contB>0) &&(jb.getClientProperty("imxBosta")==null)){
                                        contB--;
                                        jtxtBostasDisponibles.setText(String.valueOf(contB));
                                        actualizaBoton(jb,"","conBosta");
                                        if (contB==0){
                                            if (verificaGanador()){
                                                finalizaPartida(true);
                                            }
                                        }
                                    }else{
                                        if (jb.getClientProperty("imxBosta")!=null){
                                            actualizaBoton(jb,"","senBosta");
                                            contB++;
                                            jtxtBostasDisponibles.setText(String.valueOf(contB));
                                        }
                                    }           
                                }else{
                                    if (jb.getClientProperty("bosta")!=null){
                                        System.out.println("BOSTA");
                                        if (jb.getClientProperty("imxBosta")==null){
                                            finalizaPartida(false);
                                        }
                                    }
                                    else{
                                        if ((jb.getClientProperty("imxBosta")==null) &&(jb.getClientProperty("ocupado")==null)){
                                            int p=(Integer)m;
                                            int q=(Integer)r;
                                            int contBostas=calculaNumBostas(p,q);
                                            if (contBostas>0){
                                                actualizaBoton(jb,String.valueOf(contBostas),"num");
                                            }else{
                                                actualizaBoton(jb,"","cero");
                                                int radio=1;
                                                if (!(existeBombaContorno(p,q,radio))){
                                                    int num=ponNumBostas(p,q,radio);
                                                    while ((num==0)&& (!existeBombaContorno(p,q,radio))){
                                                        num=ponNumBostas(p,q,radio);
                                                        radio++;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                );
                int b = (int)(Math.random()*10)/2;
                if (b==1){
                    System.out.println("Bosta en "+i+","+j);
                    botones[i][j].putClientProperty("bosta","SI");
                    numBostas++;
                }
            }   
        /* Añadir os botóns ao panel2*/
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=0;
        for (int i=0; i < FILAS ;i++) {
            for (int j=0; j < COLS; j++){
                jPanel2.add(botones[i][j],c);
                c.gridx++;
            }
            c.gridx=0;
            c.gridy++;
	}
        pack();
    }
    private boolean existeBombaContorno(int p,int q,int r){
        boolean haiBomba=false;
        for (int a=p-r;a<=p+r;a++){
            if((a>=0) && (a<FILAS)){
                for (int b=q-r;b<=q+r;b++){
                    if ((b>=0)&&(b<COLS)){
                        if (((a==p-r)||(b==q-r)) ||((a==p+r)||(b==q+r))){
                            if (botones[a][b].getClientProperty("bosta")!=null){
                                haiBomba=true;
                                return haiBomba;
                            }
                        }
                    }
                }
            }
        }
        return haiBomba;
    }
    private void actualizarBotons(boolean cambio){
        for (int l=0; l < FILAS ;l++) {
            for (int n=0; n < COLS; n++){
                botones[l][n].setEnabled(cambio);
            }
        }
    }
    private void finalizaPartida(boolean ganhador){
        temporizador.stop();
        String msg;
        //mostra a solución
        if (!ganhador){
            for (int l=0; l < FILAS ;l++) {
                for (int n=0; n < COLS; n++){
                    if ((botones[l][n].getClientProperty("bosta")!=null) && (botones[l][n].getClientProperty("imxBosta")!=null)){
                        botones[l][n].setIcon(imxBostaOK);
                    }
                    else if ((botones[l][n].getClientProperty("bosta")!=null) && (botones[l][n].getClientProperty("imxBosta")==null)){
                        botones[l][n].setIcon(imxBostaErro);
                    }
                    else if ((botones[l][n].getClientProperty("bosta")==null) && (botones[l][n].getClientProperty("imxBosta")!=null)){
                        botones[l][n].setIcon(imxBostaErro);
                    }
                }
            }
        }
        if (ganhador){
            String msg2=conversionISO_UTF8(bundle.getString("msg_atopaches"));
            String msg3=msg2.replaceAll("<numBostas>",String.valueOf(numBostas));
            msg=msg3.replaceAll("<jtxtTimerText>",jtxtTimer.getText());
        }else{
            String msg1=bundle.getString("msg_erro");
            msg=conversionISO_UTF8(msg1);
        }
        String titulo=conversionISO_UTF8(bundle.getString("msg_mensaxe"));
        JOptionPane.showMessageDialog(this,msg,titulo,1);
        this.setNumBostas(0);
        actualizarBotons(false);
    }
    private boolean verificaGanador(){
        boolean gana=true;
        for (int i=0; i < FILAS ;i++) {
            for (int j=0; j < COLS; j++){
                if (botones[i][j].getClientProperty("bosta")!=null){
                    if (botones[i][j].getClientProperty("imxBosta")==null){
                       gana=false;
                    }
                }
            }
	}
        return gana;
    }
    private void actualizaBoton(JButton btn,String txt,String tipo){
        if (tipo.equals("num")){
            btn.setBackground(new Color(255,255,153));
            btn.setText(txt);
            btn.putClientProperty("ocupado", "SI");
        }else if (tipo.equals("cero")){
            btn.setBackground(new Color(0,204,153));
            btn.setIcon(imx);
            btn.putClientProperty("ocupado","SI");
        }else if (tipo.equals("conBosta")){
            btn.setIcon(imxBosta);
            btn.putClientProperty("imxBosta","SI");
        }else if (tipo.equals("senBosta")){
            btn.setIcon(null);
            btn.putClientProperty("imxBosta",null);
        }
    }
    private int calculaNumBostas(int p,int q){
        int contBostas=0;
        for (int a=p-1;a<=p+1;a++){
            for (int b=q-1;b<=q+1;b++){
                if((a>=0) && (a<FILAS)){
                    if ((b>=0)&&(b<COLS)){
                        if (botones[a][b].getClientProperty("bosta")!=null){
                            contBostas++;
                        }
                    }
                }
            }
        }
        return contBostas;
    }
    private int ponNumBostas(int p,int q,int r){
        int num=0;
        for (int a=p-r;a<=p+r;a++){
            if((a>=0) && (a<FILAS)){
                for (int b=q-r;b<=q+r;b++){
                    if ((b>=0)&&(b<COLS)){
                        if (((a==p-r)||(b==q-r)) ||((a==p+r)||(b==q+r))){
                            int valor=calculaNumBostas(a,b);
                            if (botones[a][b].getClientProperty("imxBosta")!=null){
                                num++;
                            }
                            if (valor>0){
                                if ((botones[a][b].getClientProperty("ocupado")==null)&&(botones[a][b].getClientProperty("imxBosta")==null)){
                                    actualizaBoton(botones[a][b],String.valueOf(valor),"num");
                                    num++;
                                }
                            }else{
                                if (botones[a][b].getClientProperty("imxBosta")==null)
                                    actualizaBoton(botones[a][b],"","cero");
                            }
                        }
                    }
                }
            }
        }
        return num;
    }
    /**este mapeo soluciona o problema de visualización dos acentos*/
    private String conversionISO_UTF8(String cad){
        String cadNova="";
        try{
            cadNova=new String(cad.getBytes("ISO-8859-1"), "UTF-8");
        }catch (Exception e){
            System.err.println(e);
        }
        return cadNova;
    }
    ActionListener actualizarTimer;
    public final void ponPanel1() {
        /*Añadir os elementos do panel1*/
        jPanel1.setLayout(new java.awt.BorderLayout());
        jtxtBostasDisponibles=new JTextField("00");
        Font font1 = new Font("Arial", Font.BOLD, 40);
        jtxtBostasDisponibles.setFont(font1);
        jtxtBostasDisponibles.setHorizontalAlignment(JTextField.CENTER);
        jtxtBostasDisponibles.setColumns(5);
        String jtxtBostasDisponibles1=bundle.getString("jtxtBostasDisponibles_tooltip");
        jtxtBostasDisponibles.setToolTipText(conversionISO_UTF8(jtxtBostasDisponibles1));     
        jtxtBostasDisponibles.setEditable(false);
        jtxtTimer=new JTextField("00:00");
        jtxtTimer.setFont(font1);
        jtxtTimer.setHorizontalAlignment(JTextField.CENTER);
        jtxtTimer.setColumns(5);
        String jtxtTimer1=bundle.getString("jtxtTimer_tooltip");
        jtxtTimer.setToolTipText(conversionISO_UTF8(jtxtTimer1));
        jtxtTimer.setEditable(false);
        imx=new ImageIcon(PisaBostas.class.getResource("/recursos/vaca2.png"));
        jbtReiniciar=new JButton(imxBosta);
        String jbtReiniciar1=bundle.getString("jbtReiniciar_tooltip");
        jbtReiniciar.setToolTipText(conversionISO_UTF8(jbtReiniciar1));
        jbtReiniciar.setBackground(new Color(0,128,255));
        jbtReiniciar.addActionListener(this);
        jPanel1.add(jtxtBostasDisponibles,BorderLayout.LINE_START);
        jPanel1.add(jbtReiniciar,BorderLayout.CENTER);
        jPanel1.add(jtxtTimer,BorderLayout.LINE_END);
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanel1MouseEntered(evt);
            }
        });

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 445, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 412, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseEntered
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jPanel1MouseEntered
    @Override
    public void actionPerformed(ActionEvent ae){
        //reinicia o xogo
        temporizador.stop();
        if (!novoXogo){
            numBostas=0;
            jPanel2.removeAll();
            ponBotones();
        }else{
            actualizarBotons(true);
        }
        jtxtBostasDisponibles.setText(String.valueOf(numBostas));
        novoXogo=false;
        jtxtTimer.setText("00:00");
        temporizador.start();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PisaBostas().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
    public static final int COLS = 10;
    public static final int FILAS = 10;
    private final JButton botones[][] = new JButton[FILAS][COLS];
    private JTextField jtxtBostasDisponibles;
    private int numBostas;
    private JTextField jtxtTimer;
    private final Timer temporizador;
    private final ImageIcon imxBosta;
    private final ImageIcon imxBostaOK;
    private final ImageIcon imxBostaErro;
    private ImageIcon imx;
    private JButton jbtReiniciar;
    private final Font fonteBotons;
    private boolean novoXogo;
    private ResourceBundle bundle;
}
