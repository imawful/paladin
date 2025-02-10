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
  public static final float FULL_SPEED = 7.5f;

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

  private Vector2 safePos;

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
    float speed,
    GhostState initialState,
    GhostState initialGameState,
    Vector2 scatterTarget,
    Vector2 enteringPenTarget
  ) {
    super(x, y, speed);
    //these fields should be updated.
    target = new Vector2(0f, 0f); //this class chooses a target based on state.
    chaseTarget = new Vector2(0f, 0f); //should be set by game.
    safePos = new Vector2(0f, 0f); //this class handles this on its own.

    this.state = initialState;
    this.gameState = initialGameState;
    this.scatterTarget = scatterTarget;
    this.enteringPenTarget = enteringPenTarget;
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
    /*
     *                    MOVEMENT
     * we check to see if we can turn with a threshold of 0.1
     * meaning that if our current position is within a tile bound
     * with a 0.1 buffer.
     */
    float threshold = 0.05f;
    if (speed >= (Ghost.FULL_SPEED * 1.25f)) threshold = 0.1f;
    if (canTurn(threshold)) {
      snap();
      vel.set(calcVelocity(delta));
    }

    Point[] walls = getWalls();
    if (!collidesWithWall(pos, walls)) {
      safePos.set(pos.cpy());
      safePos.x = Math.round(safePos.x);
      safePos.y = Math.round(safePos.y);
    }

    pos.add(vel.cpy().scl(delta));

    if (collidesWithWall(pos, walls)) pos.set(safePos.cpy());

    updateState();
  }

  private Point[] getWalls() {
    boolean ignoreGate =
      (state == GhostState.LEAVINGPEN || state == GhostState.ENTERINGPEN);
    Point[] walls = ignoreGate
      ? Arrays.stream(Entity.walls)
        .filter(wall ->
          ((wall.getX() != 13f && wall.getY() != (30f - 12f)) &&
            (wall.getX() != 14f && wall.getY() != (30f - 12f)))
        )
        .toArray(Point[]::new)
      : Entity.walls;
    return walls;
  }

  @Override
  protected boolean collidesWithWall(Vector2 pos_, Point[] walls) {
    Rectangle posRect = new Rectangle(pos_.x, pos_.y, 1f, 1f);
    for (Point w : walls) if (
      (Math.round(pos_.x) == Math.round(w.getX())) &&
      (Math.round(pos_.y) == Math.round(w.getY()))
    ) return true;
    return false;
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
        if (reachedTarget) state = GhostState.INPEN;
        break;
      case INPEN:
        //game is responsible for moving ghost from
        //in pen to leaving pen.
        break;
      case LEAVINGPEN:
        if (reachedTarget) {
          pos.set(ATE_TARGET);
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

  private boolean reachedTarget() {
    return pos.dst(target) < 1f;
  }

  /**
   * Checks to see if ghost can change direction.
   *
   * @return true if the difference between the current
   * position and the nearst tile is LESS than threshold
   */
  private boolean canTurn(float threshold) {
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
   * TODO find a way to prioritze from up, to left, to down, to right
   *
   * alt name - move toward target?
   *
   * @param delta used to scale the movement applied to our position.
   * @return Vector2 the updated velocity.
   */
  private Vector2 calcVelocity(float delta) {
    Map<Float, Vector2> distToVel = new HashMap<>();

    Vector2 opp = vel.cpy().scl(-1).nor();

    Point[] walls = getWalls();

    Vector2 upPos = pos.cpy().add(0f, 1f);
    Vector2 upVel = new Vector2(0f, 1f).scl(speed);
    if (!collidesWithWall(upPos, walls) && !opp.epsilonEquals(0f, 1f)) {
      float dist = upPos.dst(target);
      distToVel.put(dist, upVel);
    }
    Vector2 leftPos = pos.cpy().add(-1f, 0f);
    Vector2 leftVel = new Vector2(-1f, 0f).scl(speed);
    if (!collidesWithWall(leftPos, walls) && !opp.epsilonEquals(-1f, 0f)) {
      float dist = leftPos.dst(target);
      distToVel.put(dist, leftVel);
    }

    Vector2 downPos = pos.cpy().add(0f, -1f);
    Vector2 downVel = new Vector2(0f, -1f).scl(speed);
    if (!collidesWithWall(downPos, walls) && !opp.epsilonEquals(0f, -1f)) {
      float dist = downPos.dst(target);
      distToVel.put(dist, downVel);
    }

    Vector2 rightPos = pos.cpy().add(1f, 0f);
    Vector2 rightVel = new Vector2(1f, 0f).scl(speed);
    if (!collidesWithWall(rightPos, walls) && !opp.epsilonEquals(1f, 0f)) {
      float dist = rightPos.dst(target);
      distToVel.put(dist, rightVel);
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
