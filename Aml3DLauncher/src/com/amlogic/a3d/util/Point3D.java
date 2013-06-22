package com.amlogic.a3d.util;

public class Point3D
{
	private float x,y,z;
	public Point3D()
	{
		
	}
	
	public Point3D(float x1,float y1,float z1)
	{
		x=x1;y=y1;z=z1;
	}
	public float getX()
	{
		return x;
	}
	public float getY()
	{
		return y;
	}
	public float getZ()
	{
		return z;
	}
	
	public void setValue(float x1,float y1,float z1)
	{
		x=x1;y=y1;z=z1;
	}
	
	public boolean isInRect(float minx,float maxx,float miny,float maxy)
	{
		if(x>=minx&&x<=maxx&&y<=maxy&&y>=miny)
		{
			return true;
		}else
		{
			return false;
		}
	}
}