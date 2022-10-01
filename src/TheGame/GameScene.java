package TheGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;



public class GameScene extends JPanel implements KeyListener,ActionListener{
	
	boolean ballHasBounced=false;
	boolean playing=false;
	boolean endGame=false;
	boolean active=true;
	boolean meteoriteBoost=false;
	boolean cheatMode=false;
	
	double levelUpSpeedMultiplier=1.10;
	double meteoriteSpeedMultiplier=1.3;
	double ballSpeedMultiplier=1;
	double gravityMultiplier=0.032;
	
	ArrayList<Color> ballColors=new ArrayList<Color>();
	ArrayList<Integer> takenX = new ArrayList<Integer>();
	ArrayList<Integer> takenY = new ArrayList<Integer>();
	ArrayList<GameObject> objectList = new ArrayList<GameObject>();
	
	Random rn=new Random();
	
	
	Color ballColor=new Color(255,0,0);
	
	int paddleDisplacement=30;
	int level=1;
	int gravityChanger=1;
	int screenHeight=768;
	int screenWidth=1024;
	int paddleLength=120;
	int paddleHeight=10;
	int oldPosX;
	int oldPosY;
	int lives=5;
	int score=0;
	int bounceCount=0;
	int paddleMovement=0; // 0:not moving, 1.:going right, 2:going left
	int topSpotY=20;
	int middleSpotX=452;
	int bottomSpotY=610;
	
	GameObject ball = new GameObject(10,10,10,10,"ball");
	GameObject paddle = new GameObject(middleSpotX,bottomSpotY,paddleLength,paddleHeight,"paddle");
	GameObject star;
	GameObject meteorite;
	GameObject ufoship;
	GameObject gravity;
	
	

	
	double vx=rn.nextInt(4)+1;
	double vy=rn.nextInt(2)+1;
	double remainTime=60;
	double startedTime;
	double pausedTime;
	double tempPausedTime;
	double meteoriteHitTime;
	

	
	Thread thread;
	

	
	GameScene()
	{
		this.setOpaque(false);
		addKeyListener(this);
		setFocusable(true);
		
		MainClass.pause.addActionListener(this);
		
		ballColors.add(new Color(255,0,0));
		ballColors.add(new Color(0,0,255));
		ballColors.add(new Color(0,204,0));
		ballColors.add(new Color(255,204,0));
		
		CreatingObjects();
		
		objectList.add(star);
		objectList.add(meteorite);
		objectList.add(ufoship);
		objectList.add(gravity);
		
		startedTime= System.currentTimeMillis()/1000;
		tempPausedTime=System.currentTimeMillis()/1000;
		
		thread = new Thread(()->{
			while (true) {
				update();
				try {
					Thread.sleep(20);
				}
				catch (Exception e)
				{
					System.out.println(e);
				}
			}
		});
		thread.start();
		
		
		WriteOnJLabels();
		MainClass.playing.setText("<html><font color=yellow size=5><b>"+"PAUSED"+"</b></html>");
		MainClass.cheat.setText("<html><font color=yellow size=5><b>"+"CHEAT:OFF"+"</b></html>");
		
	}
	
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource()==MainClass.pause)
		{
			if(endGame==true)
			{
				//Nothing
			}
			else
			{
			playing=!playing;
			if(playing==false)
			{
				MainClass.pause.setText("Continue");
				MainClass.playing.setText("<html><font color=yellow size=5><b>"+"PAUSED"+"</b></html>");
				tempPausedTime = System.currentTimeMillis()/1000;
				MainClass.pause.setFocusable(false);
			}
			else
			{
				MainClass.pause.setText("Pause");
				MainClass.playing.setText("<html><font color=green size=5><b>"+"PLAYING"+"</b></html>");
				pausedTime+=System.currentTimeMillis()/1000-tempPausedTime;
				MainClass.pause.setFocusable(false);
			}
			}
		}
	}
	
	public void UpdateTakenPos(int objectX,int objectY)
	{
		for (int i=0;i<=140;i++)
		{
			if (!takenX.contains(objectX+i-50))
					{
						takenX.add(objectX+i-50);
					}
			if (!takenY.contains(objectY+i-50))
					{
						takenY.add(objectY+i-50);
					}
		
		}
	}
	
	
	public int getRandom(Random rnd, int start, int end, ArrayList<Integer> exclude) {
		
		ArrayList<Integer> tempList= new ArrayList();
		if(exclude==null)
		{
			exclude = new ArrayList<Integer>();
		}
		else
		{
			Collections.sort(exclude);
		}
		for (int i:exclude)
		{
			if (i>=start && i<=end)
			{
				tempList.add(i);
			}
		}
	    int random = start + rnd.nextInt(end - start + 1 - tempList.size());
	    for (int ex : tempList) {
	        if (random < ex) {
	            break;
	        }
	        random++;
	    }
	    return random;
	}
	
	
	public void paddleMovement()
	{
		if (paddleMovement==1 &&paddle.x<(screenWidth-122))  
		{
			paddle.x+=paddleDisplacement;
		}
		else if (paddleMovement==2&&paddle.x>0-28)
		{
			paddle.x-=paddleDisplacement;
		}
		
	}
	
	
	public void ballMovement()
	{	
		if(ball.y>=bottomSpotY+10)
		{
			if(gravityChanger==-1)
			{
				bounceCount++;
				
				vy=Math.abs(vy);
			}
		}
		if (ball.x<=0 || ball.x>=screenWidth-24)
		{
			if (ball.x<=0)
			{
				bounceCount++;
				vx=Math.abs(vx);
			}
			else
			{
				bounceCount++;
				vx=-Math.abs(vx);
			}
			
		}
		if (ball.y<=0)
		{
			if(gravityChanger==1)
			{
				bounceCount++;
				vy=-Math.abs(vy);
			}
			else
			{
				Death();
			}
		}
		if (ball.getBounds().intersects(paddle.getBounds()))
		{
			bounceCount++;
			BounceOnPaddle();
			vx=(ball.x-(paddle.x+60))/5;
			if(vx==0)
			{
				vx=1;
			}
		}
		ball.x=(int) (ball.x+vx);
		vy= vy-(9.8)*gravityMultiplier*gravityChanger;
		ball.y=(int) (ball.y-vy);
	}
	
	
	
	
	public void BounceOnPaddle()
	{
		
		if(ballHasBounced==false)
		{
			ballHasBounced=true;
		}
		score+=100;
		if(gravityChanger==1)
		{
			vy= -19*ballSpeedMultiplier;
			vy= Math.abs(vy);
		}
		else
		{
			vy= 19*ballSpeedMultiplier;
			vy= -Math.abs(vy);
		}
	}
	
	public void BallColorUpdate()
	{
		//maybe I won't use that because of meteorite visual feedback(yellow ball color)
		ballColor=ballColors.get(bounceCount%ballColors.size());
	}
	
	
	public void Powers()
	{
		if(ballHasBounced==true)
		{
		if(ball.intersects(star))
		{
			if (active==true)
			{
			score+=1000;
			active=false;
			}
		}
		else if((ball.intersects(meteorite)))
		{
			if (active==true)
			{
				meteoriteHitTime=System.currentTimeMillis();
				if(meteoriteBoost==false)
				{
					meteoriteBoost=true;
					ballColor=ballColors.get(3);
					ballSpeedMultiplier*=meteoriteSpeedMultiplier;
					vx*=ballSpeedMultiplier;
					vy*=ballSpeedMultiplier;
				}
				active=false;
			}
		}
		else if(ball.intersects(ufoship))
		{
			if (active==true)
			{
			ChangingPositions();
			active=false;
			}
		}
		else if(ball.intersects(gravity))
		{
			if (active==true)
			{
				vy/=2;
			gravityChanger*=-1;
			if(gravityChanger==1)
			{
				paddle.y=bottomSpotY;
			}
			else
			{
				paddle.y=topSpotY;
			}
			active=false;
			}
		}
		else
		{
			active=true;
		}
		
		}
	}
	
	public void IntersectionVisualize(GameObject object,Graphics g)
	{
		if(ballHasBounced==true)
		{
		if(ball.intersects(object))
		{
			g.setColor(new Color(0,255,51,200));
			g.fillOval(object.x,object.y,40,40);
		}
		}
	}
	
	public void paintComponent(Graphics g)
	{
		IntersectionVisualize(star,g);
		IntersectionVisualize(meteorite,g);
		IntersectionVisualize(ufoship,g);
		IntersectionVisualize(gravity,g);
		star.draw(g,this);
		ufoship.draw(g,this);
		meteorite.draw(g,this);
		gravity.draw(g,this);
		if(ballHasBounced==false)
		{
			g.setColor(new Color(0,0,0,100));
			g.fillOval(star.x,star.y,40,40);
			g.fillOval(meteorite.x,meteorite.y,40,40);
			g.fillOval(ufoship.x,ufoship.y,40,40);
			g.fillOval(gravity.x,gravity.y,40,40);
		}
		g.setColor(Color.black);
		g.fillRect(paddle.x, paddle.y, paddleLength, paddleHeight);
		g.setColor(ballColor);
		g.fillOval(ball.x,ball.y,10,10);
		paddle.draw(g, this);
		ball.draw(g,this);
	}
	
	public double Time()
	{
		return System.currentTimeMillis()/1000-startedTime;
	}
	
	
	public void EndGame(double remainTime,int lives)
	{
		if (lives<=0)
		{
			MainClass.playing.setText("<html><font color=red size=5><b>"+"GAME OVER"+"</b></html>");
			endGame=true;
		}
	}
	
	public void LevelUp()
	{
		if(remainTime<=0)
		{
			pausedTime=0; //IMPORTANT
			level+=1;
			startedTime=System.currentTimeMillis()/1000-pausedTime;
			ballSpeedMultiplier*=levelUpSpeedMultiplier;
			vx*=ballSpeedMultiplier;
			vy*=ballSpeedMultiplier;
		}
	}
	
	public void ChangingPositions()
	{
		takenX.clear();
		takenY.clear();
		int objectX=getRandom(rn,50,850,takenX);
		int objectY=getRandom(rn,50,500,takenY);
		
		star.x=objectX;
		star.y=objectY;
		
		UpdateTakenPos(objectX,objectY);
		
		objectX=getRandom(rn,50,850,null);
		if (takenX.indexOf(objectX)!=-1)
		{
			objectY=getRandom(rn,50,500,takenY);
		}
		else
		{
			objectY=getRandom(rn,50,500,null);
		}
	
		meteorite.x=objectX;
		meteorite.y=objectY;
		
		
		UpdateTakenPos(objectX,objectY);
		
		objectX=getRandom(rn,50,850,null);
		if (takenX.indexOf(objectX)!=-1)
		{
			objectY=getRandom(rn,50,500,takenY);
		}
		else
		{
			objectY=getRandom(rn,50,500,null);
		}
		
		
		ufoship.x=objectX;
		ufoship.y=objectY;
		
		UpdateTakenPos(objectX,objectY);
		
		objectX=getRandom(rn,50,850,null);
		if (takenX.indexOf(objectX)!=-1)
		{
			objectY=getRandom(rn,50,500,takenY);
		}
		else
		{
			objectY=getRandom(rn,50,500,null);
		}
		
		gravity.x=objectX;
		gravity.y=objectY;
	}
	
	public void CreatingObjects()
	{
		int objectX=getRandom(rn,50,850,takenX);
		int objectY=getRandom(rn,50,500,takenY);
		
		star = new GameObject(objectX,objectY,40,40,"star.png");
		
		UpdateTakenPos(objectX,objectY);
		
		objectX=getRandom(rn,50,850,null);
		if (takenX.indexOf(objectX)!=-1)
		{
			objectY=getRandom(rn,50,500,takenY);
		}
		else
		{
			objectY=getRandom(rn,50,500,null);
		}
	
		meteorite = new GameObject(objectX,objectY,40,40,"meteorite.png");
		
		UpdateTakenPos(objectX,objectY);
		
		objectX=getRandom(rn,50,850,null);
		if (takenX.indexOf(objectX)!=-1)
		{
			objectY=getRandom(rn,50,500,takenY);
		}
		else
		{
			objectY=getRandom(rn,50,500,null);
		}
		
		
		ufoship = new GameObject(objectX,objectY,40,40,"ufoship.png");
		
		UpdateTakenPos(objectX,objectY);
		
		objectX=getRandom(rn,50,850,null);
		if (takenX.indexOf(objectX)!=-1)
		{
			objectY=getRandom(rn,50,500,takenY);
		}
		else
		{
			objectY=getRandom(rn,50,500,null);
		}
		
		gravity = new GameObject(objectX,objectY,40,40,"gravity.png");
	}
	
	
	
	
	public void update()
	{
		
		if (playing==true && endGame==false)
		{
			if(cheatMode==true)
			{
				Cheat();
			}
			if(meteoriteBoost==true)
			{
				if((System.currentTimeMillis()-meteoriteHitTime)/1000>=5)
				{
					meteoriteBoost=false;
					ballSpeedMultiplier/=meteoriteSpeedMultiplier;
					vx/=meteoriteSpeedMultiplier;
					vy/=meteoriteSpeedMultiplier;
					ballColor=ballColors.get(0);
				}
			}
			BallHitCheck();
			Powers();
			//BallColorUpdate();
			remainTime= 59-Time()+pausedTime;
			EndGame(remainTime,lives);
			LevelUp();
			paddleMovement();
			WriteOnJLabels();
			oldPosX=ball.x;
			oldPosY=ball.y;
			ballMovement();
			repaint();	
		}
	}
	
	public void WriteOnJLabels()
	{
		try
		{
			MainClass.livesandscore.setText("<html><font color=blue size=4><b>"+"<i>"+"Score: "+score+"<br>"+"Lives: "+lives+"</i>"+"</b></html>");
			MainClass.timeandlevel.setText("<html><font color=blue size=4><b>"+"<i>"+"Time: "+(String.valueOf(remainTime)+"<br>"+"Level: "+level+"</i>"+"</b></html>"));
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	public void Death()
	{
		ball.x=10;
		ball.y=10;
		vx=rn.nextInt(4)+1;
		vy=rn.nextInt(2)+1;
		meteoriteBoost=false;
		ballSpeedMultiplier=1*Math.pow(levelUpSpeedMultiplier,(level-1));
		vx*=ballSpeedMultiplier;
		vy*=ballSpeedMultiplier;
		lives-=1;
		ballColor=ballColors.get(0);
		gravityChanger=1;
		paddle.y=bottomSpotY;
		ballHasBounced=false;
	}
	
	 //Right paddleMovement=1 left=2 stop=0
	public void Cheat()
	{
		
		if(ball.x>=paddle.x+(paddleLength/2)-30 &&ball.x<=paddle.x+(paddleLength/2)+30)
		{
			paddleMovement=0;
		}
		else if(ball.x<paddle.x+(paddleLength/2)-30)
		{
			paddleMovement=2;
		}
		else if(ball.x>paddle.x+(paddleLength/2)+30)
		{
			paddleMovement=1;
		}
		
		
	}
	
	public void BallHitCheck()
	{
		if (gravityChanger==1)
		{
		if (ball.y>paddle.y && oldPosY<paddle.y)
		{
			//System.out.println("BallHitCheck");
			float calculatedXPos=Math.abs(ball.x-(ball.x-oldPosX)*(ball.y-paddle.y)/(ball.y-oldPosY));
			if(calculatedXPos>=paddle.x && calculatedXPos<=paddle.x+paddleLength)
			{
				ball.y=paddle.y;
				bounceCount++;
				BounceOnPaddle();
				vx=(ball.x-(paddle.x+60))/7;
				if(vx==0)
				{
					vx=1;
				}
			}
			else
			{
				Death();
			}
		}
		else if(ball.y>paddle.y && oldPosY>paddle.y)
		{
			Death();
		}
		}
		else
		{
			if (ball.y<paddle.y && oldPosY>paddle.y)
			{
				//System.out.println("BallHitCheck");
				float calculatedXPos=Math.abs(ball.x-(ball.x-oldPosX)*(ball.y-paddle.y)/(ball.y-oldPosY));
				if(calculatedXPos>=paddle.x && calculatedXPos<=paddle.x+paddleLength)
				{
					ball.y=paddle.y;
					bounceCount++;
					BounceOnPaddle();
					vx=(ball.x-(paddle.x+60))/5;
					if(vx==0)
					{
						vx=1;
					}
				}
				else
				{
					Death();
					
				}
			}
			else if(ball.y<paddle.y && oldPosY<paddle.y)
			{
				Death();
			}
			}
		}
	
	
	
	
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) 
	{
		
		if(endGame==false)
		{
		if (e.getKeyCode()==KeyEvent.VK_SPACE)
		{
			playing =!playing;
			if (playing==false)
			{
				MainClass.playing.setText("<html><font color=yellow size=5><b>"+"PAUSED"+"</b></html>");
				MainClass.pause.setText("Continue");
				tempPausedTime = System.currentTimeMillis()/1000;
			}
			else
			{
				MainClass.playing.setText("<html><font color=green size=5><b>"+"PLAYING"+"</b></html>");
				MainClass.pause.setText("Pause");
				pausedTime+= System.currentTimeMillis()/1000-tempPausedTime;
			}
		}
		if (e.getKeyCode()==KeyEvent.VK_RIGHT)
		{
			paddleMovement=1;
		}
		else if (e.getKeyCode()==KeyEvent.VK_LEFT)
		{
			paddleMovement=2;
		}
		
		if (e.getKeyCode()==KeyEvent.VK_C)
		{
			cheatMode=!cheatMode;
			if(cheatMode==false)
			{
				MainClass.cheat.setText("<html><font color=yellow size=5><b>"+"CHEAT:OFF"+"</b></html>");
				paddleMovement=0;
			}
			else
			{
				MainClass.cheat.setText("<html><font color=yellow size=5><b>"+"CHEAT:ON"+"</b></html>");
			}
		}
		}
	}

	
	
	
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getKeyCode()==KeyEvent.VK_RIGHT)
		{
			if (paddleMovement!=2)
			{
				paddleMovement=0;
			}
		}
		else if (e.getKeyCode()==KeyEvent.VK_LEFT)
		{
			if (paddleMovement!=1)
			{
				paddleMovement=0;
			}
		}
	}
	
}
