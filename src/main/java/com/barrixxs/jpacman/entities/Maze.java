package com.barrixxs.jpacman.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public interface Maze {
  default boolean overlapsWall(Vector2 point) {
    return overlapsWall(point, false);
  }

  //for COLLISIONS
  boolean overlapsWall(Vector2 point, boolean ignoreGate);
  boolean checkAndEatCandy(Vector2 point);
  boolean checkAndEatSuperCandy(Vector2 point);
  boolean inTunnel(Vector2 point);

  //for use outside of COLLISIONS
  boolean isWall(int x, int y);
  boolean isCandy(int x, int y);
  boolean isSuperCandy(int x, int y);

  //regular and super pelets.
  int getCurrentCandyCount();
  int getInitialCandyCount();

  default float getUnitScale() {
    return 1f;
  }

  Rectangle getLeftTunnel();
  Rectangle getRightTunnel();
}
