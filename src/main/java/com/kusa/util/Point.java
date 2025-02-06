package com.kusa.util;

import com.badlogic.gdx.math.Rectangle;

public class Point {

  private float x;
  private float y;
  private Rectangle rect;

  public Point(float x, float y) {
    this.x = x;
    this.y = y;
    this.rect = new Rectangle(x, y, 1f, 1f);
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

  @Override
  public String toString() {
    String x = Float.toString(this.x);
    String y = Float.toString(this.y);
    return "(" + x + ", " + y + ")";
  }
}
