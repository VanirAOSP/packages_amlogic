package com.amlogic.a3d.util; 

import com.amlogic.a3d.scene.Mesh;
  
 /** 
  * A single frame in an model for animation. 
  */ 
 public class ModelFrame { 
         Mesh mesh; 
  
         /** 
          * Constructor 
          * @param name The name of this frame. 
          * @param mesh The mesh for this frame. 
          */ 
         public ModelFrame(Mesh mesh) { 
                 this.mesh = mesh; 
         } 
  
         /** 
          * Get the mesh for this frame. 
          * @return The Mesh for this Frame 
          */ 
         public Mesh getMesh() { return mesh; } 
 } 
 