package com.kusa.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.kusa.util.Point;

public class Pac extends Entity implements Inputable, Drawable {

  /**
   * Vector used to track the next direction of pac.
   */
  private Vector2 nextPos;

  /**
   * Pac has 4 different directions it can travel so
   * we have space for 4 different animations.
   */
  private TextureRegion[] upSprites, downSprites, leftSprites, rightSprites;

  /**
   * State time we use to track time for things like drawing.
   *
   * any time pac changes velocity this time is reset.
   */
  private float stateTime;

  public Pac(float x, float y, float speed) {
    this(x, y, speed, null, null, null, null);
  }

  /**
   * Constructs a pac entity with an x, y, speed,
   * and it's sprite sheets.
   */
  public Pac(
    float x,
    float y,
    float speed,
    TextureRegion[] pUp,
    TextureRegion[] pDown,
    TextureRegion[] pLeft,
    TextureRegion[] pRight
  ) {
    super(x, y, speed);
    nextPos = new Vector2(0f, 0f);

    stateTime = 0f;
    upSprites = pUp;
    downSprites = pDown;
    leftSprites = pLeft;
    rightSprites = pRight;
  }

  /**
   * Input handler for Pac.
   *
   * Really basic we just check standard four
   * directional movement and then update our next position.
   * The position doesn't update here since we need to calculate
   * if it's a valid move.
   */
  @Override
  public void input() {
    if (Gdx.input.isKeyPressed(Keys.W)) nextPos.set(0f, 1f);

    if (Gdx.input.isKeyPressed(Keys.S)) nextPos.set(0f, -1f);

    if (Gdx.input.isKeyPressed(Keys.A)) nextPos.set(-1f, 0f);

    if (Gdx.input.isKeyPressed(Keys.D)) nextPos.set(1f, 0f);
  }

  /**
   * General logic update for pac.
   *
   * - update pacs velocity.
   * - update pacs position. (stopping on collisions)
   *
   *   i pass an array of points instead of a reference
   *   to the game world. i think for the moment it's more
   *   flexible.
   *
   * @param delta used for any physics update that are framerate independent.
   * @param collisionPoints points that the entity should treat as "walls"
   */
  @Override
  public void logic(float delta, Point[] collisionPoints) {
    Vector2 oldVelocity = vel.cpy();
    stateTime += delta;

    updateVelocity(delta, collisionPoints);
    if (!oldVelocity.epsilonEquals(vel)) stateTime = 0f;

    pos.add(vel.cpy().scl(delta));
  }

  /**
   * Draw implementation for pac.
   *
   * We currently use a 1.25f w h
   * animation interval of 0.25f
   * we take precaution to check sprite arrays for null checks.
   */
  @Override
  public void draw(Batch batch) {
    Vector2 dir = vel.cpy().nor();
    float intervalSpeed = 0.25f;
    int index = (int) (stateTime / intervalSpeed);
    if (dir.x == 0 && dir.y == 1 && upSprites != null) {
      index %= upSprites.length;
      batch.draw(upSprites[index], pos.x, pos.y, 1.25f, 1.25f);
    } else if (dir.x == 0 && dir.y == -1 && downSprites != null) {
      index %= downSprites.length;
      batch.draw(downSprites[index], pos.x, pos.y, 1.25f, 1.25f);
    } else if (dir.x == -1 && dir.y == 0 && leftSprites != null) {
      index %= leftSprites.length;
      batch.draw(leftSprites[index], pos.x, pos.y, 1.25f, 1.25f);
    } else if (dir.x == 1 && dir.y == 0 && rightSprites != null) {
      index %= rightSprites.length;
      batch.draw(rightSprites[index], pos.x, pos.y, 1.25f, 1.25f);
    } else if (upSprites != null) {
      //draw a solid sprite if no direction was matched.
      index = 0;
      batch.draw(upSprites[index], pos.x, pos.y, 1.25f, 1.25f);
    }
  }

  /**
   * update's Pacs velocity vector we can come to a full stop
   * if we were to run into a collisionPoint.
   *
   * This method will check to see if we can move in the next direction
   * from our input. and check to see if we collide with any walls and
   * updating the velocity vector accordingly.
   *
   * pacs velocity will always be a 4 direction velocity
   *  (1,0) (-1,0) (0,1) (0,-1) Scaled by the speed variable.
   *
   * It's also possible we hit (0,0) since he's capable of stopping on
   * walls.
   *
   * TODO work on getting pos updates out of this method.
   *
   */
  @Override
  protected void updateVelocity(float delta, Point[] collisionPoints) {
    if (canTurn(0.05f)) {
      pos.set(snap(pos)); //snaps position to nearest tile.
      if (checkNextPosVelocity(delta, collisionPoints)) vel.set(
        nextPos.cpy().nor().scl(speed)
      );
    }

    //if next position doens't land us into a maze wall
    //we advance. the next position is just our current position
    //with the added velocity that we calculated.
    Vector2 nextPos = pos.cpy().add(vel.cpy().scl(delta));
    if (collidesWithPoints(nextPos, collisionPoints)) {
      pos.set(snap(pos));
      vel.set(0f, 0f);
    }
  }

  //overlap works best for pac.
  @Override
  protected boolean collidesWithPoints(Vector2 pos_, Point[] collisionPoints) {
    Rectangle posRect = new Rectangle(pos_.x, pos_.y, 1f, 1f);
    for (Point point : collisionPoints) if (
      posRect.overlaps(point.getRect())
    ) return true;
    return false;
  }

  /**
   * Checks to see if Pac can change direction.
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
   * Will see if the player can move in the direction
   * of nextPos and return true or false.
   *
   * -- if player will collide with a wall on next direction
   *    the prev/current? false is returned.
   *
   * @param delta used to scale the movement applied to our position.
   * @param collisionPoints the points that we are testing collisionw with.
   * @return true if next position will not collide with a wall.
   */
  private boolean checkNextPosVelocity(float delta, Point[] collisionPoints) {
    Vector2 newVelocity = nextPos.cpy().nor().scl(speed);
    Vector2 nPos = pos.cpy().add(newVelocity.cpy().scl(delta));
    if (!collidesWithPoints(nPos, collisionPoints)) return true;
    else return false;
  }
}
