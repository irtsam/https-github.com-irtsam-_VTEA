/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vtea.objects.Segmentation;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import org.scijava.plugin.Plugin;
import vteaobjects.MicroObject;
import vtea.objects.layercake.LayerCake3D;

/**
 *
 * @author winfrees
 */

@Plugin (type = Segmentation.class)

public class LayerCake3DSingleThreshold extends AbstractSegmentation {
    
    
    private int[] minConstants; // 0: minObjectSize, 1: maxObjectSize, 2: minOverlap, 3: minThreshold
    private  ImagePlus imageOriginal;
    private  ImagePlus imageResult;
    private  ImageStack stackOriginal;
    protected  ImageStack stackResult;
    private boolean watershedImageJ = true;
    
    private LayerCake3D builderRegions;
    private LayerCake3D builderVolumes;
    
    private ArrayList Volumes;
    
public LayerCake3DSingleThreshold(){
    VERSION = "0.1";
    AUTHOR = "Seth Winfree";
    COMMENT = "Blob slice algorithm for building objects.";
    NAME = "LayerCake 3D";
    KEY = "LayerCake3D";
    
    protocol = new ArrayList();
    
            protocol.add(new JLabel("Low Threshold"));
            protocol.add(new JTextField(0));
            protocol.add(new JLabel("Centroid Offset"));
            protocol.add(new JTextField(5));
            protocol.add(new JLabel("Min Vol (vox)"));
            protocol.add(new JTextField(20));
            protocol.add(new JLabel("Max Vol (vox)"));
            protocol.add(new JTextField(1000));
    
}    

    
public LayerCake3DSingleThreshold(ImageStack stack, int[] min, boolean imageOptimize) {
        
            VERSION = "0.1";
            AUTHOR = "Seth Winfree";
            COMMENT = "Blob-slice algorithm for building objects.";
            NAME = "LayerCake 3D";
            KEY = "LayerCake3D";
            
            
            minConstants = min;
            
            // 0: minObjectSize, 1: maxObjectSize, 2: minOverlap, 3: minThreshold
            

            


            
      
        
        stackOriginal = stack;
        imageOriginal = new ImagePlus("Mask", stack);
        stackResult = stackOriginal.duplicate();

        for (int n = 0; n <= stackResult.getSize(); n++) {
            for(int x = 0; x < stackResult.getWidth(); x++){
                for(int y = 0; y < stackResult.getHeight(); y++){
                    if(stackResult.getVoxel(x, y, n) <= minConstants[3]){
                        stackResult.setVoxel(x, y, n, (Math.pow(2,stack.getBitDepth()))-1);   
                    }else{
                        stackResult.setVoxel(x, y, n, 0);
                    }  
                }
            }                        
        } 
        imageResult = new ImagePlus("Mask Result", stackResult);
        IJ.run(imageResult, "8-bit", ""); 
        if(watershedImageJ){IJ.run(imageResult, "Watershed", "stack");}
        IJ.run(imageResult, "Invert", "stack");
        
     }

    @Override
    public ImagePlus getSegmentation() {
        return this.imageResult;
    }

    @Override
    public String runImageJMacroCommand(String str) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendProgressComment() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getProgressComment() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void process(ImageStack[] is, List details, boolean count) {
    
           //takes a stack and details for object definiton as defined by details
        //Details incluldes:
        //channel, segmentation_key as integer should use a centralized system, Arraylist with fields descriptors, field1, field2, field3...
        //field descriptors acts as a key for the fields.
        int[] minConstants = new int[4];
	// 0: minObjectSize, 1: maxObjectSize, 2: minOverlap, 3: minThreshold

        //derivedRegionType[][], [Channel][0, type, 1, subtype];
        int[][] derivedRegionType = new int[is.length][2];

        final List alprimary = (ArrayList) details.get(0);

        final List alsecondary = details.subList(1, details.size());

        final List fieldnames = (List) alprimary.get(2);

        minConstants[3] = Integer.parseInt(alprimary.get(fieldnames.indexOf("minThreshold") + 3).toString());
        minConstants[1] = Integer.parseInt(alprimary.get(fieldnames.indexOf("maxObjectSize") + 3).toString());
        minConstants[2] = Integer.parseInt(alprimary.get(fieldnames.indexOf("minOverlap") + 3).toString());
        minConstants[0] = Integer.parseInt(alprimary.get(fieldnames.indexOf("minObjectSize") + 3).toString());
        
//            protocol = new ArrayList();
//    
//            protocol.add(new JLabel("Low Threshold"));
//            protocol.add(new JTextField(0));
//            protocol.add(new JLabel("Centroid Offset"));
//            protocol.add(new JTextField(5));
//            protocol.add(new JLabel("Min Vol (vox)"));
//            protocol.add(new JTextField(20));
//            protocol.add(new JLabel("Max Vol (vox)"));
//            protocol.add(new JTextField(1000));
    

        long start = System.nanoTime();
        
        
        //make builder with all the detectable regions   
        System.out.println("PROFILING: ImageStack size: " + is[Integer.parseInt(alprimary.get(0).toString())].getSize() + " slices.");
        IJ.log("PROFILING: ImageStack size: " + is[Integer.parseInt(alprimary.get(0).toString())].getSize() + " slices.");
        
        builderRegions = new LayerCake3D(is[Integer.parseInt(alprimary.get(0).toString())], minConstants, false);
        long end = System.nanoTime();
        System.out.println("PROFILING: Region find time: " + ((end-start)/1000000) + " ms. " + "Found " + builderRegions.getRegionsCount() + " regions.");
        IJ.log("PROFILING: Region find time: " + ((end-start)/1000000) + " ms. " + "Found " + builderRegions.getRegionsCount() + " regions.");
        //make builder with all the volumes from the detectable regions
        start = System.nanoTime();
        builderVolumes = new LayerCake3D(builderRegions.getRegions(), minConstants, is[Integer.parseInt(alprimary.get(0).toString())]);
        end = System.nanoTime();
        System.out.println("PROFILING: Volume build time: " + ((end-start)/1000000) + " ms. " + "Made " + builderVolumes.getVolumesCount() + " volumes.");
        IJ.log("PROFILING: Volume build time: " + ((end-start)/1000000) + " ms. " + "Made " + builderVolumes.getVolumesCount() + " volumes.");
        
        ListIterator itr = alsecondary.listIterator();
        while (itr.hasNext()) {
            Object[] derived = ((ArrayList) itr.next()).toArray();
            derivedRegionType[(Integer) derived[0]][0] = Integer.parseInt(derived[1].toString());
            derivedRegionType[(Integer) derived[0]][1] = Integer.parseInt(derived[2].toString());
        }
        
        start = System.nanoTime(); 
        //builderVolumes.makeDerivedRegionsThreading(derivedRegionType, is.length, is, getResultsPointers(details));
        if(count){
        builderVolumes.makeDerivedRegionsPool(derivedRegionType, is.length, is, getResultsPointers(details));
        }
        end = System.nanoTime();
        System.out.println("PROFILING: Derived region time: " + ((end-start)/1000000) + " ms. " + "Made " + builderVolumes.getRegionsCount() + " regions.");
        IJ.log("PROFILING: Derived region time: " + ((end-start)/1000000) + " ms. " + "Made " + builderVolumes.getRegionsCount() + " regions.");
        Volumes = builderVolumes.getVolumesAsList();
    }
    
    @Override
    public ArrayList<MicroObject> getObjects() {
        return builderVolumes.getVolumesAsList();
    }   
    
    private ArrayList getResultsPointers(List details) {

        ArrayList<Integer> result = new ArrayList();

        List alprimary = (ArrayList) details.get(0);
        List alsecondary = details.subList(1, details.size());

        result.add((Integer) alprimary.get(0));

        ListIterator itr = alsecondary.listIterator();

        while (itr.hasNext()) {
            result.add((Integer) ((ArrayList) itr.next()).get(0));
        }
        return result;
    }
}
