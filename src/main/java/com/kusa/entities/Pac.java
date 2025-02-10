package com.kusa.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.kusa.util.Point;

public class Pac extends Entity {

  /**
   * Vector used to track the next direction of pac.
   */
  private Vector2 nextPos;

  /**
   * Constructs a pac entity with an x, y and speed.
   */
  public Pac(float x, float y) {
    this(x, y, 7.5f);
  }

  public Pac(float x, float y, float speed) {
    super(x, y, speed);
    nextPos = new Vector2(0f, 0f);
  }

  /**
   * Input handler for Pac.
   *
   * Really basic we just check standard four
   * directional movement and then update our next position.
   * The position doesn't update here since we need to calculate
   * if it's a valid move.
   */
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
   * @param delta used for any physics update that are framerate independent.
   */
  @Override
  public void logic(float delta) {
    /*
     *                    MOVEMENT
     * we check to see if we can turn with a threshold of 0.05
     * meaning that if our current position is within a tile bound
     * with a 0.05 buffer.
     */
    if (canTurn(0.05f)) {
      snap();
      vel.set(calcVelocity(delta));
    }

    //if next position doens't land us into a maze wall
    //we advance. the next position is just our current position
    //with the added velocity that we calculated.
    Vector2 nextPos = pos.cpy().add(vel.cpy().scl(delta));
    if (!collidesWithWall(nextPos, Entity.walls)) pos.set(nextPos);
    else {
      snap();
      vel.set(0f, 0f);
    }
    /*          END OF MOVEMENT         */
  }

  @Override
  protected boolean collidesWithWall(Vector2 pos_, Point[] walls) {
    Rectangle posRect = new Rectangle(pos_.x, pos_.y, 1f, 1f);
    for (Point w : walls) if (posRect.overlaps(w.getRect())) {
      return true;
    }
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
   * of nextPos and update velocity accordingly.
   *
   * -- if player will collide with a wall on next direction
   *    the prev/current? velocity is returned.
   *
   * @param delta used to scale the movement applied to our position.
   * @return Vector2 the new velocity if we direction changes else the current velocity is returned.
   */
  private Vector2 calcVelocity(float delta) {
    Vector2 newVelocity = nextPos.cpy().nor().scl(speed);
    Vector2 nPos = pos.cpy().add(newVelocity.cpy().scl(delta));
    if (!collidesWithWall(nPos, Entity.walls)) return newVelocity;
    else return vel.cpy();
  }
}
