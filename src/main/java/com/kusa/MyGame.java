package com.kusa;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
import com.kusa.entities.Blinky;
import com.kusa.entities.Entity;
import com.kusa.entities.Pac;
import com.kusa.util.Point;
import java.util.HashSet;
import java.util.Set;

public class MyGame extends Game {

  public enum GhostState {
    SCATTER,
    FRIGHT,
    CHASE,
  }

  public static float unitScale = 1 / 20f;

  //game rendering
  private SpriteBatch batch;
  private ExtendViewport viewport;
  private OrthographicCamera camera;
  private OrthogonalTiledMapRenderer mapRenderer;

  //game enviornment
  private TiledMap mazeMap;
  private Point[] wallPoints;
  private Point[] candyPoints;
  private Point[] superCandyPoints;

  //game entities
  private Pac pac;
  private Blinky blinky;

  //game logic
  private Set<Point> eatenPoints;

  //debug
  private ShapeRenderer shapeRenderer;

  private float ghostStateTime = 0f;
  private float ghostStateDuration = 8f;
  private GhostState ghostState = GhostState.SCATTER;

  private float ghostStateTimeTmp = 0f;
  private float ghostStateDurationTmp = 0f;
  private GhostState ghostStateTmp = GhostState.SCATTER;

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

    //setup maze array's.
    initMaze();
    Entity.setWalls(wallPoints);

    pac = new Pac(13f, 30f - 23f);

    //set 13 to start then 12 to move maybe.
    blinky = new Blinky(12f, 30f - 11f);

    eatenPoints = new HashSet<>();
  }

  /**
   * Loads the maze into an various
   * array of points based on map layers.
   *
   * Called during game construction,
   * we loads all collision coordinates
   * of tile map into wall points array so that
   * we can keep a reference of the maze in our entites.
   */
  private void initMaze() {
    // wall layer is the tiles that we cannot step on.
    TiledMapTileLayer layer = (TiledMapTileLayer) mazeMap
      .getLayers()
      .get("walls");
    this.wallPoints = new Point[490];
    int wallCount = 0;

    // candy layer are tiles that have pellets for pac to collect.
    TiledMapTileLayer layer2 = (TiledMapTileLayer) mazeMap
      .getLayers()
      .get("candies");
    this.candyPoints = new Point[240];
    int candyCount = 0;

    // super candy layer are tiles with super pellets that change the state of ghosts.
    TiledMapTileLayer layer3 = (TiledMapTileLayer) mazeMap
      .getLayers()
      .get("super candies");
    this.superCandyPoints = new Point[4];
    int superCandyCount = 0;

    for (int i = 0; i < 28; i++) {
      for (int j = 0; j < 31; j++) {
        Cell wallCell = layer.getCell(i, j);
        Cell candyCell = layer2.getCell(i, j);
        Cell superCandyCell = layer3.getCell(i, j);
        if (wallCell != null) wallPoints[wallCount++] = new Point(i, j);
        if (candyCell != null) candyPoints[candyCount++] = new Point(i, j);
        if (superCandyCell != null) superCandyPoints[superCandyCount++] =
          new Point(i, j);
      }
    }
  }

  /**
   * Calls logic method for any game entities
   * which require it.
   */
  private void logic(float delta) {
    pac.logic(delta);
    blinky.logic(delta);

    Rectangle pacSquare = new Rectangle(pac.getPos().x, pac.getPos().y, 1f, 1f);
    //check if pac pos is on a pellet.
    for (Point p : candyPoints) if (
      pacSquare.contains(p.getCenter()) && !(eatenPoints.contains(p))
    ) {
      eatenPoints.add(p);
      break;
    }

    //check if pac pos is on a super pellet.
    for (Point p : superCandyPoints) if (
      pacSquare.contains(p.getCenter()) && !(eatenPoints.contains(p))
    ) {
      setFrightMode();
      eatenPoints.add(p);
      break;
    }

    switch (ghostState) {
      case SCATTER:
        blinky.setTarget(Blinky.SCATTER_TILE);
        break;
      case FRIGHT:
        // set target
        break;
      case CHASE:
        blinky.setTarget(pac.getPos());
        break;
      default:
        break;
    }

    if (ghostStateTime >= ghostStateDuration) {
      ghostStateTime = 0f;
      ghostState = nextGhostState();
    } else {
      ghostStateTime += delta;
    }

    System.out.println("GHOST STATE: " + ghostState);
    System.out.println("Ghost State TIME: " + ghostStateTime);
  }

  private void setFrightMode() {
    ghostStateTmp = ghostState;
    ghostStateTimeTmp = ghostStateTime;
    ghostStateDurationTmp = ghostStateDuration;
    ghostStateTime = 0f;
    ghostStateDuration = 4f; //TIME FRIGHTENED
    ghostState = GhostState.FRIGHT;
  }

  private GhostState nextGhostState() {
    switch (ghostState) {
      //flip back to chase.
      case SCATTER -> {
        return GhostState.CHASE;
      }
      case FRIGHT -> {
        ghostStateTime = ghostStateTimeTmp;
        ghostStateDuration = ghostStateDurationTmp;

        ghostStateTimeTmp = 0f;
        ghostStateDurationTmp = 0f;
        return ghostStateTmp;
      }
    }
    return GhostState.SCATTER;
  }

  /**
   * Calls input method for any game entities
   * which require it.
   */
  private void input() {
    pac.input();
  }

  /**
   * Game loop.
   *
   * we call 3 main methods: input, logic, and draw.
   * delta is retrieved and passed from here.
   *
   * this loop is responsible for clearing the screen,
   * updating the camera, starting and ending any
   * batches and renderers. These can then be passed
   * to different functions that handle specific drawing tasks.
   */
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

    //render maze
    mapRenderer.setView(camera);
    mapRenderer.render();

    //rendering shapes for entities (for now).
    shapeRenderer.setProjectionMatrix(camera.combined);
    shapeRenderer.begin(ShapeType.Filled);

    //render pellets
    shapeRenderer.setColor(Color.GOLD.mul(Color.WHITE));
    //shapeRenderer.setColor(253f, 201f, 186f, 1f);
    for (Point p : candyPoints) {
      if (!(eatenPoints.contains(p))) shapeRenderer.ellipse(
        p.getX() + 0.3f,
        p.getY() + 0.2f,
        0.5f,
        0.5f
      );
    }

    //render super pellet
    shapeRenderer.setColor(Color.GOLDENROD.mul(Color.WHITE));
    //shapeRenderer.setColor(255f, 203f, 164f, 1f);
    for (Point p : superCandyPoints) {
      if (!(eatenPoints.contains(p))) shapeRenderer.ellipse(
        p.getX(),
        p.getY(),
        1.25f,
        1.25f
      );
    }

    //render pac
    shapeRenderer.setColor(255f, 255f, 0f, 1f);
    Vector2 pacpos = pac.getPos();
    shapeRenderer.ellipse(pacpos.x, pacpos.y, 1f, 1f);

    //render blinky
    shapeRenderer.setColor(255f, 0f, 0f, 1f);
    Vector2 blinkypos = blinky.getPos();
    shapeRenderer.ellipse(blinkypos.x, blinkypos.y, 1f, 1f);

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
