package com.kusa.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.HashMap;
import java.util.Map;

/*
  val BlinkyScatterTarget : Point = Point(25,-3)
  val PinkyScatterTarget : Point = Point(2,-3)
  val InkyScatterTarget : Point = Point(28,35)
  val ClydeScatterTarget : Point = Point(-1,33)
  val GateTarget : Point = Point(13,11)
  val PenTarget : Point = Point(13,15)
*/
public abstract class Ghost extends Entity {

  public enum GhostState {
    ENTERINGPEN,
    INPEN,
    LEAVINGPEN,
    SCATTER,
    CHASE,
    FRIGHT,
    ATE,
  }

  /**
   * Position of targets for ghosts.
   */
  public static final Vector2 BLINKY_SCATTER_TARGET = new Vector2(
    25f,
    30f - (-3f)
  );
  public static final Vector2 PINKY_SCATTER_TARGET = new Vector2(
    2f,
    30f - (-3f)
  );

  public static final Vector2 BLINKY_ENTERINGPEN_TARGET = new Vector2(
    11f,
    30f - (14f)
  );
  public static final Vector2 PINKY_ENTERINGPEN_TARGET = new Vector2(
    12f,
    30f - (14f)
  );

  public static final Vector2 ATE_TARGET = new Vector2(13f, 30f - (11f));

  /**
   * Current target tile.
   */
  protected Vector2 target;

  /**
   * Target used when in chase state.
   */
  protected Vector2 chaseTarget;

  protected Vector2 enteringPenTarget;
  protected Vector2 scatterTarget;

  /**
   * State of ghost.
   */
  protected GhostState state;

  /**
   * State of the ghosts in game.
   *
   * Since we sometimes cannot transition to
   * scatter or chase we always keep the scatter
   * or chase reference here to refer to it.
   */
  protected GhostState gameState; //always scatter or chase.

  /**
   * Constructs a ghost entity with an x, y, speed.
   *
   * default speed if not provided is 7.5f
   *
   * the needed fields are intitlized and set to zero
   * or scaatter.
   */
  public Ghost(float x, float y) {
    this(x, y, 7.5f);
  }

  public Ghost(float x, float y, float speed) {
    super(x, y, speed);
    chaseTarget = new Vector2(0f, 0f);
    gameState = GhostState.SCATTER;
    state = GhostState.INPEN;
    target = new Vector2(0f, 0f);
    enteringPenTarget = new Vector2(0f, 0f);
    scatterTarget = new Vector2(0f, 0f);
  }

  /**
   * A ghost needs to implement how it's chase target is calculated.
   */
  public abstract void setChaseTarget(Vector2 pacPos);

  public void setChaseTarget(Vector2 pacPos, Vector2 pacVel) {
    setChaseTarget(pacPos);
  }

  /**
   * General logic update for a ghost.
   *
   * - update the ghosts target.
   * - move towards target.
   * - update the ghosts state.
   *
   * @param delta used for any physics update that are framerate independent.
   */
  @Override
  public void logic(float delta) {
    updateTarget();
    /*
     *                    MOVEMENT
     * we check to see if we can turn with a threshold of 0.1
     * meaning that if our current position is within a tile bound
     * with a 0.1 buffer.
     */
    if (canTurn(0.1f)) {
      snap();
      vel.set(calcVelocity(delta));
    }

    pos.add(vel.cpy().scl(delta));

    updateState();
  }

  protected void updateTarget() {
    switch (state) {
      case ENTERINGPEN:
        target = enteringPenTarget.cpy();
        break;
      case INPEN:
        //game is responsible for moving ghost from
        //in pen to leaving pen.
        break;
      case LEAVINGPEN:
        target = ATE_TARGET.cpy();
        break;
      case SCATTER:
        target = scatterTarget.cpy();
        break;
      case CHASE:
        //game is responsible for updating the
        //chase target.
        target = chaseTarget.cpy();
        break;
      case FRIGHT:
        //the random fright target is a lil nasty
        //but it works.
        float randX = (float) Math.random() * 27 + 1;
        float randY = (float) Math.random() * 30 + 1;
        target = new Vector2(randX, randY);
        break;
      case ATE:
        target = ATE_TARGET.cpy();
        break;
    }
  }

  protected void updateState() {
    boolean reachedTarget = reachedTarget();
    switch (state) {
      case ENTERINGPEN:
        if (reachedTarget) state = GhostState.INPEN;
        break;
      case INPEN:
        //game is responsible for moving ghost from
        //in pen to leaving pen.
        break;
      case LEAVINGPEN:
        if (reachedTarget) {
          System.out.println("LEAVING THE PEN");
          state = gameState;
        }
        break;
      case SCATTER:
        if (gameState != GhostState.SCATTER) {
          vel.scl(-1);
          state = gameState;
        }
        break;
      case CHASE:
        if (gameState != GhostState.CHASE) {
          vel.scl(-1);
          state = gameState;
        }
        break;
      case FRIGHT:
        //game is responsible for setting and unsetting
        //the frightened mode on ghosts.
        break;
      case ATE:
        if (reachedTarget) state = GhostState.ENTERINGPEN;
        break;
    }
  }

  protected boolean reachedTarget() {
    return pos.dst(target) < 1f;
  }

  /**
   * Checks to see if ghost can change direction.
   *
   * @return true if the difference between the current
   * position and the nearst tile is LESS than threshold
   */
  protected boolean canTurn(float threshold) {
    return (
      (Math.abs(pos.x - Math.round(pos.x)) < threshold) &&
      (Math.abs(pos.y - Math.round(pos.y)) < threshold)
    );
  }

  /**
   * Will first calculate the next valid moves and then determine
   * which one is the closest to our target and return the velocity
   * that will bring us there.
   *
   * Uses the currrent target when calculating distance.
   *
   * if no valid moves are found the current velocity
   * is returned.
   *
   * TODO maybe improve this method!
   * alt name - move toward target?
   *
   * @param delta used to scale the movement applied to our position.
   * @return Vector2 the updated velocity.
   */
  protected Vector2 calcVelocity(float delta) {
    Map<Float, Vector2> distToVel = new HashMap<>();
    Vector2 opp = vel.cpy().scl(-1).nor();

    boolean ignoreGate =
      (state == GhostState.LEAVINGPEN || state == GhostState.ENTERINGPEN);

    //up down left right.
    Vector2 upVel = new Vector2(0f, 1f).nor().scl(speed);
    Vector2 upPos = pos.cpy().add(upVel.cpy().scl(delta));
    if (!collidesWithWall(upPos, ignoreGate) && !opp.epsilonEquals(0f, 1f)) {
      float dist = upPos.dst(target);
      distToVel.put(dist, upVel);
    }

    Vector2 leftVel = new Vector2(-1f, 0f).scl(speed);
    Vector2 leftPos = pos.cpy().add(leftVel.cpy().scl(delta));
    if (!collidesWithWall(leftPos, ignoreGate) && !opp.epsilonEquals(-1f, 0f)) {
      float dist = leftPos.dst(target);
      if (!distToVel.containsKey(dist)) distToVel.put(dist, leftVel);
    }

    Vector2 downVel = new Vector2(0f, -1f).scl(speed);
    Vector2 downPos = pos.cpy().add(downVel.cpy().scl(delta));
    if (!collidesWithWall(downPos, ignoreGate) && !opp.epsilonEquals(0f, -1f)) {
      float dist = downPos.dst(target);
      if (!distToVel.containsKey(dist)) distToVel.put(dist, downVel);
    }

    Vector2 rightVel = new Vector2(1f, 0f).scl(speed);
    Vector2 rightPos = pos.cpy().add(rightVel.cpy().scl(delta));
    if (!collidesWithWall(rightPos, ignoreGate) && !opp.epsilonEquals(1f, 0f)) {
      float dist = rightPos.dst(target);
      if (!distToVel.containsKey(dist)) distToVel.put(dist, rightVel);
    }

    float shortest = Float.MAX_VALUE;
    for (float dist : distToVel.keySet()) {
      shortest = Math.min(shortest, dist);
    }

    return distToVel.getOrDefault(shortest, vel.cpy());
  }

  /**
   * Sets the ghost state to frightened if passed
   * true and ghost can be frightened.
   *
   * we also need to use the function for turning off frightened
   * mode, since the ghosts cannot tell on their own when they
   * should or shouldn't be frightened.
   *
   * @param fright true if you are attempting to set a frightened state.
   */
  public void setFrightened(boolean fright) {
    if (!fright && isFrightened()) {
      state = gameState;
      return;
    }

    boolean canBeFrightened =
      (state == GhostState.SCATTER || state == GhostState.CHASE) &&
      state != GhostState.FRIGHT;

    if (fright && canBeFrightened) {
      vel.scl(-1);
      state = GhostState.FRIGHT;
    }
  }

  /**
   * Sets the ghost state to ate if possible.
   */
  public void setAte() {
    if (isFrightened()) state = GhostState.ATE;
  }

  /**
   * Sets the ghost state to leaving pen if possible.
   */
  public void setLeavingPen() {
    if (inPen()) state = GhostState.LEAVINGPEN;
  }

  /**
   * Returns true if ghost state is INPEN
   *
   * @return true if this.state == GhostState.INPEN
   */
  public boolean inPen() {
    return state == GhostState.INPEN;
  }

  /**
   * Returns true if ghost state is FRIGHT
   *
   * @return true if this.state == GhostState.FRIGHT
   */
  public boolean isFrightened() {
    return state == GhostState.FRIGHT;
  }

  /**
   * Returns true if ghost state is ATE
   *
   * @return true if this.state == GhostState.ATE
   */
  public boolean isAte() {
    return state == GhostState.ATE;
  }

  /**
   * Returns the vector 2 representing this ghosts current
   * target.
   *
   * @return vector2 representing current target.
   */
  public Vector2 getTarget() {
    return this.target.cpy();
  }

  /**
   * Sets the game state that the ghosts use as a reference.
   *
   * the game state should only ever be scatter or chase.
   * the ghost takes extra precaution to make sure it's only
   * set as such.
   *
   * @param pState the scatter or chase state you want the ghosts to be in.
   */
  public void setGameState(GhostState pState) {
    if (pState == GhostState.SCATTER || pState == GhostState.CHASE) {
      gameState = pState;
    }
  }

  @Override
  public String toString() {
    return "Ghost Target: " + target + " Ghost State: " + state;
  }
}
