package com.amlogic.a3d.util;

import java.util.ArrayList;

import android.util.Log;


/*
 *         p3---------- 
 *          /|       / |
 *       p1--|------p2 | 
 *         | |      |  |
 *         | |      |  |
 *         | /      | /
 *        p0--------
 * */
public class Cube
{
	private Point3D p0,p1,p2,p3;
	private Plane front,top,left;
	private ArrayList<Plane> planelist=null;
	public Cube(float[] pointarray)
	{
		planelist=new ArrayList<Plane>();
		p0= new Point3D(pointarray[0],pointarray[1],pointarray[2]);
		p1= new Point3D(pointarray[4],pointarray[5],pointarray[6]);
		p2= new Point3D(pointarray[8],pointarray[9],pointarray[10]);
		p3= new Point3D(pointarray[12],pointarray[13],pointarray[14]);
		front= new Plane(p0,p1,p2);
		top=new Plane(p2,p1,p3);
		left=new Plane(p0,p1,p3);
		planelist.add(front);
		planelist.add(top);
		planelist.add(left);

	}
	
	public Point3D getCrossPointWithLine(Line3D line)
	{
		Point3D cp=null;
		for(int i=0;i<planelist.size();i++)
		{
			Point3D cp1=line.getCrossPointWithPlane(planelist.get(i));
			if(cp1!=null)
			{
				if(cp==null)
					cp=cp1;
				else if(cp1.getZ()>cp.getZ())
				{
					cp=cp1;
				}
			}
		}
		return cp;
	}
}