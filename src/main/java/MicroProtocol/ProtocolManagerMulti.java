/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MicroProtocol;

import MicroProtocol.blockstepGUI.ProcessStepBlockGUI;
import MicroProtocol.listeners.BatchStateListener;
import MicroProtocol.listeners.CopyBlocksListener;
import MicroProtocol.listeners.RenameTabListener;
import MicroProtocol.listeners.RepaintTabListener;
import MicroProtocol.listeners.RequestImageListener;
import MicroProtocol.listeners.TransferProtocolStepsListener;
import MicroProtocol.menus.AvailableWorkflowsMenu;
import MicroProtocol.menus.JMenuBatch;
import MicroProtocol.menus.JMenuProtocol;
import MicroProtocol.menus.LengthFilter;
import VTC.ImageSelectionListener;
import VTC.OpenImageWindow;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JWindow;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.ListIterator;
import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;

/**
 *
 * @author winfrees
 */
public class ProtocolManagerMulti extends javax.swing.JFrame implements ImageSelectionListener, RequestImageListener, RepaintTabListener, RenameTabListener, CopyBlocksListener, BatchStateListener {

    private static int PROCESSBLOCKS = 1;
    private static int OBJECTBLOCKS = 2;
    
    public static final int PROCESS = 100;
    public static final int OBJECT = 110;
    public static final int EXPLORATION = 120;

    public JList OpenImages;
    public OpenImageWindow openerWindow = new OpenImageWindow(OpenImages);
    
    //protected ImagePlus OriginalImage;
    //protected ImagePlus ProcessedImage;
    //protected ImagePlus ThumbnailImage;
    protected JPanel thumbnail;
    
//batch support
    
    //public MultipleFilesMenu mfm = new MultipleFilesMenu(false);
    //public microBatchManager batchWindow = new microBatchManager(OpenImages);
    protected boolean batch = false;
    
    public AvailableWorkflowsMenu awf = new AvailableWorkflowsMenu();

    private ArrayList <TransferProtocolStepsListener> listeners = new ArrayList <TransferProtocolStepsListener>();
    
    public JMenuProtocol ProcessingMenu;
    public JMenuProtocol ObjectMenu;
    public JMenuProtocol ExplorationMenu;
    public JMenuBatch BatchMenu;

    public JWindow thumb = new JWindow();

    public GridLayout PreProcessingLayout = new GridLayout(5, 1, 0, 0);
    public GridLayout ObjectLayout = new GridLayout(5, 1, 0, 0);
    public GridLayout ExploreLayout = new GridLayout(5, 1, 0, 0);

    public Color ButtonBackground = new java.awt.Color(102, 102, 102);
    public String[] Channels;
    
    private MicroExperiment me = new MicroExperiment();
    
    private ArrayList<JPanel> Tabs = new ArrayList<JPanel>();
    
    //static ResultsTable rt;

    /**
     * Creates new form protocolManager
     */
    public ProtocolManagerMulti() {

      openerWindow.addImageSelectionListener(this);
//        batchWindow.addBatchFileListener(this);
//        mfm.addBatchListener(this);

        GuiSetup();
        initComponents();
        addNewTabTab();
        addSingleImagePanel();
        addMenuItems();
        this.ImageTabs.setSelectedIndex(ImageTabs.getTabCount()-1);
        //IJ.log("Starting things up!");

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        PreProcessing_Contextual = new javax.swing.JPopupMenu();
        Object_Contextual = new javax.swing.JPopupMenu();
        Explore_Contextual = new javax.swing.JPopupMenu();
        jPopUpAddParallelAnalysis = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jPopUpAvailableProtocols = new javax.swing.JPopupMenu();
        ImageTabs = new javax.swing.JTabbedPane();
        MenuBar = new javax.swing.JMenuBar();
        Settings = new javax.swing.JMenu();
        Default_Edit = new javax.swing.JMenu();

        jPopUpAddParallelAnalysis.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPopUpAddParallelAnalysisMouseClicked(evt);
            }
        });

        jMenuItem1.setText("jMenuItem1");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jPopUpAddParallelAnalysis.add(jMenuItem1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("VTEA-Protocols v." + VTC._VTC.VERSION);
        setBackground(new java.awt.Color(204, 204, 204));
        setBounds(new java.awt.Rectangle(30, 100, 890, 400));
        setMaximumSize(new java.awt.Dimension(890, 460));
        setMinimumSize(new java.awt.Dimension(830, 420));
        setName("ProcessingFrame"); // NOI18N
        setPreferredSize(new java.awt.Dimension(870, 405));
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        ImageTabs.setBackground(VTC._VTC.ACTIONPANELBACKGROUND);
        ImageTabs.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        ImageTabs.setToolTipText("");
        ImageTabs.setMaximumSize(new java.awt.Dimension(1000, 380));
        ImageTabs.setMinimumSize(new java.awt.Dimension(880, 380));
        ImageTabs.setPreferredSize(new java.awt.Dimension(920, 380));
        ImageTabs.setRequestFocusEnabled(false);
        ImageTabs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ImageTabsMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ImageTabsMouseClicked(evt);
            }
        });
        ImageTabs.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                ImageTabsComponentResized(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(ImageTabs, gridBagConstraints);

        MenuBar.setLocation(new java.awt.Point(-10, 0));
        MenuBar.setPreferredSize(new java.awt.Dimension(910, 22));

        Settings.setText("Settings");
        MenuBar.add(Settings);

        Default_Edit.setText("Edit");
        MenuBar.add(Default_Edit);

        setJMenuBar(MenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ImageTabsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ImageTabsMouseClicked
 
    }//GEN-LAST:event_ImageTabsMouseClicked

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jPopUpAddParallelAnalysisMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPopUpAddParallelAnalysisMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jPopUpAddParallelAnalysisMouseClicked

    private void ImageTabsComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_ImageTabsComponentResized
this.repaint();        // TODO add your handling code here:
    }//GEN-LAST:event_ImageTabsComponentResized

    private void ImageTabsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ImageTabsMouseReleased
      JTabbedPane jtp = (JTabbedPane)evt.getComponent();
       String tabtitle = jtp.getTitleAt(jtp.getSelectedIndex());
        
       //if(SwingUtilities.isRightMouseButton(evt)){ParallelFileMenu pfm = new ParallelFileMenu(); pfm.show(evt.getComponent(), evt.getX(), evt.getY());}
       if(evt.getClickCount() > 1 && tabtitle.equals("Add")){ 
           addSingleImagePanel();  
           this.ImageTabs.setSelectedIndex(ImageTabs.getTabCount()-1);
           evt.consume();
       }else if(evt.getClickCount() == 1 && tabtitle.equals("Add")){
           addSingleImagePanel();  
           this.ImageTabs.setSelectedIndex(ImageTabs.getTabCount()-1);
           evt.consume();
       }
       if(jtp.getTabCount() == 19){
           jtp.setEnabledAt(0, false);
       }
           refreshMenuItems();
           if(!rebuildPanels()){
               this.ImageTabs.setSelectedIndex(ImageTabs.getTabCount()-1);
           }       
    }//GEN-LAST:event_ImageTabsMouseReleased
    
    /**
     * @param args the command line arguments
     */
    private void GuiSetup() {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
//        try {
////                       // Set System L&F
////            UIManager.setLookAndFeel(
////            UIManager.getSystemLookAndFeelClassName());
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(protocolManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(protocolManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(protocolManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(protocolManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                //new pipelineManager().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu Default_Edit;
    private javax.swing.JPopupMenu Explore_Contextual;
    public javax.swing.JTabbedPane ImageTabs;
    protected javax.swing.JMenuBar MenuBar;
    private javax.swing.JPopupMenu Object_Contextual;
    private javax.swing.JPopupMenu PreProcessing_Contextual;
    private javax.swing.JMenu Settings;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPopupMenu jPopUpAddParallelAnalysis;
    private javax.swing.JPopupMenu jPopUpAvailableProtocols;
    // End of variables declaration//GEN-END:variables

    //
    
    
    
    public void addSingleImagePanel(){
            SingleImageProcessing NewPanel = new SingleImageProcessing();
            openerWindow.addImageSelectionListener(NewPanel);
            NewPanel.addRequestImageListener(this);
            NewPanel.addRepaintTabListener(this);
            Tabs.add(NewPanel);
            //Tabs.add(NewPanel);
            addTransferProtocolStepsListener(NewPanel);    
            ImageTabs.addTab("Image_" + this.Tabs.indexOf(NewPanel), NewPanel);
            JTextField label = new JTextField("Image_" + this.Tabs.indexOf(NewPanel));
            NewPanel.setTabName(this.ImageTabs.getTitleAt(this.Tabs.indexOf(NewPanel)+1));
            label.setEditable(true);
            label.setMaximumSize(new Dimension(30,50));
            label.setMargin(new Insets(3,0,0,0));
            label.setBackground(new Color(0,0,0,0));
            label.setBorder(BorderFactory.createEmptyBorder());
            ((AbstractDocument)label.getDocument()).setDocumentFilter(new LengthFilter(7));           
            ImageTabs.setTabComponentAt(this.Tabs.indexOf(NewPanel)+1, label);
            NewPanel.setTabValue(this.Tabs.size()-1);      
         }
    
    public void addBatchPanel(String selected, ArrayList tabs){
        
        ArrayList<String> al = getAllTabNames();
        
        BatchImageProcessing NewPanel = new BatchImageProcessing(getAllTabNames(), al.indexOf(selected));
        this.Tabs.add(NewPanel);
        this.ImageTabs.addTab("Batch" + this.Tabs.indexOf(NewPanel), NewPanel);
        JTextField label = new JTextField("Batch");
        label.setEditable(true);
        label.setMaximumSize(new Dimension(30,50));
            label.setMargin(new Insets(3,0,0,0));
            label.setBackground(new Color(0,0,0,0));
            label.setBorder(BorderFactory.createEmptyBorder());
            ((AbstractDocument)label.getDocument()).setDocumentFilter(new LengthFilter(7));
            
            this.ImageTabs.setTabComponentAt(this.Tabs.indexOf(NewPanel)+1, label);
            NewPanel.setTabValue(this.Tabs.size()-1); 
            
            
        SingleImageProcessing sip = (SingleImageProcessing)(this.ImageTabs.getComponentAt(tabs.indexOf(selected)));
        
        NewPanel.setPreProcessingProtocols(sip.getProProcessingProtocol());
        NewPanel.setObjectProcotols(sip.getObjectSteps());
        NewPanel.setProtocolSynopsis(sip.getProProcessingProtocol(), sip.getObjectSteps());
           
    }
    
    public void addMenuItems(){       
        this.ProcessingMenu = new JMenuProtocol("Processing", this.getTabNames(), SingleImageProcessing.PROCESSBLOCKS);
        this.ObjectMenu = new JMenuProtocol("Objects", this.getTabNames(), SingleImageProcessing.OBJECTBLOCKS);
        
        ProcessingMenu.addStepCopierListener(this);
        ObjectMenu.addStepCopierListener(this);
        
        this.MenuBar.add(ProcessingMenu);
        this.MenuBar.add(ObjectMenu);
    }
    
    public void refreshMenuItems(){
        
        this.MenuBar.removeAll();
        this.MenuBar.add(this.Settings);
        this.MenuBar.add(this.Default_Edit);
        
        addMenuItems();      
    }
    
    public boolean rebuildPanels(){
        try{
        SingleImageProcessing sip;
        BatchImageProcessing bip;
        
        if(!((JTextField)ImageTabs.getTabComponentAt(ImageTabs.getSelectedIndex())).getText().equals("Batch")){
        sip = (SingleImageProcessing)(ImageTabs.getComponentAt(ImageTabs.getSelectedIndex()));
        
        sip.RebuildPanelProcessing();
        sip.RebuildPanelObject();
        }
        }catch(NullPointerException npe){
            return false;
        }
        return true;
        
    }
    
    public void addNewTabTab(){ this.ImageTabs.addTab("Add",new JPanel());}
    
    public ArrayList getTabNames() {
        
        ArrayList<String> titles = new ArrayList<String>();
        
        int currenttab = ImageTabs.getSelectedIndex();
        
        for(int i = 1; i <= ImageTabs.getTabCount()-1; i++){
            if(i != currenttab)
            {titles.add(((JTextField)ImageTabs.getTabComponentAt(i)).getText());}   
        }
        return titles;
        
    }   public ArrayList getAllTabNames() {
        ArrayList<String> titles = new ArrayList<String>(); 
        for(int i = 1; i <= ImageTabs.getTabCount()-1; i++){
            //titles.add(ImageTabs.getTitleAt(i)); 
            if(!((JTextField)ImageTabs.getTabComponentAt(i)).getText().equals("Batch")){
           titles.add(((JTextField)ImageTabs.getTabComponentAt(i)).getText());
            }
        }
        return titles;
    }

    public void UpdateImageList() {
        openerWindow.updateImages();
    }
    
    public void openImage(int tab) {
        //this.openerWindow.setVisible(true);
        this.openerWindow.getImageFile(tab);
    }
    
    public void addTransferProtocolStepsListener(TransferProtocolStepsListener listener) {
        listeners.add(listener);
    }

    private void notifyTransferProtocolStepsListeners(int type, int tab, String arg) {
        for (TransferProtocolStepsListener listener : listeners) {
            listener.transferThese(type, tab, arg);
        }
    }

    @Override
    public void onSelect(ImagePlus imp, int tab) {
        SingleImageProcessing sip = (SingleImageProcessing)Tabs.get(tab);  
        sip.setImage(imp,tab);
        pack();
}

    @Override
    public void onRequest(int tab) {
        openImage(tab);
    }

    @Override
    public void repaintTab() {
        this.repaint();
    }

    @Override
    public void renameTab(int tab) {
        JTextField label = new JTextField("Image_" + tab);
        label.setEditable(true);
        this.ImageTabs.setTabComponentAt(tab, label);
    }

    @Override
    public void onCopy(String source, int StepsList) {
        
       // IJ.log("Copy blocks to " +  ((JTextField)ImageTabs.getTabComponentAt(ImageTabs.getSelectedIndex())).getText()  +" from " + source + " of type " + StepsList);
        
        SingleImageProcessing SourceSIP;
        SingleImageProcessing DestinationSIP;
        
        ProcessStepBlockGUI psbg;
        
        DestinationSIP = (SingleImageProcessing)(ImageTabs.getComponentAt(ImageTabs.getSelectedIndex()));
        
        
        for(int i = 1; i<=ImageTabs.getTabCount()-1; i++){
            
            SourceSIP = (SingleImageProcessing)(ImageTabs.getComponentAt(i));
            
            if(((JTextField)ImageTabs.getTabComponentAt(i)).getText().equals(source)){
                switch(StepsList){
                    case SingleImageProcessing.PROCESSBLOCKS:
                        DestinationSIP.setProcessSteps((ArrayList)SourceSIP.getProcessSteps().clone());
                         ListIterator<ProcessStepBlockGUI> itr = DestinationSIP.ProcessingStepsList.listIterator();
                            while(itr.hasNext()){    
                                psbg = (ProcessStepBlockGUI)itr.next();
                                psbg.deleteblocklisteners.clear();
                                psbg.rebuildpanelisteners.clear();
                                psbg.addRebuildPanelListener(DestinationSIP);
                                psbg.addDeleteBlockListener(DestinationSIP);
                            }
                        DestinationSIP.UpdatePositionProcessing(1);
                        DestinationSIP.RebuildPanelProcessing();
                        SourceSIP.RebuildPanelProcessing();
                        System.out.println("Copied process blocks.");
                break;
                    case SingleImageProcessing.OBJECTBLOCKS:
                 DestinationSIP.setObjectSteps((ArrayList)SourceSIP.getObjectSteps().clone());
                 DestinationSIP.UpdatePositionObject(1);
                 DestinationSIP.RebuildPanelObject();
                 System.out.println("Copied object blocks.");
                 break;   
//                    case SingleImageProcessing.EXPLOREBLOCKS:
//                 DestinationSIP.setObjectSteps((ArrayList)SourceSIP.getExploreSteps().clone());
//                 DestinationSIP.UpdatePositionExplore(1);
//                  DestinationSIP.RebuildPanelExplore();
//                System.out.println("Copied explore blocks.");
 //                break;
                    default: break;    
                }
            }        
        }     
    } 

    @Override
    public void batchStateAdd(String selected, ArrayList tabs) {
        this.addBatchPanel(selected, tabs);
    }
}

