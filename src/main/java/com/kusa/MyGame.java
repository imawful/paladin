package com.kusa;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.kusa.entities.Ghost;
import com.kusa.entities.Ghost.GhostState;
import com.kusa.entities.Pac;
import com.kusa.util.Point;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyGame extends Game {

  public static float unitScale = 1 / 20f;
  public static float ATE_SPEED_MULTIPLIER = 1.5f;

  //game rendering
  private SpriteBatch batch;
  private ExtendViewport viewport;
  private OrthographicCamera camera;
  private OrthogonalTiledMapRenderer mapRenderer;

  //sprite sheets.
  Texture pacSpriteSheet;

  TextureRegion[] pacDownSprites;
  TextureRegion[] pacLeftSprites;
  TextureRegion[] pacRightSprites;
  TextureRegion[] pacUpSprites;

  Texture blinkySpriteSheet;
  TextureRegion[] blinkyDownSprites;
  TextureRegion[] blinkyLeftSprites;
  TextureRegion[] blinkyRightSprites;
  TextureRegion[] blinkyUpSprites;

  Texture pinkySpriteSheet;
  TextureRegion[] pinkyDownSprites;
  TextureRegion[] pinkyLeftSprites;
  TextureRegion[] pinkyRightSprites;
  TextureRegion[] pinkyUpSprites;

  Texture inkySpriteSheet;
  TextureRegion[] inkyDownSprites;
  TextureRegion[] inkyLeftSprites;
  TextureRegion[] inkyRightSprites;
  TextureRegion[] inkyUpSprites;

  Texture clydeSpriteSheet;
  TextureRegion[] clydeDownSprites;
  TextureRegion[] clydeLeftSprites;
  TextureRegion[] clydeRightSprites;
  TextureRegion[] clydeUpSprites;

  //shared between ghosts.
  Texture ghostAteSpriteSheet;
  TextureRegion[] ghostAteSprites;

  Texture ghostFrightSpriteSheet;
  TextureRegion[] ghostFrightSprites;

  //game enviornment
  private TiledMap mazeMap;
  private Point[] wallPoints;
  private Point[] candyPoints;
  private Point[] superCandyPoints;
  private Rectangle leftTunnel;
  private Rectangle rightTunnel;

  //game entities
  private Pac pac;
  private Ghost[] ghosts;

  //collision rects
  private Rectangle pacRect;
  private Rectangle ghostRect;

  //game logic
  private Set<Point> eatenPoints;

  private float ghostStateTime;
  private float ghostStateDuration;
  private GhostState ghostState;

  private float ghostStateTimeTmp;
  private float ghostStateDurationTmp;
  private GhostState ghostStateTmp;

  private float[] scatterChaseIntervals;
  private int scatterChaseIndex;
  private float frightTime;

  private float pacSpeedMultiplier;
  private float pacFrightSpeedMultiplier;

  private int angryModeOneDotLimit;
  private int angryModeTwoDotLimit;

  private float angryModeOneSpeedMultiplier;
  private float angryModeTwoSpeedMultiplier;

  private float ghostSpeedMultiplier;
  private float ghostFrightSpeedMultiplier;
  private float ghostTunnelSpeedMultiplier; //highest priority

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

    //setup maze array's.
    initMaze();

    //setup sprites (or assets ig?)
    initSprites();

    pac = new Pac(
      13f,
      30f - 23f,
      Ghost.FULL_SPEED,
      pacUpSprites,
      pacDownSprites,
      pacLeftSprites,
      pacRightSprites
    );

    ghosts = new Ghost[] {
      new Ghost(
        12f,
        30f - 11f,
        Ghost.FULL_SPEED,
        GhostState.SCATTER,
        GhostState.SCATTER,
        new Vector2(25f, 30f - (-3f)),
        new Vector2(11f, 30f - (15f)),
        blinkyUpSprites,
        blinkyDownSprites,
        blinkyLeftSprites,
        blinkyRightSprites,
        ghostAteSprites,
        ghostFrightSprites
      ), //blinky
      new Ghost(
        12f,
        30f - 14f,
        Ghost.FULL_SPEED,
        GhostState.INPEN,
        GhostState.SCATTER,
        new Vector2(2f, 30f - (-3f)),
        new Vector2(12f, 30f - (15f)),
        pinkyUpSprites,
        pinkyDownSprites,
        pinkyLeftSprites,
        pinkyRightSprites,
        ghostAteSprites,
        ghostFrightSprites
      ), //pinky
      new Ghost(
        14f,
        30f - 14f,
        Ghost.FULL_SPEED,
        GhostState.INPEN,
        GhostState.SCATTER,
        new Vector2(28f, 30f - (35f)),
        new Vector2(14f, 30f - (15f)),
        inkyUpSprites,
        inkyDownSprites,
        inkyLeftSprites,
        inkyRightSprites,
        ghostAteSprites,
        ghostFrightSprites
      ), //inky
      new Ghost(
        16f,
        30f - 14f,
        Ghost.FULL_SPEED,
        GhostState.INPEN,
        GhostState.SCATTER,
        new Vector2(-1f, 30f - (33f)),
        new Vector2(16f, 30f - (15f)),
        clydeUpSprites,
        clydeDownSprites,
        clydeLeftSprites,
        clydeRightSprites,
        ghostAteSprites,
        ghostFrightSprites
      ), //clyde
    };
    pacRect = new Rectangle(pac.getPos().x, pac.getPos().y, 1f, 1f);
    ghostRect = new Rectangle(
      ghosts[0].getPos().x,
      ghosts[0].getPos().y,
      0.8f,
      0.8f
    );

    eatenPoints = new HashSet<>();

    // LEVEL DEPENDENT DATA IS SET HERE.
    frightTime = 6f;
    scatterChaseIntervals = new float[] { 7f, 20f, 7f, 20f, 5f, 20f, 5f };
    scatterChaseIndex = 0;

    pacSpeedMultiplier = 0.8f;
    pacFrightSpeedMultiplier = 0.9f;

    angryModeOneDotLimit = 20;
    angryModeTwoDotLimit = 10;

    angryModeOneSpeedMultiplier = 0.8f;
    angryModeTwoSpeedMultiplier = 0.85f;

    ghostSpeedMultiplier = 0.75f;
    ghostFrightSpeedMultiplier = 0.5f;
    ghostTunnelSpeedMultiplier = 0.4f; //highest priority

    ghostStateTime = 0f;
    ghostStateDuration = scatterChaseIntervals[0];
    ghostState = GhostState.SCATTER;

    //tmp to zero.
    ghostStateTimeTmp = 0f;
    ghostStateDurationTmp = 0f;
    ghostStateTmp = GhostState.SCATTER;
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
    this.leftTunnel = new Rectangle(0f, (30f - 15f), 5f, 3f);
    this.rightTunnel = new Rectangle(23f, (30f - 15f), 5f, 3f);
  }

  private void initSprites() {
    //pacs sprites.
    pacSpriteSheet = new Texture("pac/sprite-sheet.png");
    TextureRegion solidPac = new TextureRegion(pacSpriteSheet, 0, 300, 20, 20);
    pacDownSprites = new TextureRegion[] {
      solidPac,
      new TextureRegion(pacSpriteSheet, 0, 0, 20, 20),
      new TextureRegion(pacSpriteSheet, 0, 20, 20, 20),
    };
    pacLeftSprites = new TextureRegion[] {
      solidPac,
      new TextureRegion(pacSpriteSheet, 0, 40, 20, 20),
      new TextureRegion(pacSpriteSheet, 0, 60, 20, 20),
    };
    pacRightSprites = new TextureRegion[] {
      solidPac,
      new TextureRegion(pacSpriteSheet, 0, 80, 20, 20),
      new TextureRegion(pacSpriteSheet, 0, 100, 20, 20),
    };
    pacUpSprites = new TextureRegion[] {
      solidPac,
      new TextureRegion(pacSpriteSheet, 0, 140, 20, 20),
      new TextureRegion(pacSpriteSheet, 0, 160, 20, 20),
    };

    blinkySpriteSheet = new Texture("ghosts/blinky-sprite-sheet.png");
    blinkyDownSprites = new TextureRegion[] {
      new TextureRegion(blinkySpriteSheet, 0, 0, 20, 20),
      new TextureRegion(blinkySpriteSheet, 0, 20, 20, 20),
    };
    blinkyLeftSprites = new TextureRegion[] {
      new TextureRegion(blinkySpriteSheet, 0, 40, 20, 20),
      new TextureRegion(blinkySpriteSheet, 0, 60, 20, 20),
    };
    blinkyRightSprites = new TextureRegion[] {
      new TextureRegion(blinkySpriteSheet, 0, 80, 20, 20),
      new TextureRegion(blinkySpriteSheet, 0, 100, 20, 20),
    };
    blinkyUpSprites = new TextureRegion[] {
      new TextureRegion(blinkySpriteSheet, 0, 120, 20, 20),
      new TextureRegion(blinkySpriteSheet, 0, 140, 20, 20),
    };

    pinkySpriteSheet = new Texture("ghosts/pinky-sprite-sheet.png");
    pinkyDownSprites = new TextureRegion[] {
      new TextureRegion(pinkySpriteSheet, 0, 0, 20, 20),
      new TextureRegion(pinkySpriteSheet, 0, 20, 20, 20),
    };
    pinkyLeftSprites = new TextureRegion[] {
      new TextureRegion(pinkySpriteSheet, 0, 40, 20, 20),
      new TextureRegion(pinkySpriteSheet, 0, 60, 20, 20),
    };
    pinkyRightSprites = new TextureRegion[] {
      new TextureRegion(pinkySpriteSheet, 0, 80, 20, 20),
      new TextureRegion(pinkySpriteSheet, 0, 100, 20, 20),
    };
    pinkyUpSprites = new TextureRegion[] {
      new TextureRegion(pinkySpriteSheet, 0, 120, 20, 20),
      new TextureRegion(pinkySpriteSheet, 0, 140, 20, 20),
    };

    inkySpriteSheet = new Texture("ghosts/inky-sprite-sheet.png");
    inkyDownSprites = new TextureRegion[] {
      new TextureRegion(inkySpriteSheet, 0, 0, 20, 20),
      new TextureRegion(inkySpriteSheet, 0, 20, 20, 20),
    };
    inkyLeftSprites = new TextureRegion[] {
      new TextureRegion(inkySpriteSheet, 0, 40, 20, 20),
      new TextureRegion(inkySpriteSheet, 0, 60, 20, 20),
    };
    inkyRightSprites = new TextureRegion[] {
      new TextureRegion(inkySpriteSheet, 0, 80, 20, 20),
      new TextureRegion(inkySpriteSheet, 0, 100, 20, 20),
    };
    inkyUpSprites = new TextureRegion[] {
      new TextureRegion(inkySpriteSheet, 0, 120, 20, 20),
      new TextureRegion(inkySpriteSheet, 0, 140, 20, 20),
    };

    clydeSpriteSheet = new Texture("ghosts/clyde-sprite-sheet.png");
    clydeDownSprites = new TextureRegion[] {
      new TextureRegion(clydeSpriteSheet, 0, 0, 20, 20),
      new TextureRegion(clydeSpriteSheet, 0, 20, 20, 20),
    };
    clydeLeftSprites = new TextureRegion[] {
      new TextureRegion(clydeSpriteSheet, 0, 40, 20, 20),
      new TextureRegion(clydeSpriteSheet, 0, 60, 20, 20),
    };
    clydeRightSprites = new TextureRegion[] {
      new TextureRegion(clydeSpriteSheet, 0, 80, 20, 20),
      new TextureRegion(clydeSpriteSheet, 0, 100, 20, 20),
    };
    clydeUpSprites = new TextureRegion[] {
      new TextureRegion(clydeSpriteSheet, 0, 120, 20, 20),
      new TextureRegion(clydeSpriteSheet, 0, 140, 20, 20),
    };

    ghostAteSpriteSheet = new Texture("ghosts/ate-sprite-sheet.png");
    ghostAteSprites = new TextureRegion[] {
      new TextureRegion(ghostAteSpriteSheet, 0, 0, 20, 20), //down
      new TextureRegion(ghostAteSpriteSheet, 0, 20, 20, 20), //left
      new TextureRegion(ghostAteSpriteSheet, 0, 40, 20, 20), //right
      new TextureRegion(ghostAteSpriteSheet, 0, 60, 20, 20), //up
    };

    //first two sprites are regular blue frightened.
    // last 2 are lightly colored (for flashes)
    // this sheet isn't direction specific.
    ghostFrightSpriteSheet = new Texture("ghosts/fright-sprite-sheet.png");
    ghostFrightSprites = new TextureRegion[] {
      new TextureRegion(ghostFrightSpriteSheet, 0, 40, 20, 20),
      new TextureRegion(ghostFrightSpriteSheet, 0, 60, 20, 20),
      new TextureRegion(ghostFrightSpriteSheet, 0, 0, 20, 20),
      new TextureRegion(ghostFrightSpriteSheet, 0, 20, 20, 20),
    };
  }

  /**
   * Calls logic method for any game entities
   * which require it.
   */
  private void logic(float delta) {
    // timer(s) logic
    if (ghostStateTime >= ghostStateDuration) {
      ghostStateTime = 0f;
      ghostState = nextGhostState();
    } else {
      ghostStateTime += delta;
    }

    //game applies speed multipliers across levels.
    applySpeedMultipliers();

    //entities logic.
    pac.logic(delta, wallPoints); //moves pac

    //game has little control over
    //ghost state here including their
    //target.
    for (int i = 0; i < ghosts.length; i++) {
      Ghost ghost = ghosts[i];

      if (!pac.getVel().isZero()) ghost.setChaseTarget(getChaseTarget(i));

      ghost.setGameState(
        ghostState == GhostState.FRIGHT ? ghostStateTmp : ghostState
      );

      //we spawn ghosts under different conditions.
      //for blinky here we check if the amount of canides
      //is above 0 meaning he will leave the pen as
      //soon as he enters.
      int dotLimit = 0;
      if (i == 1) dotLimit = 7;
      if (i == 2) dotLimit = 17;
      if (i == 3) dotLimit = 32;
      if (eatenPoints.size() >= dotLimit && ghost.inPen()) {
        ghost.setLeavingPen();
      }

      ghost.logic(delta, wallPoints); //moves ghost
    }

    //check tunnels to teleport across.
    if (pac.getPos().x < leftTunnel.x) pac.setPos(
      rightTunnel.getPosition(new Vector2()).add(rightTunnel.width - 1f, 1f)
    );
    else if (
      pac.getPos().x > ((rightTunnel.x + rightTunnel.width) - 0.5f)
    ) pac.setPos(leftTunnel.getPosition(new Vector2()).add(0f, 1f));

    for (Ghost ghost : ghosts) {
      if (ghost.getPos().x < (leftTunnel.x - 0.5f)) ghost.setPos(
        rightTunnel.getPosition(new Vector2()).add(rightTunnel.width - 1f, 1f)
      );
      else if (
        ghost.getPos().x > ((rightTunnel.x + rightTunnel.width) - 0.5f)
      ) ghost.setPos(leftTunnel.getPosition(new Vector2()).add(0f, 1f));
    }

    //collision between pac and points.
    pacRect.setPosition(pac.getPos());
    //check if pac pos is on a pellet.
    for (Point p : candyPoints) if (
      pacRect.contains(p.getCenter()) && !(eatenPoints.contains(p))
    ) {
      eatenPoints.add(p);
      break;
    }

    //check if pac pos is on a super pellet.
    //this triggers fright mode for ghosts
    //so we make sure to let the ghosts know.
    for (Point p : superCandyPoints) if (
      pacRect.contains(p.getCenter()) && !(eatenPoints.contains(p))
    ) {
      eatenPoints.add(p);
      setFrightMode();

      //ghosts will take care of making sure
      //they will turn frightened only if possible
      for (Ghost ghost : ghosts) ghost.setFrightened(true);

      break;
    }

    //check if pac and ghost collide.
    for (Ghost ghost : ghosts) {
      ghostRect.setPosition(ghost.getPos());
      if (ghostRect.overlaps(pacRect)) {
        ghost.setAte();
        if (!ghost.isFrightened()) System.out.println("GAME OVERR");
      }
    }
  }

  /**
   * Calculates the chase target for all ghosts.
   *
   * we access the ghost array here so make sure its not null.
   *
   * @param ghostIndex lets us know which target we're calculating.
   */
  private Vector2 getChaseTarget(int ghostIndex) {
    Vector2 pacPos = pac.getPos();
    Vector2 pacVel = pac.getVel();
    switch (ghostIndex) {
      case 3: //clyde
        float dist = ghosts[3].getPos().dst(pacPos);
        if (dist < 8f) return ghosts[3].getScatterTarget();
        else return pacPos.cpy();
      case 2: //inky
        Vector2 blinkyPos = ghosts[0].getPos();
        Vector2 offset = pacVel.cpy().nor();
        if (pacVel.nor().epsilonEquals(0f, 1f)) {
          offset.add(-1f, 0f);
        }
        Vector2 intermediate = pacPos.cpy().add(offset.scl(2));
        Vector2 diff = intermediate.cpy().sub(blinkyPos);
        return blinkyPos.cpy().add(diff.scl(2f));
      case 1: //pinky
        Vector2 vectorToUse = pacVel.cpy().nor();
        if (pacVel.nor().epsilonEquals(0f, 1f)) {
          vectorToUse.add(-1f, 0f);
        }
        return pacPos.cpy().add(vectorToUse.scl(2f));
      default: //blinky DEFAULT
        return pac.getPos();
    }
  }

  private void applySpeedMultipliers() {
    float newPacSpeed = Ghost.FULL_SPEED;
    if (ghostState == GhostState.FRIGHT) newPacSpeed *=
      pacFrightSpeedMultiplier;
    else newPacSpeed *= pacSpeedMultiplier;

    pac.setSpeed(newPacSpeed);

    for (int i = 0; i < ghosts.length; i++) {
      Ghost ghost = ghosts[i];
      float newGhostSpeed = Ghost.FULL_SPEED;

      int candies = candyPoints.length + superCandyPoints.length;
      int dotsLeft = candies - eatenPoints.size();
      boolean angryOne = (i == 0 && dotsLeft <= angryModeOneDotLimit);
      boolean angryTwo = (i == 0 && dotsLeft <= angryModeTwoDotLimit);

      if (ghost.isAte()) newGhostSpeed *= ATE_SPEED_MULTIPLIER;
      else if (
        inLeftTunnel(ghost.getPos()) || inRightTunnel(ghost.getPos())
      ) newGhostSpeed *= ghostTunnelSpeedMultiplier;
      else if (ghost.isFrightened()) newGhostSpeed *=
        ghostFrightSpeedMultiplier;
      else if (angryTwo) newGhostSpeed *= angryModeOneSpeedMultiplier;
      else if (angryOne) newGhostSpeed *= angryModeTwoSpeedMultiplier;
      else newGhostSpeed *= ghostSpeedMultiplier;

      ghost.setSpeed(newGhostSpeed);
    }
  }

  /**
   * We force set fright mode whenever pac
   * eats a super.
   *
   * this affects only the games timers
   * for managing the scatter/chase state.
   *  ghostStateTime ghostStateTmpTime.
   *  ghostState ghostStateTmp.
   *
   * The durations are set to predefined values.
   *
   * The game is responsible for calling update to
   * update the ghosts state.
   */
  private void setFrightMode() {
    if (ghostState != GhostState.FRIGHT) {
      ghostStateTmp = ghostState;
      ghostStateTimeTmp = ghostStateTime;
      ghostStateDurationTmp = ghostStateDuration;
    }
    ghostStateTime = 0f;
    ghostStateDuration = 6f; //TIME FRIGHTENED
    ghostState = GhostState.FRIGHT;
  }

  /**
   * We flip back and forth between chase
   * and scatter.
   *
   * Special case:
   *  Fright - own duration.
   *  if our current state is fright we go back
   *  to our previous afer the duration ends.
   *
   * @return ghost state (ONLY SCATTER OR CHASE)
   */
  private GhostState nextGhostState() {
    switch (ghostState) {
      //flip back to chase.
      case SCATTER -> {
        scatterChaseIndex += 1;
        if (
          scatterChaseIndex < scatterChaseIntervals.length
        ) ghostStateDuration = scatterChaseIntervals[scatterChaseIndex];
        return GhostState.CHASE;
      }
      case FRIGHT -> {
        ghostStateTime = ghostStateTimeTmp;
        ghostStateDuration = ghostStateDurationTmp;

        ghostStateTimeTmp = 0f;
        ghostStateDurationTmp = 0f;

        //ghosts will take care of making sure
        //they will exit frightened safely
        for (Ghost ghost : ghosts) ghost.setFrightened(false);
        return ghostStateTmp;
      }
    }
    //case CHASE
    scatterChaseIndex += 1;
    if (
      scatterChaseIndex >= scatterChaseIntervals.length
    ) return GhostState.CHASE;
    ghostStateDuration = scatterChaseIntervals[scatterChaseIndex];
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
   * Returns true if the given position is in the
   * left tunnel.
   *
   * @param pos the position to check if in left tunnel.
   * @return true if position is in left tunnel.
   */
  private boolean inLeftTunnel(Vector2 pos) {
    return leftTunnel.contains(pos);
  }

  /**
   * Returns true if the given position is in the
   * right tunnel.
   *
   * @param pos the position to check if in right tunnel.
   * @return true if position is in right tunnel.
   */
  private boolean inRightTunnel(Vector2 pos) {
    return rightTunnel.contains(pos);
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

    shapeRenderer.end();

    batch.setProjectionMatrix(camera.combined);
    batch.begin();

    //render pac
    /*
    shapeRenderer.setColor(255f, 255f, 0f, 1f);
    Vector2 pacpos = pac.getPos();
    shapeRenderer.ellipse(pacpos.x, pacpos.y, 1f, 1f);
    */
    pac.draw(batch);

    //render ghosts
    /*
    for (int i = 0; i < ghosts.length; i++) {
      Ghost ghost = ghosts[i];
      Color defColor = Color.RED;
      if (i == 1) defColor = Color.PINK;
      if (i == 2) defColor = Color.CYAN;
      if (i == 3) defColor = Color.LIME;
      if (ghost.isFrightened()) shapeRenderer.setColor(0f, 0f, 255f, 1f);
      else if (ghost.isAte()) shapeRenderer.setColor(Color.WHITE);
      else shapeRenderer.setColor(defColor);
      //shapeRenderer.setColor(255f, 0f, 0f, 1f);

      shapeRenderer.ellipse(ghost.getPos().x, ghost.getPos().y, 1f, 1f);

      //ghosts target.
      if (i == 2) {
        shapeRenderer.setColor(defColor);
        shapeRenderer.rect(ghost.getTarget().x, ghost.getTarget().y, 1f, 1f);
      }
    }
    */
    for (Ghost ghost : ghosts) ghost.draw(batch);

    batch.end();
  }

  @Override
  public void resize(int width, int height) {
    super.resize(width, height);
    viewport.update(width, height, true);
  }

  @Override
  public void dispose() {
    super.dispose();
    if (batch != null) batch.dispose();
    if (pacSpriteSheet != null) pacSpriteSheet.dispose();
    if (blinkySpriteSheet != null) blinkySpriteSheet.dispose();
    if (pinkySpriteSheet != null) pinkySpriteSheet.dispose();
    if (inkySpriteSheet != null) inkySpriteSheet.dispose();
    if (clydeSpriteSheet != null) clydeSpriteSheet.dispose();
    if (ghostAteSpriteSheet != null) ghostAteSpriteSheet.dispose();
    if (ghostFrightSpriteSheet != null) ghostFrightSpriteSheet.dispose();
  }
}
