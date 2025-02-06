package com.kusa.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.kusa.util.Point;

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
   * Reference to the maze from the game class.
   */
  protected static Point[] walls;

  /**
   * Constructs an entity with an x, y and maze reference.
   */
  public Entity(float x, float y) {
    pos = new Vector2(x, y);
    vel = new Vector2(0f, 0f);
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
