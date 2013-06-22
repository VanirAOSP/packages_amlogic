package com.amlogic.AML3Dlauncher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.opengl.GLSurfaceView;
import android.util.Log;


public class ObjinfoMgr {
	public static ObjinfoMgr instance = new ObjinfoMgr();
	
	public class ObjInfo
	{
		public String id,objname,colorTexture,normalTexture;
		public float x,y,z,rotate,rx,ry,rz,scalex,scaley,scalez,alphaR,alphaG,alphaB,alphaA;
		public int needMirror=0;
		public int needTrans=0;
		public ObjInfo(String name,String obj,String colorText,String normalText,
				float x1,float y1,float z1,
				float rot,float rotx,float roty,float rotz,
				float scalx,float scaly,float scalz ,
				float alphr,float alphg,float alphb,float alpha )
		{
			id=name;
			objname=obj;
			colorTexture=colorText;
			normalTexture=normalText;
			
			x=x1;
			y=y1;
			z=z1;
			rotate=rot;
			rx=rotx;ry=roty;rz=rotz;
			scalex=scalx;scaley=scaly;scalez=scalz;
			alphaR=alphr;alphaG=alphg; alphaB=alphb; alphaA=alpha;
			//add 
			
		}
		public void setExtraInfo(int mirror,int trans )
		{
			needMirror=mirror;
			needTrans=trans;
		}
	}
	private ArrayList<ObjInfo> objinfolist;
	private ArrayList<GroupInfo> groupinfolist;
	
	public class GroupInfo
	{
		String id;
		public String[] content;
		public String extraInfo;
		public GroupInfo(String name ,String[] stl,String extstl)
		{
			id=name;
			content=stl.clone();
			extraInfo=extstl;
		}

	}
	
	private ObjinfoMgr()
	{
		objinfolist= new ArrayList<ObjInfo>();
		groupinfolist = new ArrayList<GroupInfo>();
	}
	
	public void parse(InputStream is)
	{

		
		BufferedReader input = null;
		Pattern ObjPattern = Pattern.compile(
		"\\[obj\\](.*):(.*);(.*);(.*);(.*),(.*),(.*);(.*),(.*),(.*),(.*);(.*),(.*),(.*);(.*),(.*),(.*),(.*);(.*);(.*);");
		Pattern GroupPattern = Pattern.compile(
		"\\[group\\](.*):(.*):(.*)");
		
		try {
			input= new BufferedReader( new InputStreamReader(is));
			String line=null;
			while((line=input.readLine())!=null)
			{
				if(line.startsWith("[Object]"))
				{
					while((line = input.readLine())!=null)
					{
						if(line.startsWith("//"))
							continue;
						if(line.startsWith("[/Object]"))
							break;
						Matcher m = ObjPattern.matcher(line);
						while(m.find())
						{
							ObjInfo info=new ObjInfo(m.group(1).trim(),m.group(2).trim(),m.group(3).trim(),m.group(4).trim(),
									Float.parseFloat(m.group(5)),Float.parseFloat(m.group(6)),
									Float.parseFloat(m.group(7)),Float.parseFloat(m.group(8)),
									Float.parseFloat(m.group(9)),Float.parseFloat(m.group(10)),
									Float.parseFloat(m.group(11)),Float.parseFloat(m.group(12)),
									Float.parseFloat(m.group(13)),Float.parseFloat(m.group(14)),
									Float.parseFloat(m.group(15)),Float.parseFloat(m.group(16)),
									Float.parseFloat(m.group(17)),Float.parseFloat(m.group(18)));
					    	String tmp=m.group(19);
				    		int mirr=Integer.parseInt(m.group(19).trim());
				    		int trans=Integer.parseInt(m.group(20).trim());
				    		info.setExtraInfo(mirr, trans);

					    	objinfolist.add(info);
						}
						
					}
				}
				else if(line.startsWith("[Group]"))
				{
					while((line = input.readLine())!=null)
					{
						if(line.startsWith("//"))
							continue;
						if(line.startsWith("[/Group]"))
							break;
						Matcher m = GroupPattern.matcher(line);
						while(m.find())
						{
							groupinfolist.add(new GroupInfo(m.group(1),m.group(2).split(" "),m.group(3).trim()));
						}
					}
				}

			}
		}catch(IOException e)
		{
			e.printStackTrace();
		}		
	}
	
	public ObjinfoMgr(InputStream is)
	{


		
	}
	
	public void parse()
	{
	
	}
	
	public ObjInfo getObjectInfo(String name)
	{
		int objectnumber=objinfolist.size();
		int i;
		for(i=0;i<objectnumber;i++)
		{
			if( name.compareTo(objinfolist.get(i).id)==0)
			{
				return objinfolist.get(i);
			}
		}
		return null;
	}
	
	public GroupInfo getGroupInfo(String name)
	{
		int objectnumber=groupinfolist.size();
		int i;
		for(i=0;i<objectnumber;i++)
		{
			if( name.compareTo(groupinfolist.get(i).id)==0)
			{
				return groupinfolist.get(i);
			}
		}
		return null;
	}
}
