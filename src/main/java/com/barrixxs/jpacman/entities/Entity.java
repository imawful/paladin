package com.barrixxs.jpacman.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.barrixxs.jpacman.util.Point;
import java.util.List;

public abstract class Entity {

  /**
   * Entities position vector.
   */
  protected Vector2 pos;

  /**
   * Entities velocity vector.
   */
  protected Vector2 vel;

  /**
   * Speed in pixels per second.
   */
  protected float speed;

  /**
   * State time we use to track time for entities state.
   * if you need more than one state time reconsider the entity logic.
   */
  protected float stateTime;

  /**
   * Spawn point, entitys posistion when first created.
   * can be set or updated as needed.
   */
  protected Vector2 spawn;

  /**
   * Constructs an entity with an x and y value and default speed (1f).
   *
   * @param x x position of entity.
   * @param y y posiition of entity.
   */
  public Entity(float x, float y) {
    this(x, y, 1f);
  }

  /**
   * Constructs an entity with an x, y and speed.
   *
   * @param x x position of entity.
   * @param y y position of entity.
   * @param speed float representing pixel per second speed of entity.
   */
  public Entity(float x, float y, float speed) {
    pos = new Vector2(x, y);
    spawn = new Vector2(x, y);
    vel = new Vector2(0f, 0f);
    this.speed = speed;
    this.stateTime = 0f;
  }

  /**
   * General logic update for this entity.
   *
   * @param delta used for any physics update that are framerate independent.
   */
  public abstract void logic(float delta);

  /**
   * Gets the vector of entity's position.
   * @return vector2 representing this entities position.
   */
  public Vector2 getPos() {
    return this.pos.cpy();
  }

  /**
   * Sets the vector of entitys position.
   *
   * use carefully!
   */
  public void setPos(Vector2 pPos) {
    this.pos.set(pPos);
  }

  /**
   * Gets the vector of entity's velocity.
   * @return vector2 representing this entities position.
   */
  public Vector2 getVel() {
    return this.vel.cpy();
  }

  /**
   * Sets the vector of entitys velocity.
   *
   * use carefully!
   */
  public void setVel(Vector2 pVel) {
    this.vel.set(pVel);
  }

  /**
   * Gets the spawn point of entity.
   * @return vector2 representing this entities spawn position.
   */
  public Vector2 getSpawn() {
    return this.spawn.cpy();
  }

  /**
   * Sets the spawn point of entity.
   * @param pos vector2 representing entitys new spawn point.
   */
  public void setSpawn(Vector2 pos) {
    this.spawn.set(pos);
  }

  /**
   * Gets the current speed of entity.
   * speed is represented in pixels per second.
   *
   * @return float, entity's speed in pixels per second.
   */
  public float getSpeed() {
    return this.speed;
  }

  /**
   * Sets a new speed for entity.
   * speed is represented in pixels per second.
   * @param pSpeed representing new speed for entity.
   */
  public void setSpeed(float pSpeed) {
    speed = pSpeed;
  }

  /**
   * Gets the state time for entity.
   * @return float, time entity has spent in it's current state.
   */
  public float getStateTime() {
    return this.stateTime;
  }

  /**
   * Set the state time for entity.
   * use this carefully!
   * only made it to zero out state times.
   * @param time new state time for entity.
   */
  public void setStateTime(float time) {
    this.stateTime = time;
  }

  /**
   * Returns the vector rounded to the nearest int.
   *
   * uses Math.round()
   * position's x and y are rounded to nearest integer.
   *
   * @param posisiton the vector you want to round.
   * @return Vector2 with rounded x and y values.
   */
  public static Vector2 snap(Vector2 position) {
    Vector2 pos = position.cpy();
    pos.x = Math.round(pos.x);
    pos.y = Math.round(pos.y);
    return pos;
  }

  /*MAKE STATIC*/
  protected boolean canSnap(float threshold) {
    return (
      (Math.abs(pos.x - Math.round(pos.x)) < threshold) &&
      (Math.abs(pos.y - Math.round(pos.y)) < threshold)
    );
  }
}
