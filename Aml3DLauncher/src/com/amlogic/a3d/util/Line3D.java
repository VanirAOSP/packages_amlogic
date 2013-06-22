package com.amlogic.a3d.util;

import android.util.Log;

import com.amlogic.a3d.math.Matrix;

public class Line3D
{
	private Point3D p0;
	private Point3D p1;
	private float vector[]=new float[3];
	

	public Line3D(Point3D point0,Point3D point1)
	{
		p0=point0;
		p1=point1;
		vector[0]=point1.getX()-point0.getX();
		vector[1]=point1.getY()-point0.getY();
		vector[2]=point1.getZ()-point0.getZ();

	}
	public float[] getVector()
	{
		return vector;
	}
	
	public Point3D getBeginPoint()
	{
		return p0;
	}
	public Point3D getEndPoint()
	{
		return p1;
	}	
	
	public boolean hasPoint(float x,float y,float z)
	{
		float t1=(x-p0.getX())/vector[0];
		float t2=(y-p0.getY())/vector[1];
		float t3=(z-p0.getZ())/vector[2];
		if(t1==t2&&t1==t3&&(t1>=0||t1<=1))
		{
			return true;
		}
		else 
		{
			return false;
		}
	} 
	
	public Point3D getCrossPointWithPlane(Plane p)
	{
		float[][] matrix={   //4X4
						   {1,0,0,-vector[0]},
						   {0,1,0,-vector[1]},
						   {0,0,1,-vector[2]},
						   {p.vector[0],p.vector[1],p.vector[2],0}
					     };
		
		Matrix rev=Matrix.rev(new Matrix(matrix));		
		if(rev==null) 
		{
			Log.v("","rev matrix is null");
			return null;
		}
		float[][] matrixr={    //1X4
							{p0.getX()},
							{p0.getY()},
							{p0.getZ()},
							{-p.d}
						  };
		Matrix result=Matrix.multiply(rev, new Matrix(matrixr));
		if(result==null)
		{
			Log.v("","result matrix is null");
			return null;
		}
		float t=result.getElement(4, 1);
		float x=result.getElement(1, 1);
		float y=result.getElement(2, 1);
		float z=result.getElement(3, 1);
//		Log.v("","t:"+t);
		if(t>1||t<0)   //detect whether the cross point is between the two points.  
		{
			return null;
		}
		if(p.hasPoint(x,y,z))
		{
			return new Point3D(x,y,z);
		}
		return null;
	}
	public Point3D getCrossPointWithLine(Line3D l)
	{
		//need to be added
		return null;
	}
	
	public double getDistanceWithPoint(Point3D p)
	{
		double SAB=getDistanceOfTwoPoints(p0,p1);
		double SAC=getDistanceOfTwoPoints(p0,p);
		double SBC=getDistanceOfTwoPoints(p1,p);
		double ref=(SAB+SBC+SAC)/2;
		double s=Math.sqrt(ref*(ref-SAB)*(ref-SAC)*(ref-SBC));
		double distance=2*s/SAB;
		return distance;
	}
	
	public static double getDistanceOfTwoPoints(Point3D tmpp0,Point3D tmpp1)
	{
		double S=Math.sqrt((tmpp0.getX()-tmpp1.getX())*(tmpp0.getX()-tmpp1.getX())
				+(tmpp0.getY()-tmpp1.getY())*(tmpp0.getY()-tmpp1.getY())
				+(tmpp0.getZ()-tmpp1.getZ())*(tmpp0.getZ()-tmpp1.getZ()));
		return S;
	}

}