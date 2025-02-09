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
public class Pinky extends Ghost {

  /**
   * Constructs a Pinky entity with an x, y, speed, state,
   * and target.
   *
   * Blinkys default constructor values.
   *  speed = 7.5f
   *  state = in pen.
   *  target = pinky scatter target.
   *
   */
  public Pinky(float x, float y) {
    this(x, y, 7.5f);
  }

  public Pinky(float x, float y, float speed) {
    super(x, y, speed);
    state = GhostState.INPEN;
    target = Ghost.ATE_TARGET.cpy();
    enteringPenTarget = Ghost.PINKY_ENTERINGPEN_TARGET;
    scatterTarget = Ghost.PINKY_SCATTER_TARGET;
  }

  @Override
  public void setChaseTarget(Vector2 pacPos, Vector2 pacVel) {
    if (pacVel.isZero()) return;
    if (state == GhostState.CHASE) chaseTarget = pacPos
      .cpy()
      .add(pacVel.cpy().nor().scl(4f));
  }

  @Override
  public void setChaseTarget(Vector2 pacPos) {
    //shouldnt be called. fix tthis!
  }

  @Override
  public String toString() {
    return "Pinky Target: " + target + " Pinky State: " + state;
  }
}
