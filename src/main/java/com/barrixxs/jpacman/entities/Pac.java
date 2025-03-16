package com.barrixxs.jpacman.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.barrixxs.jpacman.util.Point;

public class Pac extends Entity implements Inputable {

  /**
   * Vector used to track the next direction of pac.
   */
  private Vector2 nextPos;

  private Maze maze;

  /**
   * Constructs a pac entity with an x, y, speed,
   * and it's sprite sheets.
   */
  public Pac(float x, float y, Maze maze) {
    super(x, y);
    this.nextPos = new Vector2(0f, 0f);
    this.maze = maze;
  }

  /**
   * Input handler for Pac.
   *
   * Really basic we just check standard four
   * directional movement and then update our next position.
   * The position doesn't update here since we need to calculate
   * if it's a valid move.
   */
  @Override
  public void input() {
    if (Gdx.input.isKeyPressed(Keys.W)) nextPos.set(0f, 1f);

    if (Gdx.input.isKeyPressed(Keys.S)) nextPos.set(0f, -1f);

    if (Gdx.input.isKeyPressed(Keys.A)) nextPos.set(-1f, 0f);

    if (Gdx.input.isKeyPressed(Keys.D)) nextPos.set(1f, 0f);

    //debug
    if (Gdx.input.isKeyPressed(Keys.P))
    {

      System.out.printf("POS: %s\nVEL: %s\n", pos.toString(), vel.toString());
    }
  }

  /**
   * General logic update for pac.
   *
   * - update pacs velocity.
   * - update pacs position. (stopping on collisions)
   *
   * @param delta used for any physics update that are framerate independent.
   */
  @Override
  public void logic(float delta) {
    stateTime += delta;

    boolean isNewVelocity = !vel.cpy().nor().epsilonEquals(nextPos);
    boolean wontHitWall = !maze.overlapsWall(
      snap(pos).add(nextPos.cpy().scl(speed * delta))
    );

    if (isNewVelocity && wontHitWall) {
      vel.set(nextPos.cpy().scl(speed));
      stateTime = 0f;
    }

    pos.add(vel.cpy().scl(delta));
    if (maze.overlapsWall(pos)) {
      pos.set(snap(pos));
      //pos.lerp(snap(pos), 0.9f);
      vel.set(0f, 0f);
    }
  }
}
