package vteaobjects.layercake;


import ij.*;
import ij.process.*;
import java.util.*;
import ij.ImagePlus;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class RegionFactory extends Object implements Cloneable, java.io.Serializable {

    /**
     * Constants
     */

    /**
     * Variables
     */
    private  ImagePlus imageOriginal;
    private  ImagePlus imageResult;
    private  ImageStack stackOriginal;
    protected  ImageStack stackResult;
    
    private boolean watershedImageJ = true;
   
    private List<microRegion> alRegions = Collections.synchronizedList(new ArrayList<microRegion>());
    private List<microVolume> alVolumes = Collections.synchronizedList(new ArrayList<microVolume>());
    
    private List<microRegion> alRegionsProcessed = Collections.synchronizedList(new ArrayList<microRegion>());

    private int[] minConstants; // 0: minObjectSize, 1: maxObjectSize, 2: minOverlap, 3: minThreshold

    private microVolume[] Volumes;
    private int nVolumes;

//derivedRegionType[][], [Channel][0, type, 1, subtype];

    /**
     * Constructors
     */
//empty cosntructor
    public RegionFactory() {}
    
//constructor for volume building
    public RegionFactory(List<microRegion> Regions, int[] minConstants, ImageStack orig){
        this.stackOriginal = orig;
        this.minConstants = minConstants;
        this.alRegions = Regions;
        this.nVolumes = 0;
        
        VolumeForkPool vf = new VolumeForkPool(alRegions, minConstants, 0, alRegions.size());
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(vf);
        cleanupVolumes();
    }
    
    private synchronized void findConnectedRegions(int volumeNumber, double[] startRegion, int z){
        double[] testRegion = new double[2];
        Iterator<microRegion> itr = alRegions.listIterator();
        int i = 0;
        
         while(i < alRegions.size()){
        //while(itr.hasNext()){   
                    
                    //System.out.println("PROFILING: On region: " + i);
            
            microRegion test = new microRegion();
            test = alRegions.get(i);
            //test = itr.next(); 
            //if (!test.isAMember()) {
                testRegion[0] = test.getBoundCenterX();
                testRegion[1] = test.getBoundCenterY();
                double comparator = lengthCart(startRegion, testRegion);
                if (comparator <= minConstants[2] && ((test.getZPosition()-z) == 1)) {
                    test.setMembership(volumeNumber);                   
                    test.setAMember(true); 
                    z = test.getZPosition();
                    testRegion[0] = (testRegion[0] + startRegion[0])/2;
                    testRegion[1] = (testRegion[1] + startRegion[1])/2;
                    alRegionsProcessed.add(test);
                    alRegions.remove(i); 
                    findConnectedRegions(volumeNumber, testRegion, z);
                    //
 
                //}
            } else {
                    i++;
                }
        }

    }
    
//constructor for region building
    public RegionFactory(ImageStack stack, int[] min, boolean imageOptimize) {

        minConstants = min;
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
        makeRegionsPool(imageResult.getStack(), stackOriginal);   
    }

//constructor for region building with algorithmic threshold setting  
    public RegionFactory(ImageStack stack, ArrayList<String> threshold){
        this.minConstants = minConstants;
        stackOriginal = new ImageStack();
        stackOriginal = stack;
        ImagePlus imp = new ImagePlus("Mask", stack);
        imageOriginal = imp;
    }
    
    //private class VolumeSwingWorker extends SwingWorker {}
    
    private class RegionWorker implements Runnable  {
        
        private int maxsize = 1;      
        //private microRegion[] Regions = new microRegion[(int) (maxsize / minConstants[0])];
        private ArrayList<microRegion> alResult = new ArrayList<microRegion>();
        private int[] start_pixel = new int[3];
        int x, y, z;
        //int[] x_positions = new int[(int) minConstants[1]];
        //int[] y_positions = new int[(int) minConstants[1]];
        
        ArrayList<Integer> x_positions = new ArrayList<Integer>();
        ArrayList<Integer> y_positions = new ArrayList<Integer>();
        
        
        int n_positions = 0;
        int[] BOL = new int[5000];  //start of line position
        int[] EOL = new int[5000];  //end of line position
        int[] row = new int[5000];  //line position
        
//        ArrayList<Integer> BOL = new ArrayList<Integer>();
//        ArrayList<Integer> EOL = new ArrayList<Integer>();
//        ArrayList<Integer> row = new ArrayList<Integer>();
        
        int count = 0;
        private ImageStack stack;
        private ImageStack original;       
        private int start;
        private int stop;

        private Thread t;
        private String threadName = "regionfinder_" + System.nanoTime();

        RegionWorker(ImageStack st, ImageStack orig, int start, int stop){
            stack = st;
            original = orig;
            this.start = start;
            this.stop = stop;
            maxsize = stack.getSize() * stack.getWidth() * stack.getHeight(); 
        }

        @Override
            public void run() {
            long start = System.nanoTime();
//            try{
            //IJ.log("PROFILING: Thread: " +  threadName + " RegionWorker.  Executing thread...");
            //System.out.println("PROFILING: Thread: " +  threadName + " RegionWorker.  Executing thread...");
            defineRegions();
                setRegions();
            //IJ.log("RegionFactory::makeRegion thread found regions: " + alResult.size());
//            }
//            catch(Exception e){
//            System.out.println("Thread " +  threadName + " interrupted by " + e.getMessage());
//            }
            long end = System.nanoTime();
            //System.out.println("PROFILING: Thread: " +  threadName + " runtime: "+ ((end-start)/1000000) + " ms.");
            IJ.log("PROFILING: Thread: " +  threadName + " runtime: "+ ((end-start)/1000000) + " ms.");
            //System.out.println("PROFILING: Thread: "  +  threadName + " exiting.");
            }
        
        public void start(){
            
                     t = new Thread (this, threadName);
                     t.setPriority(t.getThreadGroup().getMaxPriority());
                     t.start ();
            try {
                t.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(RegionFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
        private void defineRegions(){
            //uses a line scan flood fill method with backfill
            for (int n = this.start; n <= this.stop; n++) {
            //IJ.showProgress(n + 1, this.stop);
            //loop through pixels to find starts for regions		
            for (int p = 0; p <= stack.getWidth() - 1; p++) {
                for (int q = 0; q <= stack.getHeight() - 1; q++) {
                    //start pixel selected if 255, new region
                    
                    if (stack.getVoxel(p, q, n) == 255) {
                        start_pixel[0] = p;
                        start_pixel[1] = q;
                        start_pixel[2] = n;

                        //position being analyzed
                        x = start_pixel[0];
                        y = start_pixel[1];
                        z = start_pixel[2];

                        //052814 while(stack.getVoxel(x-1,y,z) == 255) {x--;}
                        //start pixel is left most in first row
                        //count is for the line in question
                        BOL[count] = x;
                        row[count] = y;
                        //BOL.add(x);
                        //row.add(y);
                        //run until 0, end of first row
                        while (stack.getVoxel(x, y, z) == 255) {
                            x++;
                        }
                        EOL[count] = x - 1;
                        //EOL.add(x-1);
                        count++;
                        //second row start, search up
                       // x = BOL.get(count - 1);
                        x = BOL[count - 1];

                        while (stack.getVoxel(x, y, z) == 255) {
                            if (stack.getVoxel(x, y + 1, z) == 255) {
                                while (stack.getVoxel(x - 1, y + 1, z) == 255) {
                                    x--;
                                }
                                BOL[count] = x;
                                row[count] = y + 1;
                                
                                while (stack.getVoxel(x + 1, y + 1, z) == 255) {
                                    x++;
                                }
                                EOL[count] = x;
                                y++;
                                x = BOL[count];
                                count++;
                            }
                            x++;
                        }
                        //reset start pixel and search down
                        x = BOL[0];
                        y = start_pixel[1];
                        while (stack.getVoxel(x, y, z) == 255) {
                            if (stack.getVoxel(x, y - 1, z) == 255) {
                                while (stack.getVoxel(x - 1, y - 1, z) == 255) {
                                    x--;
                                }
                            BOL[count] = x;
                                row[count] = y - 1;
                                while (stack.getVoxel(x + 1, y - 1, z) == 255) {
                                    x++;
                                }
                                EOL[count] = x;
                                y--;
                                x = BOL[count];
                                count++;
                            }
                            x++;
                        }
                   // }
                        
                        
                        //parse tables
                        //get whole region
                        
                        for (int a = 0; a <= count - 1; a++) {				//loop rows
                            for (int c = BOL[a]; c <= EOL[a]; c++) {			//loop x or columns
                                x_positions.add(c);
                                y_positions.add(row[a]);
                            }
                        }
                        //add region to array
                        if (x_positions.size() > 0 && x_positions.size() < (int) minConstants[1]) {
                            alResult.add(new microRegion(convertPixelArrayList(x_positions), convertPixelArrayList(y_positions), x_positions.size(), n, original));  
                        }
                        //remove pixels from stack
                        for (int i = 0; i <= x_positions.size() - 1; i++) {
                            stack.setVoxel(x_positions.get(i), y_positions.get(i), n, 0);
                        }
                        //reset	arrays
                      
                        row = new int[5000];
                        //row.clear();
                        x_positions.clear();
                        y_positions.clear();
                        n_positions = 0;
                        count = 0;
                    }
                }
            }
        }
        //this.Regions = Regions;
        //this.nRegions = nRegions;
        //this.alRegions =+ ;
        //this.nRegions =+ nRegions; 
        System.out.println("PROFILING: ...Regions found:  " + alResult.size());
        
    }
        private void defineRegions2() {
//refining connected component labeling
        ImageStack resultstack = stack;
        double c = 256;
        for (int n = 0; n <= stack.getSize() - 1; n++) {
            	
            //loop through pixels to find starts		
            for (int p = 0; p <= stack.getWidth() - 1; p++) {
                for (int q = 0; q <= stack.getHeight() - 1; q++) {
                    //start pixel selected if 255
                    if (stack.getVoxel(p, q, n) == 0) {
                        resultstack.setVoxel(p, q, n, 0);
                    }
                    if (stack.getVoxel(p, q, n) == 255) {
                        int[] point = new int[4];
                        point[0] = p;
                        point[1] = q;
                        point[2] = n;
                        check8Neighbors(stack, point, c);
                        resultstack.setVoxel(p, q, n, c);                      
                    }
                }
            }
        }
    }
        private void setRegions(){
            alRegions.addAll(alResult);
        }
        
        public ArrayList<microRegion> getRegions(){
            return this.alResult;
        }
        
        public int[] convertPixelArrayList(List<Integer> integers)      {
            int[] ret = new int[integers.size()];
            Iterator<Integer> iterator = integers.iterator();
            for (int i = 0; i < ret.length; i++)
                {
                ret[i] = iterator.next().intValue();
                }
                return ret;
            }
    }
    
    private class DerivedRegionWorker implements Runnable {
        private int[][] derivedRegionType;
        int channels;
        ImageStack[] stack;
        ArrayList ResultsPointers;
        ArrayList<microVolume> Volumes;
        int stop;
        ListIterator<microVolume> itr;
        Thread t;
        private String threadName = "derivedregionmaker_" + System.nanoTime();
     
        DerivedRegionWorker(int[][] ldrt, int c, ImageStack[] st, ArrayList rp, ListIterator<microVolume> litr, int s) {
        this.derivedRegionType = ldrt;
        channels = c;
        stack = st;
        ResultsPointers = rp;
            stop = s;
            itr = litr;
        }
        
         DerivedRegionWorker(int[][] ldrt, int c, ImageStack[] st, ArrayList rp, ArrayList<microVolume> vols, int s) {
        this.derivedRegionType = ldrt;
        channels = c;
        stack = st;
        ResultsPointers = rp;
        Volumes = vols;
            stop = s;
            itr = vols.listIterator();
        }

            @Override
            public void run() {
            long start = System.nanoTime();
            defineDerivedRegions();                            
            long end = System.nanoTime();
            System.out.println("PROFILING: Thread: " +  threadName + " runtime: "+ ((end-start)/1000000) + " ms.");
        } 
        
        public void start() {
            t = new Thread (this, threadName);
            t.setPriority(Thread.MAX_PRIORITY);
                     t.start();
            try {
                t.join();
            }
            catch(Exception e){
                System.out.println("PROFILING: Thread " +  threadName + " interrupted.");
        }

        }

        private void defineDerivedRegions(){ 
            while(itr.hasNext()){
                microVolume mv = new microVolume();
                mv = itr.next();
                mv.makeDerivedRegions(derivedRegionType, channels, stack, ResultsPointers);
            }
        }
        }
    
    private class RegionSwingWorker extends SwingWorker  {
        
        private int maxsize = 1;      
        //private microRegion[] Regions = new microRegion[(int) (maxsize / minConstants[0])];
        private ArrayList<microRegion> alResult = new ArrayList<microRegion>();
        private int[] start_pixel = new int[3];
        int x, y, z;
        int[] x_positions = new int[(int) minConstants[1]];
        int[] y_positions = new int[(int) minConstants[1]];
        int n_positions = 0;
        int[] BOL = new int[(int) minConstants[1]];  //start of line position
        int[] EOL = new int[(int) minConstants[1]];  //end of line position
        int[] row = new int[(int) minConstants[1]];  //line position
        int count = 0;
        private ImageStack stack;
        private ImageStack original;       
        private int start;
        private int stop;

        private Thread t;
        private String threadName = "regionfinder_" + System.nanoTime();

        RegionSwingWorker(ImageStack st, ImageStack orig, int start, int stop){
            stack = st;
            original = orig;
            this.start = start;
            this.stop = stop;
            maxsize = stack.getSize() * stack.getWidth() * stack.getHeight(); 
        }
     
        private synchronized ArrayList<microRegion> defineRegions(){
            for (int n = this.start; n <= this.stop; n++) {
            IJ.showProgress(n + 1, this.stop);
            //loop through pixels to find starts		
            for (int p = 0; p <= stack.getWidth() - 1; p++) {
                for (int q = 0; q <= stack.getHeight() - 1; q++) {
                    //start pixel selected if 255
                    if (stack.getVoxel(p, q, n) == 255) {
                        start_pixel[0] = p;
                        start_pixel[1] = q;
                        start_pixel[2] = n;

                        //position being analyzed
                        x = start_pixel[0];
                        y = start_pixel[1];
                        z = start_pixel[2];

                        //052814 while(stack.getVoxel(x-1,y,z) == 255) {x--;}
                        //start pixel is left most in first row
                        BOL[count] = x;
                        row[count] = y;
                        //run until 0, end of first row
                        while (stack.getVoxel(x, y, z) == 255) {
                            x++;
                        }
                        EOL[count] = x - 1;
                        count++;
                        //second row start, search up
                        x = BOL[count - 1];

                        while (stack.getVoxel(x, y, z) == 255) {
                            if (stack.getVoxel(x, y + 1, z) == 255) {
                                while (stack.getVoxel(x - 1, y + 1, z) == 255) {
                                    x--;
                                }
                                BOL[count] = x;
                                row[count] = y + 1;
                                while (stack.getVoxel(x + 1, y + 1, z) == 255) {
                                    x++;
                                }
                                EOL[count] = x;
                                y++;
                                x = BOL[count];
                                count++;
                            }
                            x++;
                        }
                        //reset start pixel and search down
                        x = BOL[0];
                        y = start_pixel[1];
                        while (stack.getVoxel(x, y, z) == 255) {
                            if (stack.getVoxel(x, y - 1, z) == 255) {
                                while (stack.getVoxel(x - 1, y - 1, z) == 255) {
                                    x--;
                                }
                                BOL[count] = x;
                                row[count] = y - 1;
                                while (stack.getVoxel(x + 1, y - 1, z) == 255) {
                                    x++;
                                }
                                EOL[count] = x;
                                y--;
                                x = BOL[count];
                                count++;
                            }
                            x++;
                        }
                        //parse tables
                        //get whole region					
                        for (int a = 0; a <= count - 1; a++) {				//loop rows
                            for (int c = BOL[a]; c <= EOL[a]; c++) {			//loop x or columns
                                x_positions[n_positions] = c;
                                y_positions[n_positions] = row[a];
                                n_positions++;
                            }
                        }
                        //add region to array
                        if (n_positions > 0) {
                            alResult.add(new microRegion(x_positions, y_positions, n_positions, n, original));  
                        }
                        //remove pixels from stack
                        for (int i = 0; i <= n_positions - 1; i++) {
                            stack.setVoxel(x_positions[i], y_positions[i], n, 0);
                        }
                        //reset	arrays

                        row = new int[(int) minConstants[1]];
                        x_positions = new int[(int) minConstants[1]];
                        y_positions = new int[(int) minConstants[1]];
                        n_positions = 0;
                        count = 0;
                    }
                }
            }
        }
        //this.Regions = Regions;
        //this.nRegions = nRegions;
        //this.alRegions =+ ;
        //this.nRegions =+ nRegions; 
        System.out.println("PROFILING: ...Regions found:  " + alResult.size());
        
        return alResult;
        
    }
        private synchronized void setRegions(){
            alRegions.addAll(alResult);
        }
        public ArrayList<microRegion> getRegions(){
            return this.alResult;
        }
        
        

        @Override
        protected Object doInBackground() {
            long start = System.nanoTime();

            IJ.log("PROFILING: Thread: " +  threadName + " RegionWorker.  Executing thread...");
            System.out.println("PROFILING: Thread: " +  threadName + " RegionWorker.  Executing thread...");
            
            ArrayList al = defineRegions();
            setRegions();
            
            long end = System.nanoTime();
            System.out.println("PROFILING: Thread: " +  threadName + " runtime: "+ ((end-start)/1000000) + " ms.");
            IJ.log("PROFILING: Thread: " +  threadName + " runtime: "+ ((end-start)/1000000) + " ms.");
            System.out.println("PROFILING: Thread: "  +  threadName + " exiting.");
            return al;
        }
        
        @Override
        public void done(){}
    }
    
    private class DerivedRegionSwingWorker extends SwingWorker  {
        private int[][] derivedRegionType;
        int channels;
        ImageStack[] stack;
        ArrayList ResultsPointers;
        int stop;
        ListIterator<microVolume> itr;
        Thread t;
        private String threadName = "derivedregionmaker_" + System.nanoTime();
     
        DerivedRegionSwingWorker(int[][] ldrt, int c, ImageStack[] st, ArrayList rp, ListIterator<microVolume> litr, int s) {
        this.derivedRegionType = ldrt;
        channels = c;
        stack = st;
        ResultsPointers = rp;
            stop = s;
            itr = litr;
        }

        private synchronized void defineDerivedRegions(){ 
            while(itr.hasNext()){
                microVolume mv = new microVolume();
                mv = itr.next();
                mv.makeDerivedRegions(derivedRegionType, channels, stack, ResultsPointers);
            }
        }

        @Override
        protected Object doInBackground(){
            long start = System.nanoTime();
            defineDerivedRegions();                            
            long end = System.nanoTime();
            System.out.println("PROFILING: Thread: " +  threadName + " runtime: "+ ((end-start)/1000000) + " ms.");
            return (end-start);
        }
        }
    
    private class DerivedRegionForkPool extends RecursiveAction {
        //class splits it self into new classes...  start with largest start and stop and subdivided recursively until start-stop is the number for the number of cores or remaineder.
        private int[][] derivedRegionType;
        int channels;
        ImageStack[] stack;
        ArrayList ResultsPointers;
        int stop;
        int start;
        List<microVolume> volumes = Collections.synchronizedList(new ArrayList<microVolume>());
 
        //Thread t;
        //private String threadName = "derivedregionmaker_" + System.nanoTime();
        
         DerivedRegionForkPool(int[][] ldrt, int c, ImageStack[] st, ArrayList rp, int start, int stop) {
        derivedRegionType = ldrt;
        channels = c;
        stack = st;
        ResultsPointers = rp;       
        this.stop = stop;
        this.start = start;       
        
        //System.out.println("PROFILING-DETAILS: ForkJoin Start and Stop points:" + start + ", " + stop);
        //volumes = alVolumes.subList(start, stop);
        }
         
        


        private void defineDerivedRegions(){ 
            ListIterator<microVolume> itr = alVolumes.listIterator(start);
            int i = start;
            while(itr.hasNext() && i<=stop){  
                microVolume mv = new microVolume();
                mv = itr.next();
                
                mv.makeDerivedRegions(derivedRegionType, channels, stack, ResultsPointers);
                //System.out.println("PROFILING: making derived regions.  " + mv.getName() + ", getting " + mv.getNDRegions() + " derived regions and " + mv.getderivedConstants()[1][0]+ "  Giving: " + mv.getAnalysisResultsVolume()[0][2]);
                i++;
            }
        }

        @Override
        protected void compute() {
        
            int processors = Runtime.getRuntime().availableProcessors();
            int length = alVolumes.size()/processors;
            
            if(alVolumes.size() < processors){
                length = alVolumes.size();
            }
            
            
            //System.out.println("PROFILING-DETAILS: ForkJoin Start and Stop points:" + start + ", " + stop + " for length: " + (stop-start) + " and target length: " + length);
            
            if(stop-start > length){
            invokeAll(new DerivedRegionForkPool(derivedRegionType, channels, stack, ResultsPointers, start, ((stop-start)/2)),
                new DerivedRegionForkPool(derivedRegionType, channels, stack, ResultsPointers, ((stop-start)/2)+1, stop));
             //System.out.println("PROFILING-DETAILS: ForkJoin Splitting...");
        }
            else{
                //System.out.println("PROFILING-DETAILS: ForkJoin Computing...");
                defineDerivedRegions();
            }
        }
    }  
    
    private class RegionForkPool extends RecursiveAction  {
        
        private int maxsize = 1;      
        //private microRegion[] Regions = new microRegion[(int) (maxsize / minConstants[0])];
        private ArrayList<microRegion> alResult = new ArrayList<microRegion>();
        private int[] start_pixel = new int[3];
        int x, y, z;
        //int[] x_positions = new int[(int) minConstants[1]];
        //int[] y_positions = new int[(int) minConstants[1]];
        
        ArrayList<Integer> x_positions = new ArrayList<Integer>();
        ArrayList<Integer> y_positions = new ArrayList<Integer>();
        
        
        int n_positions = 0;
        int[] BOL = new int[5000];  //start of line position
        int[] EOL = new int[5000];  //end of line position
        int[] row = new int[5000];  //line position
        
//        ArrayList<Integer> BOL = new ArrayList<Integer>();
//        ArrayList<Integer> EOL = new ArrayList<Integer>();
//        ArrayList<Integer> row = new ArrayList<Integer>();
        
        int count = 0;
        private ImageStack stack;
        private ImageStack original;       
        private int start;
        private int stop;

        private Thread t;
        private String threadName = "regionfinder_" + System.nanoTime();

        RegionForkPool(ImageStack st, ImageStack orig, int start, int stop){
            stackOriginal = stack = st;
            stackResult = original = orig;
            this.start = start;
            this.stop = stop;
            maxsize = stack.getSize() * stack.getWidth() * stack.getHeight(); 
        }


        private void defineRegions(){
            //uses a line scan flood fill method with backfill
            for (int n = this.start; n <= this.stop; n++) {
            //IJ.showProgress(n + 1, this.stop);
            //loop through pixels to find starts for regions		
            for (int p = 0; p <= stack.getWidth() - 1; p++) {
                for (int q = 0; q <= stack.getHeight() - 1; q++) {
                    //start pixel selected if 255, new region
                    
                    if (stack.getVoxel(p, q, n) == 255) {
                        start_pixel[0] = p;
                        start_pixel[1] = q;
                        start_pixel[2] = n;

                        //position being analyzed
                        x = start_pixel[0];
                        y = start_pixel[1];
                        z = start_pixel[2];

                        //052814 while(stack.getVoxel(x-1,y,z) == 255) {x--;}
                        //start pixel is left most in first row
                        //count is for the line in question
                        BOL[count] = x;
                        row[count] = y;
                        //BOL.add(x);
                        //row.add(y);
                        //run until 0, end of first row
                        while (stack.getVoxel(x, y, z) == 255) {
                            x++;
                        }
                        EOL[count] = x - 1;
                        //EOL.add(x-1);
                        count++;
                        //second row start, search up
                       // x = BOL.get(count - 1);
                        x = BOL[count - 1];

                        while (stack.getVoxel(x, y, z) == 255) {
                            if (stack.getVoxel(x, y + 1, z) == 255) {
                                while (stack.getVoxel(x - 1, y + 1, z) == 255) {
                                    x--;
                                }
                                BOL[count] = x;
                                row[count] = y + 1;
                                
                                while (stack.getVoxel(x + 1, y + 1, z) == 255) {
                                    x++;
                                }
                                EOL[count] = x;
                                y++;
                                x = BOL[count];
                                count++;
                            }
                            x++;
                        }
                        //reset start pixel and search down
                        x = BOL[0];
                        y = start_pixel[1];
                        while (stack.getVoxel(x, y, z) == 255) {
                            if (stack.getVoxel(x, y - 1, z) == 255) {
                                while (stack.getVoxel(x - 1, y - 1, z) == 255) {
                                    x--;
                                }
                            BOL[count] = x;
                                row[count] = y - 1;
                                while (stack.getVoxel(x + 1, y - 1, z) == 255) {
                                    x++;
                                }
                                EOL[count] = x;
                                y--;
                                x = BOL[count];
                                count++;
                            }
                            x++;
                        }
                   // }
                        
                        
                        //parse tables
                        //get whole region
                        
                        for (int a = 0; a <= count - 1; a++) {				//loop rows
                            for (int c = BOL[a]; c <= EOL[a]; c++) {			//loop x or columns
                                x_positions.add(c);
                                y_positions.add(row[a]);
                            }
                        }
                        //add region to array
                        if (x_positions.size() > 0 && x_positions.size() < (int) minConstants[1]) {
                            alResult.add(new microRegion(convertPixelArrayList(x_positions), convertPixelArrayList(y_positions), x_positions.size(), n, original));  
                        }
                        //remove pixels from stack
                        for (int i = 0; i <= x_positions.size() - 1; i++) {
                            stack.setVoxel(x_positions.get(i), y_positions.get(i), n, 0);
                        }
                        //reset	arrays
                      
                        row = new int[5000];
                        //row.clear();
                        x_positions.clear();
                        y_positions.clear();
                        n_positions = 0;
                        count = 0;
                    }
                }
            }
        }
        //this.Regions = Regions;
        //this.nRegions = nRegions;
        //this.alRegions =+ ;
        //this.nRegions =+ nRegions; 
        //System.out.println("PROFILING: ...Regions found:  " + alResult.size());
        
    }
        private void defineRegions2() {
//refining connected component labeling
        ImageStack resultstack = stack;
        double c = 256;
        for (int n = 0; n <= stack.getSize() - 1; n++) {
            	
            //loop through pixels to find starts		
            for (int p = 0; p <= stack.getWidth() - 1; p++) {
                for (int q = 0; q <= stack.getHeight() - 1; q++) {
                    //start pixel selected if 255
                    if (stack.getVoxel(p, q, n) == 0) {
                        resultstack.setVoxel(p, q, n, 0);
                    }
                    if (stack.getVoxel(p, q, n) == 255) {
                        int[] point = new int[4];
                        point[0] = p;
                        point[1] = q;
                        point[2] = n;
                        check8Neighbors(stack, point, c);
                        resultstack.setVoxel(p, q, n, c);                      
                    }
                }
            }
        }
    }
        private void setRegions(){
            alRegions.addAll(alResult);
        }
        
        public ArrayList<microRegion> getRegions(){
            return this.alResult;
        }
        
        public int[] convertPixelArrayList(List<Integer> integers)      {
            int[] ret = new int[integers.size()];
            Iterator<Integer> iterator = integers.iterator();
            for (int i = 0; i < ret.length; i++)
                {
                ret[i] = iterator.next().intValue();
                }
                return ret;
            }

        @Override
        protected void compute() {
            
            int processors = Runtime.getRuntime().availableProcessors();
            int length = stack.getSize()/processors;
            
            if(stack.getSize() < processors){
                length = stack.getSize();
            }
            
            
            //int remainder = alRegions.size()%processors; 
            
            //System.out.println("PROFILING-DETAILS: Region Making ForkJoin Start and Stop points:" + start + ", " + stop + " for length: " + (stop-start) + " and target length: " + length);
            
            if(stop-start > length){
                // RegionForkPool(ImageStack st, ImageStack orig, int start, int stop)
            invokeAll(new RegionForkPool(stack, original, start, ((stop-start)/2)),
                new RegionForkPool(stack, original, ((stop-start)/2)+1, stop));
             //System.out.println("PROFILING-DETAILS: Region Making ForkJoin Splitting...");
        }
            else{
                defineRegions();
                setRegions();
            }
        }
    }
    
    private class VolumeForkPool extends RecursiveAction {
        
        private int start;
        private int stop;
        
        private List<microRegion> alRegionsLocal = Collections.synchronizedList(new ArrayList<microRegion>());
        private List<microRegion> alRegionsProcessedLocal = Collections.synchronizedList(new ArrayList<microRegion>());
        
        private int[] minConstantsLocal; 
        
        private int nVolumesLocal;
        
        
        
     public VolumeForkPool(List<microRegion> Regions, int[] minConstants, int start, int stop){
        this.minConstantsLocal = minConstants;
        this.alRegionsLocal = Regions.subList(start, stop);
        this.nVolumesLocal = 0;
        this.start = start;
        this.stop = stop;
     }
     
     private synchronized void defineVolumes(){
        int z;
        microVolume volume = new microVolume();
        double[] startRegion = new double[2];
        
        microRegion test = new microRegion();

        int i = start;

         while(i < stop){      
            test = alRegions.get(i);  
                if(!test.isAMember()){
                    nVolumesLocal++;
                    startRegion[0] = test.getBoundCenterX();
                    startRegion[1] = test.getBoundCenterY();
                    test.setMembership(nVolumesLocal);          
                    test.setAMember(true);
                    z = test.getZPosition();
                    alRegionsProcessedLocal.add(test);
                    findConnectedRegions(nVolumesLocal, startRegion, z); 
                }
            i++;
        }

        for(int j = 1; j <= this.nVolumesLocal; j++){
            volume = new microVolume();
            volume.setName("vol_" + j);
            Iterator<microRegion> vol = alRegionsProcessedLocal.listIterator();
            microRegion region = new microRegion();
                while(vol.hasNext()){
                    region = vol.next();
                    if(j == region.getMembership()){
                        volume.addRegion(region);
                    }
                }
                if(volume.getNRegions() > 0){
                    volume.calculateVolumeMeasurements();
                    if(volume.getPixelCount() >= minConstantsLocal[0]){
                        alVolumes.add(volume); 
                    }
                }  
        }
    }

        @Override
        protected void compute() {
            int processors = Runtime.getRuntime().availableProcessors();
            int length = alRegions.size()/processors;
            //int remainder = alRegions.size()%processors; 
            
            if(alRegions.size() < processors){
                length = alRegions.size();
            }
           
            
            
            if(stop-start > length){
                // RegionForkPool(ImageStack st, ImageStack orig, int start, int stop)
                invokeAll(new VolumeForkPool(alRegionsLocal, minConstantsLocal, start, ((stop-start)/2)),
                new VolumeForkPool(alRegionsLocal, minConstantsLocal, ((stop-start)/2)+1, stop));
                //System.out.println("PROFILING-DETAILS: Region Making ForkJoin Splitting...");
        }
            else{
                //System.out.println("PROFILING-DETAILS: Volume Making Fork Join Start and Stop points: " + start + ", " + stop + " for length: " + alRegionsLocal.size());
                defineVolumes();
            }
        }

        private synchronized void findConnectedRegions(int volumeNumber, double[] startRegion, int z){
        
        double[] testRegion = new double[2];
        int i = start;
         while(i < stop-1){
            microRegion test = new microRegion();
            test = alRegions.get(i);
                testRegion[0] = test.getBoundCenterX();
                testRegion[1] = test.getBoundCenterY();
                double comparator = lengthCart(startRegion, testRegion);
                if(!test.isAMember()){
                    if (comparator <= minConstants[2] && ((test.getZPosition()-z) == 1)) {
                        test.setMembership(volumeNumber);                   
                        test.setAMember(true); 
                        z = test.getZPosition();
                        testRegion[0] = (testRegion[0] + startRegion[0])/2;
                        testRegion[1] = (testRegion[1] + startRegion[1])/2;
                        alRegionsProcessedLocal.add(test);
                        //alRegions.remove(i); 
                        findConnectedRegions(volumeNumber, testRegion, z);
                        //System.out.println("PROFILING: Adding regions: " + i);
                    } 
                }
        i++;
    }
    }
    }
    
    private synchronized void cleanupVolumes() {

        //loop through all volumes
        List<microVolume> alVolumesTrim = Collections.synchronizedList(new ArrayList<microVolume>());
        microVolume[] mvs = new microVolume[alVolumes.size()];
        mvs = alVolumes.toArray(mvs);
        microVolume testVolume;
        List<microRegion> testRegions;
        microRegion testRegion;
        microVolume referenceVolume;
        List<microRegion> referenceRegions;
        microRegion referenceRegion;
        int referenceZ;
        int[] assigned = new int[alVolumes.size()];

        for(int i = 0; i < assigned.length; i++){
            assigned[i] = 0;
        }
        microVolume resultVolume = new microVolume();  
        double testCentroid[] = new double[2]; 
        double referenceCentroid[] = new double[2];

        for(int i = 0; i < mvs.length; i++){
            if(assigned[i] == 0){
            referenceVolume = mvs[i];
            
            referenceRegions = referenceVolume.getRegions();
            referenceRegion = referenceRegions.get(referenceRegions.size()-1);
            referenceCentroid[0] = referenceRegion.getBoundCenterX();
            referenceCentroid[1] = referenceRegion.getBoundCenterY();
            referenceZ = referenceRegion.getZPosition();
            
            //System.out.println("PROFILING-volume cleanup, on: " + mvs[i].getName() + " at: " + referenceZ + " and " + mvs[i].getNRegions() + " regions.");
            
            //System.out.println("PROFILING-checking for neighboring volumes: " + i);
            
            for(int j = 0; j < mvs.length; j++){
                
                testVolume = mvs[j];
                testRegions = testVolume.getRegions();
                testRegion = testRegions.get(0);
                testCentroid[0] = testRegion.getBoundCenterX();
                testCentroid[1] = testRegion.getBoundCenterY();
                if(i != j && assigned[j] != 1 && lengthCart(testCentroid, referenceCentroid) < minConstants[2] && testRegion.getZPosition()-referenceZ == 1){
                    ListIterator<microRegion> testItr = testRegions.listIterator();
                    while(testItr.hasNext()){
                        microRegion reg = testItr.next();
                        resultVolume.addRegion(new microRegion(reg.getPixelsX(),reg.getPixelsY(),reg.getPixelCount(),reg.getZPosition(), stackOriginal));
                    }
                    resultVolume.addRegions(referenceRegions);
                    resultVolume.addRegions(testRegions); 
                    resultVolume.setName(referenceVolume.getName() + "_" + testVolume.getName());
                    assigned[i] = 1;
                    assigned[j] = 1;

                    //System.out.println("PROFILING-found a partner: " + mvs[j].getName() + " at z: " + testRegion.getZPosition() + " at, " + lengthCart(testCentroid, referenceCentroid) + " pixels.");
                }
                testVolume = new microVolume();     
            }

             if(assigned[i] == 1)  {      
                resultVolume.calculateVolumeMeasurements();
                //System.out.println("PROFILING-calculated volume measures: " + resultVolume.getName() + ". Giving derived: " + resultVolume.getAnalysisResultsVolume()[0][2] + " for "+ resultVolume.getNRegions() + " regions.");
                //System.out.println("PROFILING-calculated volume measures: " + resultVolume.getName() + ".  Giving region: " + resultVolume.getAnalysisMaskVolume()[2] + " for "+ resultVolume.getNRegions() + " regions.");
                alVolumesTrim.add(resultVolume);
                //System.out.println("PROFILING: Adding to list: " + resultVolume.getName());
                resultVolume = new microVolume();
                resultVolume.setName("");
                referenceVolume = new microVolume();
             }
            }

        }
        
        for(int k = 0; k < mvs.length; k++){
            if(assigned[k] == 0){
                microVolume mv = new microVolume();
                mv = mvs[k];
                mv.calculateVolumeMeasurements();
                alVolumesTrim.add(mv);
                //System.out.println("PROFILING: Adding to list: " + mv.getName());
            }
        }
        

        alVolumes.clear();
        //System.out.println("PROFILING: Volumes found: " + alVolumesTrim.size());
        alVolumes.addAll(alVolumesTrim);
    }
    /**
     * Methods
     */

    private void makeRegions(ImageStack stack, ImageStack original) {
        
        int processors = Runtime.getRuntime().availableProcessors();
        int length = stack.getSize()/processors;
        int remainder = stack.getSize()%processors;
        int start = 0;
        int stop = start+length-1;
        
      
        
        CopyOnWriteArrayList<RegionWorker> rw = new CopyOnWriteArrayList<RegionWorker>();
        
        if(stack.getSize() < processors){
            length = 1;
            remainder = 0;
            processors = stack.getSize();
        }

        for(int i=0; i < processors; i++) {
            if(i == processors-1){
               RegionWorker region = new RegionWorker(stack.duplicate(), original.duplicate(), start, stop); 
               //region.doInBackground();
               //IJ.log("RegionFactory::makeRegion Created thread #"+i +" for slices: " + start + " to " + stop + ".");
               rw.add(region);
               start = stop+1;
               stop = stop+length+remainder; 
            } else {
               RegionWorker region = new RegionWorker(stack.duplicate(), original.duplicate(), start, stop); 
               //region.doInBackground();
               //IJ.log("RegionFactory::makeRegion Created thread #"+i +" for slices: " + start + " to " + stop + ".");
               rw.add(region);
               start = stop+1;
               stop = start+length; 
    }
  
        }
        ListIterator<RegionWorker> itr = rw.listIterator();
        while(itr.hasNext()){
            itr.next().start();
        }

        //IJ.log("RegionFactory::makeRegion Combined regions found: " + alRegions.size());
    }
    
    private void makeRegionsPool(ImageStack stack, ImageStack original){
        RegionForkPool rrf = new RegionForkPool(stack, original, 0, stack.getSize());
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(rrf);
    }
    
    private void makeRegions2(ImageStack stack, ImageStack original) {
//refining connected component labeling
        ImageStack resultstack = stack;
        double c = 256;
        for (int n = 0; n <= stack.getSize() - 1; n++) {
            	
            //loop through pixels to find starts		
            for (int p = 0; p <= stack.getWidth() - 1; p++) {
                for (int q = 0; q <= stack.getHeight() - 1; q++) {
                    //start pixel selected if 255
                    if (stack.getVoxel(p, q, n) == 0) {
                        resultstack.setVoxel(p, q, n, 0);
                    }
                    if (stack.getVoxel(p, q, n) == 255) {
                        int[] point = new int[4];
                        point[0] = p;
                        point[1] = q;
                        point[2] = n;
                        check8Neighbors(stack, point, c);
                        resultstack.setVoxel(p, q, n, c);                      
                    }
                }
            }
        }
    }

    private double[] check8Neighbors(ImageStack stack, int[] point, double counter) {
        double[] result = new double[2];
        int x = point[0];
        int y = point[1];
        int z = point[2];
        double[] neighbors = new double[10];

        //N
        try {
            neighbors[0] = stack.getVoxel(x, y + 1, z);
        } catch (NullPointerException e) {
            neighbors[0] = 0;
        }
        //NE
        try {
            neighbors[1] = stack.getVoxel(x + 1, y + 1, z);
        } catch (NullPointerException e) {
            neighbors[1] = 0;
        }
        //E
        try {
            neighbors[2] = stack.getVoxel(x, y + 1, z);
        } catch (NullPointerException e) {
            neighbors[2] = 0;
        }
        //SE
        try {
            neighbors[3] = stack.getVoxel(x - 1, y + 1, z);
        } catch (NullPointerException e) {
            neighbors[3] = 0;
        }
        //S
        try {
            neighbors[4] = stack.getVoxel(x, y - 1, z);
        } catch (NullPointerException e) {
            neighbors[4] = 0;
        }
        //SW
        try {
            neighbors[5] = stack.getVoxel(x - 1, y - 1, z);
        } catch (NullPointerException e) {
            neighbors[5] = 0;
        }
        //W
        try {
            neighbors[6] = stack.getVoxel(x - 1, y, z);
        } catch (NullPointerException e) {
            neighbors[6] = 0;
        }
        //NW
        try {
            neighbors[7] = stack.getVoxel(x - 1, y + 1, z);
        } catch (NullPointerException e) {
            neighbors[7] = 0;
        }
        //up
        try {
            neighbors[8] = stack.getVoxel(x, y, z + 1);
        } catch (NullPointerException e) {
            neighbors[7] = 0;
        }
        //down
        try {
            neighbors[9] = stack.getVoxel(x, y, z - 1);
        } catch (NullPointerException e) {
            neighbors[7] = 0;
        }

        //parse neighbors array
        double tag = counter;
        for (int i = 0; i <= 10; i++) {
            if (neighbors[i] > 255) {
                tag = neighbors[i];
            }
            if (neighbors[i] == 255) {
                tag = counter++;
            }
        }

        result[0] = tag;
        result[1] = counter;

        return result;
    }
    
    public void makeDerivedRegions(int[][] localDerivedRegionTypes, int channels, ImageStack[] stack, ArrayList ResultsPointers) {
        ListIterator<microVolume> itr = alVolumes.listIterator();
        while(itr.hasNext()){
            microVolume mv = new microVolume();
            mv = itr.next();
            mv.makeDerivedRegions(localDerivedRegionTypes, channels, stack, ResultsPointers);
        }
    }

    public void makeDerivedRegionsThreading(int[][] localDerivedRegionTypes, int channels, ImageStack[] Stack, ArrayList ResultsPointers) {

        int processors = Runtime.getRuntime().availableProcessors();
        int length = alVolumes.size()/processors;
        int remainder = alVolumes.size()%processors; 
        
        int start = 0;
        int stop = start+length-1;
        
        CopyOnWriteArrayList<DerivedRegionWorker> rw = new CopyOnWriteArrayList<DerivedRegionWorker>();

        for(int i=0; i < processors; i++) {
            ArrayList<microVolume> volume = new ArrayList<microVolume>();
            if(i == processors-1){
               synchronized(alVolumes){
                    //ListIterator<microVolume> itr = alVolumes.listIterator(start);
                   //DerivedRegionWorker region = new DerivedRegionWorker(localDerivedRegionTypes, channels, Stack, ResultsPointers, itr, stop);  
                    ArrayList<microVolume> process = new ArrayList<microVolume>();
                    process.addAll(alVolumes.subList(start, stop));
                    
                    DerivedRegionWorker region = new DerivedRegionWorker(localDerivedRegionTypes, channels, Stack, ResultsPointers, process, stop); 
                    rw.add(region);
    }
               //IJ.log("RegionFactory::makeDerivedRegion Created thread #"+i +" for volumes: " + start + " to " + stop + ", " + volume.size() + " total.");

               start = stop+1;
               stop = stop+length+remainder; 
            } else {
               synchronized(alVolumes){
                    //ListIterator<microVolume> itr = alVolumes.listIterator(start);
                    //DerivedRegionWorker region = new DerivedRegionWorker(localDerivedRegionTypes, channels, Stack, ResultsPointers, itr, stop); 
                    ArrayList<microVolume> process = new ArrayList<microVolume>();
                    process.addAll(alVolumes.subList(start, stop));
                    DerivedRegionWorker region = new DerivedRegionWorker(localDerivedRegionTypes, channels, Stack, ResultsPointers, process, stop); 
                    rw.add(region);
               }
               //IJ.log("RegionFactory::makeDerivedRegion Created thread #"+i +" for volumes: " + start + " to " + stop + ", " + volume.size() + " total.");

               start = stop+1;
               stop = start+length; 
            } 
        }
        ListIterator<DerivedRegionWorker> itr = rw.listIterator();
        while(itr.hasNext()){
            itr.next().start();
        }     
    }
    
    public void makeDerivedRegionsPool(int[][] localDerivedRegionTypes, int channels, ImageStack[] Stack, ArrayList ResultsPointers) {
        
        DerivedRegionForkPool drf = new DerivedRegionForkPool(localDerivedRegionTypes, channels, Stack, ResultsPointers, 0, alVolumes.size());
 
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(drf);

    }

    private float maxPixel(ImageStack stack) {
        float max = this.minConstants[0];	
        for (int n = 0; n <= stack.getSize() - 1; n++) {
            ImageProcessor ipStack = stack.getProcessor(n + 1);
            if (ipStack.getMax() > max) {
                max = (float) ipStack.getMax();
            }
        }
        return max;
    }

    private boolean containsPoint(int x1, int y1, int[] x, int[] y, int n) {

        for (int i = 0; i <= n; i++) {
            if (x1 == x[n]) {
                if (y1 == y[n]) {
                    return true;
                }
            }
        }
        return false;
    }

    private ImageStack optimizeMask(ImageStack inputStack) {
        int localwidth = inputStack.getWidth();
        int localheight = inputStack.getHeight();  

        ImagePlus localImp = new ImagePlus(null, inputStack);

        IJ.run(localImp, "Subtract Background...", "rolling=50 stack");

        StackProcessor sp = new StackProcessor(localImp.getStack());
        ImageStack s1 = sp.resize(localwidth * 2, localheight * 2, true);
        localImp.setStack(null, s1);

        IJ.run(localImp, "Median...", "radius=2 stack");

        sp = new StackProcessor(localImp.getStack());
        ImageStack s2 = sp.resize(localwidth, localheight, true);

        localImp.setStack("Mask Channel-Optimized", s2);
        localImp.show();

        return s2;
    }

    private int[] calculateCartesian(int pixel, int width, int slice) {
        int[] result = new int[3];
        result[1] = (int) Math.ceil(pixel / width);
        result[0] = pixel - (result[1] * width);
        result[2] = slice - 1;
        return result;
    }

    private int calculateLinear(int x, int y, int width) {
        int result = (width * y) - (width - x);
        return result;
    }

    private double lengthCart(double[] position, double[] reference_pt) {
        double distance;
        double part0 = position[0] - reference_pt[0];
        double part1 = position[1] - reference_pt[1];
        distance = Math.sqrt((part0 * part0) + (part1 * part1));
        return distance;
    }

    public List<microRegion> getRegions() {
        return alRegions;
    }

    public int getRegionsCount() {
        return this.alRegions.size();
    }
    
        public int getProcessedRegionsCount() {
        return this.alRegionsProcessed.size();
    }
    
    public void setWaterShedImageJ(boolean b) {
        this.watershedImageJ = b;
    }

    public microVolume[] getVolumes() {
        return this.Volumes;
    }

    public int getVolumesCount() {
        return this.alVolumes.size();
    }

    public ImagePlus getMaskImage() {
        return this.imageResult;
    }

    public ImagePlus getOriginalImage() {
        return this.imageOriginal;
    }

    public ArrayList getVolumesAsList() {
        return new ArrayList(alVolumes);
    }

}
