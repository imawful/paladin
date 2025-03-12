package com.kusa;

import com.kusa.entities.Ghost;
import com.kusa.entities.Ghost.GhostState;
import com.kusa.entities.Maze;

public class GameState {

  private float ghostStateTime;
  private float ghostStateDuration;
  private GhostState ghostState;

  private float ghostStateTimeTmp;
  private float ghostStateDurationTmp;
  private GhostState ghostStateTmp;

  private LevelData levelData;
  private int scatterChaseIndex;

  private boolean pacIsEating;
  private float pacChompTime;

  private boolean starting;
  private float startTime;
  private float startDuration;

  /**
   * maybe make this class manage the game state.
   *
   * it's already being told what to do by the main game.
   */
  public GameState(LevelData ld) {
    levelData = ld;
    scatterChaseIndex = 0;

    ghostStateTime = 0f;
    ghostStateDuration = levelData.getScatterChaseIntervals()[0];
    ghostState = GhostState.SCATTER;

    //tmp to zero.
    ghostStateTimeTmp = 0f;
    ghostStateDurationTmp = 0f;
    ghostStateTmp = GhostState.SCATTER;

    //entity data
    pacIsEating = false;
    pacChompTime = 0f;

    starting = true;
    startTime = 0f;
    startDuration = 5f;
  }

  public boolean isFrightState() {
    return ghostState == GhostState.FRIGHT;
  }

  /*
  public boolean isScatterState() { return ghostState == GhostState.SCATTER; }
  public boolean isChaseState() { return ghostState == GhostState.CHASE; }
  public boolean isTempScatter() { return ghostStateTmp == GhostState.SCATTER; }
  public boolean isTempChase() { return ghostStateTmp == GhostState.CHASE; }
  */

  public void restartLevel() {
    scatterChaseIndex = 0;
    ghostStateTime = 0f;
    ghostStateDuration = levelData.getScatterChaseIntervals()[0];
    ghostState = GhostState.SCATTER;

    ghostStateTimeTmp = 0f;
    ghostStateDurationTmp = 0f;
    ghostStateTmp = GhostState.SCATTER;

    pacIsEating = false;
    pacChompTime = 0f;
  }

  public void setLevelData(LevelData ld) {
    this.levelData = ld;
  }

  public void setFrightState() {
    if (ghostState != GhostState.FRIGHT) {
      ghostStateTmp = ghostState;
      ghostStateTimeTmp = ghostStateTime;
      ghostStateDurationTmp = ghostStateDuration;
    }
    ghostStateTime = 0f;
    ghostStateDuration = levelData.getFrightDuration(); //TIME FRIGHTENED
    ghostState = GhostState.FRIGHT;
  }

  public void update(float delta) {
    if (starting) {
      if (startTime >= startDuration) {
        startTime = 0f;
        starting = false;
      }
      startTime += delta;
      return;
    }

    if (ghostStateTime >= ghostStateDuration) {
      ghostStateTime = 0f;
      ghostState = nextGhostState();
    } else {
      ghostStateTime += delta;
    }

    if (pacChompTime >= 0.5f) pacIsEating = false;
    else pacChompTime += delta;
  }

  /*
   * once we pass the scatter chase index
   * we are stuck in chase mode.
   */
  private GhostState nextGhostState() {
    switch (ghostState) {
      //flip back to chase.
      case SCATTER -> {
        scatterChaseIndex += 1;
        if (
          scatterChaseIndex < levelData.getScatterChaseIntervals().length
        ) ghostStateDuration =
          levelData.getScatterChaseIntervals()[scatterChaseIndex];
        return GhostState.CHASE;
      }
      case FRIGHT -> {
        ghostStateTime = ghostStateTimeTmp;
        ghostStateDuration = ghostStateDurationTmp;

        ghostStateTimeTmp = 0f;
        ghostStateDurationTmp = 0f;

        return ghostStateTmp;
      }
    }
    //case CHASE
    scatterChaseIndex += 1;
    if (
      scatterChaseIndex >= levelData.getScatterChaseIntervals().length
    ) return GhostState.CHASE;
    ghostStateDuration =
      levelData.getScatterChaseIntervals()[scatterChaseIndex];
    return GhostState.SCATTER;
  }

  public float getNewPacSpeed() {
    float newPacSpeed = levelData.FULL_SPEED;
    if (ghostState == GhostState.FRIGHT) newPacSpeed *=
      levelData.getPacFrightSpeedMultiplier();
    else newPacSpeed *= levelData.getPacSpeedMultiplier();

    return newPacSpeed;
  }

  public float getNewGhostSpeed(Ghost ghost, Maze maze, boolean allowAngry) {
    float newGhostSpeed = levelData.FULL_SPEED;
    int dotsLeft = maze.getInitialCandyCount() - maze.getCurrentCandyCount();
    boolean angryOne =
      (allowAngry && dotsLeft <= levelData.getAngryModeOneDotLimit());
    boolean angryTwo =
      (allowAngry && dotsLeft <= levelData.getAngryModeTwoDotLimit());
    if (ghost.isAte()) newGhostSpeed *= levelData.getAteSpeedMultiplier();
    else if (
      maze.inTunnel(ghost.getPos()) || ghost.isLeavingPen() || ghost.inPen()
    ) newGhostSpeed *= levelData.getGhostTunnelSpeedMultiplier();
    else if (ghost.isFrightened()) newGhostSpeed *=
      levelData.getGhostFrightSpeedMultiplier();
    else if (angryTwo) newGhostSpeed *=
      levelData.getAngryModeTwoSpeedMultiplier();
    else if (angryOne) newGhostSpeed *=
      levelData.getAngryModeOneSpeedMultiplier();
    else newGhostSpeed *= levelData.getGhostSpeedMultiplier();

    return newGhostSpeed;
  }

  public void setGhostGameState(Ghost ghost) {
    ghost.setGameState(
      ghostState == GhostState.FRIGHT ? ghostStateTmp : ghostState
    );
  }

  public void pacAteCandy() {
    pacIsEating = true;
    pacChompTime = 0f;
  }

  public void pacAteSuper() {
    pacChompTime = 0f;
  }

  public void setStarting() {
    starting = true;
    startTime = 0f;
  }

  public boolean isStarting() {
    return starting;
  }

  public boolean getPacIsEating() {
    return this.pacIsEating;
  }
}
