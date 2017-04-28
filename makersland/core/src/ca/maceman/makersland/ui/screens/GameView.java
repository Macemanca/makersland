package ca.maceman.makersland.ui.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import ca.maceman.makersland.ui.screens.debug.TerrainDebugWindow;
import ca.maceman.makersland.world.actor.GameObject;
import ca.maceman.makersland.world.actor.shapes.Shape;
import ca.maceman.makersland.world.actor.shapes.Sphere;
import ca.maceman.makersland.world.terrain.Ocean;
import ca.maceman.makersland.world.terrain.Terrain;
import ca.maceman.makersland.world.terrain.parts.TerrainChunk;
import ca.maceman.makersland.world.terrain.parts.TerrainTile;
import ca.maceman.makersland.world.terrain.parts.TerrainType;

public class GameView extends AbstractScreen {

	public boolean debug = true;
	private CameraInputController camController;
	private Environment environment;
	private float time;
	private ModelBatch worldModelBatch;
	private ModelInstance terrainInstance;
	private ModelInstance oceanInstance;
	private PerspectiveCamera cam;
	private Terrain terrain;
	private Ocean ocean;
	private TerrainDebugWindow terrainDebugWindow;
	private ArrayList<GameObject> vModelList;
	private AssetManager assets;

	public GameView() {

	}

	public GameView(Terrain terrain) {
		super();
		vModelList = new ArrayList<GameObject>();
		if (debug) {
			terrainDebugWindow = new TerrainDebugWindow(this);
		}

		this.terrain = terrain;

		ocean = new Ocean(3f, terrain);
		oceanInstance = ocean.oceanModelInstance;

		create();
		refreshModels();
	}

	public void refreshModels() {
		terrainInstance = new ModelInstance(terrain.getTerrainModel());
		this.ocean = new Ocean(ocean.getSeaLevel(), terrain);
		oceanInstance = ocean.oceanModelInstance;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.graphics.getGL20().glClearColor(0.0f, 0.0f, 0.0f, 1);

		time += delta;

		camController.update();

		worldModelBatch.begin(cam);
		 worldModelBatch.render(oceanInstance, environment);
		 worldModelBatch.render(terrainInstance, environment);

		for (GameObject mi : vModelList) {
			if (mi.isVisible(cam)) {
				worldModelBatch.render(mi);
			}
		}

		worldModelBatch.end();

		if (debug) {
			terrainDebugWindow.updateUI(time);
			terrainDebugWindow.stage.draw();
		}
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void create() {

		/* setup environment */
		prepareEnvironment();

		/* setup Camera */
		prepareCam();
		setupInput();

		/* setup Builders and assets */
		worldModelBatch = new ModelBatch();

		assets = new AssetManager();

		setupNatureActors();
		time = 0;

	}

	private void prepareEnvironment() {
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1f));
		environment.add(new DirectionalLight().set(.9f, .9f, .9f, 100f, -100f, 100f));
	}

	private void prepareCam() {
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(100f, 100f, 200f);
		cam.near = 1f;
		cam.far = 1000f;
		cam.lookAt(0f, 0f, 0f);
		camController = new CameraInputController(cam);
		camController.scrollFactor = 20f;
		camController.translateUnits = 100f;
		cam.update();
	}

	private void setupInput() {

		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(terrainDebugWindow.stage);
		multiplexer.addProcessor(camController);
		multiplexer.addProcessor(this);
		Gdx.input.setInputProcessor(multiplexer);
	}

	private void setupNatureActors() {
		GameObject treeObj;

        ModelLoader loader = new ObjLoader();
        Model treeModel = loader.loadModel(Gdx.files.internal("models/nature/Oak_Green_01.obj"));
        BoundingBox boundingBox = new BoundingBox();
        treeModel.calculateBoundingBox(boundingBox);
		Shape shape = (Shape) new Sphere(boundingBox);

		for (TerrainChunk[] chunkCol : terrain.getChunks()) {
			for (TerrainChunk chunk : chunkCol) {
				for (TerrainTile[] tileCol : chunk.tiles) {
					for (TerrainTile tile : tileCol) {
						if (tile.getBottomTri().terrainType == TerrainType.TEMPERATE) {
							treeObj = new GameObject(treeModel, tile.getV1().toVector3(), shape);
							treeObj.transform.rotate(Vector3.X, 90f);
							vModelList.add(treeObj);

						}
					}
				}
			}
		}
	}

	@Override
	public void show() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

	public void setTerrain(Terrain terrain) {
		this.terrain = terrain;
	}

	public Terrain getTerrain() {
		return terrain;
	}

	public Ocean getOcean() {
		return ocean;
	}

	public void setOcean(Ocean ocean) {
		this.ocean = ocean;
	}

}
