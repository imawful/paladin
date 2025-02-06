package com.kusa.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.kusa.util.Point;

public abstract class Entity {

  /**
   * Reference to the maze from the game class.
   *
   * All entities will use this reference for
   * checking collisions. this array only
   * contains the 'walls' for collision purposes.
   */
  protected static Point[] walls;

  /**
   * Standard 4 directional vectors.
   * might delete
   */
  public static Vector2 UP = new Vector2(0f, 1f);
  public static Vector2 DOWN = new Vector2(0f, -1f);
  public static Vector2 LEFT = new Vector2(-1f, 0f);
  public static Vector2 RIGHT = new Vector2(1f, 0f);

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
    vel = new Vector2(0f, 0f);
    this.speed = speed;
  }

  /**
   * General logic update for this entity.
   *
   * @param delta used for any physics update that are framerate independent.
   */
  public abstract void logic(float delta);

  /**
   * Sets new position entity.
   *
   * updates the position vector. probably
   * won't need to call this unless you want more
   * conrol of this entity outside its implemented class.
   *
   * @param x new x position for entity.
   * @param y new y position for entity.
   */
  public void setPos(float x, float y) {
    pos.set(x, y);
  }

  /**
   * Sets new position entity.
   *
   * updates the position vector. probably
   * won't need to call this unless you want more
   * conrol of this entity outside its implemented class.
   *
   * @param v new vector for entity's position.
   */
  public void setPos(Vector2 v) {
    pos.set(v);
  }

  /**
   * Gets the vector of entities position.
   * @return vector2 representing this entities position.
   */
  public Vector2 getPos() {
    return this.pos;
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
   * Updates position of entity to nearest tile.
   *
   * position's x and y are rounded to nearest integer.
   */
  public void snap() {
    pos.x = Math.round(pos.x);
    pos.y = Math.round(pos.y);
  }

  /**
   * Sets the "walls" refrence for entities.
   *
   * Should be set by the game or screen during intial construction.
   * Enities need a valid set of walls so make sure one is set early.
   *
   * @param walls array of points representing the maze.
   */
  public static void setWalls(Point[] walls) {
    Entity.walls = walls;
  }

  /**
   * Returns true if the point pos_ were to collide
   * with a wall.
   *
   * a rectangle is construced from point pos with 1f width and height.
   * the collsion checks to see if any wall overlaps this rectangle.
   *
   * @param pos_ the position to check wether it collides with a wall.
   */
  public static boolean collidesWithWall(Vector2 pos_) {
    Rectangle p = new Rectangle(pos_.x, pos_.y, 1f, 1f);
    for (Point w : walls) if (w.getRect().overlaps(p)) return true;
    return false;
  }
}
