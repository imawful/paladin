package com.kusa.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

public class Pac extends Entity {

  /**
   * Vector used to track the next direction of pac.
   */
  private Vector2 nextPos;

  /**
   * Speed in pixels per second.
   */
  private float speed;

  /**
   * Constructs a pac entity with an x, y and maze reference.
   */
  public Pac(float x, float y) {
    super(x, y);
    nextPos = new Vector2(0f, 0f);
    speed = 7.5f;
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
     * we check to see if we can turn with a threshold of 0.1
     * meaning that if our current position is within a tile bound
     * with a 0.1 buffer.
     */
    if (canTurn(0.1f)) {
      snap();
      vel.set(calcVelocity(delta));
    }

    //if next position doens't land us into a maze wall
    //we advance. the next position is just our current position
    //with the added velocity that we calculated.
    Vector2 nextPos = pos.cpy().add(vel.cpy().scl(delta));
    if (!collidesWithWall(nextPos)) pos.set(nextPos);
    else {
      snap();
      vel.set(0f, 0f);
    }
    /*          END OF MOVEMENT         */
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
    if (!collidesWithWall(nPos)) return newVelocity;
    else return vel.cpy();
  }

  /**
   * Gets the current speed of Pac.
   * speed is represented in pixels per second.
   *
   * @return float, pac's speed in pixels per second.
   */
  public float getSpeed() {
    return this.speed;
  }

  /**
   * Sets a new speed for Pac.
   * speed is represented in pixels per second.
   * @param pSpeed representing new speed for pac.
   */
  public void setSpeed(float pSpeed) {
    speed = pSpeed;
  }
}
