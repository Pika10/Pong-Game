package TheGame;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import javax.imageio.ImageIO;

public class GameObject extends Rectangle{

	 Image pic;

	GameObject(int x, int y, int w, int h, String s)
	{
		this.x=x;
		this.y=y;
		this.width=w;
		this.height=h;
		try
		{
			if (s.equals("paddle")||s.equals("ball"))
			{
				pic=null;
			}
			else
			{
				pic = ImageIO.read(new File("src/TheGame/"+s));
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
			try
			{
			pic = ImageIO.read(new File("src/TheGame/error.png"));
			}
			catch(Exception a)
			{
				System.out.println("There is an important error that must be handled: "+e);
				System.exit(0);
			}
		}
	}
	
	public void draw(Graphics g, Component c)
	{	
		g.drawImage(pic, x, y, width, height,c);
	}
	
}
