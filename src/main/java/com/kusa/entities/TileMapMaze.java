package com.kusa.entities;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Class representing a maze from Tiled.
 */
public class TileMapMaze implements Maze {

  private final boolean[][] wall;
  private final boolean[][] candy;
  private final boolean[][] superCandy;

  //for collision checks.
  private Rectangle mazeTile;
  private Rectangle pointTile; //sets points to a rect.
  private final Rectangle leftTunnel;
  private final Rectangle rightTunnel;
  private final TiledMap mazeMap;

  private int initialCandyCount;
  private int currentCandyCount;

  private float unitScale;

  public TileMapMaze() {
    // 20px by 20px = 1 game world unit.
    unitScale = 1 / 20f;
    mazeMap = new TmxMapLoader().load("map/maze.tmx");

    mazeTile = new Rectangle();
    pointTile = new Rectangle();

    wall = new boolean[28][31];
    candy = new boolean[28][31];
    superCandy = new boolean[28][31];
    leftTunnel = new Rectangle(0f, (30f - 15f), 5f, 3f);
    rightTunnel = new Rectangle(23f, (30f - 15f), 5f, 3f);
    initialCandyCount = 0;
    fillArrays();
  }

  private void fillArrays() {
    TiledMapTileLayer layer = (TiledMapTileLayer) mazeMap
      .getLayers()
      .get("walls");

    TiledMapTileLayer layer2 = (TiledMapTileLayer) mazeMap
      .getLayers()
      .get("candies");

    TiledMapTileLayer layer3 = (TiledMapTileLayer) mazeMap
      .getLayers()
      .get("super candies");

    for (int i = 0; i < 28; i++) {
      for (int j = 0; j < 31; j++) {
        Cell wallCell = layer.getCell(i, j);
        Cell candyCell = layer2.getCell(i, j);
        Cell superCandyCell = layer3.getCell(i, j);
        wall[i][j] = wallCell != null;
        candy[i][j] = candyCell != null;
        superCandy[i][j] = superCandyCell != null;
        if (candy[i][j] || superCandy[i][j]) initialCandyCount++;
      }
    }
  }

  @Override
  public float getUnitScale() {
    return this.unitScale;
  }

  public TiledMap getTiledMap() {
    return this.mazeMap;
  }

  /*
   * how many canides the maze started with.
   * includes super and regular.
   */
  @Override
  public int getInitialCandyCount() {
    return this.initialCandyCount;
  }

  /*
   * how many candies are "left"
   *
   * includes super and regular.
   */
  @Override
  public int getCurrentCandyCount() {
    int count = 0;
    for (int i = 0; i < 28; i++) for (int j = 0; j < 31; j++) if (
      candy[i][j] || superCandy[i][j]
    ) count++;

    return count;
  }

  /**
   * Check if point overlaps a maze wall.
   *
   * @param point position to check if overlapping maze wall.
   * @param ignoreGate if true we dont count the ghost gate as a wall.
   * @return true if point overlaps a maze wall.
   */
  @Override
  public boolean overlapsWall(Vector2 point, boolean ignoreGate) {
    pointTile.set(point.x, point.y, 1f, 1f);
    int x = Math.round(point.x);
    int y = Math.round(point.y);
    for (int i = x - 2; i < x + 2; i++) {
      for (int j = y - 2; j < y + 2; j++) {
        if (i < 0 || j < 0 || i >= wall.length || j >= wall[i].length) continue;
        boolean isWall = isWall(i, j, ignoreGate);
        mazeTile.set(i, j, 1f, 1f);
        if (isWall && pointTile.overlaps(mazeTile)) return true;
      }
    }
    return false;
  }

  /**
   * Checks if point is touching a candy and updates
   * candy array.
   *
   * if point is touching a candy we return true and update
   * our candy array of the points position to false.
   *
   * @param point entitys position that will check and eat the candy.
   * @return true if point is touching candy.
   */
  public boolean checkAndEatCandy(Vector2 point) {
    pointTile.set(point.x, point.y, 1f, 1f);
    int x = Math.round(point.x);
    int y = Math.round(point.y);
    for (int i = x - 2; i < x + 2; i++) {
      for (int j = y - 2; j < y + 2; j++) {
        if (
          i < 0 || j < 0 || i >= candy.length || j >= candy[i].length
        ) continue;
        boolean isCandy = isCandy(i, j);
        if (isCandy && pointTile.contains(i + 0.5f, j + 0.5f)) {
          candy[i][j] = false;
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Checks if point is touching a super candy and updates
   * super candy array.
   *
   * if point is touching a super we return true and update
   * our super candy array of the points position to false.
   *
   * @param point entitys position that will check and eat the candy.
   * @return true if point is touching super candy.
   */
  @Override
  public boolean checkAndEatSuperCandy(Vector2 point) {
    pointTile.set(point.x, point.y, 1f, 1f);
    int x = Math.round(point.x);
    int y = Math.round(point.y);
    for (int i = x - 2; i < x + 2; i++) {
      for (int j = y - 2; j < y + 2; j++) {
        if (
          i < 0 || j < 0 || i >= superCandy.length || j >= superCandy[i].length
        ) continue;
        boolean isSuperCandy = isSuperCandy(i, j);
        if (isSuperCandy && pointTile.contains(i + 0.5f, j + 0.5f)) {
          superCandy[i][j] = false;
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public boolean inTunnel(Vector2 point) {
    return (leftTunnel.contains(point) || rightTunnel.contains(point));
  }

  /*
   * we should refactor logic so we don't need this
   */
  @Override
  public Rectangle getLeftTunnel() {
    return leftTunnel;
  }

  @Override
  public Rectangle getRightTunnel() {
    return rightTunnel;
  }

  private boolean isGate(int x, int y) {
    boolean isLeftDoor = (x == 13) && (y == (30 - 12));
    boolean isRightDoor = (x == 14) && (y == (30 - 12));
    return (isLeftDoor || isRightDoor);
  }

  @Override
  public boolean isWall(int x, int y) {
    return isWall(x, y, false);
  }

  private boolean isWall(int x, int y, boolean ignoreGate) {
    if (wall[x][y] && ignoreGate) return !isGate(x, y);
    return wall[x][y];
  }

  @Override
  public boolean isCandy(int x, int y) {
    return candy[x][y];
  }

  @Override
  public boolean isSuperCandy(int x, int y) {
    return superCandy[x][y];
  }
}
