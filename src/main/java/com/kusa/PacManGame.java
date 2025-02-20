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

    //MOVE THIS. we should NOT be playing sounds in this class!!!!
    this.pacChomp = Gdx.audio.newSound(
      Gdx.files.internal("sounds/pacman_chomp.wav")
    );
    this.pacChompID = -1;
  }

  public Entity getPac() {
    return this.pac;
  }

  public Ghost[] getGhosts() {
    return this.ghosts;
  }

  /**
   * General game simulation update.
   */
  public void update(float delta) {
    //updates the game state (timers, data we pull, ...)
    gameState.update(delta);

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

  private void updateEntities(float delta) {
    pac.input(); //TODO fix this!!! (should be called elsewhere?)
    pac.logic(delta); //moves pac

    //game has little control over
    //ghost state here including their
    //target.
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
      if (eatenCandy >= dotLimit && ghost.inPen()) {
        ghost.setLeavingPen();
      }

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
        if (
          ghost.isFrightened() || ghost.isAte()
        ) ghost.setAte(); // we check atte also so no false cases //ghost will handle if they can be set to ate state.
        else System.out.println("GAME OVERR");
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
