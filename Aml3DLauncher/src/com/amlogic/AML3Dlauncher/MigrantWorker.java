package com.amlogic.AML3Dlauncher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.microedition.khronos.opengles.GL11;

//import android.app.ActivityThread.PackageInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.os.SystemProperties;
import android.util.Log;

import com.amlogic.AML3Dlauncher.AnimationMgr.mAnimation;
import com.amlogic.AML3Dlauncher.ObjinfoMgr.GroupInfo;
import com.amlogic.AML3Dlauncher.ObjinfoMgr.ObjInfo;
import com.amlogic.a3d.anim.A3DAnimation;
import com.amlogic.a3d.anim.A3DAnimator;
import com.amlogic.a3d.anim.A3DTransformAnimation;
import com.amlogic.a3d.scene.A3DEye;
import com.amlogic.a3d.scene.A3DLight;
import com.amlogic.a3d.scene.A3DObject;
import com.amlogic.a3d.scene.A3DObjectGroup;
import com.amlogic.a3d.scene.A3DWorld;
import com.amlogic.a3d.scene.Texture;
import com.amlogic.a3d.scene.TextureManager;
import com.amlogic.a3d.util.Cube;
import com.amlogic.a3d.util.Line3D;
import com.amlogic.a3d.util.Model;
import com.amlogic.a3d.util.ObjLoader;
import com.amlogic.a3d.util.Point3D;

class MigrantWorker
{
	Context context;
	A3DWorld world;
	String[] inifile={"world.ini","animation.ini","objlist.ini","path.ini"};
	String objsavepath;
	int lastfocus=-1;
	int focus=0;
	int pos_offset=0;
	boolean reverse=false;
	boolean subreverse=false;
//	Resource amlres;
	private ArrayList<String> focusSenquence;
	private ArrayList<AnimationInfo> focusAniInfoList;
	private String backgroudpath=""; 
	private ArrayList<String> touchObjList;
	private ArrayList<String> slipgroupList;
	private ArrayList<String> slipanimList;
	private ArrayList<String> intentlist=null;
	private SubMenuInfo subMenu=null;
	private int subIndex=1;
	private String InfoLabel=null;
	private String[] InfoLabelAnamation=null;
	
	public class SubMenuInfo
	{
		public ArrayList<String> subobjectnamelist=null;
		public ArrayList<Integer> numberList=null;
		public ArrayList<String> animationList=null;
		public ArrayList<String> subslip=null;

		public SubMenuInfo()
		{
			subobjectnamelist=new ArrayList<String>();
			numberList=new ArrayList<Integer>();
			animationList=new ArrayList<String>();
			subslip=new ArrayList<String>();
		}
		public void addObjects(String[]  stl)
		{
			for (int i=0;i<stl.length;i++)
			{
				subobjectnamelist.add(stl[i].trim());
			}
		}
		
		public void addAnimationList(String[]  stl)
		{
			for (int i=0;i<stl.length;i++)
			{
				animationList.add(stl[i].trim());
			}
		}
		
		public void addNumberList(String[] stl)
		{
			for (int i=0;i<stl.length;i++)
			{
				numberList.add(Integer.parseInt(stl[i]));
			}
		}
		
		public void addSlipList(String stl)
		{
				subslip.add(stl.trim());
		}
	}

	
//	public class SubMenuInfo
//	{
//		private int totalInCurrentGroup=0;
//		//contain how many subobject
//		private ArrayList<String> subobjectnamelist=null;
//		private int totalGroup=0;
//		//every index ,point to a string,so the number of subobjectnamelist is maxIndex.
//		private ArrayList<String> textursPool =null;
//		private int currentIndex=0;
//		private String[] currentTextureList;
//		
// 
//		public SubMenuInfo()
//		{
//			subobjectnamelist=new ArrayList<String>();
//			textursPool=new ArrayList<String>();
//		}
//		
//		public void addObjects(String[]  stl)
//		{
//			for (int i=0;i<stl.length;i++)
//			{
//				subobjectnamelist.add(stl[i]);
//			}
//		}
//		
//		
//		public void addTextureItem(String st)
//		{
//			textursPool.add(st);
//		}
//		
//		public void setCurrentGroup(int index1)
//		{
//			if(index1<totalGroup)
//			{	
//				currentIndex=index1;
//				String st=textursPool.get(index1);
//				String[] sttmp=st.trim().split(":");
//				totalInCurrentGroup= Integer.parseInt(sttmp[0]);
//				currentTextureList=sttmp[1].trim().split(";");
//				for(int i=0;i<currentTextureList.length;i++)
//				{
//					if(TextureManager.global.getTexture(currentTextureList[i])==null)
//					{
//						//color texture
//						InputStream tmpst =ResourceMgr.instance.getInputStream(context,currentTextureList[i]);  
//						if(tmpst!=null)
//						{
//							TextureManager.global.addTexture(new Texture(currentTextureList[i], BitmapFactory.decodeStream(tmpst)));
//						}
//					}
//				}
//			}
//		}
//		public String getTextureName(int index2 )
//		{
//			if(index2<totalInCurrentGroup)
//			{
//				return currentTextureList[index2];
//			}
//			else
//			{
//				return "empty.png";//return a touming picture;
//			}
//		}
//	}

	public String getBackground()
	{
		return backgroudpath;
	}

	private class AnimationInfo
	{
		String id;
		String[] info;
		public AnimationInfo(String name,String st)
		{
			id=name;
			info=st.trim().split(",");
		}
	}

	public MigrantWorker(Context ctx,A3DWorld city)
	{
		world=city;
		context=ctx;
		focusSenquence=new ArrayList<String>();
		focusAniInfoList=new ArrayList<AnimationInfo>();
		touchObjList=new ArrayList<String>();
		slipanimList=new ArrayList<String>();
		slipgroupList=new ArrayList<String>();
		subMenu=new SubMenuInfo();
		objsavepath=ResourceMgr.instance.getA3DPath();
		AnimationMgr.instance.clearAnimation();
		A3DAnimator.getInstance().clearAnimation();
//		amlres = new Resource();
		initMaterials();
		changetoWork(focus);
//		world.addObject(creatObject("TV"));
//		world.addGroup(creatGroup("group3"));
	}
	
	public void initMaterials()
	{
        TextureManager.global.removeAll();
		InputStream is = ResourceMgr.instance.getInputStream(context,inifile[2]);
		if(is!=null)
			ObjinfoMgr.instance.parse(is);
        is =ResourceMgr.instance.getInputStream(context,inifile[3]); 
        if(is!=null)
        	PathMgr.instance.parse(is);
        is =ResourceMgr.instance.getInputStream(context,inifile[1]);  
        if(is!=null)
        	AnimationMgr.instance.parse(context,is);
        is =ResourceMgr.instance.getInputStream(context,inifile[0]);   
        if(is!=null)
        	parseWordFile(is);
	}

	public void parseWordFile(InputStream st)
	{
		BufferedReader input = null;
		try {
			input= new BufferedReader( new InputStreamReader(st));
			String line=null;
			while((line = input.readLine())!=null) 
			{
				if(line.startsWith("[Light]"))
				{
					Pattern LightPattern = Pattern.compile(
							"(.*):(.*);(.*),(.*),(.*),(.*);(.*),(.*),(.*),(.*);(.*),(.*),(.*),(.*);(.*)");
					while((line = input.readLine())!=null)
					{
						if(line.startsWith("//"))
							continue;
						if(line.startsWith("[/Light]"))
							break;
						Matcher m = LightPattern.matcher(line);
						while(m.find())
						{
							String[] stl= m.group(2).trim().split(",");
							float pos[]=null;
							if(stl.length==3)
							{
								pos= new float[]{Float.parseFloat(stl[0]),Float.parseFloat(stl[1]),
									Float.parseFloat(stl[2]),0};
							}else
							{
								pos= new float[]{Float.parseFloat(stl[0]),Float.parseFloat(stl[1]),
										Float.parseFloat(stl[2]),Float.parseFloat(stl[3])};
							}
							float ambient[]= new float[]{Float.parseFloat(m.group(3)),Float.parseFloat(m.group(4)),
									Float.parseFloat(m.group(5)),Float.parseFloat(m.group(6))};
							float diffuse[]= new float[]{Float.parseFloat(m.group(7)),Float.parseFloat(m.group(8)),
									Float.parseFloat(m.group(9)),Float.parseFloat(m.group(10))};

							float specular[]= new float[]{Float.parseFloat(m.group(11)),Float.parseFloat(m.group(12)),
									Float.parseFloat(m.group(13)),Float.parseFloat(m.group(14))};
							if(pos[3]==0)
							{
								world.addLight(new A3DLight(m.group(1),A3DLight.Type.Directional,ambient,diffuse,specular,pos,null));
							}else
							{
								String st1=m.group(15).trim();
								if(st1.contains("{")&&st1.contains("}"))
								{
									st1=st1.substring(st1.indexOf("{")+1,st1.indexOf("}"));
								}
								String[] dirl=st1.split(",");
								if(dirl.length<3)
								{
									world.addLight(new A3DLight(m.group(1),A3DLight.Type.Point,ambient,diffuse,specular,pos,null));
								}else
								{
									float[] direction = new float[]{Float.parseFloat(dirl[0]),Float.parseFloat(dirl[1]),
											Float.parseFloat(dirl[2])};
									world.addLight(new A3DLight(m.group(1),A3DLight.Type.Spot,ambient,diffuse,specular,pos,direction));
									//when spot light, you may need some else parmeter to set up; add it down here.
								}
								
							}
								
										
						}
					
					}
				}
				else if(line.startsWith("[Eye]"))
				{
					Pattern EyePattern = Pattern.compile(
					"(.*):(.*),(.*),(.*);(.*),(.*),(.*);(.*),(.*),(.*);(.*);");
			
					while((line = input.readLine())!=null)
					{
						if(line.startsWith("//"))
							continue;
						if(line.startsWith("[/Eye]"))
							break;
						Matcher m = EyePattern.matcher(line);
						while(m.find())
						{
							float[] eyePosition = new float[3];
							float[] viewcenter= new float[3];
							float[] upDirection= new float[3];
							for(int i=0;i<3;i++)
							{
								eyePosition[i]= Float.parseFloat(m.group(i+2));
							}
							for(int i=3;i<6;i++)
							{
								viewcenter[i-3]= Float.parseFloat(m.group(i+2));
							}
							for(int i=6;i<9;i++)
							{
								upDirection[i-6]= Float.parseFloat(m.group(i+2));
							}
							A3DEye eye=new A3DEye(eyePosition,viewcenter,
									upDirection,Float.parseFloat(m.group(11)));
							world.setEye(eye);

						}
					}					
				}
				else if(line.startsWith("[Objects]"))
				{
					while((line = input.readLine())!=null)
					{
						if(line.startsWith("//"))
							continue;
						if(line.startsWith("[/Objects]"))
						{
							break;	
						}
						String stp=line.trim();
						if(ObjinfoMgr.instance.getObjectInfo(stp)!=null )
						{//need modify,if null
							A3DObject objtmp=creatObject(stp);
							if(objtmp!=null)
							{
								if(objtmp.getTransFlag())
									world.addTransObject(objtmp);
								else
									world.addObject(objtmp);

							}
						}else if(ObjinfoMgr.instance.getGroupInfo(stp)!=null )
						{
							world.addGroup(creatGroup(stp));
						}
					}
					
				}
				else if(line.startsWith("[Animation Normal]"))
				{
					while((line = input.readLine())!=null)
					{
						if(line.startsWith("//"))
							continue;
						if(line.startsWith("[/Animation Normal]"))
						{	
							break;	
						}
						Pattern AniPattern = Pattern.compile(
										"(.*):(.*)");
						Matcher m = AniPattern.matcher(line);
						while(m.find())
						{
							String sttmp=m.group(2).trim();
							String[] stlist=sttmp.split(",");
							for(int i=0;i<stlist.length;i++)
							{
								makeitlive(m.group(1).trim(),stlist[i],0);
							}
						
						}
						
					}					
				}
				else if(line.startsWith("[Animation Focus]"))
				{
					while((line = input.readLine())!=null)
					{
//						Log.v("","readLine "+line);

						if(line.startsWith("//"))
							continue;
						if(line.startsWith("[/Animation Focus]"))
							break;	
						Pattern AniPattern = Pattern.compile(
										"(.*):(.*)");
						Matcher m = AniPattern.matcher(line);
						while(m.find())
						{
//							Log.v("",""+m.group(1)+" "+m.group(2));
							focusAniInfoList.add(new AnimationInfo(m.group(1).trim(),m.group(2).trim()));
						}
					}
				}
				else if(line.startsWith("[Focus seqence]"))
				{
					while((line = input.readLine())!=null)
					{
						if(line.startsWith("//")||line.equals(""))
							continue;
						if(line.startsWith("[/Focus seqence]"))
							break;	
						focusSenquence.add(line.trim());
					}				
				}
				else if(line.startsWith("[Other]"))
				{
					while((line = input.readLine())!=null)
					{
						if(line.startsWith("//"))
							continue;
						if(line.startsWith("[/Other]"))
							break;	
						if(line.startsWith("InitFocus:"))
						{
							String tmp=line.substring(10);
							focus=Integer.parseInt(tmp.trim());
						}
						else if(line.startsWith("Background:"))
						{
							backgroudpath=line.substring(11).trim();
						}
						else if(line.startsWith("SubMenuList:"))
						{
							subMenu.addObjects(line.substring(12).trim().split(","));
						}
						else if(line.startsWith("SubMenuNumberList:"))
						{
							subMenu.addNumberList(line.substring(18).trim().split(","));
						}
						else if(line.startsWith("SubMenuChangeText:"))
						{
							subMenu.addAnimationList(line.substring(18).trim().split(","));
						}
						else if(line.startsWith("Obj0BindFocus:"))
						{
							InfoLabel=line.substring(14).trim();
						}
						else if(line.startsWith("Obj0ChangeText:"))
						{
							InfoLabelAnamation=line.substring(15).trim().split(",");
						}
					}
				}
				else if(line.startsWith("[TouchObject]"))
				{
					while((line = input.readLine())!=null)
					{
						if(line.startsWith("//")||line.equals(""))
							continue;
						if(line.startsWith("[/TouchObject]"))
							break;	
						touchObjList.add(line.trim());
					}
				}
				else if(line.startsWith("[LoopGroup]"))
				{
					while((line = input.readLine())!=null)
					{
						if(line.startsWith("//")||line.equals(""))
							continue;
						if(line.startsWith("[/LoopGroup]"))
							break;	
						slipgroupList.add(line.trim());
					}
				}
				else if(line.startsWith("[SlipAnimList]"))
				{
					while((line = input.readLine())!=null)
					{
						if(line.startsWith("//")||line.equals(""))
							continue;
						if(line.startsWith("[/SlipAnimList]"))
							break;	
						slipanimList.add(line.trim());
					}
				}
				else if(line.startsWith("[SubMenuSlipAnimList]"))
				{
					while((line = input.readLine())!=null)
					{
						if(line.startsWith("//")||line.equals(""))
							continue;
						if(line.startsWith("[/SubMenuSlipAnimList]"))
							break;	
						subMenu.addSlipList(line.trim());

					}
				}
				else if(line.startsWith("[IntentList]"))
				{
					intentlist= new ArrayList<String>();
					while((line = input.readLine())!=null)
					{
						if(line.startsWith("//")||line.equals(""))
							continue;
						if(line.startsWith("[/IntentList]"))
							break;
						String[] tmp=line.split(";");
						for(int k=0;k<5;k++)
						{
							if(k<tmp.length)
								intentlist.add(tmp[k]);
							else
								intentlist.add("");
						}
					}	
				}
			}
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public A3DObject creatObject(String st)
	{
		ObjInfo info=ObjinfoMgr.instance.getObjectInfo(st);
		String objName=info.id;
		if(info==null)
			return null;
	  	A3DObject obj;
    	Model m;
    	ObjLoader objloader = new ObjLoader();
    	
    	try{
    		File rootDirFile = new File(objsavepath);
    		if(!rootDirFile.exists()) 
    			rootDirFile.mkdir();
    		Log.v("",""+objsavepath+"/"+objName+".a3d");
	        obj = new A3DObject(objName, objsavepath+"/"+objName+".a3d", null, null, false);
	        if (obj.getFrameCount()==0){
	        	InputStream is=ResourceMgr.instance.getInputStream(context,info.colorTexture);  
	        	if(is!=null)
	        		TextureManager.global.addTexture(new Texture(info.colorTexture, BitmapFactory.decodeStream(is)));

	        	is=ResourceMgr.instance.getInputStream(context,info.objname);  
	        	if(is!=null)
	            {
		            m = objloader.load(is, info.colorTexture, null);
		            obj = new A3DObject(objName, m, false);
		            obj.save(objsavepath+"/"+objName+ ".a3d");	            	
	            }
	        }
	        if(obj!=null)
	        {
	        	obj.enableMirror(info.needMirror>0?true:false);
        		obj.enableTransparent(info.needTrans>0?true:false);
			    obj.setPosition(info.x,info.y,info.z);
			    obj.setRotation(info.rotate, info.rx, info.ry, info.rz);
			    obj.setColor(info.alphaA,info.alphaG,info.alphaB,info.alphaA);	        	
	        }
		    return obj;
	    }
    	catch(java.io.IOException ex){
    		ex.printStackTrace();
    	}
		return null;
    }
	
	
	
	//only creat the group and add its all object into the group
	public A3DObjectGroup creatGroup(String st)
	{
		GroupInfo ginfo=ObjinfoMgr.instance.getGroupInfo(st);
		A3DObjectGroup group=new  A3DObjectGroup(ginfo.id);
		String[] info=ginfo.content.clone();
		String[] stl=ginfo.extraInfo.split("[;,]");
		Log.v("","Group stl"+stl.length);
		if(stl.length==10)
		{
			group.setPosition(Float.parseFloat(stl[0]), Float.parseFloat(stl[1]), Float.parseFloat(stl[2]));
			group.setRotation(Float.parseFloat(stl[3]), Float.parseFloat(stl[4]), Float.parseFloat(stl[5]),Float.parseFloat(stl[6]));
			group.setScale(Float.parseFloat(stl[7]),Float.parseFloat(stl[8]),Float.parseFloat(stl[9]));
		}

		int i=0;
		for(i=0;i<ginfo.content.length;i++)
		{
			if(ObjinfoMgr.instance.getGroupInfo(info[i])!=null)
			{
				group.addGroup(creatGroup(info[i]));
			}
			else if(ObjinfoMgr.instance.getObjectInfo(info[i])!=null)
			{
				A3DObject objtmp=creatObject(info[i]);
				if(objtmp!=null)
					group.addObject(objtmp);
			}
		}
		return group;
	}
	
	public void makeitlive(String name,String animname,long delaytime)
	{
		A3DObject obj;
		A3DAnimation anim= AnimationMgr.instance.getAnimation(animname);
		if(anim==null)
			return ;
		
		if(name.startsWith("eye"))
		{
			anim.setNode(world.getEyeObj());
			anim.start(delaytime);
			return;
		}
		
		A3DObjectGroup grp=world.getGroup(name);

		if(grp!=null)
		{
			anim.setNode(grp);
			anim.start();
		}else 
		{
			obj=world.getObject(name);
//			float t[]=obj.getCubeGeometry();
//			Log.v("",name+":"+t[0]+" "+t[1]+" "+t[2]+" "+t[3]+" "+t[4]+" "+t[5] );
			if(obj!=null)
			{
				anim.setNode(obj);
				anim.start(delaytime);
			}
		}
	}
	
	public void doNextWork()
	{
		reverse=false;
		pos_offset++;
		if(focus<focusSenquence.size()-1)
			changetoWork(focus+1);
		else 
			changetoWork(0);
	}	
	public void doPrevWork()
	{
		reverse=true;
		pos_offset--;
		if(focus>0)
			changetoWork(focus-1);
		else 
			changetoWork(focusSenquence.size()-1);
	}
	
	public void nextSubItem()
	{
		int t=subMenu.numberList.get(focus);
		if(t>0 )
		{
			if(subIndex<t-1)
			{
				subreverse=true;
				subIndex++;
				changeSubWork();				
			}else if(subIndex==t-1)
			{
				subreverse=true;
				subIndex=0;
				changeSubWork();			
			}
		}
		
		
	}
	public void  prevSubItem()
	{
		int t=subMenu.numberList.get(focus);
		if(t>0 )
		{
			if(subIndex>0)
			{
				subreverse=false;
				subIndex--;
				changeSubWork();				
			}else if(subIndex==0)
			{
				subreverse=false;
				subIndex=t-1;
				changeSubWork();				
			}
		}	
	}
	
	private String[] getAnimationInfo(String name)
	{
		int number=focusAniInfoList.size();
		for(int i=0;i<number;i++)
		{
			if( name.compareTo(focusAniInfoList.get(i).id)==0)
			{
				return focusAniInfoList.get(i).info;
			}
		}
		return null;
	}
	private String getObjectInfoFromSenquence(int i)
	{
		String stl=focusSenquence.get(i);
		if(stl.contains(":"))
			return stl.substring(0, stl.indexOf(":")).trim();
		else return stl;
	}
	
	private String getIntentInfoFromSenquence(int i,int subindex)
	{
		if(intentlist!=null)
		{
			String st=intentlist.get(i*5+subindex).trim();
			if(st.contains("{")&&st.contains("}"))
			{
				return st.substring(st.indexOf("{")+1, st.indexOf("}")).trim();
			}else
			{
				return null;
			}
		}
		
		String stl=focusSenquence.get(i);
		if(stl.contains(":"))
		{
			String stl2=stl.substring( stl.indexOf(":")+1 ).trim();
			if(stl2.contains("{")&&stl2.contains("}"))
			{
				return stl2.substring(stl2.indexOf("{")+1, stl2.indexOf("}")).trim();
			}else
			{
				return null;
			}
			
		}
		else
		{
			return null;
		}
	}	
	
	private void stopInfoAnimation()
	{
		if(InfoLabelAnamation==null)
			return;
		for(int i=0;i<InfoLabelAnamation.length;i++)
		{

			A3DTransformAnimation anim= (A3DTransformAnimation)AnimationMgr.instance.getAnimation(InfoLabelAnamation[0]);
			if(anim.getNode()!=null)
				anim.stop();
		}
	}
	
	private void initInfoLable()
	{
		if(InfoLabelAnamation==null)
			return;
		for(int i=0;i<InfoLabelAnamation.length;i++)
		{
			if(i==0)
			{
				A3DTransformAnimation anim= (A3DTransformAnimation)AnimationMgr.instance.getAnimation(InfoLabelAnamation[0]);
				anim.stepToTexture(focus);
				makeitlive( InfoLabel,InfoLabelAnamation[i],0);
			}
			else
			{
				makeitlive( InfoLabel,InfoLabelAnamation[i],0);
			}
		}
	}
	
	
	private void changeSubWork()
	{
		Log.v("","changeSubWork----------");

		int count=subMenu.subslip.size();
		for(int p=0;p<count;p++)
		{
			A3DAnimation anim= AnimationMgr.instance.getAnimation(subMenu.subslip.get(p));
			if(anim!=null)
			{
				anim.reverse(subreverse);
				if(anim.getState()==anim.STATE_RUNNING)
					anim.stop();
			}
		}
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int menusize=subMenu.numberList.size();
		if(menusize==0)
			return ;
		int total_tmp=subMenu.numberList.get(focus);
		int size=subMenu.subobjectnamelist.size();

		if(subreverse)
		{
			int currenttotal=subMenu.numberList.get(focus);
//			for(int i=0;i<subMenu.subobjectnamelist.size();i++)
			for(int i=0;i<currenttotal;i++)
			{
				if(currenttotal==size)
				{
					if(subIndex==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(1),0);
					}else if((subIndex+1)%currenttotal==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(2),0);
					}else if((subIndex+2)%currenttotal==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(3),0);
					}else if((subIndex+3)%currenttotal==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(4),0);
					}else if((subIndex+4)%currenttotal==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(0),0);
					}
				}
				else if(currenttotal==size-1)
				{
					if(subIndex==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(1),0);
					}else if((subIndex+1)%currenttotal==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(2),0);
					}else if((subIndex+2)%currenttotal==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(3),0);
					}else if((subIndex+3)%currenttotal==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(size),0);
					}
					
				}
				else if(currenttotal==size-2)
				{
					if(subIndex==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(1),0);
					}else if((subIndex+1)%currenttotal==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(2),0);
					}else if((subIndex+2)%currenttotal==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(size+1),0);
					}
				}
				else if(currenttotal==size-3)
				{
					if(subIndex==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(1),0);
					}else if((subIndex+1)%currenttotal==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(size+2),0);
					}
				}

			}
		}else  //subreverse==false,prev sub item;subIndex-- ,up move
		{
			int currenttotal=subMenu.numberList.get(focus);
//			for(int i=0;i<subMenu.subobjectnamelist.size();i++)
			for(int i=0;i<currenttotal;i++)
			{
				if(currenttotal==size)
				{
					if(subIndex==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(0),0);
					}else if((subIndex+1)%currenttotal==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(1),0);
					}else if((subIndex+2)%currenttotal==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(2),0);
					}else if((subIndex+3)%currenttotal==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(3),0);
					}else if((subIndex+4)%currenttotal==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(4),0);
					}					
				}
				else if(currenttotal==size-1)
				{
					if(subIndex==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(size),0);
					}else if((subIndex+1)%currenttotal==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(1),0);
					}else if((subIndex+2)%currenttotal==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(2),0);
					}else if((subIndex+3)%currenttotal==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(3),0);
					}			
				}
				else if(currenttotal==size-2)
				{
					if(subIndex==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(size+1),0);
					}else if((subIndex+1)%currenttotal==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(1),0);
					}else if((subIndex+2)%currenttotal==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(2),0);
					}
				}				
				else if(currenttotal==size-3)
				{
					if(subIndex==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(size+2),0);
					}else if((subIndex+1)%currenttotal==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(1),0);
					}else if((subIndex+2)%currenttotal==i)
					{
						makeitlive(subMenu.subobjectnamelist.get(i),subMenu.subslip.get(2),0);
					}				}	
			}
	
		}
	}

	
	private void changetoWork(int i)
	{
		Pattern pattern = Pattern.compile(
			"(.*)\\{(.*)\\}");	
		if(i==lastfocus)
			return ;
		for(int k=0;k<subMenu.animationList.size();k++)
		{
			String st=subMenu.animationList.get(k);
			A3DTransformAnimation anim= (A3DTransformAnimation)AnimationMgr.instance.getAnimation(st);
			if(anim!=null)
			{
				anim.stop();
			}
		}
		if(lastfocus>=0&&lastfocus<focusSenquence.size())
		{
			String[] stl=getObjectInfoFromSenquence(lastfocus).split(",");

			for(int j=0;j<stl.length;j++)
			{
				String[] anml;
				Matcher m = pattern.matcher(stl[j]);
				if(m.find())
				{
					anml=m.group(2).trim().split("[,;]");
				}else
				{
					anml=getAnimationInfo(stl[j]);
				}
				if(anml!=null)
				{
					for(int k=0;k<anml.length;k++)
					{
						String tmp=anml[k];
						if(tmp.contains("("))
							tmp=tmp.substring(0,tmp.indexOf("("));
						Log.v("kill",""+tmp);
						A3DAnimation anim= AnimationMgr.instance.getAnimation(tmp);
						if(anim!=null)
						{
							anim.stop();
							if(stl[j].startsWith("eye")!=true)
							{
								anim.resumeToStart();					
							}
						}

					}					
				}
			}
			int count=slipgroupList.size();
			for(int p=0;p<count;p++)
			{
				String[] stltmp=slipanimList.get(p).split(",");
				for(int k=0;k<stltmp.length;k++)
				{
					A3DAnimation anim= AnimationMgr.instance.getAnimation(stltmp[k]);
					if(anim!=null)
					{
						anim.stop();
					}
				}
			}
		}
		stopInfoAnimation();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		lastfocus=i;
		focus=i;
		if(i>=0&&i<focusSenquence.size())
		{
			String[] stl=getObjectInfoFromSenquence(focus).split(",");
			for(int j=0;j<stl.length;j++)
			{
				String[] anml;
				Matcher m = pattern.matcher(stl[j]);
				if(m.find())
				{
					anml=m.group(2).trim().split("[,;]");
				}else
				{
					anml=getAnimationInfo(stl[j]);
				}
				if(anml!=null)
				{
					for(int k=0;k<anml.length;k++)
					{
						Log.v("start",""+stl[j]+"  "+anml[k]);
						String sttmp=anml[k];
						long delaytime=0;
						if(anml[k].contains("(")&&anml[k].contains(")"))
						{
							sttmp=anml[k].substring(0,anml[k].indexOf("(") );
							delaytime=Long.parseLong(anml[k].substring(anml[k].indexOf("(")+1,anml[k].indexOf("ms") ).trim());
						}
						Log.v("",""+sttmp+delaytime);
						makeitlive(stl[j],sttmp,delaytime);		
					}					
				}

			}
			int count=slipgroupList.size();
			for(int p=0;p<count;p++)
			{
				int tmp=(p+focus)%(count);
				if(reverse)
				{
					tmp=(p+focus+1)%count;
				}
//				Log.v("",""+focus);
//				Log.v("","slip::"+slipgroupList.get(p)+":"+slipanimList.get(tmp));
				
				
				
				String[] stltmp=slipanimList.get(tmp).split(",");
				for(int k=0;k<stltmp.length;k++)
				{
					int t=k;
					if(reverse==true)
					{
						t=stltmp.length-1-k;
					}

					A3DAnimation anim= AnimationMgr.instance.getAnimation(stltmp[t]);
					anim.reverse(reverse);
					makeitlive(slipgroupList.get(p),stltmp[t],0);
				}
			}
			for(int k=0;k<subMenu.animationList.size();k++)
			{
				String st=subMenu.animationList.get(k);
				Log.v("",""+st);
				A3DTransformAnimation anim= (A3DTransformAnimation)AnimationMgr.instance.getAnimation(st);
				anim.stepToTexture(focus);
				anim.start();
			}
			while(subIndex>0)
			{
				subIndex--;
				subreverse=false;
				changeSubWork();
			}
			initInfoLable();
		}
		
	}
	
	private class TouchInfo
	{
		String name;
		Point3D point;
		int index;
		public TouchInfo(String st,Point3D p,int i){
			name=st;
			point=p;
			index=i;
			
		}
	}
	
	public TouchInfo isAnyObjectTouched(Line3D line)
	{
		TouchInfo touchinfo=null;
		for(int i=0;i<touchObjList.size();i++)
		{
			String st=touchObjList.get(i);
			String[] stl = null;
			int index=-1;
			if(st.contains(":"))
			{
				stl=st.substring(0, st.indexOf(":")).trim().split(",");
				index=Integer.parseInt(st.substring(st.indexOf(":")+1).trim());
			}else
			{
				stl=st.trim().split(",");
			}
			for(int j=0;j<stl.length;j++)
			{
				Point3D point=getTouchPoint(line,stl[j].trim());
				if(point!=null)
				{
					if(touchinfo==null)
					{
						touchinfo=new TouchInfo(stl[j].trim(),point,index);
					}
					else if(point.getZ()>touchinfo.point.getZ())
					{
						touchinfo=new TouchInfo(stl[j].trim(),point,index);
					}					
				}
			}
		}
		
		if(touchinfo!=null)
		{
			Log.v("Touch object:",""+touchinfo.name);
			if(touchinfo.index>=0)
			{
				if(touchinfo.index==lastfocus)
				{
					select();
				}else
				{
					changetoWork(touchinfo.index);
				}
			}
		}
		return touchinfo;
	}
	
	
	private Point3D getTouchPoint(Line3D line,String objname)
	{
		A3DObject obj=world.getObject(objname);
		if(obj==null)
			return null;
		float[] pointarray=obj.getCubeGeometry();
		Cube cube= new Cube(pointarray);
		Point3D result=cube.getCrossPointWithLine(line);
		Log.v("",objname+" :"+result);
		return result;
		
	}
	
	public void select()
	{
	
		String st=getIntentInfoFromSenquence(focus,subIndex);
		String[] sl = null;
		if(st!=null)
		{
			try
			{
				sl=st.split(",");
				Intent intent = new Intent();
				intent.setClassName(sl[0].trim(), sl[1].trim());
				context.startActivity(intent);				
			}catch(android.content.ActivityNotFoundException ex){
	    		ex.printStackTrace();
	    	}
		}
	}
}