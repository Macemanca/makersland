package ca.maceman.makersland.ui.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
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
	private Random random;

	public GameView() {

	}

	public GameView(Terrain terrain) {
		super();
		random = new Random();
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
		vModelList = new ArrayList<GameObject>();
		setupNatureActors();
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
				worldModelBatch.render(mi, environment);
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

	/*
	 * TODO NEEDS TWEAKING : way too heavy
	 */
	private void setupNatureActors() {
		GameObject ob;
		BoundingBox boundingBox = new BoundingBox();

		// General
		assets.load("models/nature/rock_small1.g3db", Model.class);
		assets.load("models/nature/rock_tall1.g3db", Model.class);

		// Mountains

		// Boreal
		assets.load("models/nature/pineTree.g3db", Model.class);

		// Temperate
		assets.load("models/nature/oakTree.g3db", Model.class);
		assets.load("models/nature/treeTrunk.g3db", Model.class);

		// Grass

		// Beach

		assets.finishLoading();

		/*
		 * Make an array of all models, a add them randomly based on terrain
		 * type
		 */
		Map<String, ArrayList<Model>> models = new HashMap<String, ArrayList<Model>>();

		models.put("general", new ArrayList<Model>());
		models.get("general").add(assets.get("models/nature/rock_small1.g3db", Model.class));
		models.get("general").add(assets.get("models/nature/rock_tall1.g3db", Model.class));

		models.put(TerrainType.BOREAL.toString(), new ArrayList<Model>());
		models.get(TerrainType.BOREAL.toString()).add(assets.get("models/nature/pineTree.g3db", Model.class));
		models.get(TerrainType.BOREAL.toString()).add(assets.get("models/nature/pineTree.g3db", Model.class));
		models.get(TerrainType.BOREAL.toString()).add(assets.get("models/nature/pineTree.g3db", Model.class));
		models.get(TerrainType.BOREAL.toString()).add(assets.get("models/nature/pineTree.g3db", Model.class));
		models.get(TerrainType.BOREAL.toString()).add(assets.get("models/nature/pineTree.g3db", Model.class));
		models.get(TerrainType.BOREAL.toString()).add(assets.get("models/nature/treeTrunk.g3db", Model.class));
		models.get(TerrainType.BOREAL.toString()).addAll(models.get("general"));

		models.put(TerrainType.TEMPERATE.toString(), new ArrayList<Model>());
		models.get(TerrainType.TEMPERATE.toString()).add(assets.get("models/nature/oakTree.g3db", Model.class));
		models.get(TerrainType.TEMPERATE.toString()).add(assets.get("models/nature/oakTree.g3db", Model.class));
		models.get(TerrainType.TEMPERATE.toString()).add(assets.get("models/nature/oakTree.g3db", Model.class));
		models.get(TerrainType.TEMPERATE.toString()).add(assets.get("models/nature/oakTree.g3db", Model.class));
		models.get(TerrainType.TEMPERATE.toString()).add(assets.get("models/nature/oakTree.g3db", Model.class));
		models.get(TerrainType.TEMPERATE.toString()).add(assets.get("models/nature/treeTrunk.g3db", Model.class));
		models.get(TerrainType.TEMPERATE.toString()).addAll(models.get("general"));

		models.put(TerrainType.MOUNTAIN.toString(), new ArrayList<Model>());
		models.get(TerrainType.MOUNTAIN.toString()).addAll(models.get("general"));

		models.put(TerrainType.GRASSLAND.toString(), new ArrayList<Model>());
		models.get(TerrainType.GRASSLAND.toString()).addAll(models.get("general"));

		for (TerrainChunk[] chunkCol : terrain.getChunks()) {
			for (TerrainChunk chunk : chunkCol) {
				for (TerrainTile[] tileCol : chunk.tiles) {
					for (TerrainTile tile : tileCol) {
						for (int i = 0; i < terrain.getScale() * 2; i++) {
							if (models.containsKey(tile.getTopTri().terrainType.toString())
									&& models.get(tile.getTopTri().terrainType.toString()).size() > random.nextInt(
											(models.get(tile.getTopTri().terrainType.toString()).size() * 10))) {

								Model model = models.get(tile.getTopTri().terrainType.toString()).get(
										random.nextInt(models.get(tile.getTopTri().terrainType.toString()).size()));
								model.calculateBoundingBox(boundingBox);

								// rvec = v1+r1(v2−v1)+r2(v3−v1)
								Vector3 rvec = tile.getV4().toVector3()
										.mulAdd(((tile.getV2().toVector3().sub(tile.getV4().toVector3()))),
												random.nextFloat())
										.mulAdd(tile.getV3().toVector3().sub(tile.getV4().toVector3()),
												random.nextFloat());

								ob = new GameObject(model, rvec, (Shape) new Sphere(boundingBox));

								ob.transform.rotate(Vector3.X, 90f);
								ob.transform.rotate(Vector3.Y, random.nextFloat() * 360);
								ob.transform.scale(0.01f, 0.01f, 0.01f);
								ob.transform.scale(random.nextFloat() + 0.5f, random.nextFloat() + 0.5f,
										random.nextFloat() + 0.5f);
								vModelList.add(ob);

							}

							if (models.containsKey(tile.getBottomTri().terrainType.toString())
									&& models.get(tile.getBottomTri().terrainType.toString()).size() > random.nextInt(
											(models.get(tile.getBottomTri().terrainType.toString()).size() * 5))) {

								Model model = models.get(tile.getBottomTri().terrainType.toString()).get(
										random.nextInt(models.get(tile.getBottomTri().terrainType.toString()).size()));
								model.calculateBoundingBox(boundingBox);

								// rvec = v1+r1(v2−v1)+r2(v3−v1)
								Vector3 rvec = tile.getV1().toVector3()
										.mulAdd(((tile.getV2().toVector3().sub(tile.getV1().toVector3()))),
												random.nextFloat())
										.mulAdd(tile.getV3().toVector3().sub(tile.getV1().toVector3()),
												random.nextFloat());

								ob = new GameObject(model, rvec, (Shape) new Sphere(boundingBox));

								ob.transform.rotate(Vector3.X, 90f);
								ob.transform.rotate(Vector3.Y, random.nextFloat() * 360);
								ob.transform.scale(random.nextFloat() + 0.5f, random.nextFloat() + 0.5f,
										random.nextFloat() + 0.5f);
								vModelList.add(ob);

							}

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
