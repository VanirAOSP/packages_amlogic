package com.amlogic.a3d.util;

import android.util.Log;

import com.amlogic.a3d.math.MatrixUtils;

public class Plane
{
	float vector[]=new float[3];
	float d;
	private Point3D p0,p1,p2,p3;
	public Plane(Point3D p0tmp,Point3D p1tmp,Point3D p2tmp)
	{
		p0=p0tmp;
		p1=p1tmp;
		p2=p2tmp;
		p3=new Point3D(p0.getX()+p2.getX()-p1.getX(),p0.getY()+p2.getY()-p1.getY(),
					   p0.getZ()+p2.getZ()-p1.getZ());
		Line3D line1=new Line3D(p0,p1);
		Line3D line2=new Line3D(p1,p2);
		MatrixUtils.cross(line1.getVector(), line2.getVector(), vector);
		d=-vector[0]*p0.getX()-vector[1]*p0.getY()-vector[2]*p0.getZ();
	} 
	
	
	boolean hasPoint(Point3D point)
	{
		return hasPoint(point.getX(),point.getY(),point.getZ());
	}
	
	
	//detect if the point is in the range of four points;
	boolean hasPoint(float x,float y,float z)
	{   
		Point3D ptmp=new Point3D(x,y,z);
		float result=vector[0]*x+vector[1]*y+vector[2]*z;

		Line3D line1=new Line3D(p0,p1);
		Line3D line2=new Line3D(p1,p2);
		Line3D line3=new Line3D(p2,p3);
		Line3D line4=new Line3D(p3,p0);
//		Log.v("",""+line1.getDistanceWithPoint(ptmp)+" "+line3.getDistanceWithPoint(ptmp)+" "+Line3D.getDistanceOfTwoPoints(p1,p2));
		if(line1.getDistanceWithPoint(ptmp)+line3.getDistanceWithPoint(ptmp)>Line3D.getDistanceOfTwoPoints(p1,p2)+0.02)
			return false;
//		Log.v("",""+line2.getDistanceWithPoint(ptmp)+" "+line4.getDistanceWithPoint(ptmp)+" "+Line3D.getDistanceOfTwoPoints(p1,p0));

		if(line2.getDistanceWithPoint(ptmp)+line4.getDistanceWithPoint(ptmp)>Line3D.getDistanceOfTwoPoints(p0,p1)+0.02)
			return false;
		return true;

	}

	
		

}