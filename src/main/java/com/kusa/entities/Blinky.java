package com.kusa.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import java.util.HashMap;
import java.util.Map;

public class Blinky extends Entity {

  /**
   * Speed in pixels per second.
   */
  private float speed;

  /**
   * Position of the target tile in scatter mode.
   */
  private final Vector2 scatterTarget;

  /**
   * Constructs a blinky entity with an x, y.
   */
  public Blinky(float x, float y) {
    super(x, y);
    scatterTarget = new Vector2(25f, 30f - (-3f));
    speed = 7.5f;
  }

  /**
   * General logic update for blinky.
   *
   * - update blinkys velocity.
   * - update blinkys position.
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
      vel.set(calcVelocity(delta, scatterTarget));
    }

    pos.add(vel.cpy().scl(delta));
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
   * Will first calculate the next valid moves and then determine
   * which one is the closest to our target and return the velocity
   * that will bring us there. 
   *
  val BlinkyScatterTarget : Point = Point(25,-3)
  val PinkyScatterTarget : Point = Point(2,-3)
  val InkyScatterTarget : Point = Point(28,35)
  val ClydeScatterTarget : Point = Point(-1,33)
  val GateTarget : Point = Point(13,11)
  val PenTarget : Point = Point(13,15)
   *
   * @param delta used to scale the movement applied to our position.
   * @return Vector2 the updated velocity.
   */
  private Vector2 calcVelocity(float delta, Vector2 target) {
    Map<Float, Vector2> distToVel = new HashMap<>();
    Vector2 opp = vel.cpy().scl(-1).nor();

    //up down left right.
    Vector2 upVel = new Vector2(0f, 1f).nor().scl(speed);
    Vector2 upPos = pos.cpy().add(upVel.cpy().scl(delta));
    if (!collidesWithWall(upPos) && !opp.epsilonEquals(0f, 1f)) {
      float dist = upPos.dst(target);
      distToVel.put(dist, upVel);
    }

    Vector2 leftVel = new Vector2(-1f, 0f).scl(speed);
    Vector2 leftPos = pos.cpy().add(leftVel.cpy().scl(delta));
    if (!collidesWithWall(leftPos) && !opp.epsilonEquals(-1f, 0f)) {
      float dist = leftPos.dst(target);
      if (!distToVel.containsKey(dist)) distToVel.put(dist, leftVel);
    }

    Vector2 downVel = new Vector2(0f, -1f).scl(speed);
    Vector2 downPos = pos.cpy().add(downVel.cpy().scl(delta));
    if (!collidesWithWall(downPos) && !opp.epsilonEquals(0f, -1f)) {
      float dist = downPos.dst(target);
      if (!distToVel.containsKey(dist)) distToVel.put(dist, downVel);
    }

    Vector2 rightVel = new Vector2(1f, 0f).scl(speed);
    Vector2 rightPos = pos.cpy().add(rightVel.cpy().scl(delta));
    if (!collidesWithWall(rightPos) && !opp.epsilonEquals(1f, 0f)) {
      float dist = rightPos.dst(target);
      if (!distToVel.containsKey(dist)) distToVel.put(dist, rightVel);
    }

    float shortest = Float.MAX_VALUE;
    for (float dist : distToVel.keySet()) {
      shortest = Math.min(shortest, dist);
    }

    return distToVel.get(shortest);
  }

  /**
   * Gets the current speed of Blinky.
   * speed is represented in pixels per second.
   *
   * @return float, blinky's speed in pixels per second.
   */
  public float getSpeed() {
    return this.speed;
  }

  /**
   * Sets a new speed for Blinky.
   * speed is represented in pixels per second.
   * @param pSpeed representing new speed for blinky.
   */
  public void setSpeed(float pSpeed) {
    speed = pSpeed;
  }
}
