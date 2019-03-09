package com.javarush.games.minesweeper;

import com.javarush.engine.cell.*;
import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game{
    private static int SIDE = 10;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private int countFlags;
    private int countClosedTiles = SIDE*SIDE;
    private int score;
    private boolean isGameStopped;



    @Override
    public void initialize() {
        setScreenSize(SIDE,SIDE);
        createGame();
    }



    /**
     * create game
     */
    private void createGame(){

        int mineCount = 0;
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE ; j++) {
                boolean isMine = false;
                if ( getRandomNumber(10) == 0 ) {
                   isMine = true;
                   mineCount++;
                }
                gameField[j][i] = new GameObject(i,j,isMine);
                setCellColor(i, j, Color.ORANGE);
                setCellValue(i, j, "");
            }
        }

        countMinesOnField = mineCount;
        countFlags = countMinesOnField;
        countMineNeighbors();

    }

    /**
     * Method counts neigbors of cell
     */
    private void countMineNeighbors(){
        List<GameObject> listOfNeighbors = new ArrayList<GameObject>();
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE ; j++) {
                if( !gameField[j][i].isMine) {
                    listOfNeighbors = getNeighbors(gameField[j][i]);
                    for ( GameObject object : listOfNeighbors) {
                        if(object.isMine) {
                            gameField[j][i].countMineNeighbors++;
                        }
                    }
                }

            }
        }

    }


    /**
     * Getting sell neighbors
     * @param gameObject - cell of gamefield
     * @return Arraylist of neighbors
     */
    private List<GameObject> getNeighbors(GameObject gameObject){
        List<GameObject> listOfNeighbors = new ArrayList<GameObject>();
        for (int i = gameObject.y - 1; i <= gameObject.y + 1 ; i++) {
            for (int j = gameObject.x - 1; j <= gameObject.x + 1 ; j++) {
                if ( ( i!=gameObject.y || j!= gameObject.x) && ( i >= 0 && j >= 0 && i < SIDE && j < SIDE)) {
                    listOfNeighbors.add(gameField[i][j]);
                }
            }
        }
        return listOfNeighbors;
    }

    /**
     * Opening cells
     * @param x - coordinate X
     * @param y - coordinate Y
     */
    private void openTile(int x, int y){
      if (gameField[y][x].isOpen || gameField[y][x].isFlag || isGameStopped) {
      } else {
          gameField[y][x].isOpen = true;
          countClosedTiles--;
          setCellColor(x, y, Color.AQUA);
          if(gameField[y][x].isMine) {
              setCellValueEx(x, y, Color.RED, MINE);
              gameOver();
          } else if ( countClosedTiles == countMinesOnField) {
              win();
          } else if(!gameField[y][x].isMine && gameField[y][x].countMineNeighbors == 0) {
              score = score + 5;
              setCellValue(x, y, "");
              List<GameObject> listOfNeighbors = new ArrayList<GameObject>();
              listOfNeighbors = getNeighbors(gameField[y][x]);
              for ( GameObject object : listOfNeighbors) {
                  if(!object.isOpen){
                      openTile(object.x, object.y);
                  }
              }
          } else if ( !gameField[y][x].isMine && gameField[y][x].countMineNeighbors != 0) {
              setCellNumber(x, y, gameField[y][x].countMineNeighbors);
              score = score + 5;
          }
          setScore(score);
      }

    }

    /**
     * Method marks sell as a flag and dismark
     * @param x - coordinates X
     * @param y - coordinates Y
     */
    private void markTile(int x , int y){
       if (gameField[y][x].isOpen || (countFlags == 0 && !gameField[y][x].isFlag) || isGameStopped) {
       } else if (gameField[y][x].isFlag) {
           gameField[y][x].isFlag = false;
           setCellColor(x, y, Color.ORANGE);
           setCellValue(x, y, "");
           countFlags++;
       }
       else if (!gameField[y][x].isFlag){
           gameField[y][x].isFlag = true;
           setCellColor(x, y, Color.YELLOW);
           setCellValue(x, y, FLAG);
           countFlags--;
       }
    }

    /**
     * Method sets variable isGameStopped true value
     * and stops the game
     */
    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.YELLOW, "Game over, retard", Color.BLACK, 25 );
    }

    /**
     * Method shows win message
     * and close game
     */
    private void win(){
        isGameStopped = true;
        showMessageDialog(Color.YELLOW, "You win, retard", Color.BLACK, 25);
    }


    /**
     * restarts game, cleans old field
     * sets another new field
     */
    private void restart() {
        isGameStopped = false;
        countClosedTiles = SIDE*SIDE;
        score = 0;
        countMinesOnField = 0;
        setScore(score);
        createGame();
    }

    /**
     * Method opens cell on click left mouse button
     * restarts, if game lost
     * @param x - coordinates X
     * @param y - coordinates Y
     */
    @Override
    public void onMouseLeftClick(int x, int y) {
        if(isGameStopped) {
            restart();
        }
        else {
            openTile(x,y);
        }
    }

    /**
     * Method flags cell on click right mouse button
     * @param x - coordinates X
     * @param y - coordinates Y
     */
    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x,y);
    }
}

