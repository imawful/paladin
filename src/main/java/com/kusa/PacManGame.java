package com.kusa;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.kusa.entities.Entity;
import com.kusa.entities.Ghost;
import com.kusa.entities.Ghost.GhostState;
import com.kusa.entities.Maze;
import com.kusa.entities.Pac;

public class PacManGame {

  public static float unitScale = 1 / 20f;

  //SOUNDS.
  private Sound pacChomp;
  private long pacChompID;
  private Sound pacDeath;
  private boolean pacDeathPlaying;
  private Sound pacEatGhostSound;
  private boolean pacEatGhostPlaying;

  //game enviornment
  private Maze maze;

  //game entities
  private Pac pac;
  private Ghost[] ghosts;

  //collision rects
  private Rectangle pacRect;
  private Rectangle ghostRect;

  //holds state + level data.
  private GameState gameState;

  private boolean died;
  private float diedTime;
  private float diedDuration;

  private boolean ateGhost;
  private float ateGhostTime;
  private float ateGhostDuration;

  public PacManGame(Maze maze) {
    this.maze = maze;
    this.gameState = new GameState(
      new LevelData.LevelBuilder().buildLevelOne()
    );

    this.pac = new Pac(13f, 30f - 23f, maze);
    Ghost blinky = new Ghost(
      12f,
      30f - 11f,
      maze,
      GhostState.SCATTER,
      GhostState.SCATTER,
      new Vector2(25f, 30f - (-3f)),
      new Vector2(11f, 30f - (15f))
    );
    Ghost pinky = new Ghost(
      12f,
      30f - 14f,
      maze,
      GhostState.INPEN,
      GhostState.SCATTER,
      new Vector2(2f, 30f - (-3f)),
      new Vector2(12f, 30f - (15f))
    );
    Ghost inky = new Ghost(
      14f,
      30f - 14f,
      maze,
      GhostState.INPEN,
      GhostState.SCATTER,
      new Vector2(28f, 30f - (35f)),
      new Vector2(14f, 30f - (15f))
    );
    Ghost clyde = new Ghost(
      16f,
      30f - 14f,
      maze,
      GhostState.INPEN,
      GhostState.SCATTER,
      new Vector2(-1f, 30f - (33f)),
      new Vector2(16f, 30f - (15f))
    );
    this.ghosts = new Ghost[] { blinky, pinky, inky, clyde };

    //used to check collisions in game.
    this.pacRect = new Rectangle(pac.getPos().x, pac.getPos().y, 1f, 1f);
    this.ghostRect = new Rectangle(
      ghosts[0].getPos().x,
      ghosts[0].getPos().y,
      0.8f,
      0.8f
    );

    died = false;
    diedTime = 0f;
    diedDuration = 2f;

    ateGhost = false;
    ateGhostTime = 0f;
    ateGhostDuration = 1f;

    //MOVE THIS. we should NOT be playing sounds in this class!!!!
    this.pacChomp = Gdx.audio.newSound(
      Gdx.files.internal("sounds/pacman_chomp.wav")
    );
    this.pacChompID = -1;
    this.pacDeath = Gdx.audio.newSound(
      Gdx.files.internal("sounds/pacman_death.mp3")
    );
    this.pacDeathPlaying = false;

    this.pacEatGhostSound = Gdx.audio.newSound(
        Gdx.files.internal("sounds/pacman_eat.mp3")
    );
    this.pacEatGhostPlaying = false;
  }

  public Entity getPac() {
    return this.pac;
  }

  public Ghost[] getGhosts() {
    return this.ghosts;
  }

  public boolean pacDying() {
    return this.died;
  }

  public boolean isStarting() {
    return gameState.isStarting();
  }

  public float getDiedTime() {
    return this.diedTime;
  }

  /**
   * General game simulation update.
   */
  public void update(float delta) {
    if (died) {
      if (pacChompID != -1) pacChomp.pause(pacChompID);

      if (!pacDeathPlaying) {
        pacDeath.play();
        pacDeathPlaying = true;
      }

      if (diedTime > diedDuration) {
        diedTime = 0f;
        pacHit();
        died = false;
        pacDeathPlaying = false;
        pacDeath.stop();
      }
      diedTime += delta;
      return;
    }

    if (ateGhost) {
      if (!pacEatGhostPlaying) {
        pacEatGhostSound.play();
        pacEatGhostPlaying = true;
      }
      if (ateGhostTime > ateGhostDuration) {
        ateGhostTime = 0f;
        ateGhost = false;
        pacEatGhostPlaying = false;
        pacEatGhostSound.stop();
      }
      ateGhostTime += delta;
    }

    //updates the game state (timers, data we pull, ...)
    gameState.update(delta);
    if (gameState.isStarting()) return;

    //sets speed of pac and ghosts.
    updateSpeed();

    //moves pac and ghosts.
    updateEntities(delta);

    //teleports any entites crossing the tunnel.
    checkTunnelTeleport();

    //check to see if pac ate a candy or super candy
    //and handle state
    checkPacAteCandy();

    //SOUND
    if (pacChompID == -1 && gameState.getPacIsEating()) pacChompID =
      pacChomp.loop();

    if (gameState.getPacIsEating()) pacChomp.resume(pacChompID);
    else if (pacChompID != -1) pacChomp.pause(pacChompID);

    //check if pac hit any ghost.
    //and handle state
    checkPacAndGhostCollide();
  }

  public void dispose() {
    if (pacChomp != null) pacChomp.dispose();
    if (pacDeath != null) pacDeath.dispose();
    if (pacEatGhostSound != null) pacEatGhostSound.dispose();
  }

  private void updateSpeed() {
    //game applies speed multipliers across levels.
    pac.setSpeed(gameState.getNewPacSpeed());

    //blinky has special "angry" mode affects his speed.
    for (int i = 0; i < ghosts.length; i++) {
      Ghost ghost = ghosts[i];
      boolean allowAngry = i == 0;
      ghost.setSpeed(gameState.getNewGhostSpeed(ghost, maze, allowAngry));
    }
  }

  private void pacHit() {
    pac.setPos(pac.getSpawn());
    pac.setVel(new Vector2(0f, 0f));
    pac.setStateTime(0f);

    for (Ghost ghost : ghosts) ghost.setStart();

    gameState.setStarting();
  }

  private void updateEntities(float delta) {

    //pause moving when pac just ate a ghost.
    boolean ateTimePassed = ateGhost && (ateGhostTime >= ateGhostDuration/2); 
    if(!ateGhost || ateTimePassed)
    {
      pac.input(); //TODO fix this!!! (should be called elsewhere?)
      pac.logic(delta); //moves pac
    }

    //game has little control over
    //ghost state here including their
    //target.
    boolean ghostLeaving = false; //used for making ghosts leave 1 at a time.
    for (int i = 0; i < ghosts.length; i++) {
      Ghost ghost = ghosts[i];

      if (!pac.getVel().isZero()) ghost.setChaseTarget(getChaseTarget(i));

      gameState.setGhostGameState(ghost);

      if (
        !gameState.isFrightState() && ghost.isFrightened()
      ) ghost.setFrightened(false);

      //we spawn ghosts under different conditions.
      //for blinky here we check if the amount of canides
      //is above 0 meaning he will leave the pen as
      //soon as he enters.
      int dotLimit = 0;
      int eatenCandy =
        maze.getInitialCandyCount() - maze.getCurrentCandyCount();
      if (i == 1) dotLimit = 7;
      if (i == 2) dotLimit = 17;
      if (i == 3) dotLimit = 32;
      if (eatenCandy >= dotLimit && ghost.inPen() && !ghostLeaving) {
        ghost.setLeavingPen();
      }

      if (ghost.isLeavingPen()) ghostLeaving = true;

      //pause moving entities when pac just ate a ghost.
      if(!ateGhost || ateTimePassed || ghost.isAte()) 
        ghost.logic(delta); //moves ghost
    }
  }

  private void checkTunnelTeleport() {
    //check tunnels to teleport across.
    Rectangle leftTunnel = maze.getLeftTunnel();
    Rectangle rightTunnel = maze.getRightTunnel();

    if (pac.getPos().x < leftTunnel.x) pac.setPos(
      rightTunnel.getPosition(new Vector2()).add(rightTunnel.width - 1f, 1f)
    );
    else if (
      pac.getPos().x > ((rightTunnel.x + rightTunnel.width) - 0.5f)
    ) pac.setPos(leftTunnel.getPosition(new Vector2()).add(0f, 1f));

    for (Ghost ghost : ghosts) {
      if (ghost.getPos().x < (leftTunnel.x - 0.5f)) ghost.setPos(
        rightTunnel.getPosition(new Vector2()).add(rightTunnel.width - 1f, 1f)
      );
      else if (
        ghost.getPos().x > ((rightTunnel.x + rightTunnel.width) - 0.5f)
      ) ghost.setPos(leftTunnel.getPosition(new Vector2()).add(0f, 1f));
    }
  }

  private void checkPacAteCandy() {
    //collision between pac and points.
    if (maze.checkAndEatCandy(pac.getPos())) gameState.pacAteCandy();

    if (maze.checkAndEatSuperCandy(pac.getPos())) {
      gameState.pacAteSuper();
      gameState.setFrightState();

      //ghosts will take care of making sure
      //they will turn frightened only if possible
      for (Ghost ghost : ghosts) ghost.setFrightened(true);
    }
  }

  private void checkPacAndGhostCollide() {
    //check if pac and ghost collide.
    pacRect.setPosition(pac.getPos());
    for (Ghost ghost : ghosts) {
      ghostRect.setPosition(ghost.getPos());
      if (ghostRect.overlaps(pacRect)) {
        if ((ghost.isFrightened() || ghost.isAte()) && !ateGhost) {
          //we check atte also so no false cases 
          //ghost will handle if they can be set to ate state.
          ghost.setAte(); 
          ateGhost = true;
        }
        else {
          if(!ateGhost)
            died = true; //pacHit();//System.out.println("GAME OVERR");
        }
      }
    }
  }

  /**
   * Calculates the chase target for all ghosts.
   *
   * we access the ghost array here so make sure its not null.
   * TODO move this to ghosts AI!!!
   *
   * @param ghostIndex lets us know which target we're calculating.
   */
  private Vector2 getChaseTarget(int ghostIndex) {
    Vector2 pacPos = pac.getPos();
    Vector2 pacVel = pac.getVel();
    switch (ghostIndex) {
      case 3: //clyde
        float dist = ghosts[3].getPos().dst(pacPos);
        if (dist < 8f) return ghosts[3].getScatterTarget();
        else return pacPos.cpy();
      case 2: //inky
        Vector2 blinkyPos = ghosts[0].getPos();
        Vector2 offset = pacVel.cpy().nor();
        if (pacVel.nor().epsilonEquals(0f, 1f)) {
          offset.add(-1f, 0f);
        }
        Vector2 intermediate = pacPos.cpy().add(offset.scl(2));
        Vector2 diff = intermediate.cpy().sub(blinkyPos);
        return blinkyPos.cpy().add(diff.scl(2f));
      case 1: //pinky
        Vector2 vectorToUse = pacVel.cpy().nor();
        if (pacVel.nor().epsilonEquals(0f, 1f)) {
          vectorToUse.add(-1f, 0f);
        }
        return pacPos.cpy().add(vectorToUse.scl(2f));
      default: //blinky DEFAULT
        return pac.getPos();
    }
  }
}
