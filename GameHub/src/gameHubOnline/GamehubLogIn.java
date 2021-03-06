package gameHubOnline;

/**
 * The login to get into GameHub
 * 
 * @author Isaiah Smoak
 * @author Zachary Jones
 * @version 1.0
 */

import games.Game;
import games.brickBreaker.BrickBreaker;
import games.connectFour.ConnectFour;
import games.hangman.Hangman;
import games.pong.Pong;
import games.snake.Snake;
import games.ticTacToe.TicTacToe;
import games.triviaGame.Trivia;
import games.wordWhomp.WordWhomp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import gameHub.PlayerAccount;

public class GamehubLogIn implements FocusListener, KeyListener, ActionListener, Runnable, ListSelectionListener {
	
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	double width = screenSize.getWidth();
	double height = screenSize.getHeight();
	
	public final int GAME_ICON_SIZE = 150;
	
	/* The player's Account */
	private PlayerAccount p1;
	/* Socket to connect to the Server */
	private Socket s;
	/* To Send data */
	private ObjectOutputStream oos;
	/* To Receive data */
	private ObjectInputStream ois;
	/* Is this Login connect? */
	private boolean isConnected;
	
	/*
	 * IP_ADDRESS WINDOW
	 * 1st Window seen
	 * Purpose: To input ip_address of the Host Server
	 * Components: ipAddressWindow, ipAddressMessage, ipAddressBox, ipAddressSubmit
	 */
	private JFrame ipAddressWindow = new JFrame("HOST SERVER IP_ADDRESS");
	private JLabel ipAddressMessage = new JLabel("HOST SERVER IP_ADDRESS: ");
	private JPanel ipAddressPanel = new JPanel();
	private JTextField ipAddressBox = new JTextField("");
	private JButton ipAddressSubmit = new JButton("Submit");
	private String ip_Address = null;
	
	/*
	 * MAIN WINDOW
	 * 2nd Window seen
	 * Purpose: To input username name and password in, and login to the Gamehub
	 * Components: mainWindow, welcomeeMessage, instru, loginbox, passBox, loginbutton, registerButton
	 */
	private JFrame mainWindow = new JFrame("Welcome to GameHub!");
	private JPanel mainWindowPanel = new JPanel();
	private JLabel welcomeMessage = new JLabel("Welcome to GameHub");
	private JLabel instru = new JLabel("Enter username and password below!");
	private JTextField loginBox = new JTextField("Enter Username");
	private JPasswordField passBox = new JPasswordField("");
	private JButton loginButton = new JButton("Log In!");
	private JButton registerButton = new JButton("Register!");
	
	/*
	 * GAMEHUB WINDOW
	 * 3rd Window seen
	 * Purpose: Full access to all of GameHubs functionalities
	 * Components:
	 */
	private JFrame gameHubWindow; //Needs values not initilized yet
	private JTabbedPane mainPanel = new JTabbedPane();
	private JTextField chatOut = new JTextField();
	private JTextArea chatIn = new JTextArea();
	private JButton chatSubmitButton = new JButton("Submit");
	private JButton tictactoeButton;
	private JButton connectfourButton;
	private JButton snakeButton;
	private JButton hangManButton;
	private JButton triviaGameButton;
	private JButton wordWhompButton;
	private JButton brickBreakerButton;
	private JButton pongButton;
	private JList<String> onlineList = new JList<String>();
	private JLabel errormsg = new JLabel();
	private JPanel homePanel = new JPanel();
	private JScrollPane bd = new JScrollPane(homePanel);
	private JScrollPane wd = new JScrollPane(chatIn);
	private JPanel scorePanel = new JPanel();
	private JPanel trophyPanel = new JPanel();
	private ImageIcon tictac = new ImageIcon("icons/ticTacToeIcon.gif", "tictactoe");
	private ImageIcon con4 = new ImageIcon("icons/connectFour.png", "connect 4");
	private ImageIcon snake = new ImageIcon("icons/snake.png", "snake");
	private ImageIcon hangman = new ImageIcon("icons/hangman.png", "hangman");
	private ImageIcon triviaGame = new ImageIcon("icons/triviaGame.png", "triviaGame");
	private ImageIcon wordWhomp = new ImageIcon("icons/wordWhomp.png", "wordWhomp");
	private ImageIcon brickBreaker = new ImageIcon("icons/brickBreaker.png", "brickBreaker");
	private ImageIcon pong = new ImageIcon("icons/pong.png", "pong");
	private JPanel onlinePanel = new JPanel();
	private JPanel chatPanel = new JPanel(); //for the bottom
	
	/*
	 * INVITE WINDOW
	 * 4th Window seen
	 * Purpose: to invite a fellow friend on the server to a game (more info needed)
	 * Components: inviteWindow, middlePanel, acceptButton, denyButton
	 */
	JFrame inviteWindow = new JFrame("Invite");
	JPanel middlePanel = new JPanel();
	JButton acceptButton = new JButton("Accept");
	JButton denyButton = new JButton("Deny");

	/**
	 * Helper method to set up the IP_Address JFrame
	 * 
	 * @author Zachary R Jones
	 */
	public void setUpIp_AddressWindow() {
		ipAddressWindow.requestFocus();
		ipAddressWindow.toFront();
		ipAddressWindow.setBackground(Color.black);
		ipAddressWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Already there
		//ipAddressWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
		//ipAddressWindow.setUndecorated(true);
		ipAddressWindow.setLocation((int)width/2 - 300, (int)height/2 - 50);
		//ipAddressWindow.setOpacity(0.75F);
		
		ipAddressWindow.setSize(new Dimension(600,100));
		
		ipAddressPanel.setSize(new Dimension(600,100));
		ipAddressPanel.setLayout(new GridLayout());
		ipAddressPanel.setBorder(BorderFactory.createLineBorder(Color.black, 10));
		
		ipAddressBox.setSize(new Dimension(200, 100));
		ipAddressBox.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
		
		ipAddressMessage.setSize(new Dimension(200, 100));
		
		ipAddressSubmit.setSize(new Dimension(200, 100));
		ipAddressSubmit.addActionListener(this);
		
		BufferedImage pop = null;
		try {
			pop = ImageIO.read(new File("icons/gamehub_logo.png"));
			ipAddressWindow.setIconImage(pop);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ipAddressPanel.add(ipAddressMessage);
		ipAddressPanel.add(ipAddressBox);
		ipAddressPanel.add(ipAddressSubmit);
		ipAddressWindow.add(ipAddressPanel);
		
		ipAddressWindow.setVisible(true);
	}
	
	/**
	 * Helper method to set up the MainWindow JFrame
	 * 
	 * @author Zachary R Jones
	 */
	public void setUpMainWindow() {
		
		mainWindow.requestFocus();
		mainWindow.toFront();
		mainWindow.setBackground(Game.saddleBrown);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Already there
		//mainWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
		//mainWindow.setUndecorated(true);
		mainWindow.setLocation((int)width/2 - 300, (int)height/2 - 300);
		
		loginButton.setBackground(Game.saddleBrown);
		loginButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		registerButton.setBackground(Game.saddleBrown);
		registerButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		//games.setListData(listData);
		welcomeMessage.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
		//welcomeMessage.setForeground(Color.DARK_GRAY);
		welcomeMessage.setBackground(Game.saddleBrown);
		welcomeMessage.setForeground(Game.saddleBrown);
		welcomeMessage.setBorder(BorderFactory.createLineBorder(Game.saddleBrown, 5));
		instru.setBorder(BorderFactory.createLineBorder(Game.saddleBrown, 5));
		instru.setForeground(Game.saddleBrown);
		mainWindow.requestFocus(); //gets focus of window so loginBox isn't empty
		loginBox.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
		loginBox.addFocusListener(this);
		passBox.addKeyListener(this);
		//loginBox.addKeyListener(this);
		loginBox.setPreferredSize(new Dimension(170,30));
		loginBox.setMinimumSize(new Dimension(170,30));
		passBox.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
		passBox.setPreferredSize(new Dimension(170, 30));
		passBox.setMinimumSize(new Dimension(170, 30));
		passBox.addFocusListener(this);
		//passBox.addKeyListener(this);
		mainWindowPanel.setBackground(Color.black);
		mainWindowPanel.add(welcomeMessage);
		BufferedImage pop = null;
		try {
			pop = ImageIO.read(new File("icons/gamehub_logo.png"));
			JLabel img_show = new JLabel(new ImageIcon(pop));
			mainWindowPanel.add(img_show);
			mainWindow.setIconImage(pop);
		} catch (IOException e) {
			e.printStackTrace();
		}
		instru.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
		mainWindowPanel.setBorder(BorderFactory.createLineBorder(Game.saddleBrown, 10));
		mainWindowPanel.add(instru);
		mainWindowPanel.add(Box.createRigidArea(new Dimension(100, 20)));
		mainWindowPanel.add(loginBox);
		mainWindowPanel.add(passBox);
		mainWindowPanel.add(loginButton);
		mainWindowPanel.add(registerButton);
		errormsg.setForeground(Color.red);
		mainWindow.getContentPane().add(mainWindowPanel);
		mainWindow.getContentPane().add(errormsg, "South");
		mainWindow.setSize(590,700);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setVisible(true);
		mainWindow.requestFocus();
		loginButton.addActionListener(this);
		registerButton.addActionListener(this);
		loginBox.setText("Username");
		onlineList.addListSelectionListener(this);
	}
	
	/**
	 * Helper method to set up the GameHubWindow JFrame
	 * 
	 * @author Zachary R Jones
	 */
	public void setUpGameHubWindow() {
		isConnected = true;
		if(gameHubWindow != null && !gameHubWindow.isShowing())
			mainWindow.dispose(); //destroy the window, load up new ones for a signed in account
		gameHubWindow = new JFrame("Welcome to GameHub " +p1.getUsername() +"!" + System.lineSeparator());
		gameHubWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameHubWindow.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	if (JOptionPane.showConfirmDialog(gameHubWindow, 
		                "Are you sure to close this window?", "Really Closing?", 
		                JOptionPane.YES_NO_OPTION,
		                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
		    			isConnected = disconnect();
		            }
		    }
		});
		BufferedImage pop = null;
		try {
			pop = ImageIO.read(new File("icons/gamehub_logo.png"));
			gameHubWindow.setIconImage(pop);
		} catch (IOException e) {
			e.printStackTrace();
		}
		gameHubWindow.setMinimumSize(new Dimension(1024,800));
		gameHubWindow.setResizable(false);
		gameHubWindow.setVisible(true);
		gameHubWindow.getContentPane().setBackground(new Color(52,36,74));
		gameHubWindow.setLayout(null);
		//gameHubWindow.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
		onlineList.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 30));
		onlineList.setPreferredSize(new Dimension(224,600));
		onlineList.setBackground(new Color(215,204,230));
		//stopped
		wd.setPreferredSize(new Dimension(1010,100));
		homePanel.setBackground(Color.black);
		homePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		scorePanel.setBackground(new Color(240,250,240));
		trophyPanel.setBackground(Color.green);
		mainPanel.setForeground(Color.black);
		mainPanel.setBackground(Color.orange);
		mainPanel.addTab("Home", bd);
		scorePanel.add(new JLabel("This is where to put scores!"));
		trophyPanel.add(new JLabel("Trophy listing!"));
		//add code to flesh out home panel or show games
		Image img = tictac.getImage() ;  
		Image newimg = img.getScaledInstance( GAME_ICON_SIZE, GAME_ICON_SIZE,  java.awt.Image.SCALE_SMOOTH ) ;  
		tictac = new ImageIcon( newimg );
		img = con4.getImage() ;  
		newimg = img.getScaledInstance( GAME_ICON_SIZE, GAME_ICON_SIZE,  java.awt.Image.SCALE_SMOOTH ) ;  
		con4 = new ImageIcon( newimg );
		img = snake.getImage() ;  
		newimg = img.getScaledInstance( GAME_ICON_SIZE, GAME_ICON_SIZE,  java.awt.Image.SCALE_SMOOTH ) ;  
		snake = new ImageIcon( newimg );
		img = hangman.getImage() ;  
		newimg = img.getScaledInstance( GAME_ICON_SIZE, GAME_ICON_SIZE,  java.awt.Image.SCALE_SMOOTH ) ;  
		hangman = new ImageIcon( newimg );
		img = triviaGame.getImage() ;  
		newimg = img.getScaledInstance( GAME_ICON_SIZE, GAME_ICON_SIZE,  java.awt.Image.SCALE_SMOOTH ) ;  
		triviaGame = new ImageIcon( newimg );
		img = wordWhomp.getImage() ;  
		newimg = img.getScaledInstance( GAME_ICON_SIZE, GAME_ICON_SIZE,  java.awt.Image.SCALE_SMOOTH ) ;  
		wordWhomp = new ImageIcon( newimg );
		img = brickBreaker.getImage() ;  
		newimg = img.getScaledInstance( GAME_ICON_SIZE, GAME_ICON_SIZE,  java.awt.Image.SCALE_SMOOTH ) ;  
		brickBreaker = new ImageIcon( newimg );
		img = pong.getImage() ;  
		newimg = img.getScaledInstance( GAME_ICON_SIZE, GAME_ICON_SIZE,  java.awt.Image.SCALE_SMOOTH ) ;  
		pong = new ImageIcon( newimg );
		tictactoeButton = new JButton(tictac);
		connectfourButton = new JButton(con4);
		snakeButton = new JButton(snake);
		hangManButton = new JButton(hangman);
		triviaGameButton = new JButton(triviaGame);
		wordWhompButton = new JButton(wordWhomp);
		brickBreakerButton = new JButton(brickBreaker);
		pongButton = new JButton(pong);
		tictactoeButton.setEnabled(false);
		connectfourButton.setEnabled(false);
		snakeButton.setEnabled(false);
		hangManButton.setEnabled(false);
		triviaGameButton.setEnabled(false);
		wordWhompButton.setEnabled(false);
		brickBreakerButton.setEnabled(false);
		pongButton.setEnabled(false);
		//homePanel.add(new JLabel("Do the thing here!"));
		c.gridx = 0;
		c.gridy = 0;
		homePanel.add(tictactoeButton, c);
		c.gridx = 1;
		c.gridy = 0;
		homePanel.add(connectfourButton, c);
		c.gridx = 2;
		c.gridy = 0;
		homePanel.add(snakeButton, c);
		c.gridx = 0;
		c.gridy = 1;
		homePanel.add(hangManButton, c);
		c.gridx = 1;
		c.gridy = 1;
		homePanel.add(triviaGameButton, c);
		c.gridx = 2;
		c.gridy = 1;
		homePanel.add(wordWhompButton, c);
		c.gridx = 0;
		c.gridy = 2;
		homePanel.add(brickBreakerButton, c);
		c.gridx = 1;
		c.gridy = 2;
		homePanel.add(pongButton, c);
		tictactoeButton.addActionListener(this);
		connectfourButton.addActionListener(this);
		snakeButton.addActionListener(this);
		hangManButton.addActionListener(this);
		triviaGameButton.addActionListener(this);
		wordWhompButton.addActionListener(this);
		brickBreakerButton.addActionListener(this);
		pongButton.addActionListener(this);
		mainPanel.addTab("Scores", scorePanel);
		mainPanel.addTab("Trophies", trophyPanel);
		chatSubmitButton.addActionListener(this);

		mainPanel.setBackground(new Color(52,36,74));
		mainPanel.setBounds(0, 0, 800, 600);
		onlinePanel.setBackground(new Color(52,36,74));
		onlinePanel.setBounds(804, 0, 215, 600);
		onlineList.setBorder(new LineBorder(Color.black));
		chatPanel.setBackground(new Color(52,36,74));
		JLabel this_online = new JLabel("Who's Online");
		this_online.setForeground(Color.white);
		this_online.setFont(new Font("Arial MT Bold", Font.BOLD, 15));
		onlinePanel.add(this_online);
		onlinePanel.add(onlineList);
		chatPanel.setBounds(0, 600, 1024, 200);
		//chatIn.setPreferredSize(new Dimension(1010, 100));
		chatIn.setFont(new Font("Comic Sans MS", Font.BOLD, 15));
		chatIn.setBorder(new LineBorder(Color.black));
		chatOut.setPreferredSize(new Dimension(920,30));
		chatOut.setFont(new Font("Comic Sans MS", Font.BOLD, 15));
		chatIn.setForeground(Color.BLUE);
		chatIn.setBackground(new Color(185,219,217));
		chatOut.requestFocus();
		chatOut.addKeyListener(this);
		chatOut.setBorder(new LineBorder(Color.black));
		chatIn.setEditable(false);
		chatPanel.add(wd);
		chatPanel.add(chatOut);
		chatPanel.add(chatSubmitButton);
		gameHubWindow.add(mainPanel);
		gameHubWindow.add(onlinePanel);
		gameHubWindow.add(chatPanel);
	}
	
	/**
	 * Helper method to set up the GameHub JFrame
	 * 
	 * @author Zachary R Jones
	 */
	public void setUpInviteWindow() {
		
	}
	public GamehubLogIn(String ipaddress) {
		if(ipaddress != null) 
			ip_Address = ipaddress;
		setUpIp_AddressWindow();
	}
	
	//Disconnects from remote server
	//Returns true on success, false on failure
	public boolean disconnect(){
	    try{
	        this.oos.writeObject("0x000000");
	        this.s.close();
	        this.isConnected = false;
	        System.out.println("The Socket is closed");
	        return false;
	    }catch(Exception e){
	    	System.out.println("The Socket is open");
	        return true;
	    }
	}

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			new GamehubLogIn(null);
		} else {
			new GamehubLogIn(args[0]); //load Gamehub dynamically instead of static, so we can pass in ip address
		}
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		//If logIn box gets clicked on, clear the error messages as well as the login/password fields
		errormsg.setText(""); 
		if(arg0.getSource() == loginBox)
			loginBox.setText("");
		else if(arg0.getSource() == passBox)
			passBox.setText("");
		else if(arg0.getSource() == chatOut){
			if(chatOut.getText().equals("Outgoing"))
				chatOut.setText("");
		}
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		//If focus is lost from loginBox, restore instructions to enter username
		if(arg0.getSource() == loginBox && loginBox.getText().isEmpty())
			loginBox.setText("Enter Username");
		//else if(arg0.getSource() == passBox && passBox.getPassword().length == 0)
		//passBox.setText("Enter Password   ");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		p1 = new PlayerAccount("Zach", "Jones");
		
		setUpGameHubWindow();
		
		errormsg.setText(""); //always clear at any time something happens
		if(arg0.getSource() == loginButton){ //if login button, begin process of getting online
			try {
				if(loginBox.getText().equals("Enter Username")){ //user hasn't typed anything
					errormsg.setText("Error: Must enter a valid Username!");
					return;
				}

				else if(loginBox.getText().equals("")) //if blank, ignore
					return;
				System.out.println(ip_Address);
				s = new Socket(ip_Address, 4555); //create connection to server
				oos = new ObjectOutputStream(s.getOutputStream());
				oos.writeObject(loginBox.getText() + "/" + String.valueOf(passBox.getPassword())); //initial "hello" to server, sends username and password
				ois = new ObjectInputStream(s.getInputStream()); 
				String serverReply = (String) ois.readObject();
				System.out.println("Server Reply:" + serverReply); 
				chatIn.append(serverReply + "!" + System.lineSeparator());
				if(!serverReply.contains("Welcome")){ //server sends back welcome... if not, then error --> incorrect username/password!
					errormsg.setText("Error: Incorrect Username or Password");
					return;
				}
				p1 = new PlayerAccount(loginBox.getText().toUpperCase(), String.valueOf(passBox.getPassword())); //create a new player account with typed in username/password now that we've signed in
				mainWindow.dispose(); //destroy the window, load up new ones for a signed in account
				mainWindow = null;
				
				//the changes are affected here
				setUpGameHubWindow();
				new Thread(this).start(); //begin separate thread for listening on the created socket
			}

			catch(IOException | ClassNotFoundException e){
				errormsg.setText("The ip_address: " + ip_Address + " was invalid.\n Please restart and enter correct Host IP_Address");;
			}

		}
		else if (arg0.getSource() == registerButton){
			//put code here
			try {
				if(loginBox.getText().equals("Enter Username")){ //user hasn't typed anything
					errormsg.setText("Error: Must enter a valid Username!");
					return;
				}

				else if(loginBox.getText().equals("") || loginBox.getText().contains("*")) //if blank, ignore
					return;
				s = new Socket(ip_Address, 2021); //create connection to server
				oos = new ObjectOutputStream(s.getOutputStream());
				oos.writeObject("*"+loginBox.getText() + "/" + String.valueOf(passBox.getPassword())); //initial "hello" to server, sends username and password
				System.out.println("Hello");
				ois = new ObjectInputStream(s.getInputStream()); 
				String serverReply = (String) ois.readObject();
				System.out.println("Server Reply:" + serverReply); 
				if(!serverReply.contains("Welcome")){ //server sends back welcome... if not, then error --> incorrect username/password!
					errormsg.setText("Error: Incorrect Username or Password");
					return;
				}
				p1 = new PlayerAccount(loginBox.getText().toUpperCase(), String.valueOf(passBox.getPassword())); //create a new player account with typed in username/password now that we've signed in
				setUpGameHubWindow();
				new Thread(this).start(); //begin separate thread for listening on the created socket
			}

			catch(IOException e){
				errormsg.setText(e.getMessage());
				System.out.println("Even funnier");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		else if(arg0.getSource() == chatSubmitButton){
			try {
				oos.writeObject(new ChatMessage(chatOut.getText(),p1.getUsername() , "Everyone")); //Sends the chatMessage to everyone
				chatOut.setText(""); //Clear out the box
			} catch (IOException e){e.printStackTrace();}
		}
		
		else if(arg0.getSource() == connectfourButton){
			if (p1.getUsername().equals((String) onlineList.getSelectedValue())) {
				new ConnectFour();
			} else {
				makeInvite(p1.getUsername(), (String) onlineList.getSelectedValue(), GameInvite.connect4);
			}
		}
		else if(arg0.getSource() == tictactoeButton){
			if (p1.getUsername().equals((String) onlineList.getSelectedValue())) {
				new TicTacToe();
			} else {
				makeInvite(p1.getUsername(), (String) onlineList.getSelectedValue(), GameInvite.tictactoe);
			}
		}
		else if(arg0.getSource() == snakeButton) {
			if (p1.getUsername().equals((String) onlineList.getSelectedValue())) {
				new Snake();
			} else {
				makeInvite(p1.getUsername(), (String) onlineList.getSelectedValue(), GameInvite.snake);
			}
		}
		else if(arg0.getSource() == hangManButton) {
			if (p1.getUsername().equals((String) onlineList.getSelectedValue())) {
				try {
					new Hangman();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				makeInvite(p1.getUsername(), (String) onlineList.getSelectedValue(), GameInvite.hangman);
			}
		}
		else if(arg0.getSource() == brickBreakerButton) {
			if (p1.getUsername().equals((String) onlineList.getSelectedValue())) {
				new BrickBreaker();
			} else {
				makeInvite(p1.getUsername(), (String) onlineList.getSelectedValue(), GameInvite.brickBreaker);
			}
		} else if(arg0.getSource() == pongButton) {
			if (p1.getUsername().equals((String) onlineList.getSelectedValue())) {
				new Pong();
			} else {
				makeInvite(p1.getUsername(), (String) onlineList.getSelectedValue(), GameInvite.pong);
			}
		}
		else if(arg0.getSource() == triviaGameButton) {
			if (p1.getUsername().equals((String) onlineList.getSelectedValue())) {
				new Trivia();
			} else {
				makeInvite(p1.getUsername(), (String) onlineList.getSelectedValue(), GameInvite.triviaGame);
			}
		}
		else if(arg0.getSource() == wordWhompButton) {
			if (p1.getUsername().equals((String) onlineList.getSelectedValue())) {
				try {
					new WordWhomp();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				makeInvite(p1.getUsername(), (String) onlineList.getSelectedValue(), GameInvite.wordWhomp);
			}
		}
		else if(arg0.getSource() == ipAddressSubmit) {
			System.out.println("jkdasjfkd");
			ip_Address = ipAddressBox.getText();
			ipAddressWindow.dispose();
			setUpMainWindow();
		}
	}
	
	public void makeInvite(String username, String selectedValue, String game) {
		System.out.println("create invite window!");
		//new game invite window created here
		try {
				oos.writeObject(new GameInvite(username, selectedValue, game));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void get_scores(){;}
	public void add_trophy(){;}
	
	
	@Override
	public void run() {
		//this is where I begin to run the code after login

		/*
		 * This loop listens for input and de-multiplexes it to handle it depending on what type of object it is
		 * E.g if it is chatmessage, echo it to everyone. If it is a gameInvite, selectively forward it, etc..
		 */
		while(isConnected){
			try {
				Object message = ois.readObject();
				System.out.println("Got something!");
				if(message instanceof String[]){ //if it is a string array, it is list of those who are online
					onlineList.setListData((String[]) message);
					onlinePanel.updateUI();
					onlineList.updateUI();
				}
				else if(message instanceof GameInvite){ //It recieved a gameInvite, proceed to create a notification window
					GameInvite invite = (GameInvite) message; 
					System.out.println("Got invite from " +invite.from + " to " +invite.to+ "!");
					if(invite.to.equalsIgnoreCase(p1.getUsername())){
						
						inviteWindow.setSize(300, 300);
						inviteWindow.add(new JLabel("You got an invite!"));
						acceptButton.addActionListener(new ActionListener() {
							//finished creating notification window, add temporary actionlisteners
							@Override
							public void actionPerformed(ActionEvent arg0) {
								invite.Accept(); //if acceptbutton is pressed, invite is accepted
								try {
									oos.writeObject(invite); //accepted invite is sent back to server who notifies sender client that it's invite was accepted
								} catch (IOException e1) {
									e1.printStackTrace();
								}
								System.out.println("Accepted invite!");
								if(invite.game.contains(GameInvite.tictactoe)){ //now that you created invite, load up tic tac toe.. haven't added one for connect4
									try {
										new TicTacToe(p1.getUsername(), invite.from, false, ip_Address, false);
									} catch (UnknownHostException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								else if(invite.game.contains(GameInvite.connect4)){
									try{
										new ConnectFour(p1.getUsername(), invite.from, false, ip_Address, false);
									} catch(UnknownHostException e){
										e.printStackTrace();
									} catch(IOException e){
										e.printStackTrace();
									}
								}
								else if(invite.game.contains(GameInvite.snake)){
									try{
										new Snake(p1.getUsername(), invite.from, false, ip_Address, false);
									} catch(UnknownHostException e){
										e.printStackTrace();
									} catch(IOException e){
										e.printStackTrace();
									}
								}
								else if(invite.game.contains(GameInvite.hangman)){
									try{
										new Hangman(p1.getUsername(), invite.from, false, ip_Address, false);
									} catch(UnknownHostException e){
										e.printStackTrace();
									} catch(IOException e){
										e.printStackTrace();
									}
								}
								else if(invite.game.contains(GameInvite.triviaGame)){
									try{
										new Trivia(p1.getUsername(), invite.from, false, ip_Address, false);
									} catch(UnknownHostException e){
										e.printStackTrace();
									} catch(IOException e){
										e.printStackTrace();
									}
								}
								else if(invite.game.contains(GameInvite.wordWhomp)){
									try{
										new WordWhomp(p1.getUsername(), invite.from, false, ip_Address, false);
									} catch(UnknownHostException e){
										e.printStackTrace();
									} catch(IOException e){
										e.printStackTrace();
									}
								}
								else if(invite.game.contains(GameInvite.brickBreaker)){
									try{
										new BrickBreaker(p1.getUsername(), invite.from, false, ip_Address, false);
									} catch(UnknownHostException e){
										e.printStackTrace();
									} catch(IOException e){
										e.printStackTrace();
									}
								} else if(invite.game.contains(GameInvite.pong)){
									//try{
										//new Pong(p1.getUsername(), invite.from, false, ip_Address, false);
									//} catch(UnknownHostException e){
									//	e.printStackTrace();
									//} catch(IOException e){
									//	e.printStackTrace();
									//}
								}
								inviteWindow.dispose(); //destroy invite/notificaiton window as no longer needed
							}
						}); //make local function
						middlePanel.add(acceptButton);
						denyButton.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								// Opposite of accepted... it is denied
								invite.Deny();
								try {
									oos.writeObject(invite);
								} catch (IOException e1) {
									e1.printStackTrace();
								}
								System.out.println("Denied invitation!");
								inviteWindow.dispose();
							}
						});
						middlePanel.add(denyButton);
						inviteWindow.add(middlePanel);
						inviteWindow.setTitle("Invite from " +invite.from + "!");
						inviteWindow.setVisible(true);
					}
					else if(invite.from.equals(p1.getUsername()) && invite.isAccepted()){
						System.out.println("Opponent accepted invite, opening game now");
						if(invite.game.equals(GameInvite.tictactoe)) {
							new TicTacToe(p1.getUsername(), (String)onlineList.getSelectedValue(), true, ip_Address, true); //attempt to pass in the sockets to tictactoe so it can connect directly to the server
						} else if (invite.game.equals(GameInvite.connect4)) {
							new ConnectFour(p1.getUsername(), (String)onlineList.getSelectedValue(), true, ip_Address, true);
						} else if (invite.game.equals(GameInvite.snake)) {
							new Snake(p1.getUsername(), (String)onlineList.getSelectedValue(), true, ip_Address, true);
						} else if (invite.game.equals(GameInvite.hangman)) {
							new Hangman(p1.getUsername(), (String)onlineList.getSelectedValue(), true, ip_Address, true);
						} else if (invite.game.equals(GameInvite.triviaGame)) {
							new Trivia(p1.getUsername(), (String)onlineList.getSelectedValue(), true, ip_Address, true);
						} else if (invite.game.equals(GameInvite.wordWhomp)) {
							new WordWhomp(p1.getUsername(), (String)onlineList.getSelectedValue(), true, ip_Address, true);
						} else if (invite.game.equals(GameInvite.brickBreaker)) {
							new BrickBreaker(p1.getUsername(), (String)onlineList.getSelectedValue(), true, ip_Address, true);
						} else if (invite.game.equals(GameInvite.pong)){
							//new Pong(p1.getUsername(), (String)onlineList.getSelectedValue(), true, ip_Address, true);
						}
					}
				}
				else if(message instanceof ChatMessage){
					System.out.println("Abe Lincoln");
					ChatMessage chat = (ChatMessage) message;
					/*if(chat.from.equalsIgnoreCase(p1.getUsername()))
						chatIn.setForeground(Color.blue);
					else
						chatIn.setForeground(Color.red);  */
					chatIn.append(chat.from + ": " + chat.message); //reveal chat message to GUI window
					chatIn.setCaretPosition(chatIn.getDocument().getLength());
				} 
				//this does not CATCH anything that isn't one of these! The games should catch the rest!
				else{
					System.out.println("Hey, received something:" + message);//this is where I look at my matches list and handle it correspondingly(forward it to whoever is also in match)
					
					chatIn.append(message.toString()+ "!" + System.lineSeparator());
				}
			}catch(java.net.SocketException s) {
				System.out.println("For some reason these is an exception here\nCan't seem to get rid of");
			}catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		if(!tictactoeButton.isEnabled() ||
				!connectfourButton.isEnabled() ||
				!snakeButton.isEnabled() ||
				!hangManButton.isEnabled() ||
				!triviaGameButton.isEnabled() ||
				!wordWhompButton.isEnabled() ||
				!brickBreakerButton.isEnabled() ||
				!pongButton.isEnabled()){
			tictactoeButton.setEnabled(true);
			connectfourButton.setEnabled(true);
			snakeButton.setEnabled(true);
			hangManButton.setEnabled(true);
			triviaGameButton.setEnabled(true);
			wordWhompButton.setEnabled(true);
			brickBreakerButton.setEnabled(true);
			pongButton.setEnabled(true);
		}
	}

	
	public void keyPressed(KeyEvent kp) {  
		//If Enter Is Pressed
		if(kp.getKeyCode() == KeyEvent.VK_ENTER) { 
			if(kp.getSource() == chatOut){
				chatSubmitButton.doClick();
			}
			else if (kp.getSource() == passBox){
				System.out.println("hallelujah");
				loginButton.doClick();
			}
		}
	
	}

	@Override
	public void keyReleased(KeyEvent arg0) {		}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}

}
