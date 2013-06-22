package com.amlogic.AML3Dlauncher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.amlogic.AML3Dlauncher.ObjinfoMgr.ObjInfo;
import com.amlogic.a3d.anim.A3DAnimation;
import com.amlogic.a3d.anim.A3DPathAnimation;
import com.amlogic.a3d.scene.A3DLight;

class PathMgr
{
	public static PathMgr instance = new PathMgr();

    private class mPathAnimation
    {
    	String id;
    	A3DPathAnimation pathAnimation;
    	public mPathAnimation(String st,A3DPathAnimation animation)
    	{
    		id=st;
    		pathAnimation=animation;
    	}
    	public void addpoint(float x,float y,float z)
    	{
    		pathAnimation.addPoint(x, y, z);
    	}
    }
    
	private ArrayList<mPathAnimation> AnimationList;
	
	private PathMgr()
	{
		AnimationList=new ArrayList<mPathAnimation>();
	}
//	
//	public class point
//	{
//		public float x,y,z;
//		public point(float x1,float y1,float z1)
//		{
//			x=x1;y=y1;z=z1;
//		}
//	}
//    private class PathM {
//        public String mName;
//        public  ArrayList<point> path;
//        
//        public PathM(String name){
//        	mName=name;
//        	path = new ArrayList<point>();
//        }
//        public void addpoint(float x1,float y1,float z1)
//        {
//        	path.add(new point(x1,y1,z1));
//        }
//    }

	public void parse(InputStream is)
	{
		BufferedReader input = null;
		try {
			input= new BufferedReader( new InputStreamReader(is));
			String line=null;
			Pattern PathPattern = Pattern.compile(
			"(.*):(.*)ms,(.*),(.*),(.*),(.*),(.*)");

			Pattern PointPattern = Pattern.compile(
			"\\{(.*),(.*),(.*)\\},.*");

			while((line = input.readLine())!=null) 
			{
				if(line.startsWith("[Path]"))
				{
					mPathAnimation pathAnimation=null;
					while((line = input.readLine())!=null)
					{
						if(line.startsWith("//"))
							continue;
						if(line.startsWith("[/Path]"))
						{
							if(pathAnimation!=null)
								AnimationList.add(pathAnimation);
							break;
						}
						if(pathAnimation==null)
						{
							Matcher m = PathPattern.matcher(line);
							while(m.find())
							{
								
								
								long duration=Long.parseLong(m.group(2).trim());
								int type=Integer.parseInt(m.group(3).trim());
								int count=Integer.parseInt(m.group(4).trim());
								A3DPathAnimation a= new A3DPathAnimation(null,new LinearInterpolator(),duration,type,count);
								a.setDelayTime(Long.parseLong(m.group(5).trim()), Long.parseLong(m.group(6).trim()));
								pathAnimation= new mPathAnimation(m.group(1),a);
								if(m.group(7).trim().startsWith("R"))
								{
									pathAnimation.pathAnimation.setPostionType(1);
								}
							}							
						}else
						{
							Matcher m = PointPattern.matcher(line);
							while(m.find())
							{
								float x=Float.parseFloat(m.group(1));
								float y=Float.parseFloat(m.group(2));
								float z=Float.parseFloat(m.group(3));

								pathAnimation.addpoint(x, y, z);
							}	
						}
					}
				}
			}
		}catch(IOException e)
		{
			e.printStackTrace();
		}		
	}
	
	public A3DAnimation getAnimation(String name)
	{
		int number=AnimationList.size();
		for(int i=0;i<number;i++)
		{
			
			if( name.compareTo(AnimationList.get(i).id)==0)
			{
				return AnimationList.get(i).pathAnimation;
			}
		}
		return null;
	}

	
}