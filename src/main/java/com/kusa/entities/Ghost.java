package com.kusa.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.kusa.util.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ghost extends Entity implements Drawable {

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

  private TextureRegion[] upSprites;
  private TextureRegion[] downSprites;
  private TextureRegion[] leftSprites;
  private TextureRegion[] rightSprites;
  private TextureRegion[] ateSprites;
  private TextureRegion[] frightSprites;

  private float stateTime;

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
    this(
      x,
      y,
      speed,
      initialState,
      initialGameState,
      scatterTarget,
      enteringPenTarget,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }

  public Ghost(
    float x,
    float y,
    float speed,
    GhostState initialState,
    GhostState initialGameState,
    Vector2 scatterTarget,
    Vector2 enteringPenTarget,
    TextureRegion[] upSprites,
    TextureRegion[] downSprites,
    TextureRegion[] leftSprites,
    TextureRegion[] rightSprites,
    TextureRegion[] ateSprites,
    TextureRegion[] frightSprites
  ) {
    super(x, y, speed);
    //these fields should be updated.
    target = new Vector2(0f, 0f); //this class chooses a target based on state.
    chaseTarget = new Vector2(0f, 0f); //should be set by game.
    safePos = new Vector2(0f, 0f); //this class handles this on its own.

    this.stateTime = 0f;
    this.state = initialState;
    this.gameState = initialGameState;
    this.scatterTarget = scatterTarget;
    this.enteringPenTarget = enteringPenTarget;

    this.upSprites = upSprites;
    this.downSprites = downSprites;
    this.leftSprites = leftSprites;
    this.rightSprites = rightSprites;
    this.ateSprites = ateSprites;
    this.frightSprites = frightSprites;
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
  public void logic(float delta, Point[] collisionPoints) {
    updateTarget();

    //we pass a filtered out collision point for the ghost
    //since we wanna bypass collisions like the ghost gate at times.
    Point[] walls = getWalls(collisionPoints);

    updateVelocity(delta, walls);

    //store safe pos
    if (!collidesWithPoints(pos, walls)) {
      safePos.set(pos.cpy());
      safePos.x = Math.round(safePos.x);
      safePos.y = Math.round(safePos.y);
    }

    pos.add(vel.cpy().scl(delta));
    if (collidesWithPoints(pos, walls)) pos.set(safePos.cpy());

    stateTime += delta;
    updateState();
  }

  @Override
  public void draw(Batch batch) {
    final float scale = 1.5f;
    final float xOffset = -0.25f;
    final float yOffset = -0.25f;
    Vector2 dir = vel.cpy().nor();
    boolean goingUp = dir.x == 0 && dir.y == 1;
    boolean goingDown = dir.x == 0 && dir.y == -1;
    boolean goingLeft = dir.x == -1 && dir.y == 0;
    boolean goingRight = dir.x == 1 && dir.y == 0;

    float intervalSpeed = 0.15f;
    int index = (int) (stateTime / intervalSpeed);
    if (isFrightened() && frightSprites != null) {
      intervalSpeed = 0.5f; // or some decided flash interval.
      index %= frightSprites.length;
      batch.draw(frightSprites[index], pos.x, pos.y, 1.5f, 1.5f);
    } else if (isAte() && ateSprites.length >= 4) {
      //down, left, right, up;
      if (goingDown) index = 0;
      if (goingLeft) index = 1;
      if (goingRight) index = 2;
      else index = 3;
      batch.draw(
        ateSprites[index],
        pos.x + xOffset,
        pos.y + yOffset,
        scale,
        scale
      );
    } else if (goingUp && upSprites != null) {
      index %= upSprites.length;
      batch.draw(
        upSprites[index],
        pos.x + xOffset,
        pos.y + yOffset,
        scale,
        scale
      );
    } else if (goingDown && downSprites != null) {
      index %= downSprites.length;
      batch.draw(
        downSprites[index],
        pos.x + xOffset,
        pos.y + yOffset,
        scale,
        scale
      );
    } else if (goingLeft && leftSprites != null) {
      index %= leftSprites.length;
      batch.draw(
        leftSprites[index],
        pos.x + xOffset,
        pos.y + yOffset,
        scale,
        scale
      );
    } else if (goingRight && rightSprites != null) {
      index %= rightSprites.length;
      batch.draw(
        rightSprites[index],
        pos.x + xOffset,
        pos.y + yOffset,
        scale,
        scale
      );
    } else if (upSprites != null) {
      //draw a solid sprite if no direction was matched.
      index = 0;
      batch.draw(
        upSprites[index],
        pos.x + xOffset,
        pos.y + yOffset,
        scale,
        scale
      );
    }
  }

  @Override
  protected void updateVelocity(float delta, Point[] collisionPoints) {
    /*
     *                    MOVEMENT
     * we check to see if we can turn with a threshold of 0.1
     * meaning that if our current position is within a tile bound
     * with a 0.1 buffer.
     */
    float threshold = 0.05f;
    if (speed >= (Ghost.FULL_SPEED * 1.25f)) threshold = 0.1f;
    if (canTurn(threshold)) {
      pos.set(snap(pos));
      vel.set(findNextVelocity(delta, collisionPoints));
    }
  }

  /**
   * we are hardcoding the ghost gates coordinates here
   * so maybe come back to this.
   *
   *
   * @param collisionPoints the collision points given from logic method.
   */
  private Point[] getWalls(Point[] collisionPoints) {
    boolean ignoreGate =
      (state == GhostState.LEAVINGPEN || state == GhostState.ENTERINGPEN);
    Point[] walls = ignoreGate
      ? Arrays.stream(collisionPoints)
        .filter(wall ->
          ((wall.getX() != 13f && wall.getY() != (30f - 12f)) &&
            (wall.getX() != 14f && wall.getY() != (30f - 12f)))
        )
        .toArray(Point[]::new)
      : collisionPoints;
    return walls;
  }

  /**
   * rounding the points and checking equality worked well for the ghosts.
   */
  @Override
  protected boolean collidesWithPoints(Vector2 pos_, Point[] collisionPoints) {
    Rectangle posRect = new Rectangle(pos_.x, pos_.y, 1f, 1f);
    for (Point point : collisionPoints) if (
      (Math.round(pos_.x) == Math.round(point.getX())) &&
      (Math.round(pos_.y) == Math.round(point.getY()))
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
  private Vector2 findNextVelocity(float delta, Point[] walls) {
    Map<Float, Vector2> distToVel = new HashMap<>();
    Vector2 opp = vel.cpy().scl(-1).nor();

    Vector2 upPos = pos.cpy().add(0f, 1f);
    Vector2 upVel = new Vector2(0f, 1f).scl(speed);
    if (!collidesWithPoints(upPos, walls) && !opp.epsilonEquals(0f, 1f)) {
      float dist = upPos.dst(target);
      distToVel.put(dist, upVel);
    }
    Vector2 leftPos = pos.cpy().add(-1f, 0f);
    Vector2 leftVel = new Vector2(-1f, 0f).scl(speed);
    if (!collidesWithPoints(leftPos, walls) && !opp.epsilonEquals(-1f, 0f)) {
      float dist = leftPos.dst(target);
      distToVel.put(dist, leftVel);
    }

    Vector2 downPos = pos.cpy().add(0f, -1f);
    Vector2 downVel = new Vector2(0f, -1f).scl(speed);
    if (!collidesWithPoints(downPos, walls) && !opp.epsilonEquals(0f, -1f)) {
      float dist = downPos.dst(target);
      distToVel.put(dist, downVel);
    }

    Vector2 rightPos = pos.cpy().add(1f, 0f);
    Vector2 rightVel = new Vector2(1f, 0f).scl(speed);
    if (!collidesWithPoints(rightPos, walls) && !opp.epsilonEquals(1f, 0f)) {
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
