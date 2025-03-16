package com.barrixxs.jpacman;

public class LevelData {

  public static final float FULL_SPEED = 7.5f;
  public static float ATE_SPEED_MULTIPLIER = 1.5f;
  private float[] scatterChaseIntervals;
  private float pacSpeedMultiplier;
  private float pacFrightSpeedMultiplier;
  private int angryModeOneDotLimit;
  private int angryModeTwoDotLimit;
  private float angryModeOneSpeedMultiplier;
  private float angryModeTwoSpeedMultiplier;
  private float ghostSpeedMultiplier;
  private float ghostFrightSpeedMultiplier;
  private float ghostTunnelSpeedMultiplier; //highest priority
  private float frightDuration;

  private LevelData(
    float[] scatterChaseIntervals,
    float pacSpeedMultiplier,
    float pacFrightSpeedMultiplier,
    int angryModeOneDotLimit,
    int angryModeTwoDotLimit,
    float angryModeOneSpeedMultiplier,
    float angryModeTwoSpeedMultiplier,
    float ghostSpeedMultiplier,
    float ghostFrightSpeedMultiplier,
    float ghostTunnelSpeedMultiplier,
    float frightDuration
  ) {
    this.scatterChaseIntervals = scatterChaseIntervals;
    this.pacSpeedMultiplier = pacSpeedMultiplier;
    this.pacFrightSpeedMultiplier = pacFrightSpeedMultiplier;

    this.angryModeOneDotLimit = angryModeOneDotLimit;
    this.angryModeTwoDotLimit = angryModeTwoDotLimit;
    this.angryModeOneSpeedMultiplier = angryModeOneSpeedMultiplier;
    this.angryModeTwoSpeedMultiplier = angryModeTwoSpeedMultiplier;

    this.ghostSpeedMultiplier = ghostSpeedMultiplier;
    this.ghostFrightSpeedMultiplier = ghostFrightSpeedMultiplier;
    this.ghostTunnelSpeedMultiplier = ghostTunnelSpeedMultiplier; //highest priority

    this.frightDuration = frightDuration;
  }

  public float[] getScatterChaseIntervals() {
    return this.scatterChaseIntervals;
  }

  public float getPacSpeedMultiplier() {
    return this.pacSpeedMultiplier;
  }

  public float getPacFrightSpeedMultiplier() {
    return this.pacFrightSpeedMultiplier;
  }

  public int getAngryModeOneDotLimit() {
    return this.angryModeOneDotLimit;
  }

  public int getAngryModeTwoDotLimit() {
    return this.angryModeTwoDotLimit;
  }

  public float getAngryModeOneSpeedMultiplier() {
    return this.angryModeOneSpeedMultiplier;
  }

  public float getAngryModeTwoSpeedMultiplier() {
    return this.angryModeTwoSpeedMultiplier;
  }

  public float getGhostSpeedMultiplier() {
    return this.ghostSpeedMultiplier;
  }

  public float getGhostFrightSpeedMultiplier() {
    return this.ghostFrightSpeedMultiplier;
  }

  public float getGhostTunnelSpeedMultiplier() {
    return this.ghostTunnelSpeedMultiplier;
  }

  public float getFrightDuration() {
    return this.frightDuration;
  }

  public float getAteSpeedMultiplier() {
    return ATE_SPEED_MULTIPLIER;
  }

  public static class LevelBuilder {

    public LevelData buildLevelOne() {
      float[] scatterChaseIntervals = new float[] {
        7f,
        20f,
        7f,
        20f,
        5f,
        20f,
        5f,
      };
      return new LevelData(
        scatterChaseIntervals,
        0.8f,
        0.9f,
        20,
        10,
        0.8f,
        0.85f,
        0.75f,
        0.5f,
        0.4f,
        6f
      );
    }
  }
}
