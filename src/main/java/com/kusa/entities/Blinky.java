package com.kusa.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.HashMap;
import java.util.Map;

/*
  val BlinkyScatterTarget : Point = Point(25,-3)
  val PinkyScatterTarget : Point = Point(2,-3)
  val InkyScatterTarget : Point = Point(28,35)
  val ClydeScatterTarget : Point = Point(-1,33)
  val GateTarget : Point = Point(13,11)
  val PenTarget : Point = Point(13,15)
*/
public class Blinky extends Ghost {

  /**
   * Constructs a blinky entity with an x, y, speed, state,
   * and target.
   *
   * Blinkys default constructor values.
   *  speed = 7.5f
   *  state = scatter.
   *  target = blinky scatter target.
   *
   */
  public Blinky(float x, float y) {
    this(x, y, 7.5f);
  }

  //Call This One.
  public Blinky(float x, float y, float speed) {
    super(x, y, speed);
    state = GhostState.SCATTER;
    target = Ghost.BLINKY_SCATTER_TARGET.cpy();
    enteringPenTarget = Ghost.BLINKY_ENTERINGPEN_TARGET;
    scatterTarget = Ghost.BLINKY_SCATTER_TARGET;
  }

  @Override
  public void setChaseTarget(Vector2 pacPos) {
    if (state == GhostState.CHASE) chaseTarget = pacPos.cpy();
  }

  @Override
  public String toString() {
    return "Blinky Target: " + target + " Blinky State: " + state;
  }
}
