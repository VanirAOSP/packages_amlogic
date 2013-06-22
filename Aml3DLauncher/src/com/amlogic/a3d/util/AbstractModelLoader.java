package com.amlogic.a3d.util; 
  
 import java.io.File; 
 import java.io.FileInputStream; 
 import java.io.IOException; 
  
 /** 
  * A helper abstract handler implementation which concrete ModelLoaders can extend from. 
  * Extending classes must provide the: 
  * <ul> 
  * <li><code>void load(InputStream is);</code> 
  * <li><code>boolean canLoad(File f);</code> 
  * </ul> 
  */ 
 public abstract class AbstractModelLoader implements ModelLoader { 
          
         /** 
          * {@inheritDoc} 
          */ 
         public Model load(String file) throws IOException 
         { 
                 return load(new File(file)); 
         } 
          
         /** 
          * {@inheritDoc} 
          */ 
         public Model load(File f) throws IOException  
         { 
                 return load(new FileInputStream(f)); 
         } 
          
         /** 
          * {@inheritDoc} 
          */ 
         public boolean canLoad(String f) { 
                 return canLoad(new File(f)); 
         } 
 } 
 