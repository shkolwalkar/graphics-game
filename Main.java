import java.util.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.sound.sampled.*;

public class Main {
   public static Game ms; // static game object of the class
   
   public static void main(String[] args) {
      ms = new Game(); // first thing to do: start a new game
   }
   
   public static void resetGame() {
      ms = new Game(); // if reset is clicked then the ms will reference to a new Game object, effectively resetting the board
   }
   
}

class Game { // represents one run through of the game

   // static variables of the game overall
   public static JFrame frame = new JFrame("My JFrame"); // the JFrame
   public boolean whoseTurn; // boolean for whose turn it is (false for Player A, true for Player B)
   private Block[][] blocks = new Block[12][12]; // array of blocks (the minefield)
   public int numMinesLeft = 16; // number of mines left (which is 16)
   public JLabel turn = new JLabel("Turn: A", SwingConstants.CENTER); // label which shows whose turn it is (starts on Player A)
   public JButton start = new JButton("Start"); // start/pause/reset button
   
   
   public double a = 20; // time values for each player (start at 20)
   public double b = 20;
   public static JLabel aTime; // labels that show those times
   public static JLabel bTime;
   
   public int scoreA = 0; // visible scores for each player
   public int scoreB = 0;
   public static JLabel aScore; // labels to show those scores
   public static JLabel bScore;
   public int secretScoreA = 0; // invisible scores for each player
   public int secretScoreB = 0;   
   public int minesClickedA = 1; // stores the number of mines clicked (so that the points lost can increase)
   public int minesClickedB = 1;
   
   public File musicPath; // file which contains path for music file 
   public AudioInputStream input; // input stream for audio file
   public Clip clip; // Clip to load audio data prior to playback
   public boolean musicIsOn = false; // tells us whether user has toggled 'music' button on or off
   
   public File finishPath; // file which contains path for music file 
   public AudioInputStream finishInput; // input stream for audio file
   public Clip finishClip; // Clip to load audio data prior to playback

   
   public File minePath; // file which contains path for mine sfx
   public AudioInputStream mineInput; // input stream for audio file
   public Clip mineClip; // Clip to load audio data prior to playback
   public boolean mineIsOn = false; // tells us whether user has toggled 'sfx' button on or off
   
   public boolean finished = false; // is the game finished?
   
   public static java.util.Timer timer = new java.util.Timer(); // static timer to use to time players
   public static boolean timeRun = false; // used to define whether or not the time is ticking
   
   public Game() {
      initialize();
      frame.setVisible(true);
   }
   
   public void initialize() { // initializes the game
      // initializing the main menu (background)
      frame.setSize(800, 640); // initializing the characteristics of our JFrame (Size)
      frame.setLayout(null); // we're not specifying a layout
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // JFrame will close upon exist
      
      
      
      try { // set the background to the desired image using a try-catch
            frame.setContentPane(new JLabel(new ImageIcon(ImageIO.read(new File("skibidi.jpg")))));
      } 
      catch (IOException e) {
            e.printStackTrace();
      }
      
      // defining the position and color of the title above the field
      JLabel label = new JLabel("2-Player Minesweeper", SwingConstants.CENTER);
      label.setFont(new Font("Consolas", Font.BOLD, 40));
      label.setForeground(Color.WHITE);
      label.setBounds(40,40,480,40);
      frame.add(label); // add it to the frame     
      
      // initializing the font of the turn label
      turn.setFont(new Font("Consolas",Font.BOLD,24));
      turn.setForeground(Color.WHITE);
      turn.setBounds(610,40,100,40);
      frame.add(turn); // add it to the frame
      
      // defining position and color of the start button
      start.setFont(new Font("Consolas", Font.BOLD, 24));
      start.setForeground(Color.WHITE);
      start.setBackground(new Color(0,107,117));
      start.setBounds(656,500,112,80);
      start.addActionListener(new ActionListener() { // add action listener to define its functions
         @Override
         public void actionPerformed(ActionEvent e) {
            if (timeRun == false && !finished) { // if the game is paused (which is tested via timeRun)
               timer = new java.util.Timer(); // create a new Timer in place of the one destroyed by pausing
               start.setText("Pause"); // change the name of the box back to pause
               periodic(); // restart periodic with new timer
            }
            else if (timeRun == true && !finished) { // if the game is running (i.e. we want to pause the game)
               timer.cancel(); // kill the timer
               timer.purge();
               timer = null; // dereference the old timer object, but leave the reference for when we restart
               timeRun = false; // we've paused, so time isn't running
               start.setText("Start"); // change button name to start, so player knows that button restarts
            }
            else {
               Main.resetGame(); // resets the game
            }
         }
      });
      frame.add(start); // add it to the frame
      
      String ruleString = """
         Rules of 2-Player Game
         Each player starts with 20 seconds on the clock.
         Each player will take turns uncovering (left click) or flagging (right click) one square on the board.
         Each square uncovered or flagged nets the player who uncovered it 2 extra seconds.
         When a non-mine square is uncovered, it will show how many mines are adjacent to it.
         When a mine square is uncovered, it will cost that player an increasing number of points (the more mines they uncover).
         When a mine square is flagged, it nets the player one point.
         The game ends when every mine is flagged or when a player runs out of time.
         If the former happens, then the player with more points wins!
         If the latter happens, then the player with more time wins!
         """;
         // text for rules menu
      
      // defining position and color for Rules button
      JButton rules = new JButton("Rules");
      rules.setFont(new Font("Consolas", Font.BOLD, 24));
      rules.setForeground(Color.WHITE);
      rules.setBackground(new Color(0,107,117));
      rules.setBounds(532,500,112,80);
      rules.addActionListener(new ActionListener() { // when clicked, show the rule menu made above
         @Override
         public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null, ruleString);
         }
      });
      frame.add(rules); // add it to the frame
      
      for (int row = 0; row < 12; row++) { // creates new Block objects in a 12 x 12 array
         for (int col = 0; col < 12; col++) {
            Block b = new Block(36 + 40 * row, 100 + 40 * col); // defines their position on the frame
            blocks[row][col] = b; // adds them to the blocks array
            b.regularSquare(); // sets it to behave like a regular square
         }
      }
      
      for (int mine = 0; mine < numMinesLeft; mine++) { // chooses 16 mines
         int randX = (int)(Math.random() * 12); // chooses a random row and a random column
         int randY = (int)(Math.random() * 12);
         // since there is replacement when choosing these, you could end up with less mines, but usually around 15-16 (it's supposed to be unpredictable)
         
         blocks[randX][randY].mine = true; // set the randomly chosen block to a mine
         blocks[randX][randY].mineSquare(); // make it execute mine behaviors
      }
      for (int i = 0; i < blocks.length; i++) { // iterate through the array
         for (int j = 0; j < blocks[0].length; j++) {
            Block q = blocks[i][j];
            
            for (int k = i - 1; k <= i + 1; k++) { // for each block, check the mine status of each block in its vicinity
               for (int l = j - 1; l <= j + 1; l++) {
                  if (k >= 0 && k < 12 && l >= 0 && l < 12 && blocks[k][l].mine) {
                     q.adjacent++; // for each nearby block that is a mine, add one to its adjacent counter
                  }
               }
            }
         }
      }
      whoseTurn = false; // start turn on Player A
      
      // here's where we do music
      try {
         musicPath = new File("music.wav"); // create the file for music
         input = AudioSystem.getAudioInputStream(musicPath); // create the audio stream from file
         clip = AudioSystem.getClip(); // create and open clip
         clip.open(input);
         
         minePath = new File("boom9.wav"); // do the same thing for the mine sfx
         mineInput = AudioSystem.getAudioInputStream(minePath);
         mineClip = AudioSystem.getClip();
         mineClip.open(mineInput);
         
         finishPath = new File("finish.wav"); // create the file for finishing music
         finishInput = AudioSystem.getAudioInputStream(finishPath); // create the audio stream from file
         finishClip = AudioSystem.getClip(); // create and open clip
         finishClip.open(finishInput);
      }
      catch (Exception e) {
         System.out.println(e); // use try catch to support FileNotFoundException, among others
      }
      
      // create a JButton to toggle Music on and off
      JButton audio = new JButton("Music");
      audio.setFont(new Font("Consolas", Font.BOLD, 24));
      audio.setBackground(new Color(0,107,117));
      audio.setForeground(Color.WHITE);
      audio.setBounds(532,430,112,50);
      audio.addActionListener(new ActionListener() { // when clicked, turn on music
         @Override
         public void actionPerformed(ActionEvent e) {
            if (!musicIsOn) {
               clip.loop(Clip.LOOP_CONTINUOUSLY); // loop it continuously
               musicIsOn = true; // basically, system knows we turned music on
            }
            else {
               clip.stop(); // stop the clip
               musicIsOn = false; // system knows we turned it off
            }
         }
      });
      frame.add(audio); // add to frame
      
      // create a JButton to toggle SFX on and off
      JButton sfx = new JButton("SFX");
      sfx.setFont(new Font("Consolas", Font.BOLD, 24));
      sfx.setBackground(new Color(0,107,117));
      sfx.setForeground(Color.WHITE);
      sfx.setBounds(656,430,112,50);
      sfx.addActionListener(new ActionListener() { // when clicked, turn on SFX
         @Override
         public void actionPerformed(ActionEvent e) {
            if (!mineIsOn) {
               mineIsOn = true; // basically, system knows we turned music on
            }
            else {
               mineIsOn = false; // system knows we turned it off
            }
         }
      });
      frame.add(sfx); // add to frame
      
      
      
      // defines position, font and color for 'Player A' text
      JLabel pOne = new JLabel("Player A", SwingConstants.CENTER);
      pOne.setFont(new Font("Consolas", Font.BOLD, 30));
      pOne.setForeground(new Color(70,255,255));
      pOne.setBounds(538,120,225,40);
      frame.add(pOne);
      
      // defines position, font and color for 'Player B' text
      JLabel pTwo = new JLabel("Player B", SwingConstants.CENTER);
      pTwo.setFont(new Font("Consolas", Font.BOLD, 30));
      pTwo.setForeground(new Color(255,70,255));
      pTwo.setBounds(538,280,225,40);
      frame.add(pTwo);
      
      // defines position, font and color for A's timer text
      aTime = new JLabel(String.valueOf(a), SwingConstants.CENTER);
      aTime.setForeground(Color.WHITE);
      aTime.setFont(new Font("Consolas", Font.BOLD, 30));
      aTime.setBounds(583,170,135,40);
      frame.add(aTime);
      
      // defines position, font and color for B's timer text
      bTime = new JLabel(String.valueOf(b), SwingConstants.CENTER);
      bTime.setForeground(Color.WHITE);
      bTime.setFont(new Font("Consolas", Font.BOLD, 30));
      bTime.setBounds(583,330,135,40);
      frame.add(bTime);
      
      // defines position, font and color for A's score text
      aScore = new JLabel(String.valueOf(scoreA), SwingConstants.CENTER);
      aScore.setForeground(Color.WHITE);
      aScore.setFont(new Font("Consolas", Font.BOLD, 30));
      aScore.setBounds(617,220,68,32);
      aScore.setHorizontalAlignment(SwingConstants.CENTER);
      frame.add(aScore);
      
      // defines position, font and color for B's score text
      bScore = new JLabel(String.valueOf(scoreB), SwingConstants.CENTER);
      bScore.setForeground(Color.WHITE);
      bScore.setFont(new Font("Consolas", Font.BOLD, 30));
      bScore.setBounds(617,380,68,32);
      bScore.setHorizontalAlignment(SwingConstants.CENTER);
      frame.add(bScore);

   }
   
   
   // periodic method that runs the game, essentially
   public void periodic() {
      timeRun = true; // the time is running when periodic is running
      
      TimerTask updateTimes = new TimerTask() { // create a timer task for our static timer
         @Override
         public void run() {
            if (!whoseTurn) { // if it's A's turn
               a -= 0.1; // de-increment A's time by 0.1
               aTime.setText(String.valueOf(Math.round(a * 10.0) / 10.0));
               turn.setForeground(new Color(70,255,255)); // set the 'turn' text to show that it's A's turn
               turn.setText("Turn: A");
            }
            else { // if it's B's turn
               b -= 0.1; // de-increment B's time by 0.1
               bTime.setText(String.valueOf(Math.round(b * 10.0) / 10.0));
               turn.setForeground(new Color(255,70,255)); // set the 'turn' text to show that it's B's turn
               turn.setText("Turn: B");
            }
            
            aScore.setText(String.valueOf(scoreA)); // set the visible scores of both players
            bScore.setText(String.valueOf(scoreB));
            
            if (a < 0) { // check for if the time is out for either player (win condition)
               timer.cancel(); // kill the timer
               timer.purge();
               clip.stop(); // stop game music and play finish music
               finishClip.start();
               JOptionPane.showMessageDialog(null, "Player B wins"); // show that player B wins
               finished = true; // shows object that game is finished
               start.setText("Reset"); // gives player option to reset game
               return;
            }
            else if (b < 0) {
               timer.cancel();
               timer.purge();
               clip.stop(); // stop game music and play finish music
               finishClip.start();
               JOptionPane.showMessageDialog(null, "Player A wins"); // or if vice versa, show that player A wins
               finished = true;
               start.setText("Reset"); // gives player option to reset game
               return;
            }
         
            else if (numMinesLeft <= 0) { // check for if all the mines have been found, either by flagging or uncovering (win condition)
               timer.cancel(); // kill the timer
               timer.purge(); 
               JOptionPane.showMessageDialog(null, "All mines found! Calculating scores..."); // show players that mines have been found
               try {
                  Thread.sleep(3000); // wait a little time
               }
               catch (InterruptedException e) {
                  System.out.println("Error");
               }
               aScore.setText(String.valueOf(scoreA + secretScoreA)); // set final scores on frame
               bScore.setText(String.valueOf(scoreB + secretScoreB));
               try {
                  Thread.sleep(2000); // wait a little longer
               }
               catch (InterruptedException e) {
                  System.out.println("Error");
               }
               clip.stop(); // stop game music and play finish music
               finishClip.start();
               if (scoreA + secretScoreA > scoreB + secretScoreB) { // compare scores
                  JOptionPane.showMessageDialog(null, "Player A wins \n Score: " + (scoreA + secretScoreA) + " to " + (scoreB + secretScoreB)); // if A's score is higher, A wins
                  finished = true;
                  start.setText("Reset"); // gives player option to reset game
                  return;
               }
               else {
                  JOptionPane.showMessageDialog(null, "Player B wins \n Score: " + (scoreB + secretScoreB) + " to " + (scoreA + secretScoreA)); // if B's score is higher, B wins
                  finished = true;
                  start.setText("Reset"); // gives player option to reset game
                  return;
               }
         }
            
         }
      };
      timer.schedule(updateTimes,0,100); // this happens every 100 ms, or every 0.1 seconds, so schedule task to occur that frequently
      
   }
   
   
}

// helper class for each individual square on the minefield
class Block {
   public boolean mine; // is it a mine (if yes, then true, else no)
   public int adjacent; // how many mines are adjacent to this square
   private JButton b; // the JButton associated with it
   
   public Block(int x, int y) { // construct a Block given its x and y coordinates
      b = new JButton("C"); // create the JButton, set its font/position/color, remove its (huge) margins, and add it to the frame
      b.setFont(new Font("Consolas", Font.BOLD, 25));
      b.setBounds(x,y,40,40);
      b.setBackground(new Color(16,100,50));
      b.setMargin(new Insets(0,0,0,0));
      Game.frame.add(b);
   }
   
   // action to do if square is mine
   public void mineSquare() {
      b.setFocusable(false); // can't receive keyboard input
      for (ActionListener listener : b.getActionListeners()) { // remove any existing action listeners (the ones from the regular square sequence)
            b.removeActionListener(listener);
      }
      b.addMouseListener(new MouseAdapter() { // add a mouse listener to listen to mouse/touchpad interactions
         @Override
         public void mouseClicked(MouseEvent e) { // if this mine is clicked and its enabled
            if (b.isEnabled()) {
               if (e.getButton() == MouseEvent.BUTTON1) { // left click (they uncovered it)
                  b.setText("M"); // change appearance to reflect that
                  b.setForeground(Color.WHITE);
                  b.setBackground(Color.BLACK);
                  if (Main.ms.whoseTurn) { // decrease scores based on whichever player uncovered it
                     Main.ms.scoreA -= Main.ms.minesClickedA; // take away their minesClicked (how many mines they've clicked on before + 1)
                     Main.ms.minesClickedA++; // add one for this mine
                  }
                  else { // vice versa
                     Main.ms.scoreB -= Main.ms.minesClickedB;
                     Main.ms.minesClickedB++;
                  }
                  if (Main.ms.mineIsOn) { // if we've toggled on sfx...
                     Main.ms.mineClip.start(); // start the sfx
                     Main.ms.mineClip.setFramePosition(0); // reset to beginning of audio
                  }
               }
               else if (e.getButton() == MouseEvent.BUTTON3) { // right click 
                  b.setForeground(Color.WHITE); // change apparance to reflect its flagged state
                  if (Main.ms.whoseTurn) {
                     b.setBackground(new Color(70,255,255)); // to Player A
                     b.setText("A");
                     Main.ms.secretScoreA++; // add one to the secret score, because they guessed right
                  }
                  else {
                     b.setBackground(new Color(255,70,255)); // to Player B
                     b.setText("B");
                     Main.ms.secretScoreB++;
                  }
               }
               // in either case (uncovering or flagging)
               Main.ms.numMinesLeft--; // # mines left to find decreases by 1
               if (Main.ms.whoseTurn) {
                  Main.ms.a += 2.0; // add time for each move and switch turns
                  Main.ms.whoseTurn = true;
               }
               else {
                  Main.ms.b += 2.0;
                  Main.ms.whoseTurn = false;
               }
               for (MouseListener listener : b.getMouseListeners()) { // remove action listeners when done (to prevent double clicks)
                  b.removeMouseListener(listener);
               }
            }
         }
      });
   }
   
   // action to do if square is regular
   public void regularSquare() {
      b.setFocusable(false); // no keyboard action
      b.addMouseListener(new MouseAdapter() { // listen to mouse/touchpad
         @Override
         public void mouseClicked(MouseEvent e) {
            if (b.isEnabled()) { // if it's enabled
               if (e.getButton() == MouseEvent.BUTTON1) { // left click (is uncovered)
                  b.setText(Integer.toString(adjacent)); // set text to how many mines are near it
                  switch (adjacent) { // change the text color accordingly (more extreme color = more danger)
                     case 0:
                        b.setForeground(Color.DARK_GRAY);
                        break;
                     case 1:
                        b.setForeground(Color.YELLOW);
                        break;
                     case 2:
                        b.setForeground(Color.ORANGE);
                        break;
                     case 3:
                        b.setForeground(Color.RED);
                        break;
                     default:
                        b.setForeground(Color.PINK);
                  }
                  b.setBackground(new Color(196,161,66)); // set background to brown color
               }
               else if (e.getButton() == MouseEvent.BUTTON3) { // right clicked (is flagged)
                  b.setForeground(Color.WHITE); // change cosmetic appearance to match player who clicked it
                  if (!Main.ms.whoseTurn) {
                     b.setBackground(new Color(70,255,255));
                     b.setText("A");
                  }
                  else {
                     b.setBackground(new Color(255,70,255));
                     b.setText("B");
                  }
                  // no points are gained for such 'false flags' because there's no mine under it
               }
               if (!Main.ms.whoseTurn) {
                  Main.ms.a += 2.0; // increment times as needed
               }
               else {
                  Main.ms.b += 2.0;
               }
            
               Main.ms.whoseTurn = !Main.ms.whoseTurn; // flip turns
               for (MouseListener listener : b.getMouseListeners()) { // remove action listeners when done (to prevent double clicks)
                  b.removeMouseListener(listener);
               }
            }
            
         }
      });
   }
   
}