package com.crome.BreakoutGame;

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.awt.Color;
import java.awt.event.*;
import java.awt.color.*;
import java.awt.*;

@SuppressWarnings("restriction")
public class Program extends GraphicsProgram{
	
	private final int GAMEWIDTH=400;
	private final int GAMEHEIGHT=600;
	private final int BRICKSPACE=4;
	private final int NUMBEROFBRICKS=10;
	private final int BRICKWIDTH=GAMEWIDTH/NUMBEROFBRICKS-BRICKSPACE;
	private final int BRICKHEIGHT=8;
	private final int BRICKSTARTPOSITIONX=0;
	private final int BRICKSTARTPOSITIONY = 60;
	private final int BALLDIAMETER=10;
	private final int PADDLEWIDTH=60;
	private final int PADDLEHEIGHT=8;
	private final int PADDLEOFFSET=70;
	
	/** Number of turns */
	private static final int NTURNS = 3;
	
	//set the class variables for ball vectors
	private double ballVX=2;
	private double ballVY;
		
	//declare the ball and paddle objects
	private GRect paddle;
	private GOval ball;
	
	public void run(){
		for(int p=0; p < NTURNS; p++) {
			setSize(GAMEWIDTH, GAMEHEIGHT);
		
			int x=BRICKSTARTPOSITIONX;
			int y=BRICKSTARTPOSITIONY;
			
			for (int r=1; r < 11; r++){
				
				Color c = pickColor(r);
			
				for (int i=1;i<11;i++){
					
					GRect brick = new GRect(x,y,BRICKWIDTH,BRICKHEIGHT);
					brick.setFillColor(c);
					brick.setFilled(true);
					add(brick);
					x+=BRICKWIDTH + 4;
				}
				x=0;
				y+=BRICKHEIGHT + 4;
			}
			
			createPaddle();
			addKeyListeners();
			createBall();
			//it starts with a click
			waitForClick();
			moveBall();
			if(brickCounter == 0) {
				ball.setVisible(false);
				printWinner();
				break;
				}
			if(brickCounter > 0) {
				removeAll();
			}
		}
			if(brickCounter > 0) {
				printGameOver();
			}
	}
	
	private Color pickColor(int row){
			Color c=Color.red;
			switch (row)
			{
				case 1:
				case 2:
					c=Color.red;
					break;
				case 3:
				case 4:
					c=Color.orange;
					break;
				case 5:
				case 6:
					c=Color.yellow;
					break;
				case 7:
				case 8:
					c=Color.green;
					break;
				case 9:
				case 10:
					c=Color.blue;
					break;
				default:
					c=Color.gray;
					break;						
			}
			
			return c;
		}
	
	//this method creates the paddle
	private void createPaddle(){
		paddle = new GRect (GAMEWIDTH/2-PADDLEWIDTH/2,PADDLEOFFSET,  PADDLEWIDTH, PADDLEHEIGHT);
		paddle.setFillColor(Color.black);
		paddle.setFilled(true);
		add(paddle);
		addMouseListeners();
	}
	
	//this method handles the key press event
	//for the paddle
	public void keyPressed(KeyEvent e){
		double x = paddle.getX();
		double y=paddle.getY();
		
		//in the switch I try to limit the movement of the
		//paddle to the form width but it doesn't work
		//something to troubleshoot in class
		switch (e.getKeyCode()){		
			case KeyEvent.VK_RIGHT:
				if(x!=GAMEWIDTH){
					paddle.move(PADDLEWIDTH,0);
					}
					else
					{
						paddle.setLocation(GAMEWIDTH-PADDLEWIDTH, PADDLEOFFSET);
					}
					
					break;
			case KeyEvent.VK_LEFT:
				if(x!=0){
					paddle.move(-PADDLEWIDTH,0);
				}
				else
				{
					paddle.setLocation(x+PADDLEWIDTH, PADDLEOFFSET);
				}
				break;
			default:
				break;
			}
		}
		
	//this creates the ball and uses a random generator object
	//randomly choose the first angle for the ball vector
	private void createBall(){
		RandomGenerator rand = new RandomGenerator();
		ballVY=rand.nextDouble(1.0, 3.0);
		//position the ball in the center of the form
		ball=new GOval(GAMEWIDTH/2-BALLDIAMETER/2,
			    GAMEHEIGHT/2-BALLDIAMETER/2,
			    BALLDIAMETER,BALLDIAMETER);
		ball.setFillColor(Color.BLUE);
		ball.setFilled(true);
		add(ball);

	}
	
	private void moveBall(){
		
		boolean keepGoing=true;
		
		while(keepGoing){
			   //this makes it so when the ball hits the edges
			   //it reverse direction
			   if(ball.getX()>=GAMEWIDTH-BALLDIAMETER || ball.getX() <=0){
				   ballVX=-ballVX;
			   }
			   //same for top and bottom
			   if (ball.getY()>=GAMEHEIGHT-BALLDIAMETER || ball.getY() <= 0){
				   ballVY = -ballVY;
			   }
			   //this checks for the location of the paddle. If it and the ball's
			   //coordinates are the same, it bounces off the paddle
			   if (getElementAt(ball.getX() + BALLDIAMETER, ball.getY()+BALLDIAMETER)== paddle){
				   ballVY=-ballVY;
			   }
			   
			   //if the ball goes below the paddle we exit the loop
			   //and end the game
			   if (ball.getY() > paddle.getY()){
				   keepGoing=false;
			   }
			   //check for other objects
			   GObject collider = getCollidingObject();
			   
			   if (collider == paddle) {
			   /* We need to make sure that the ball only bounces off the top part of the paddle
			   * and also that it doesn't "stick" to it if different sides of the ball hit the paddle quickly and get the ball "stuck" on the paddle.
			   * I ran "println ("vx: " + vx + ", vy: " + vy + ", ballX: " + ball.getX() + ", ballY: " +ball.getY());"
			   * and found that the ball.getY() changes by 4 every time, instead of 1,
			   * so it never reaches exactly the the height at which the ball hits the paddle (paddle height + ball height),
			   * therefore, I estimate the point to be greater or equal to the height at which the ball hits the paddle,
			   * but less than the height where the ball hits the paddle minus 4.
			   */
			   if(ball.getY() >= getHeight() - PADDLEOFFSET - PADDLEHEIGHT - BALLDIAMETER*2 && ball.getY() < getHeight() - PADDLEOFFSET - PADDLEHEIGHT - BALLDIAMETER*2 + 4) {
				   ballVY = -ballVY;
			   }
			   }
			   //since we lay down a row of bricks, the last brick in the brick wall is assigned the value brick.
			   //so we narrow it down by saying that the collier does not equal to a paddle or null,
			   //so all that is left is the brick
			   else if (collider != null) {
				   remove(collider);
				   brickCounter--;
				   ballVY = -ballVY;
			   }
			   //move the ball
			   //ball.move(ballVX, ballVY);
			   //slight pause between positions 
			   //(actually pretty slow)
			   //pause(8);
			   ball.move(ballVX, ballVY);
			   pause(8);
			   }			
	}
	
	private GObject getCollidingObject() {
		if((getElementAt(ball.getX(), ball.getY())) != null) {
			return getElementAt(ball.getX(), ball.getY());
		}
		else if (getElementAt( (ball.getX() + BALLDIAMETER*2), ball.getY()) != null ){
			return getElementAt(ball.getX() + BALLDIAMETER*2, ball.getY());
		}
		else if(getElementAt(ball.getX(), (ball.getY() + BALLDIAMETER*2)) != null ){
			return getElementAt(ball.getX(), ball.getY() + BALLDIAMETER*2);
		}
		else if(getElementAt((ball.getX() + BALLDIAMETER*2), (ball.getY() + BALLDIAMETER*2)) != null ){
			return getElementAt(ball.getX() + BALLDIAMETER*2, ball.getY() + BALLDIAMETER*2);
		}
		//need to return null if there are no objects present
		else{
			return null;
		}
	}
	
	private void printGameOver() {
		GLabel gameOver = new GLabel ("Game Over", getWidth()/2, getHeight()/2);
		gameOver.move(-gameOver.getWidth()/2, -gameOver.getHeight());
		gameOver.setColor(Color.RED);
		add (gameOver);
	}
	
	private int brickCounter = 100;
	private void printWinner() {
		GLabel Winner = new GLabel ("Winner!!", getWidth()/2, getHeight()/2);
		Winner.move(-Winner.getWidth()/2, -Winner.getHeight());
		Winner.setColor(Color.RED);
		add (Winner);
	}	
	
	public void mouseMoved(MouseEvent e) {
		/* The mouse tracks the middle point of the paddle.
		* If the middle point of the paddle is between half paddle width of the screen
		* and half a paddle width before the end of the screen,
		* the x location of the paddle is set at where the mouse is minus half a paddle's width,
		* and the height remains the same
		*/
		if ((e.getX() < getWidth() - PADDLEWIDTH/2) && (e.getX() > PADDLEWIDTH/2)) {
			paddle.setLocation(e.getX() - PADDLEWIDTH/2, getHeight() - PADDLEOFFSET - PADDLEHEIGHT);
		}
	}
	
}

