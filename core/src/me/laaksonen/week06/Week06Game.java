package me.laaksonen.week06;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP_PINGPONG;
import static javax.swing.JOptionPane.showMessageDialog;

class Carrot {
    Polygon p;
    Sprite s;
    Vector2 victor;

    public Carrot(float x, float y, Vector2 momentumVector) {
        s = new Sprite(new Texture("carrot.png"));
        s.setX(x);
        s.setY(y);
        p = new Polygon(new float[] {17,0,13,-5.33333f,7.66667f,-17.6667f,6,-29,5.66667f,-45,15.8333f,-54.6667f,29.3333f,-53.1667f,36.3333f,-47.6667f,36.3333f,-26.6667f,28.8333f,-5.16667f,25.3333f,0.333333f});
        p.setPosition(x,y+s.getHeight()/2);
        victor = new Vector2(momentumVector.x,10);
    }

    public void move() {
        s.setX(s.getX()+victor.x);
        s.setY(s.getY()+victor.y);
        p.translate(victor.x,victor.y);
    }
}

class Enemy {
    ArrayList<Polygon> collisionPolygons = new ArrayList<Polygon>();
    TextureRegion[] frames;
    Animation<TextureRegion> animation;
    Rectangle r;
    Vector2 victor;

    public void initWalkAnimation() {
        frames = new TextureRegion[5];
        for(int i = 0; i < 5; i++) {
            int index = i + 1;
            frames[i] = new TextureRegion(new Texture("wingMan/wingMan" + index + ".png"));
        }
        animation = new Animation<TextureRegion>(1.100f, frames);
        animation.setPlayMode(LOOP_PINGPONG);
        victor = new Vector2(new Random().nextFloat()*2-1,new Random().nextFloat()*2-1);
    }

    public Enemy(float x, float y) {
        r = new Rectangle(x,y,216,126);
        initWalkAnimation();
        initPolygons();
    }

    private void initPolygons() {
        initPolygon(new float[] {73,-1,64,-20,67,-73,98,-103,133,-97,151,-75,153,-20,144,-2});
        initPolygon(new float[] {75,-9,63,-30,65,-78,80,-100,108,-110,130,-103,148,-85,152,-62,152,-28,143,-9});
        initPolygon(new float[] {76,-13,64,-33,65,-81,77,-101,99,-112,127,-111,147,-94,152,-65,151,-33,141,-14});
        initPolygon(new float[] {74,-15,67,-35,66,-83,85,-108,104,-116,134,-109,151,-86,152,-35,141,-14});
        initPolygon(new float[] {75,-25,65,-43,67,-101,99,-124,128,-121,151,-99,153,-41,140,-24});
    }

    private void initPolygon(float[] vertices) {
        Polygon p = new Polygon(vertices);
        p.setOrigin(108,63);
        p.rotate(180);
        p.setPosition(r.getX(),r.getY()-r.height);
        collisionPolygons.add(p);
    }

    public void move() {
        r.x += victor.x;
        r.y += victor.y;
        for(Polygon p : collisionPolygons) {
            p.translate(victor.x,victor.y);
        }
    }
}

class Coin {
    TextureRegion[] frames;
    Animation<TextureRegion> animation;

    public void initWalkAnimation() {
        frames = new TextureRegion[4];
        for(int i = 0; i < 4; i++) {
            int index = i + 1;
            frames[i] = new TextureRegion(new Texture("goldCoin/gold_" + index + ".png"));
        }
        animation = new Animation<TextureRegion>(0.100f, frames);
        animation.setPlayMode(LOOP_PINGPONG);
    }
}

class Bunny {
    ArrayList<Carrot> munchies = new ArrayList<Carrot>();
    Polygon p;
    Sprite s;
    Sprite flame;
    Vector2 movementVector;
    Vector2 targetVector;
    float[] vertices = {0f,0f,0.5f,-27.5f,-23f,-62f,-21f,-100.5f,4f,-138.5f,4.5f,-197.5f,70f,-198.5f,70.5f,-137,95f,-99f,96.5f,-51f,75.5f,-26f,75.5f,1.5f};

    public Bunny() {
        s = new Sprite(new Texture("bunnyJetpack.png"));
        flame = new Sprite(new Texture("PNG/Particles/flame.png"));
        movementVector = new Vector2(0,0);
        targetVector = new Vector2(0,0);
        p = new Polygon(vertices);
        p.setOrigin(s.getWidth()/2,s.getHeight()/2);
        p.rotate(180);
    }

    public void move() {
            s.translateX(targetVector.x/10f);
            s.translateY(targetVector.y/10f);
            p.translate(targetVector.x/10f,targetVector.y/10f);
    }

    public void launchCarrot() {
        munchies.add(new Carrot(s.getX()+s.getWidth()/2,s.getY()+s.getHeight(),new Vector2(targetVector.x,targetVector.y)));
    }

}

public class Week06Game extends ApplicationAdapter {
    Coin goldCoin = new Coin();
    SpriteBatch batch;
    TiledMap map;
    private OrthographicCamera cam;
    OrthogonalTiledMapRenderer renderer;
    Bunny b;
    boolean onGround;
    ShapeRenderer shapeRenderer;
    long lastContact;
    long lastJetTime;
    float fuel;
    MapLayer spikeLayer;
    MapObjects spikeObjects;
    Array<PolygonMapObject> spikePolygonObjects;
    MapLayer platformLayer;
    MapObjects platformObjects;
    Array<PolygonMapObject> platformPolygonObjects;
    MapLayer coinLayer;
    MapObjects coinObjects;
    Array<EllipseMapObject> coinEllipseObjects;
    float stateTime;
    ArrayList<Enemy> buzzards;
    long timer;
    boolean paused = false;
    boolean debugOn = false;
    boolean gameOver = false;
    boolean startScren = true;
    Sound youDiedSound;
    Sprite youDiedSprite;
    Sound bounceSound;
    Music bgm;

    @Override
    public void create() {
        buzzards = new ArrayList<Enemy>();
        for (int i = 0; i < 10; i++) {
            buzzards.add(new Enemy(new Random().nextFloat() * 1280, new Random().nextFloat() * 6400));
        }
        goldCoin.initWalkAnimation();
        youDiedSprite = new Sprite(new Texture(Gdx.files.internal("youdied.PNG")));
        youDiedSound = Gdx.audio.newSound(Gdx.files.internal("youdied.mp3"));
        bounceSound = Gdx.audio.newSound(Gdx.files.internal("hop.mp3"));
        bgm = Gdx.audio.newMusic(Gdx.files.internal("diamondus.mp3"));
        bgm.play();
        fuel = 100;
        b = new Bunny();
        b.s.setX(640);
        b.s.setY(150);
        b.p.setPosition(b.s.getX() - b.s.getWidth() / 4, b.s.getY() - b.s.getHeight());
        map = new TmxMapLoader().load("map.tmx");
        batch = new SpriteBatch();
        initCamera();
        renderer = new OrthogonalTiledMapRenderer(map);
        onGround = false;
        shapeRenderer = new ShapeRenderer();
        initSpikes();
        initPlatforms();
        initCoins();
    }


    @Override
    public void render() {
        if(!paused) {
            Gdx.gl.glClearColor(0, 0, 1, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.begin();
            batch.setProjectionMatrix(cam.combined);
            clampCamera();
            cam.update();
            gravity();
            platformForce();
            b.move();
            handleInput();
            fuelTimer();
            removeCarrots();
            checkCoinCollision();
            for (Enemy bee : buzzards) {
                TextureRegion currentFrame = bee.animation.getKeyFrame(stateTime, true);
                batch.draw(currentFrame, bee.r.x, bee.r.y);
            }

            b.s.draw(batch);
            cam.update();

            float x = cam.position.x - cam.viewportWidth * cam.zoom;
            float y = cam.position.y - cam.viewportHeight * cam.zoom;

            float width = cam.viewportWidth * cam.zoom * 2;
            float height = cam.viewportHeight * cam.zoom * 2;

            renderer.setView(cam.combined, x, y, width, height);
            if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
                cam.zoom += 0.01f;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.E)) {
                cam.zoom -= 0.01f;
            }

            batch.end();
            renderer.render();
            checkSpikeCollision();
            checkOutOfBounds();
            if(debugOn) {
                debugRender();
            }
            moveEnemyRandom();
            checkEnemyCollision();
            checkCarrotCollision();





            if(gameOver) {
                batch.begin();
                batch.setProjectionMatrix(cam.combined);
                youDiedSprite.setSize(cam.viewportWidth*cam.zoom,cam.viewportHeight/4*cam.zoom);
                youDiedSprite.setCenter(cam.position.x,cam.position.y);
                youDiedSprite.draw(batch);
                batch.end();
                if(Gdx.input.isTouched()) {
                    gameOver = false;
                    bgm.stop();
                    create();
                }
            } else if(startScren && !Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
                showMessageDialog(null, "WASD to move, SPACEBAR to fire\n" +
                        "Bounce off platforms while avoiding the spikes \n" + "You can move through platforms when coming from below\n" + "The jet pack's fuel tank regenerates over time");
                startScren = false;
            }
        }

    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    private void checkCarrotCollision() {
        Iterator<Carrot> iterator = b.munchies.iterator();
        while (iterator.hasNext()) {
            Carrot carrot = iterator.next();
            Iterator<Enemy> enemyIterator = buzzards.iterator();
            while (enemyIterator.hasNext()) {
                Enemy enemy = enemyIterator.next();
                int index = enemy.animation.getKeyFrameIndex(stateTime);
                if (Intersector.overlapConvexPolygons(carrot.p,enemy.collisionPolygons.get(index))) {
                    iterator.remove();
                    enemyIterator.remove();
                }
            }
        }
    }

    private void checkOutOfBounds() {
        if (b.s.getX() < 0 || b.s.getX() + b.s.getWidth() > 1280) {
            Gdx.app.log("OOB", "HORIZONTAL");
        } else if (b.s.getY() < 50) {
            b.targetVector.y = 50;
            bounceSound.play();
        }
    }

    private void fuelTimer() {
        // Gdx.app.log("FUEL",String.valueOf(fuel));
        if (TimeUtils.timeSinceMillis(lastJetTime) > 1500 && fuel <= 100) {
            fuel += 2.5;
        }
    }

    private void handleInput() {
        if(!gameOver) {
            if (Gdx.input.isKeyPressed(Input.Keys.W) && fuel > 0) {
                fuel -= 0.5f;
                drawLeftJet();
                drawRightJet();
                lastJetTime = TimeUtils.millis();
                if (b.targetVector.y < 50f) {
                    b.targetVector.y += 1f;
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A) && fuel > 0) {
                fuel -= 0.5f;
                drawRightJet();
                lastJetTime = TimeUtils.millis();
                if (b.targetVector.x > -50f) {
                    b.targetVector.x -= 1f;
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D) && fuel > 0) {
                fuel -= 0.5f;
                lastJetTime = TimeUtils.millis();
                drawLeftJet();
                if (b.targetVector.x < 50f) {
                    b.targetVector.x += 1f;
                }
            }
            if(Gdx.input.getAccelerometerY() < 0) {
                drawRightJet();
                if (b.targetVector.x > -50f) {
                    b.targetVector.x -= 1f;
                }
            }else if(Gdx.input.getAccelerometerY() > 0) {
                drawLeftJet();
                if (b.targetVector.x < 50f) {
                    b.targetVector.x += 1f;
                }
            }
            if(Gdx.input.getAccelerometerZ() > 0 && fuel > 0) {
                fuel -= 0.5f;
                drawLeftJet();
                drawRightJet();
                lastJetTime = TimeUtils.millis();
                if (b.targetVector.y < 50f) {
                    b.targetVector.y += 1f;
                }
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || (Gdx.input.isTouched() && Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer))) {
                b.launchCarrot();
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            debugOn = !debugOn;
        }
        for(Carrot carrot : b.munchies) {
            carrot.s.draw(batch);
            carrot.move();
        }
    }

    private void debugRender() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.polygon(b.p.getTransformedVertices());
        shapeRenderer.rect(b.s.getX(), b.s.getY(), b.s.getWidth(), b.s.getHeight());
        MapLayer platformLayer = (MapLayer) map.getLayers().get("Spike Layer");
        MapObjects objects = platformLayer.getObjects();
        Array<PolygonMapObject> polygonObjects = objects.getByType(PolygonMapObject.class);
        for (PolygonMapObject polygonObject : polygonObjects) {
            Polygon polygon = polygonObject.getPolygon();
            shapeRenderer.polygon(polygon.getTransformedVertices());
        }
        platformLayer = (MapLayer) map.getLayers().get("Platform Layer");
        objects = platformLayer.getObjects();
        polygonObjects = objects.getByType(PolygonMapObject.class);
        for (PolygonMapObject polygonObject : polygonObjects) {
            Polygon polygon = polygonObject.getPolygon();
            shapeRenderer.polygon(polygon.getTransformedVertices());
        }
        for (EllipseMapObject ellipseObject : coinEllipseObjects) {
            Ellipse ellipse = ellipseObject.getEllipse();
            shapeRenderer.ellipse(ellipse.x, ellipse.y, ellipse.width, ellipse.height);
        }
        for (Enemy bee : buzzards) {
            shapeRenderer.rect(bee.r.x, bee.r.y, bee.r.width, bee.r.height);
            int index = bee.animation.getKeyFrameIndex(stateTime);
            shapeRenderer.polygon(bee.collisionPolygons.get(index).getTransformedVertices());
        }
        for(Carrot carrot : b.munchies) {
            shapeRenderer.polygon(carrot.p.getTransformedVertices());
        }
        shapeRenderer.end();
    }

    private void initCamera() {
        cam = new OrthographicCamera();
        cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.update();
    }

    private void clampCamera() {
        cam.position.set(b.s.getX() + b.s.getWidth() / 2, b.s.getY() + b.s.getHeight() / 2, 0);
        cam.zoom = MathUtils.clamp(cam.zoom, 0.1f, 1280 / cam.viewportWidth);
        float effectiveViewportWidth = cam.viewportWidth * cam.zoom;
        float effectiveViewportHeight = cam.viewportHeight * cam.zoom;
        cam.position.x = MathUtils.clamp(cam.position.x, effectiveViewportWidth / 2f, 1280 - effectiveViewportWidth / 2f);
        cam.position.y = MathUtils.clamp(cam.position.y, effectiveViewportHeight / 2f, 6400 - effectiveViewportHeight / 2f);
    }

    private void drawLeftJet() {
        b.flame.setX(b.s.getX() + 5f);
        b.flame.setY(b.s.getY() - 50f);
        b.flame.draw(batch);
    }

    private void drawRightJet() {
        b.flame.setX(b.s.getX() + 90f);
        b.flame.setY(b.s.getY() - 50f);
        b.flame.draw(batch);
    }

    private void gravity() {
        if (!onGround) {
            if (b.targetVector.y > -50f) {
                b.targetVector.y -= 0.5f;
            }
        }
    }

    private void platformForce() {
        boolean foundGround = false;
        for (PolygonMapObject polygonObject : platformPolygonObjects) {
            Polygon polygon = polygonObject.getPolygon();
            if (Intersector.overlapConvexPolygons(b.p, polygon)) {
                onGround = true;
                if (b.targetVector.y < 0 && b.s.getY() - b.s.getWidth() / 2 > polygon.getY()) {
                    Gdx.app.log("COLLISION", "FROM ABOVE");
                    bounceSound.play();
                    onGround = true;
                    foundGround = true;
                    b.targetVector.y = 50f;
                    lastContact = TimeUtils.millis();
                } else if (b.targetVector.y > 0 && TimeUtils.timeSinceMillis(lastContact) > 250) {
                    Gdx.app.log("COLLISION", "FROM BELOW");
                }
            } else if (!foundGround) {
                onGround = false;
            }
        }
    }

    private void checkSpikeCollision() {
        for (PolygonMapObject polygonObject : spikePolygonObjects) {
            Polygon polygon = polygonObject.getPolygon();
            if (Intersector.overlapConvexPolygons(b.p, polygon)) {
                Gdx.app.log("COLLISION", "SPIKE HIT");
                if(!gameOver) {
                    youDiedSound.play();
                }
                gameOver = true;
            }
        }
    }

    private void initSpikes() {
        spikeLayer = (MapLayer) map.getLayers().get("Spike Layer");
        spikeObjects = spikeLayer.getObjects();
        spikePolygonObjects = spikeObjects.getByType(PolygonMapObject.class);
    }

    private void initCoins() {
        coinLayer = (MapLayer) map.getLayers().get("Collectible Layer");
        coinObjects = coinLayer.getObjects();
        coinEllipseObjects = coinObjects.getByType(EllipseMapObject.class);
    }

    private void initPlatforms() {
        platformLayer = (MapLayer) map.getLayers().get("Platform Layer");
        platformObjects = platformLayer.getObjects();
        platformPolygonObjects = platformObjects.getByType(PolygonMapObject.class);
    }

    private void checkCoinCollision() {
        Iterator<EllipseMapObject> iterator = coinEllipseObjects.iterator();
        while (iterator.hasNext()) {
            EllipseMapObject object = iterator.next();
            Ellipse ellipse = object.getEllipse();
            TextureRegion currentFrame = goldCoin.animation.getKeyFrame(stateTime, true);
            batch.draw(currentFrame, ellipse.x, ellipse.y);
            if (overlaps(b.p, ellipse)) {
                iterator.remove();
            }
        }
        stateTime += Gdx.graphics.getDeltaTime();
    }

    private boolean overlaps(Polygon polygon, Ellipse ellipse) {
        float[] vertices = polygon.getTransformedVertices();
        Vector2 center = new Vector2(ellipse.x + ellipse.width / 2, ellipse.y + ellipse.height / 2);
        float squareRadius = (ellipse.width / 2) * (ellipse.width / 2);
        for (int i = 0; i < vertices.length; i += 2) {
            if (i == 0) {
                if (Intersector.intersectSegmentCircle(new Vector2(vertices[vertices.length - 2], vertices[vertices.length - 1]), new Vector2(vertices[i], vertices[i + 1]), center, squareRadius))
                    return true;
            } else {
                if (Intersector.intersectSegmentCircle(new Vector2(vertices[i - 2], vertices[i - 1]), new Vector2(vertices[i], vertices[i + 1]), center, squareRadius))
                    return true;
            }
        }
        return false;
    }

    private void moveEnemyRandom() {
        if (TimeUtils.timeSinceMillis(timer) > 1000) {
            timer = TimeUtils.millis();
            for (Enemy bee : buzzards) {
                bee.victor.x = new Random().nextFloat() * 2 - 1;
                bee.victor.y = new Random().nextFloat() * 2 - 1;
            }
        }
        for (Enemy bee : buzzards) {
            bee.move();
        }
    }

    private void checkEnemyCollision() {
        for (Enemy bee : buzzards) {
            int index = bee.animation.getKeyFrameIndex(stateTime);
            if (Intersector.overlapConvexPolygons(b.p, bee.collisionPolygons.get(index))) {
                Gdx.app.log("COLLISION","BUZZARD COLLISION");
                if(!gameOver) {
                    youDiedSound.play();
                }
                gameOver = true;
            }
        }
    }

    private void removeCarrots() {
        Iterator<Carrot> iterator = b.munchies.iterator();
        while (iterator.hasNext()) {
            Carrot carrot = iterator.next();
            if(carrot.s.getX()+carrot.s.getWidth() < 0 || carrot.s.getX() > 1280 || carrot.s.getY() > 6400) {
                iterator.remove();
                Gdx.app.log("DESTROY","CARROT REMOVED");
            }
        }
    }
}
