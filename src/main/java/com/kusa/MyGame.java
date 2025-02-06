package com.kusa;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kusa.entities.Entity;
import com.kusa.entities.Pac;
import com.kusa.util.Point;
import java.util.ArrayList;
import java.util.List;

public class MyGame extends Game {

  private static float unitScale = 1 / 20f;

  private SpriteBatch batch;
  private ExtendViewport viewport;
  private OrthographicCamera camera;
  private OrthogonalTiledMapRenderer mapRenderer;

  private Pac pac;

  private TiledMap mazeMap;
  private Point[] wallPoints;

  //debug
  private ShapeRenderer shapeRenderer;

  @Override
  public void create() {
    // map width : 28
    // map height: 31
    // tile units 20x20

    batch = new SpriteBatch();

    camera = new OrthographicCamera();
    camera.setToOrtho(false, 28, 31);

    viewport = new ExtendViewport(28, 31, camera);

    mazeMap = new TmxMapLoader().load("map/maze.tmx");
    mapRenderer = new OrthogonalTiledMapRenderer(mazeMap, unitScale);

    shapeRenderer = new ShapeRenderer();

    //setup wall points array.
    initWallPoints();
    Entity.setWalls(wallPoints);

    pac = new Pac(13f, 30f - 23f);
  }

  /**
   * Loads the maze into an array of points.
   *
   * Called during game construction,
   * we loads all collision coordinates
   * of tile map into wall points array so that
   * we can keep a reference of the maze in our entites.
   */
  private void initWallPoints() {
    TiledMapTileLayer layer = (TiledMapTileLayer) mazeMap
      .getLayers()
      .get("walls");
    this.wallPoints = new Point[490];
    int count = 0;
    for (int i = 0; i < 28; i++) {
      for (int j = 0; j < 31; j++) {
        Cell cell = layer.getCell(i, j);
        if (cell != null) {
          wallPoints[count++] = new Point(i, j);
        }
      }
    }
  }

  private void logic(float delta) {
    pac.logic(delta);
  }

  private void input() {
    pac.input();
  }

  @Override
  public void render() {
    super.render();

    final float delta = Gdx.graphics.getDeltaTime();

    input();

    logic(delta);

    //r g b a clearDepthBuffer
    ScreenUtils.clear(0, 0, 0, 1, true);

    camera.position.set(28f / 2f, 31f / 2f, 0);
    camera.update();

    batch.setProjectionMatrix(camera.combined);
    batch.begin();
    batch.end();

    mapRenderer.setView(camera);
    mapRenderer.render();

    shapeRenderer.setProjectionMatrix(camera.combined);

    shapeRenderer.begin(ShapeType.Filled);
    shapeRenderer.setColor(255f, 255f, 0f, 1f);
    Vector2 pacpos = pac.getPos();
    shapeRenderer.ellipse(pacpos.x, pacpos.y, 1f, 1f);

    shapeRenderer.end();
  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height, true);
  }

  @Override
  public void dispose() {
    super.dispose();
    if (batch != null) batch.dispose();
  }
}
