package com.amlogic.AML3Dlauncher;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;



import android.content.Context;
import android.os.SystemProperties;

public class ResourceMgr
{
	public static ResourceMgr instance = new ResourceMgr();

	private String resourcePath;
	private String a3dPath;
	private ResourceMgr()
	{
//		resourcePath=SystemProperties.get("AML3DRESPATH", "/sdcard/amlres");
		a3dPath=SystemProperties.get("A3DPATH", "/sdcard/a3d");
		resourcePath=SystemProperties.get("AML3DRESPATH", "default");
//		a3dPath=SystemProperties.get("A3DPATH", "/data/a3d");
	}
	
	public String getA3DPath()
	{
		return a3dPath;
	}

	public InputStream getInputStream(Context ctx,String name) 
	{
		
		InputStream is;
		if (resourcePath.compareTo("default")==0)
		{
			String namemodify=name;
			int index=name.indexOf(".");
			if(index!=-1)
			{
				namemodify=name.substring(0, name.indexOf("."));
			}
	        int resID = ctx.getResources().getIdentifier(namemodify, "drawable", ctx.getPackageName());
	        if(resID!=0)
	        {
	    		is = ctx.getResources().openRawResource(resID);
	    		return is;    	
	        }

		}
		else
		{
			try {
				is =new FileInputStream(resourcePath+"/"+name);
				return is;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
		
	}
	


}

