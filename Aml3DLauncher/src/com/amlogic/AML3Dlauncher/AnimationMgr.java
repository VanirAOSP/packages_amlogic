package com.amlogic.AML3Dlauncher;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.Color;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;

import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.amlogic.AML3Dlauncher.ObjinfoMgr.GroupInfo;
import com.amlogic.AML3Dlauncher.ObjinfoMgr.ObjInfo;
import com.amlogic.a3d.anim.A3DAnimation;
import com.amlogic.a3d.anim.A3DEyeAnimation;
import com.amlogic.a3d.anim.A3DTransformAnimation;
import com.amlogic.a3d.scene.A3DEye;
import com.amlogic.a3d.scene.A3DNode;
import com.amlogic.a3d.scene.Texture;
import com.amlogic.a3d.scene.TextureManager;


public class AnimationMgr
{
	public static AnimationMgr instance = new AnimationMgr();
	TextPaint mTextPaint =null;
	private AnimationMgr()
	{
		animlist = new ArrayList<mAnimation>();
		
		mTextPaint  =  new TextPaint();
	    mTextPaint.setTypeface(Typeface.DEFAULT);
	   // mTextPaint.setTextSize(25);
		mTextPaint.setTextScaleX(1.75f);
  //	mTextPaint.setColor(0xffffffff);
	    mTextPaint.setColor(Color.WHITE);
       // mTextPaint.setShadowLayer(8, 0, 0, 0xff000000);
      //mTextPaint.setAlpha(127);
	    mTextPaint.setAntiAlias(true);

    
	}
	private ArrayList<mAnimation> animlist;
	public class mAnimation
	{
		String id;
		A3DAnimation anim;
		public mAnimation(String s,A3DAnimation a)
		{
			id=s;
			anim=a;
		}
	}
	
	Bitmap b;
	private Bitmap createTextBitmap(String text,int width,int height)
	{
		int mBitmapWidth=width;
		int mBitmapHeight=height;
		mTextPaint.setTextSize(height*1/4);
        b = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight, Bitmap.Config.ARGB_8888 );
//        b.setDensity(240);
        Canvas c = new Canvas(b);
        int x = (int)((mBitmapWidth - mTextPaint.measureText(text)) * 0.5f);
        int y = height*3/5;
        c.drawText(text, x, y, mTextPaint);
        return b;
	}
	
	
	int titleId[] ={R.string.title0,R.string.title1,R.string.title2,R.string.title3,R.string.title4,
			R.string.title5,R.string.title6,R.string.title7,R.string.title8};

	String titleTexureID[] ={"title0_tex","title1_tex","title2_tex","title3_tex","title4_tex",
			"title5_tex","title6_tex","title7_tex","title8_tex"};
	
	int menuId[][]={
			{R.string.menu0_0,R.string.menu0_1,R.string.menu0_2,R.string.menu0_3,R.string.menu0_4},
			{R.string.menu1_0,R.string.menu1_1,R.string.menu1_2,R.string.menu1_3,R.string.menu1_4},
			{R.string.menu2_0,R.string.menu2_1,R.string.menu2_2,R.string.menu2_3,R.string.menu2_4},
			{R.string.menu3_0,R.string.menu3_1,R.string.menu3_2,R.string.menu3_3,R.string.menu3_4},
			{R.string.menu4_0,R.string.menu4_1,R.string.menu4_2,R.string.menu4_3,R.string.menu4_4},
			{R.string.menu5_0,R.string.menu5_1,R.string.menu5_2,R.string.menu5_3,R.string.menu5_4},
			{R.string.menu6_0,R.string.menu6_1,R.string.menu6_2,R.string.menu6_3,R.string.menu6_4},
			{R.string.menu7_0,R.string.menu7_1,R.string.menu7_2,R.string.menu7_3,R.string.menu7_4},
			{R.string.menu8_0,R.string.menu8_1,R.string.menu8_2,R.string.menu8_3,R.string.menu8_4},
	};
	

	
	private void getTitleTex(Context ctx)
	{
		for(int i=0;i<9;i++)
		{
			if(TextureManager.global.getTexture(titleTexureID[i])==null)
			{
				Bitmap bitmap=createTextBitmap(ctx.getString(titleId[i]),840,128);
				TextureManager.global.addTexture(new Texture(titleTexureID[i], bitmap));
				bitmap.recycle();
			}
		}
	}
	private void getTextByString(Context ctx,String st)
	{
		if(st.startsWith("title")&&st.endsWith("_tex"))
		{
			int i=Integer.parseInt(st.substring(5, 6));
			getTitleTex(ctx,i);
		}else if(st.startsWith("menu")&&st.contains("_tex"))
		{

			int i=Integer.parseInt(st.substring(4, 5));
			int j=Integer.parseInt(st.substring(9, 10));
			Bitmap bitmap=createTextBitmap(ctx.getString(menuId[i][j]),420,64);
			TextureManager.global.addTexture(new Texture(st, bitmap));
			bitmap.recycle();

		}
		
	}	
	private void getTitleTex(Context ctx,int i)
	{
		if(TextureManager.global.getTexture(titleTexureID[i])==null)
		{
			Bitmap bitmap=createTextBitmap(ctx.getString(titleId[i]),840,128);
			TextureManager.global.addTexture(new Texture(titleTexureID[i], bitmap));
			bitmap.recycle();
		}
	}
	
	public void parse(Context ctx,InputStream is)
	{
	
		BufferedReader input = null;
		String n="\\"+System.getProperty("line.separator");
		Pattern EyePattern = Pattern.compile(
				"(.*):.*\\{(.*)\\};.*\\{(.*)\\};(.*)ms;(.*);(.*),(.*)");
		Pattern RotatePattern = Pattern.compile(
				"(.*):(.*),(.*),(.*),(.*),(.*);(.*)ms;(.*);(.*),(.*)");
		Pattern ScalePattern = Pattern.compile(
				"(.*):(.*),(.*),(.*),(.*),(.*),(.*);(.*)ms;(.*);(.*),(.*)");
		Pattern AlphaPattern = Pattern.compile(
				"(.*):(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*);(.*)ms;(.*);(.*),(.*)");	
		Pattern PathPattern = Pattern.compile("(.*):(.*)");
		Pattern TexturePattern = Pattern.compile(
				"(.*):(.*)ms;(.*);(.*),(.*);.*?\\{(.*)\\}");

		try {
			input= new BufferedReader( new InputStreamReader(is));
			String line=null;
			while((line=input.readLine())!=null)
			{
				if(line.startsWith("[Eye]"))
				{
					while((line = input.readLine())!=null)
					{
						if(line.startsWith("//"))
							continue;
						if(line.startsWith("[/Eye]"))
							break;
						Matcher m = EyePattern.matcher(line);
						while(m.find())
						{
							String[] eye0=m.group(2).split("[,;]");
							String[] eye1=m.group(3).split("[,;]");
							if(eye0.length!=eye1.length||eye1.length<10)
							{
								continue;
							}
							float[] eyePosition = {Float.parseFloat(eye0[0].trim()),Float.parseFloat(eye0[1].trim()),Float.parseFloat(eye0[2].trim())};
							float[] viewcenter= {Float.parseFloat(eye0[3].trim()),Float.parseFloat(eye0[4].trim()),Float.parseFloat(eye0[5].trim())};
							float[] upDirection= {Float.parseFloat(eye0[6].trim()),Float.parseFloat(eye0[7].trim()),Float.parseFloat(eye0[8].trim())};
							A3DEye e0=new A3DEye(eyePosition, viewcenter, upDirection,Float.parseFloat(eye0[9].trim()));
							float[] eyePosition1 = {Float.parseFloat(eye1[0].trim()),Float.parseFloat(eye1[1].trim()),Float.parseFloat(eye1[2].trim())};
							float[] viewcenter1 = {Float.parseFloat(eye1[3].trim()),Float.parseFloat(eye1[4].trim()),Float.parseFloat(eye1[5].trim())};
							float[] upDirection1= {Float.parseFloat(eye1[6].trim()),Float.parseFloat(eye1[7].trim()),Float.parseFloat(eye1[8].trim())};
							A3DEye e1=new A3DEye(eyePosition1, viewcenter1, upDirection1,Float.parseFloat(eye1[9].trim()));
							long  dur=Long.parseLong(m.group(4).trim());
							int loops=Integer.parseInt(m.group(5).trim());
							
							A3DEyeAnimation a= new A3DEyeAnimation(null, new LinearInterpolator(), dur, e0, e1, loops);
							a.setDelayTime(Long.parseLong(m.group(6).trim()), Long.parseLong(m.group(7).trim()));
							animlist.add(new mAnimation(m.group(1).trim(),a));
						}
						
					}
				}			
				else if(line.startsWith("[Rotate]"))
				{
					while((line = input.readLine())!=null)
					{
						if(line.startsWith("//"))
							continue;
						if(line.startsWith("[/Rotate]"))
							break;
						Matcher m = RotatePattern.matcher(line);
						while(m.find())
						{
							float fromAngle=Float.parseFloat(m.group(2));
							float toAngle=Float.parseFloat(m.group(3));
							float x=Float.parseFloat(m.group(4));
							float y=Float.parseFloat(m.group(5));
							float z=Float.parseFloat(m.group(6));
							long  dur=Long.parseLong(m.group(7).trim());
							int loops=Integer.parseInt(m.group(8).trim());
							A3DTransformAnimation a= new A3DTransformAnimation(null,new LinearInterpolator(),
									dur, loops);
							a.setRotation(fromAngle, toAngle, x, y, z);
							a.setDelayTime(Long.parseLong(m.group(9).trim()), Long.parseLong(m.group(10).trim()));

							animlist.add(new mAnimation(m.group(1).trim(),a));
						}
						
					}
				}
				else if(line.startsWith("[Scale]"))
				{
					while((line = input.readLine())!=null)
					{
						if(line.startsWith("//"))
							continue;
						if(line.startsWith("[/Scale]"))
							break;
						Matcher m = ScalePattern.matcher(line);
						while(m.find())
						{
							float fromx=Float.parseFloat(m.group(2));
							float fromy=Float.parseFloat(m.group(3));
							float fromz=Float.parseFloat(m.group(4));
							float tox=Float.parseFloat(m.group(5));
							float toy=Float.parseFloat(m.group(6));
							float toz=Float.parseFloat(m.group(7));

							long  dur=Long.parseLong(m.group(8).trim());
							int loops=Integer.parseInt(m.group(9).trim());
							A3DTransformAnimation a= new A3DTransformAnimation(null,new LinearInterpolator(),
									dur, loops);
							a.setScale(fromx, fromy, fromz, tox, toy, toz);
							a.setDelayTime(Long.parseLong(m.group(10).trim()), Long.parseLong(m.group(11).trim()));
							animlist.add(new mAnimation(m.group(1).trim(),a));						}
					}
				}
				else if(line.startsWith("[Alpha]"))
				{
					while((line = input.readLine())!=null)
					{
						if(line.startsWith("//"))
							continue;
						if(line.startsWith("[/Alpha]"))
							break;
						Matcher m = AlphaPattern.matcher(line);
						while(m.find())
						{
							float fromr=Float.parseFloat(m.group(2));
							float fromg=Float.parseFloat(m.group(3));
							float fromb=Float.parseFloat(m.group(4));
							float froma=Float.parseFloat(m.group(5));
							float tor=Float.parseFloat(m.group(6));
							float tog=Float.parseFloat(m.group(7));
							float tob=Float.parseFloat(m.group(8));
							float toa=Float.parseFloat(m.group(9));

							long  dur=Long.parseLong(m.group(10).trim());
							int loops=Integer.parseInt(m.group(11).trim());
							A3DTransformAnimation a= new A3DTransformAnimation(null,new LinearInterpolator(),
									dur, loops);
							a.setAlphaColor(fromr, fromg, fromb, froma, tor, tog, tob, toa);
							a.setDelayTime(Long.parseLong(m.group(12).trim()), Long.parseLong(m.group(13).trim()));
							animlist.add(new mAnimation(m.group(1).trim(),a));							}
					}
				}
				else if(line.startsWith("[Trans]"))
				{
					while((line = input.readLine())!=null)
					{
						if(line.startsWith("//"))
							continue;
						if(line.startsWith("[/Trans]"))
							break;
						Matcher m = PathPattern.matcher(line);
						while(m.find())
						{
							String st=m.group(2).trim();
							A3DAnimation ani=PathMgr.instance.getAnimation(st);
							if(ani!=null)
							{
								animlist.add(new mAnimation(m.group(1).trim(),ani));
								
							}
						}
					}				
				}
				else if(line.startsWith("[Texture]"))
				{
					while((line = input.readLine())!=null)
					{
						if(line.startsWith("//"))
							continue;
						if(line.startsWith("[/Texture]"))
							break;
						Matcher m = TexturePattern.matcher(line);
						while(m.find())
						{
							long  dur=Long.parseLong(m.group(2).trim());
							int loops=Integer.parseInt(m.group(3).trim());

							A3DTransformAnimation a= new A3DTransformAnimation(null,new LinearInterpolator(),
									dur, loops);
							a.setDelayTime(Long.parseLong(m.group(4).trim()), Long.parseLong(m.group(5).trim()));

							String[] stl=m.group(6).trim().split("[;,]");
							for(int k=0;k<stl.length;k++)
							{
								stl[k]=stl[k].trim();
							}
							
							if(stl.length>1)
							{
								a.setTextureList(stl);
								for(int j=0;j<stl.length;j++)
								{
									if(stl[j].compareTo("null")==0)
										continue;
									if(stl[j].contains("."))
									{
										if(TextureManager.global.getTexture(stl[j])==null)
										{
											if(j%2==0)//color texture
											{
												InputStream tmpst =ResourceMgr.instance.getInputStream(ctx,stl[j]);  
												if(tmpst!=null)
												{
													TextureManager.global.addTexture(new Texture(stl[j], BitmapFactory.decodeStream(tmpst)));
												}
											}
											else  //normal texture
											{
												
											}
										}
									}else
									{
										if(TextureManager.global.getTexture(stl[j])==null)
										{
											getTextByString(ctx,stl[j]);
//											getTitleTex(ctx,j/2);
										}
									}
								}
								animlist.add(new mAnimation(m.group(1).trim(),a));		
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
		int i=0;
		int j=animlist.size();
		for(i=0;i<j;i++)
		{
			if(animlist.get(i).id.compareTo(name)==0)
			{
				return animlist.get(i).anim;
			}
		}
		Log.v("","getAnimation null");
		return null;
	}
	public void clearAnimation()
	{
		int i=0;
		int j=animlist.size();
		for(i=0;i<j;i++)
		{
			A3DAnimation anim= animlist.get(i).anim;
			if(anim.getNode()!=null)
				anim.stop();
		}
		animlist.clear();
	}
	
}

