package com.kusa.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.kusa.util.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ghost extends Entity {

  public enum GhostState {
    ENTERINGPEN,
    INPEN,
    LEAVINGPEN,
    SCATTER,
    CHASE,
    FRIGHT,
    ATE,
  }

  //maybe move (to entity??)

  //maybe move
  public static final Vector2 ATE_TARGET = new Vector2(13f, 30f - (11f));

  /**
   * Current target tile.
   */
  private Vector2 target;

  /**
   * Target used when in chase state.
   */
  private Vector2 chaseTarget;

  private final Vector2 enteringPenTarget;

  private final Vector2 scatterTarget;

  /**
   * State of ghost.
   */
  private GhostState state;

  /**
   * State of the ghosts in game.
   *
   * Since we sometimes cannot transition to
   * scatter or chase we always keep the scatter
   * or chase reference here to refer to it.
   */
  private GhostState gameState; //always scatter or chase.

  private Maze maze;

  /**
   * Constructs a ghost entity with an x, y, speed,
   * initial state, the initial game state, a final scatter and
   * entering pen target.
   *
   * if game state provided isn't scatter or chase we choose SCATTER
   *
   */
  public Ghost(
    float x,
    float y,
    Maze maze,
    GhostState initialState,
    GhostState initialGameState,
    Vector2 scatterTarget,
    Vector2 enteringPenTarget
  ) {
    super(x, y);
    //these fields should be updated.
    target = new Vector2(0f, 0f); //this class chooses a target based on state.
    chaseTarget = new Vector2(0f, 0f); //should be set by game.

    this.state = initialState;
    this.gameState = initialGameState;
    this.scatterTarget = scatterTarget;
    this.enteringPenTarget = enteringPenTarget;
    this.maze = maze;
  }

  public void setChaseTarget(Vector2 target) {
    this.chaseTarget.set(target);
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

    //determine threshold based on speed.
    float threshold = 0.05f;
    if (speed >= (7.5f * 1.25f)) threshold = 0.1f;

    if (canSnap(threshold)) pos.set(snap(pos));

    vel.set(findNextVelocity(delta));
    boolean ignoreGate =
      (state == GhostState.LEAVINGPEN || state == GhostState.ENTERINGPEN);

    //if move is invalid we snap back.
    pos.add(vel.cpy().scl(delta));
    if (maze.overlapsWall(pos, ignoreGate)) pos.set(snap(pos));

    stateTime += delta;
    updateState();
  }

  private void updateTarget() {
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

  private void updateState() {
    boolean reachedTarget = reachedTarget();
    switch (state) {
      case ENTERINGPEN:
        if (reachedTarget) {
          state = GhostState.INPEN;
          stateTime = 0f;
        }
        break;
      case INPEN:
        //game is responsible for moving ghost from
        //in pen to leaving pen.
        break;
      case LEAVINGPEN:
        if (reachedTarget) {
          pos.set(ATE_TARGET);
          state = gameState;
          stateTime = 0f;
        }
        break;
      case SCATTER:
        if (gameState != GhostState.SCATTER) {
          vel.scl(-1);
          state = gameState;
          stateTime = 0f;
        }
        break;
      case CHASE:
        if (gameState != GhostState.CHASE) {
          vel.scl(-1);
          state = gameState;
          stateTime = 0f;
        }
        break;
      case FRIGHT:
        //game is responsible for setting and unsetting
        //the frightened mode on ghosts.
        break;
      case ATE:
        if (reachedTarget) {
          pos.set(ATE_TARGET);
          state = GhostState.ENTERINGPEN;
          stateTime = 0f;
        }
        break;
    }
  }

  private boolean reachedTarget() {
    return pos.dst(target) < 1f;
  }

  /**
   * Will first calculate the next valid moves and then determine
   * which one is the closest to our target and return the velocity
   * that will bring us there (or the old velocity).
   *
   * @param delta used to scale the movement applied to our position.
   * @return Vector2 the updated velocity.
   */
  private Vector2 findNextVelocity(float delta) {
    Map<Float, Vector2> distToVel = new HashMap<>();

    Vector2 upVel = new Vector2(0f, 1f).scl(speed);
    if (validNextVel(upVel, delta)) {
      float dist = pos.cpy().add(upVel.cpy().scl(delta)).dst(target);
      distToVel.put(dist, upVel);
    }

    Vector2 leftVel = new Vector2(-1f, 0f).scl(speed);
    if (validNextVel(leftVel, delta)) {
      float dist = pos.cpy().add(leftVel.cpy().scl(delta)).dst(target);
      distToVel.put(dist, leftVel);
    }

    Vector2 downVel = new Vector2(0f, -1f).scl(speed);
    if (validNextVel(downVel, delta)) {
      float dist = pos.cpy().add(downVel.cpy().scl(delta)).dst(target);
      distToVel.put(dist, downVel);
    }

    Vector2 rightVel = new Vector2(1f, 0f).scl(speed);
    if (validNextVel(rightVel, delta)) {
      float dist = pos.cpy().add(rightVel.cpy().scl(delta)).dst(target);
      distToVel.put(dist, rightVel);
    }

    float shortest = Float.MAX_VALUE;
    for (float dist : distToVel.keySet()) {
      shortest = Math.min(shortest, dist);
    }

    return distToVel.getOrDefault(shortest, vel.cpy());
  }

  private boolean validNextVel(Vector2 vel_, float delta) {
    boolean ignoreGate =
      (state == GhostState.LEAVINGPEN || state == GhostState.ENTERINGPEN);
    Vector2 opp = vel.cpy().scl(-1).nor();
    Vector2 nPos = pos.cpy().add(vel_.cpy().scl(delta));
    boolean backwardCondition = !opp.epsilonEquals(vel_.cpy().nor());
    boolean wallCondition = !maze.overlapsWall(nPos, ignoreGate);
    return (wallCondition && backwardCondition);
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
      stateTime = 0f;
      return;
    }

    boolean canBeFrightened =
      (state == GhostState.SCATTER || state == GhostState.CHASE) &&
      state != GhostState.FRIGHT;

    if (fright && canBeFrightened) {
      vel.scl(-1);
      state = GhostState.FRIGHT;
      stateTime = 0f;
    }
  }

  /**
   * Sets the ghost state to ate if possible.
   */
  public void setAte() {
    if (isFrightened()) {
      state = GhostState.ATE;
      stateTime = 0f;
    }
  }

  /**
   * Sets the ghost state to leaving pen if possible.
   */
  public void setLeavingPen() {
    if (inPen()) {
      state = GhostState.LEAVINGPEN;
      stateTime = 0f;
    }
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

  public Vector2 getScatterTarget() {
    return this.scatterTarget.cpy();
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
