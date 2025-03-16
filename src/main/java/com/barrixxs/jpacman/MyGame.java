package com.barrixxs.jpacman;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.barrixxs.jpacman.sim.PacManGame;
import com.barrixxs.jpacman.entities.Entity;
import com.barrixxs.jpacman.entities.Ghost;
import com.barrixxs.jpacman.entities.Ghost.GhostState;
import com.barrixxs.jpacman.entities.Pac;
import com.barrixxs.jpacman.entities.TileMapMaze;

public class MyGame extends Game {

  //game utilities
  private SpriteBatch batch;
  private ExtendViewport viewport;
  private OrthographicCamera camera;
  private OrthogonalTiledMapRenderer mapRenderer;

  private ShapeRenderer shapeRenderer; //DEBUG.

  //game sprites.
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

  Texture candySprite;
  Texture superCandySprite;
  Texture readySprite;

  //game enviornment
  private TileMapMaze maze;

  private PacManGame game;

  @Override
  public void create() {
    // map width : 28
    // map height: 31
    // tile units 20x20

    batch = new SpriteBatch();

    camera = new OrthographicCamera();
    camera.setToOrtho(false, 28, 31);

    viewport = new ExtendViewport(28, 31, camera);

    maze = new TileMapMaze();
    mapRenderer = new OrthogonalTiledMapRenderer(
      maze.getTiledMap(),
      maze.getUnitScale()
    );

    shapeRenderer = new ShapeRenderer();
    //setup sprites (or assets ig?)
    initSprites();

    this.game = new PacManGame(maze);
  }

  private void initSprites() {
    //load pacs sprites.

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

    candySprite = new Texture("map/pellet.png");
    superCandySprite = new Texture("map/super-pellet.png");
    readySprite = new Texture("map/ready.png");
  }

  /**
   * Game loop.
   *
   * we perform 3 general tasks: input, update, and draw.
   * delta is retrieved and passed from here.
   *
   * this loop is responsible for clearing the screen,
   * updating the camera, starting and ending any
   * batches and renderers.
   */
  @Override
  public void render() {
    super.render();

    final float delta = Gdx.graphics.getDeltaTime();

    if(game.isGameOver())
    {
      Gdx.app.exit();
      return;
    }

    //maybe handle input somewhere else.
    //i mean like outside of game object.
    game.update(delta); // <--- makes an internal call to pac.input()

    //r g b a clearDepthBuffer
    ScreenUtils.clear(0, 0, 0, 1, true);

    camera.position.set(28f / 2f, 31f / 2f, 0);
    camera.update();

    //render maze
    mapRenderer.setView(camera);
    mapRenderer.render();

    /* batch draw order
     * - pellets BOTTOM LAYER
     * - ghosts
     * - pac
     * - hud (ready screen only rn) TOP LAYER
     */
    batch.setProjectionMatrix(camera.combined);
    batch.begin();

    for (int i = 0; i < 28; i++) {
      for (int j = 0; j < 31; j++) {
        if (maze.isCandy(i, j)) {
          batch.draw(candySprite, i, j, 1, 1);
        }
        if (maze.isSuperCandy(i, j)) {
          batch.draw(superCandySprite, i, j, 1, 1);
        }
      }
    }

    for (int i = 0; i < game.getGhosts().length; i++) {
      Ghost g = game.getGhosts()[i];
      if (i == 3) renderClyde(g);
      else if (i == 2) renderInky(g);
      else if (i == 1) renderPinky(g);
      else renderBlinky(g);
    }

    renderPac(game.getPac());

    if (game.isStarting()) batch.draw(readySprite, 11, 12, 7, 2);

    batch.end();
  }

  private void renderPac(Entity pac) {
    //define draw data.
    Vector2 dir = pac.getVel().nor();
    float intervalSpeed = 0.25f;
    float drawW = 1.5f;
    float drawH = 1.5f;

    //shrink pac as he dies
    if (game.pacDying()) {
      drawW -= game.getDiedTime();
      drawH -= game.getDiedTime();
      if (drawW < 0f) drawW = 0f;
      if (drawH < 0f) drawH = 0f;
    }

    //center the drawing.
    float drawX = pac.getPos().x - (drawW - 1f) / 2;
    float drawY = pac.getPos().y - (drawH - 1f) / 2;

    //find index based on entities state time.
    int index = (int) (pac.getStateTime() / intervalSpeed);

    //use direction to determine which sprites to draw.
    if (dir.x == 0 && dir.y == 1 && pacUpSprites != null) {
      index %= pacUpSprites.length;
      batch.draw(pacUpSprites[index], drawX, drawY, drawW, drawH);
    } else if (dir.x == 0 && dir.y == -1 && pacDownSprites != null) {
      index %= pacDownSprites.length;
      batch.draw(pacDownSprites[index], drawX, drawY, drawW, drawH);
    } else if (dir.x == -1 && dir.y == 0 && pacLeftSprites != null) {
      index %= pacLeftSprites.length;
      batch.draw(pacLeftSprites[index], drawX, drawY, drawW, drawH);
    } else if (dir.x == 1 && dir.y == 0 && pacRightSprites != null) {
      index %= pacRightSprites.length;
      batch.draw(pacRightSprites[index], drawX, drawY, drawW, drawH);
    } else if (pacUpSprites != null) {
      //draw a solid sprite if no direction was matched.
      index = 0;
      batch.draw(pacUpSprites[index], drawX, drawY, drawW, drawH);
    }
  }

  private void renderBlinky(Ghost blinky) {
    renderGhost(
      blinky,
      blinky.isFrightened(),
      blinky.isAte(),
      blinkyUpSprites,
      blinkyDownSprites,
      blinkyLeftSprites,
      blinkyRightSprites
    );
  }

  private void renderPinky(Ghost pinky) {
    renderGhost(
      pinky,
      pinky.isFrightened(),
      pinky.isAte(),
      pinkyUpSprites,
      pinkyDownSprites,
      pinkyLeftSprites,
      pinkyRightSprites
    );
  }

  private void renderInky(Ghost inky) {
    renderGhost(
      inky,
      inky.isFrightened(),
      inky.isAte(),
      inkyUpSprites,
      inkyDownSprites,
      inkyLeftSprites,
      inkyRightSprites
    );
  }

  private void renderClyde(Ghost clyde) {
    renderGhost(
      clyde,
      clyde.isFrightened(),
      clyde.isAte(),
      clydeUpSprites,
      clydeDownSprites,
      clydeLeftSprites,
      clydeRightSprites
    );
  }

  private void renderGhost(
    Entity ghost,
    boolean isFrightened,
    boolean isAte,
    TextureRegion[] upSprites,
    TextureRegion[] downSprites,
    TextureRegion[] leftSprites,
    TextureRegion[] rightSprites
  ) {
    Vector2 dir = ghost.getVel().nor();
    boolean goingUp = dir.x == 0 && dir.y == 1;
    boolean goingDown = dir.x == 0 && dir.y == -1;
    boolean goingLeft = dir.x == -1 && dir.y == 0;
    boolean goingRight = dir.x == 1 && dir.y == 0;

    float drawW = 1.75f;
    float drawH = 1.75f;
    float drawX = ghost.getPos().x - (drawW - 1f) / 2;
    float drawY = ghost.getPos().y - (drawH - 1f) / 2;

    float intervalSpeed = 0.15f;
    int index = (int) (ghost.getStateTime() / intervalSpeed);

    if (isFrightened && ghostFrightSprites != null) {
      intervalSpeed = 0.5f; // or some decided flash interval.
      index %= ghostFrightSprites.length;
      batch.draw(ghostFrightSprites[index], drawX, drawY, drawW, drawH);
    } else if (isAte && ghostAteSprites.length >= 4) {
      //down, left, right, up;
      if (goingDown) index = 0;
      if (goingLeft) index = 1;
      if (goingRight) index = 2;
      else index = 3;
      batch.draw(ghostAteSprites[index], drawX, drawY, drawW, drawH);
    } else if (goingUp && upSprites != null) {
      index %= upSprites.length;
      batch.draw(upSprites[index], drawX, drawY, drawW, drawH);
    } else if (goingDown && downSprites != null) {
      index %= downSprites.length;
      batch.draw(downSprites[index], drawX, drawY, drawW, drawH);
    } else if (goingLeft && leftSprites != null) {
      index %= leftSprites.length;
      batch.draw(leftSprites[index], drawX, drawY, drawW, drawH);
    } else if (goingRight && rightSprites != null) {
      index %= rightSprites.length;
      batch.draw(rightSprites[index], drawX, drawY, drawW, drawH);
    } else if (upSprites != null) {
      //draw a solid sprite if no direction was matched.
      index = 0;
      batch.draw(upSprites[index], drawX, drawY, drawW, drawH);
    }
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
    if (candySprite != null) candySprite.dispose();
    if (superCandySprite != null) superCandySprite.dispose();
    if (readySprite != null) readySprite.dispose();
    if (pacSpriteSheet != null) pacSpriteSheet.dispose();
    if (blinkySpriteSheet != null) blinkySpriteSheet.dispose();
    if (pinkySpriteSheet != null) pinkySpriteSheet.dispose();
    if (inkySpriteSheet != null) inkySpriteSheet.dispose();
    if (clydeSpriteSheet != null) clydeSpriteSheet.dispose();
    if (ghostAteSpriteSheet != null) ghostAteSpriteSheet.dispose();
    if (ghostFrightSpriteSheet != null) ghostFrightSpriteSheet.dispose();
    game.dispose();
  }
}
