package com.kusa.util;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Point {

  private float x;
  private float y;
  private float w;
  private float h;
  private Rectangle rect;
  private Vector2 center;

  public Point(float x, float y) {
    this(x, y, 1f, 1f);
  }

  //x point, y point, width, height.
  public Point(float x, float y, float w, float h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.rect = new Rectangle(x, y, w, h);
    this.center = new Vector2(x + (w * 0.5f), y + (h * 0.5f));
  }

  public void setX(float x) {
    this.x = x;
  }

  public float getX() {
    return this.x;
  }

  public void setY(float y) {
    this.y = y;
  }

  public float getY() {
    return this.y;
  }

  public Rectangle getRect() {
    return this.rect;
  }

  public Vector2 getCenter() {
    return this.center;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 71 * hash + Math.round(this.x);
    hash = 71 * hash + Math.round(this.y);
    return hash;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof Point)) return false;
    Point o_ = (Point) o;
    return ((o_.getX() == this.x) && (o_.getY() == this.y));
  }

  @Override
  public String toString() {
    String x = Float.toString(this.x);
    String y = Float.toString(this.y);
    return "(" + x + ", " + y + ")";
  }
}
