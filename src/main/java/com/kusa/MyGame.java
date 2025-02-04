package com.kusa;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MyGame extends Game
{
    private SpriteBatch batch;
    private FitViewport viewport;
    private OrthographicCamera camera;
    private Texture worldImage;

    @Override
    public void create()
    {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 320, 180);
        viewport = new FitViewport(320, 180, camera);

        worldImage = new Texture("levelone.png");
    }

    @Override
    public void render()
    {
        super.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        //draw world
        batch.draw(worldImage,0,0);

        batch.end();
    }

    @Override
    public void dispose()
    {
        super.dispose();
         if(batch != null)
            batch.dispose();

        if(worldImage != null)
            worldImage.dispose();
    }
}
