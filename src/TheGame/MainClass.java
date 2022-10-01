package TheGame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MainClass {
	static JLabel timeandlevel;
	static JLabel livesandscore;
	static JLabel playing;
	static JLabel cheat;
	static JLabel info;
	static JButton pause;
	
	public static void main(String[] args)
	{
		
		timeandlevel = new JLabel("Time: 60");
		livesandscore= new JLabel("Lives: 3");
		playing= new JLabel("PAUSED");
		pause=new JButton("Play");
		cheat=new JLabel("CHEAT:OFF");
		
		info =new JLabel("<html><font size='4' color=Aqua> Paddle Movement: Left-Right Arrow Key   </font> <font            size='4'color=orange> Pause/Play: Space or Pause Button   </font>  <font            size='4'color=Purple> Cheat On/Off: C</font></html>");
		JFrame mainFrame = new JFrame();
		JPanel mainPanel = new JPanel(new GridBagLayout());
		JPanel firstPanel = new JPanel();
		JPanel thirdPanel = new JPanel();
		
		firstPanel.setLayout(new BorderLayout());
		thirdPanel.setLayout(new BorderLayout());
		
		GameScene gameScene = new GameScene();
		
		cheat.setHorizontalAlignment(JLabel.CENTER);
		info.setHorizontalAlignment(JLabel.CENTER);
		
		firstPanel.add(livesandscore,BorderLayout.WEST);
		firstPanel.add(cheat,BorderLayout.CENTER);
		firstPanel.add(timeandlevel,BorderLayout.EAST);
		
		
		
		thirdPanel.add(playing,BorderLayout.WEST);
	 	thirdPanel.add(pause,BorderLayout.EAST);
	    thirdPanel.add(info,BorderLayout.CENTER);
		
		firstPanel.setBackground(Color.gray);
		thirdPanel.setBackground(Color.gray);
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.weightx=1;
		gbc.weighty=1;   
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy=0;
		mainPanel.add(firstPanel,gbc);
		
		gbc.gridy=1;
		gameScene.setPreferredSize(new Dimension(gameScene.getPreferredSize().width, gameScene
		        .getPreferredSize().height + 600));
		
		mainPanel.add(gameScene,gbc);
		gbc.gridy=2;
		
		mainPanel.add(thirdPanel,gbc);
		mainFrame.add(mainPanel);
		
		mainFrame.setSize(1024,768);
		mainFrame.setResizable(false);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		mainFrame.setVisible(true);
	}
}
