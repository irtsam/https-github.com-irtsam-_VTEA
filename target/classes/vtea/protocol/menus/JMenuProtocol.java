/* 
 * Copyright (C) 2016 Indiana University
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
package vtea.protocol.menus;

import vtea.protocol.listeners.BatchStateListener;
import vtea.protocol.listeners.CopyBlocksListener;
import vtea.protocol.listeners.FileOperationListener;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.ListIterator;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 *
 * @author vinfrais
 */
public class JMenuProtocol extends JMenu implements ActionListener, ItemListener {
                JMenuItem LoadSteps;
                JMenuItem SaveSteps;
                JMenu CopySteps;
                //JMenuItem Item3;
                boolean batch;              
                int ProtocolType;
                
                String ItemString1 = "Load...";
                String ItemString2 = "Save...";
                String ItemString3 = "Copy from...";
                
                private ArrayList<BatchStateListener> listeners = new ArrayList<BatchStateListener>();
                private ArrayList<CopyBlocksListener> CopyListeners = new ArrayList<CopyBlocksListener>();
                private ArrayList<FileOperationListener> fileOperationListeners = new ArrayList<FileOperationListener>();
                
                public JMenuProtocol(final String text, ArrayList<String> tabs, int type){
                    
                    super(text);
                    
                    this.ProtocolType = type;
                    
                     LoadSteps = new JMenuItem("Load...");
                     LoadSteps.setActionCommand("Load");
                     LoadSteps.addActionListener(new ActionListener(){
                         @Override
                         public void actionPerformed(ActionEvent ae) {   
                            notifyFileOperationListener(ae);
                         }
                     });
                     
                     SaveSteps = new JMenuItem("Save...");
                     SaveSteps.setActionCommand("Save");
                     SaveSteps.addActionListener(new ActionListener(){
                         @Override
                         public void actionPerformed(ActionEvent ae) {   
                             notifyFileOperationListener(ae);
                         }
                     });
                     
                     CopySteps = new JMenu("Copy from...");
                     CopySteps.setActionCommand("Copy");
                     CopySteps.addActionListener(this);
                     
                     ListIterator<String> itr = tabs.listIterator();
                     
                     JMenuItem OpenTab = new JMenuItem(); 
                     
                     while(itr.hasNext())
                     {  
                     OpenTab = new JMenuItem(itr.next());
                     OpenTab.setActionCommand(this.getName());
                     OpenTab.addActionListener(new ActionListener(){
                         @Override
                         public void actionPerformed(ActionEvent ae) {   
                            notifyStepCopierListener(((JMenuItem)ae.getSource()).getText(), ProtocolType);
                         }
                     });
                     CopySteps.add(OpenTab);
                     }
                    add(LoadSteps);
                    add(SaveSteps);
                    add(CopySteps);
                } 

                
    //private void copySteps           
                
                
    @Override
    public void itemStateChanged(ItemEvent ie) {
        
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        
    }
    
    
    public void addStepCopierListener(CopyBlocksListener listener){
         CopyListeners.add(listener);
         
    }
    
    public void notifyStepCopierListener(String source, int type){
        for (CopyBlocksListener listener : CopyListeners){
     //IJ.log(type + ", Event: " + source);
            listener.onCopy(source, type);
        }
    }
    
    public void addFileOperationListener(FileOperationListener listener) {
		fileOperationListeners.add(listener);
	   }

    private void notifyFileOperationListener(ActionEvent ae) {
            JMenuItem temp = (JMenuItem)(ae.getSource());
	     for (FileOperationListener listener : fileOperationListeners) {
	    	 
	    	 if(temp.getText().equals("Load...")){
				try {
                                    System.out.println("PROFILING: loading.");
                                    int returnVal = listener.onFileOpen();
                                    
						if(returnVal == 1){
							
						}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this.getParent(),
					"File was opened...",
					vtea._vtea.VERSION,
					JOptionPane.WARNING_MESSAGE);
                                        System.out.println("PROFILING: "+ e.getMessage());
					}
					
	    	} else if (temp.getText().equals("Save...")){
				try {
					listener.onFileSave();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this.getParent(),
					"File was not saved...",
					vtea._vtea.VERSION,
					JOptionPane.WARNING_MESSAGE);
                                        System.out.println("PROFILING: "+ e.getMessage());
				}
		} else if (temp.getText().equals("Export")){
				try {
					listener.onFileExport();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this.getParent(),
					"File could not be exported...",
					vtea._vtea.VERSION,
					JOptionPane.WARNING_MESSAGE);
                                        System.out.println("PROFILING: "+ e.getMessage());
				}
			}
                }
            
	}

    
                }