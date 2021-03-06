/*
 * Copyright (C) 2018 q
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package vteaexploration;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.ListIterator;
import javax.swing.JPanel;
import vtea.exploration.listeners.AxesChangeListener;

/**
 *
 * @author sethwinfree
 */
public class PlotAxesSetup extends javax.swing.JFrame {
    
    ArrayList<AxesChangeListener> AxesChangeListeners = new ArrayList();
    ArrayList<Component> ContentList = new ArrayList();
    

    /**
     * Creates new form PlotAxesSetup
     */
    public PlotAxesSetup() {
        initComponents();
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

        TItleBar = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        Content = new javax.swing.JPanel();
        Decision = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        PreviewProgress = new javax.swing.JLabel();
        BlockSetupOK = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 32), new java.awt.Dimension(10, 32), new java.awt.Dimension(10, 32));
        PreviewButton1 = new javax.swing.JButton();

        setTitle("Plot settings");
        setResizable(false);
        setType(java.awt.Window.Type.POPUP);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        TItleBar.setBackground(vtea._vtea.BACKGROUND);
        TItleBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        TItleBar.setMinimumSize(new java.awt.Dimension(400, 50));
        TItleBar.setPreferredSize(new java.awt.Dimension(400, 32));

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout TItleBarLayout = new javax.swing.GroupLayout(TItleBar);
        TItleBar.setLayout(TItleBarLayout);
        TItleBarLayout.setHorizontalGroup(
            TItleBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TItleBarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );
        TItleBarLayout.setVerticalGroup(
            TItleBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TItleBarLayout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        getContentPane().add(TItleBar, new java.awt.GridBagConstraints());

        Content.setBackground(vtea._vtea.BACKGROUND);
        Content.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Content.setMinimumSize(new java.awt.Dimension(400, 50));
        Content.setPreferredSize(new java.awt.Dimension(400, 50));

        javax.swing.GroupLayout ContentLayout = new javax.swing.GroupLayout(Content);
        Content.setLayout(ContentLayout);
        ContentLayout.setHorizontalGroup(
            ContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 396, Short.MAX_VALUE)
        );
        ContentLayout.setVerticalGroup(
            ContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 46, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        getContentPane().add(Content, gridBagConstraints);

        Decision.setMinimumSize(new java.awt.Dimension(400, 36));

        buttonPanel.setMinimumSize(vtea._vtea.BLOCKSETUPPANEL);
        buttonPanel.setLayout(new java.awt.GridBagLayout());
        buttonPanel.add(PreviewProgress, new java.awt.GridBagConstraints());

        BlockSetupOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/dialog-apply.png"))); // NOI18N
        BlockSetupOK.setToolTipText("Accept changes");
        BlockSetupOK.setMaximumSize(vtea._vtea.SMALLBUTTONSIZE);
        BlockSetupOK.setMinimumSize(vtea._vtea.SMALLBUTTONSIZE);
        BlockSetupOK.setPreferredSize(vtea._vtea.SMALLBUTTONSIZE);
        BlockSetupOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BlockSetupOKActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        buttonPanel.add(BlockSetupOK, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        buttonPanel.add(filler1, gridBagConstraints);

        PreviewButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/eye.png"))); // NOI18N
        PreviewButton1.setToolTipText("Set ranges");
        PreviewButton1.setMaximumSize(vtea._vtea.SMALLBUTTONSIZE);
        PreviewButton1.setMinimumSize(vtea._vtea.SMALLBUTTONSIZE);
        PreviewButton1.setPreferredSize(vtea._vtea.SMALLBUTTONSIZE);
        PreviewButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PreviewButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        buttonPanel.add(PreviewButton1, gridBagConstraints);

        javax.swing.GroupLayout DecisionLayout = new javax.swing.GroupLayout(Decision);
        Decision.setLayout(DecisionLayout);
        DecisionLayout.setHorizontalGroup(
            DecisionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DecisionLayout.createSequentialGroup()
                .addContainerGap(314, Short.MAX_VALUE)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        DecisionLayout.setVerticalGroup(
            DecisionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DecisionLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(Decision, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BlockSetupOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BlockSetupOKActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
        

    }//GEN-LAST:event_BlockSetupOKActionPerformed

    private void PreviewButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PreviewButton1ActionPerformed
        notifyAxesChangeListeners(ContentList);
    }//GEN-LAST:event_PreviewButton1ActionPerformed

    
    public void setAdjustable(boolean state){
        Content.setEnabled(state);
    }
    
    public ArrayList<Component> getSettings(){
        ArrayList<Component> al = new ArrayList();
        return al;
    }
    
    public void setContent(ArrayList<Component> al){
        
        //Content = new JPanel();       
        Content.setSize(new Dimension(350,350));
        Content.setLayout(new GridBagLayout());

        Content.removeAll();
        
        ContentList.clear();
        ContentList.addAll(al);
        
        GridBagConstraints layoutConstraints = new GridBagConstraints();

        //MethodDetail
        if (al.size() > 0) {
            layoutConstraints.fill = GridBagConstraints.EAST;
            layoutConstraints.gridx = 0;
            layoutConstraints.gridy = 0;
            layoutConstraints.weightx = 1;
            layoutConstraints.weighty = 1;
            Content.add((Component) al.get(0), layoutConstraints);
        }
        if (al.size() > 1) {
            layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
            layoutConstraints.gridx = 1;
            layoutConstraints.gridy = 0;
            layoutConstraints.weightx = 1;
            layoutConstraints.weighty = 1;
            Content.add((Component) al.get(1), layoutConstraints);
        }
        if (al.size() > 2) {
            layoutConstraints.fill = GridBagConstraints.EAST;
            layoutConstraints.gridx = 2;
            layoutConstraints.gridy = 0;
            layoutConstraints.weightx = 1;
            layoutConstraints.weighty = 1;
            Content.add((Component) al.get(2), layoutConstraints);
        }
        if (al.size() > 3) {
            layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
            layoutConstraints.gridx = 3;
            layoutConstraints.gridy = 0;
            layoutConstraints.weightx = 1;
            layoutConstraints.weighty = 1;
            Content.add((Component) al.get(3), layoutConstraints);
        }
        if (al.size() > 4) {
            layoutConstraints.fill = GridBagConstraints.WEST;
            layoutConstraints.gridx = 4;
            layoutConstraints.gridy = 0;
            layoutConstraints.weightx = 1;
            layoutConstraints.weighty = 1;
            Content.add((Component) al.get(4), layoutConstraints);
        }
        if (al.size() > 5) {
            layoutConstraints.fill = GridBagConstraints.EAST;
            layoutConstraints.gridx = 0;
            layoutConstraints.gridy = 1;
            layoutConstraints.weightx = 1;
            layoutConstraints.weighty = 1;
            Content.add((Component) al.get(5), layoutConstraints);
        }
        if (al.size() > 6) {
            layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
            layoutConstraints.gridx = 1;
            layoutConstraints.gridy = 1;
            layoutConstraints.weightx = 1;
            layoutConstraints.weighty = 1;
            Content.add((Component) al.get(6), layoutConstraints);
        }
        if (al.size() > 7) {
            layoutConstraints.fill = GridBagConstraints.EAST;
            layoutConstraints.gridx = 2;
            layoutConstraints.gridy = 1;
            layoutConstraints.weightx = 1;
            layoutConstraints.weighty = 1;
            Content.add((Component) al.get(7), layoutConstraints);
        }
        if (al.size() > 8) {
            layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
            layoutConstraints.gridx = 3;
            layoutConstraints.gridy = 1;
            layoutConstraints.weightx = 1;
            layoutConstraints.weighty = 1;
            Content.add((Component) al.get(8), layoutConstraints);
        }
        if (al.size() > 9) {
            layoutConstraints.fill = GridBagConstraints.WEST;
            layoutConstraints.gridx = 4;
            layoutConstraints.gridy = 1;
            layoutConstraints.weightx = 1;
            layoutConstraints.weighty = 1;
            Content.add((Component) al.get(9), layoutConstraints);
        }
//        if (al.size() > 10) {
//            layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
//            layoutConstraints.gridx = 5;
//            layoutConstraints.gridy = 1;
//            Content.add((Component) al.get(7), layoutConstraints);
//        }
        
        
        Content.setVisible(true);
        
        pack();   
    }
    
    public void addAxesChangeListener(AxesChangeListener listener){
        AxesChangeListeners.add(listener);
    }
    
    public void notifyAxesChangeListeners(ArrayList al) {
        for (AxesChangeListener listener : AxesChangeListeners) {
            listener.onAxesSetting(al);
        }
    }
    
    public void setDescriptor(String str){
        this.jLabel1.setText(str);
    }
    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(PlotAxesSetup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(PlotAxesSetup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(PlotAxesSetup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(PlotAxesSetup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new PlotAxesSetup().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton BlockSetupOK;
    private javax.swing.JPanel Content;
    private javax.swing.JPanel Decision;
    public javax.swing.JButton PreviewButton1;
    public javax.swing.JLabel PreviewProgress;
    private javax.swing.JPanel TItleBar;
    public javax.swing.JPanel buttonPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
