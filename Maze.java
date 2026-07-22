import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

public class Maze extends JPanel implements KeyListener {
    JFrame frame;
    String[][] maze, miniMaze;
    int dim = 70;

    int hRow, hCol, endRow, endCol, keyRow, keyCol, chestRow, chestCol;
    int finishRow, finishCol, startRow, startCol, hRow2, hCol2, portalRow, portalCol;
    boolean gameOn, gameOver, hasKey, inMiniMaze, hasPowerUp, onCheatCode, mRowCanSee, mColCanSee;


    int numMonsters = 100;
    int cheatIndex=0;

    int[] mRow=new int[numMonsters];
    int[] mCol=new int[numMonsters];
    int[] mCount=new int[numMonsters];
    String[] mDir=new String[numMonsters];

    String endGameMessage, endGameMessage2;

    int rlcount=0, udcount=0;

    BufferedImage[] rightImg, leftImg, upImg, downImg, mRight, mLeft, mUp, mDown;
    BufferedImage rightSheet, leftSheet, upSheet, downSheet, door, blue_portal, red_portal, yellow_portal, keyImage, openedChest, closedChest, monsterSheet, monsterImg;


    int count = 0;
    String dir = "R";

    public Maze() {
        frame = new JFrame("Mahit's Maze Program");
        frame.add(this);
        frame.setSize(1400, 700);
        frame.addKeyListener(this);

        setup();
        loadImages();

        // Real-time game loop: monsters move and animate independently of player input
        javax.swing.Timer gameTimer = new javax.swing.Timer(120, e -> {
            if (gameOn && !gameOver && !inMiniMaze) {
                monsterMove();
                repaint();
            }
        });
        gameTimer.start();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        if (gameOn && !gameOver && !inMiniMaze) {
            int midR = this.getHeight() / 2;
            int midC = this.getWidth() / 2;

            for (int r = -12; r <= 12; r++) {
                for (int c = -12; c <= 12; c++) {
                    try {
                        if (maze[hRow + r][hCol + c].equals("#")) {
                            g.setColor(Color.YELLOW);
                            g.fillRect(midC + c * dim, midR + r * dim, dim, dim);
                        }
                        if (maze[hRow + r][hCol + c].equals("E") && hasKey) {
                            g.setColor(Color.GREEN);
                            g.drawImage(door, midC + c * dim, midR + r * dim, this);
                        }
                        if (maze[hRow + r][hCol + c].equals("P")) {
                            g.drawImage(blue_portal, midC + c * dim, midR + r * dim, this);
                        }
                        if (maze[hRow + r][hCol + c].equals("M")) {
						    g.drawImage(mDown[count%9], midC + c * dim, midR + r * dim, this);
                        }
                        if (maze[hRow + r][hCol + c].equals("C")) {
							if(hasPowerUp)
								g.drawImage(openedChest, midC + c * dim, midR + r * dim, this);
							else
								g.drawImage(closedChest, midC + c * dim, midR + r * dim, this);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                }
            }

			loadMonsters(g);
        }
        else if(gameOn && inMiniMaze && !gameOver)
        {
			miniMap(g);
		}
		else
		{
            Font font = new Font("Broadway", Font.PLAIN, 50);
            g.setColor(Color.YELLOW);
            g.setFont(font);
            g.drawString(endGameMessage, 50, 200);
        }
        switch (dir) {
            case "R":
                g.drawImage(rightImg[count % 9], this.getWidth() / 2, this.getHeight() / 2, this);
                break;

            case "L":
                g.drawImage(leftImg[count % 9], this.getWidth() / 2, this.getHeight() / 2, this);
                break;

            case "U":
                g.drawImage(upImg[count % 6], this.getWidth() / 2, this.getHeight() / 2, this);
                break;

            case "D":
                g.drawImage(downImg[count % 6], this.getWidth() / 2, this.getHeight() / 2, this);
                break;
        }
    }

    public void loadImages() {
        try {
            rightSheet = ImageIO.read(new File("Images/PenguinRight.png"));
            leftSheet = ImageIO.read(new File("Images/PenguinLeft.png"));
            upSheet = ImageIO.read(new File("Images/PenguinUp.png"));
            downSheet = ImageIO.read(new File("Images/PenguinDown.png"));
            monsterSheet = ImageIO.read(new File("Images/MonsterSheet2.png"));
            door = ImageIO.read(new File("Images/door.png"));
            blue_portal = ImageIO.read(new File("Images/blue_portal.png"));
            red_portal = ImageIO.read(new File("Images/red_portal.png"));
            yellow_portal = ImageIO.read(new File("Images/yellow_portal.png"));
            keyImage = ImageIO.read(new File("Images/key.png"));
            openedChest = ImageIO.read(new File("Images/openChest.png"));
            closedChest = ImageIO.read(new File("Images/closedChest.png"));
        } catch (IOException e) {
			System.out.println("One of the Images didn't load :\\ ");
			e.printStackTrace(); //helps find out which image didn't load
        }
        rightImg = new BufferedImage[9];
        leftImg = new BufferedImage[9];
        upImg = new BufferedImage[6];
        downImg = new BufferedImage[6];
        mRight = new BufferedImage[9];
        mLeft = new BufferedImage[9];
        mUp = new BufferedImage[9];
        mDown = new BufferedImage[9];

        for(int x=0; x<mCount.length;x++)
        	mCount[x]=x%9;

        for (int x = 8; x >= 0; x--) {
            rightImg[x] = rightSheet.getSubimage(x * 130, 0, 130, 135);
            rightImg[x] = resize(rightImg[x], dim, dim);
            leftImg[x] = leftSheet.getSubimage(x * 130, 0, 130, 135);
            leftImg[x] = resize(leftImg[x], dim, dim);

            mRight[x]=monsterSheet.getSubimage(x*64, 712, 64, 60);
            mRight[x]=resize(mRight[x], dim, dim);
            mLeft[x]=monsterSheet.getSubimage(x*64, 584, 64, 60);
            mLeft[x]=resize(mLeft[x], dim, dim);
            mUp[x]=monsterSheet.getSubimage(x*64, 520, 64, 60);
            mUp[x]=resize(mUp[x], dim, dim);
            mDown[x]=monsterSheet.getSubimage(x*64, 648, 64, 60);
            mDown[x]=resize(mDown[x], dim, dim);
        }
        for (int x = 0; x < 6; x++) {
            upImg[x] = upSheet.getSubimage(x * 130, 0, 130, 135);
            upImg[x] = resize(upImg[x], dim, dim);
            downImg[x] = downSheet.getSubimage(x * 130, 0, 130, 135);
            downImg[x] = resize(downImg[x], dim, dim);
        }

        for(int x=0; x<mRow.length; x++)
        	mDir[x]="D";

        door = resize(door, dim, dim);
        blue_portal = resize(blue_portal, dim, dim);
        red_portal = resize(red_portal, dim, dim);
        yellow_portal = resize(yellow_portal, dim, dim);
        keyImage = resize(keyImage, dim, dim);
        openedChest = resize(openedChest, dim, dim);
        closedChest = resize(closedChest, dim, dim);
    }

    public BufferedImage resize(BufferedImage image, int width, int height) {
        Image temp = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage scaledVersion = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaledVersion.createGraphics();
        g2.drawImage(temp, 0, 0, this);
        g2.dispose();
        return scaledVersion;
    }

    public void setup() {

        gameOn = true;
        gameOver = false;
        hasPowerUp = false;
        onCheatCode = false;
        inMiniMaze = false;

        endGameMessage = "EndGame Message not Given :(";

        hasKey = false;
        File file = new File("maze.txt");
        maze = new String[61][];
        int row = 0;
        try {
            BufferedReader input = new BufferedReader(new FileReader(file));
            String st;
            while ((st = input.readLine()) != null) {
                String[] rowSt = st.split("");
                maze[row] = rowSt;
                if (st.indexOf("H") >= 0) {
                    hRow = row;
                    hCol = st.indexOf("H");
                }
                if (st.indexOf("M") >= 0) {
					mRow[0] = row;
					mCol[0] = st.indexOf("M");
               	}
                if (st.indexOf("E") >= 0) {
                    endRow = row;
                    endCol = st.indexOf("E");
                }
                if (st.indexOf("P") >= 0) {
                    portalRow = row;
                    portalCol = st.indexOf("P");
                }

                if (st.indexOf("C") >= 0) {
				    chestRow = row;
				    chestCol = st.indexOf("C");
                }

                row++;
            }
        } catch (IOException e) {
        }
        for(int x=0;x<numMonsters;x++)
        {
			do
			{
				mRow[x]=(int)(Math.random()*maze.length);
				mCol[x]=(int)(Math.random()*maze[mRow[x]].length);
			}while(!maze[mRow[x]][mCol[x]].equals(" "));
			maze[mRow[x]][mCol[x]]=""+x;
		}



        miniMapSetup();
    }

    public void miniMapSetup()
    {
		//No Monsters so like this isn't really needed
        hasPowerUp = false;
        onCheatCode = false;

		int startPortalRow=0;
		int startPortalCol=0;

		 		File file = new File("mini-maze.txt");
		        miniMaze = new String[15][];
		        int row = 0;
		        try {
		            BufferedReader input = new BufferedReader(new FileReader(file));
		            String st;
		            while ((st = input.readLine()) != null) {
		                String[] rowSt = st.split("");
		                miniMaze[row] = rowSt;
		                if (st.indexOf("H") >= 0) {
		                    hRow2 = row;
		                    hCol2 = st.indexOf("H");
		                }
		                if (st.indexOf("M") >= 0) {
							mRow[0] = row;
							mCol[0] = st.indexOf("M");
               			 }
		                if (st.indexOf("P") >= 0) {
		                    finishRow = row;
		                    finishCol = st.indexOf("P");
		                }
		                if (st.indexOf("p") >= 0) {
		                    startRow = row;
		                    startCol = st.indexOf("p");
		                }
						if (st.indexOf("K") >= 0) {
                   			keyRow = row;
                    		keyCol = st.indexOf("K");
                		}
		                row++;

		            }
		        } catch (IOException e) {
					//System.out.println("Can't find the places in the Mini Map (like keys portals, monsters etc)");
		        }


    }

    public void miniMap(Graphics g)
    {

			if (gameOn) {

				inMiniMaze = true;


				int midR2 = this.getHeight() / 2;
				int midC2 = this.getWidth() / 2;

				for (int r = -12; r <= 12; r++) {
					for (int c = -12; c <= 12; c++) {
						try {
							if (miniMaze[hRow2 + r][hCol2 + c].equals("#")) {
								g.setColor(Color.BLUE);
								g.fillRect(midC2 + c * dim, midR2 + r * dim, dim, dim);
							}
							if (miniMaze[hRow2 + r][hCol2 + c].equals("E") && hasKey) {
								g.setColor(Color.GREEN);
								g.drawImage(door, midC2 + c * dim, midR2 + r * dim, this);
							}
							if (miniMaze[hRow2 + r][hCol2 + c].equals("K")) {
								if(!hasKey)
							    	g.drawImage(keyImage, midC2 + c * dim, midR2 + r * dim, this);
							}
							if (miniMaze[hRow2 + r][hCol2 + c].equals("P")) {
								if(hasKey)
								g.drawImage(yellow_portal, midC2 + c * dim, midR2 + r * dim, this);
							}
							if (miniMaze[hRow2 + r][hCol2 + c].equals("p")) {

								g.drawImage(red_portal, midC2 + c * dim, midR2 + r * dim, this);
							}


						} catch (ArrayIndexOutOfBoundsException e) {
							//System.out.println("Can't load the Places in the Mini Map (Like portals, keys, exists etc)");
						}
					}
				}
			}
	}


	public void loadMonsters(Graphics g)
	{
		for (int i = 0; i < numMonsters; i++)
		{
			int r = mRow[i] - hRow;
			int c = mCol[i] - hCol;
			int midR = this.getHeight() / 2;
			int midC = this.getWidth() / 2;

			if (r >= -12 && r <= 12 && c >= -12 && c <= 12)
			{
				switch (mDir[i])
				{
					case "U": g.drawImage(mUp[mCount[i] % 9], midC + c * dim, midR + r * dim, this); break;
					case "D": g.drawImage(mDown[mCount[i] % 9], midC + c * dim, midR + r * dim, this); break;
					case "L": g.drawImage(mLeft[mCount[i] % 9], midC + c * dim, midR + r * dim, this); break;
					case "R": g.drawImage(mRight[mCount[i] % 9], midC + c * dim, midR + r * dim, this); break;
				}
			}
		}
	}


	public void monsterMove(){

	    for (int x = 0; x < numMonsters; x++) {

	        mCount[x]++;

	        int monsterRow = mRow[x];
	        int monsterCol = mCol[x];

	        int rowDist = hRow - monsterRow;
	        int colDist = hCol - monsterCol;

	        //Monster can see
	        if (Math.abs(rowDist) <= 5 && Math.abs(colDist) <= 5) {

	            //Monster can see through col (up/down)
	            if (Math.abs(rowDist) > Math.abs(colDist)) {
	                if (rowDist < 0 && isValidMove(monsterRow - 1, monsterCol)) {
	                    mDir[x] = "U";  //up
	                    mRow[x]--;
	                } else if (rowDist > 0 && isValidMove(monsterRow + 1, monsterCol)) {
	                    mDir[x] = "D";  //down
	                    mRow[x]++;
	                }
	            }
	            //Monster can see through row (left/right)
	            else {
	                if (colDist < 0 && isValidMove(monsterRow, monsterCol - 1)) {
	                    mDir[x] = "L";  //left
	                    mCol[x]--;
	                } else if (colDist > 0 && isValidMove(monsterRow, monsterCol + 1)) {
	                    mDir[x] = "R";  //right
	                    mCol[x]++;
	                }
	            }
	        } else {
	            //just do it random (same as before)
	            int rand = (int) (Math.random() * 4);
	            switch (rand) {
	                case 0: //left
	                    if (isValidMove(monsterRow, monsterCol - 1)) {
	                        mDir[x] = "L";
	                        mCol[x]--;
	                    }
	                    break;
	                case 1: //up
	                    if (isValidMove(monsterRow - 1, monsterCol)) {
	                        mDir[x] = "U";
	                        mRow[x]--;
	                    }
	                    break;
	                case 2: //down
	                    if (isValidMove(monsterRow + 1, monsterCol)) {
	                        mDir[x] = "D";
	                        mRow[x]++;
	                    }
	                    break;
	                case 3: //right
	                    if (isValidMove(monsterRow, monsterCol + 1)) {
	                        mDir[x] = "R";
	                        mCol[x]++;
	                    }
	                    break;
	            }
	        }

	        if (mRow[x] == hRow && mCol[x] == hCol && !onCheatCode && !inMiniMaze) {
	            gameOver = true;
	            endGameMessage = "You were caught by a monster! Press r to Restart...";
	            repaint();
	        }
	    }
	}


	//probably would've been better if i had this in the start *important method*

	public boolean isValidMove(int newRow, int newCol) {
	    if (newRow < 0 || newRow >= maze.length || newCol < 0 || newCol >= maze[newRow].length) {
	        return false;
	    }
	    return !maze[newRow][newCol].equals("#");
	}


	public void gameLogic() //For all the logic parts
	{

		if (inMiniMaze || maze[hRow][hCol].equals("C")) //If user found the Chest
		{
			hasPowerUp = true;
		}
		if (inMiniMaze || maze[hRow][hCol].equals("P")) //If user found the miniMap portal
		{
			inMiniMaze = true;
		}

		if (maze[hRow][hCol].equals("p")) //If user is trying to go back using the start portal -_-
		{
			System.out.println("That's not how this works... *palm face* you can't just go back without finding the key, How else ya gonna escape?");
		}

		if (inMiniMaze && hRow2 == keyRow && hCol2 == keyCol) //If the user found the key
		{
			hasKey = true;
			System.out.println("You collected the key!");
		}


		if (inMiniMaze && hRow2 == finishRow && hCol2 == finishCol &&hasKey) //If the user is at the end portal of the miniMap
		{
			inMiniMaze = false;
			hRow = portalRow;  // or wherever you want the player to appear after returning
			hCol = portalCol-1;
		}

		if (inMiniMaze && hRow2 == finishRow && hCol2 == finishCol &&!hasKey) //If user is at the exit but without the key
		{
		 System.out.println("You probably shouldn't leave without finding the Key...");
		}

		if (maze[hRow][hCol].equals("E") && !hasKey) //If at the exit and with the key
				{
					//System.out.println("You found the Exit! But don't have the key to unlock it :( Try finding a portal..");
		}

		if (maze[hRow][hCol].equals("E") && hasKey) //If at the exit and with the key
		{
			gameOver = true;
			gameOn = false;
			endGameMessage = "You finally escaped the Maze! Press r to Restart...";
		}




	}

    public void keyPressed(KeyEvent e) {

        rlcount++;
        udcount++;

	if(gameOn)
	{
        if (e.getKeyCode() == 37) { // Left
            dir = "L";
            if(inMiniMaze && !miniMaze[hRow2][hCol2 - 1].equals("#"))
            	hCol2--;
            if(hasPowerUp && inMiniMaze && hCol2 > 0 && !miniMaze[hRow2][hCol2 - 1].equals("#"))
            	hCol2--;
            if (!inMiniMaze && !maze[hRow][hCol - 1].equals("#"))
                hCol--;
            if (hasPowerUp && !inMiniMaze && hCol > 0 && !maze[hRow][hCol - 1].equals("#"))
                hCol--;
        }
        if (e.getKeyCode() == 38) { // Up
            dir = "U";
            if (inMiniMaze && !miniMaze[hRow2 - 1][hCol2].equals("#"))
                hRow2--;
            if (hasPowerUp && inMiniMaze && hRow2 > 0 && !miniMaze[hRow2 - 1][hCol2].equals("#"))
                hRow2--;
            if (!inMiniMaze && !maze[hRow - 1][hCol].equals("#"))
                hRow--;
            if (hasPowerUp && !inMiniMaze && hRow > 0 && !maze[hRow - 1][hCol].equals("#"))
                hRow--;
        }
        if (e.getKeyCode() == 39) { // Right
            dir = "R";
            if (inMiniMaze && !miniMaze[hRow2][hCol2 + 1].equals("#"))
                hCol2++;
            if (hasPowerUp && inMiniMaze && hCol2 < miniMaze[hRow2].length - 1 && !miniMaze[hRow2][hCol2 + 1].equals("#"))
                hCol2++;
            if (!inMiniMaze && !maze[hRow][hCol + 1].equals("#"))
                hCol++;
            if (hasPowerUp && !inMiniMaze && hCol < maze[hRow].length - 1 && !maze[hRow][hCol + 1].equals("#"))
                hCol++;
        }
        if (e.getKeyCode() == 40) { // Down
            dir = "D";
            if (inMiniMaze && !miniMaze[hRow2 + 1][hCol2].equals("#"))
                hRow2++;
            if (hasPowerUp && inMiniMaze && hRow2 < miniMaze.length - 1 && !miniMaze[hRow2 + 1][hCol2].equals("#"))
                hRow2++;
            if (!inMiniMaze && !maze[hRow + 1][hCol].equals("#"))
                hRow++;
            if (hasPowerUp && !inMiniMaze && hRow < maze.length - 1 && !maze[hRow + 1][hCol].equals("#"))
                hRow++;
        }
	}

		//pause the game with (p key)

		if(e.getKeyCode() == 80)
		{
			gameOn = !gameOn;
			endGameMessage = "Game Paused! Press p to Play...";
		}

		//if game over
		if(e.getKeyCode() == 82)
		{
			if(gameOver)
			{
				setup();
				repaint();
			}
		}

		//Quit the Game

		//up left up up down right
		    //38 37 38 38 40 39


		int[] cheatCode = {38, 37, 38, 38, 40, 39};

		if (e.getKeyCode() == cheatCode[cheatIndex])
		{
		        cheatIndex++;
		        if (cheatIndex == cheatCode.length)
		        {
					if(onCheatCode) //Alr on Cheat Code
					{
						onCheatCode = false;
						hasPowerUp = false;
						System.out.println("Disabled the Cheat Code");
					}
					else
					{
						onCheatCode = true;
						hasPowerUp = true;
						System.out.println("Cheat Code Activated!");
					}
		            cheatIndex = 0;
		        }
		    } else {
		        cheatIndex = 0;
    	}



		gameLogic();

        repaint();
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args) {
        Maze app = new Maze();
    }
}
