package ca.maceman.makersland.world.terrain;

import ca.maceman.makersland.world.terrain.parts.TerrainChunk;
import ca.maceman.makersland.world.terrain.parts.TerrainTile;
import ca.maceman.makersland.world.terrain.parts.TerrainVector;
import ca.maceman.makersland.world.utils.terrain.NoiseGenerator;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

/**
 * Holds the Terrain information and model. The terrain Model is made using an
 * array of Terrain Regions and their models. This is because a model can only
 * have a certain number of vectors.
 * 
 * @author andy.masse
 * 
 */
public class Terrain {

	/* Triangles */
	private VertexInfo vi1 = new VertexInfo();
	private VertexInfo vi2 = new VertexInfo();
	private VertexInfo vi3 = new VertexInfo();

	private boolean isIsland = false;
	private float scale;
	private float strength;
	private float[][] heightMap;
	private float maxDepth = -1f;
	private int octave;
	private int borderSize;
	private int chunkBorderWidth;
	private int chunksHeight;
	private int chunksWidth;
	private int octaveCount;
	private int octaves;
	private Mesh mesh;
	private Model terrainModel;
	private TerrainChunk[][] chunks;

	public TerrainVector[][] vectors;
	private long seed = 0;

	/**
	 * Create a new Terrain
	 * 
	 * @param octaves
	 * @param octaveCount
	 * @param strength
	 * @param scale
	 * @param chunksWidth
	 * @param chunksHeight
	 * @param borderSize
	 * @param isIsland
	 */
	public Terrain(int octaves, int octaveCount, float strength, float scale, int chunksWidth, int chunksHeight,
			int borderSize, boolean isIsland) {

		this.octaves = octaves;
		this.octaveCount = octaveCount;
		this.strength = strength;
		this.scale = scale;
		this.chunksWidth = chunksWidth;
		this.chunksHeight = chunksHeight;
		this.borderSize = borderSize;
		this.isIsland = isIsland;

		generateModel();

	}

	public Terrain(int octaves, int octaveCount, float strength, float scale, int chunksWidth, int chunksHeight,
			int borderSize, boolean isIsland, long seed) {
		this.octaves = octaves;
		this.octaveCount = octaveCount;
		this.strength = strength;
		this.scale = scale;
		this.chunksWidth = chunksWidth;
		this.chunksHeight = chunksHeight;
		this.borderSize = borderSize;
		this.isIsland = isIsland;
		this.seed = seed;

		generateModel();

	}

	/**
	 * Generates the terrain model by using @TerrainChunks
	 */
	private void generateModel() {

		/* setup Builders */
		ModelBuilder modelBuilder = new ModelBuilder();
		MeshBuilder meshBuilder = new MeshBuilder();

		chunks = new TerrainChunk[chunksWidth][chunksHeight];

		buildHeightmap();

		modelBuilder.begin();
		/* For each chunk */
		for (int x = 0; x < chunksWidth; x++) {
			for (int y = 0; y < chunksHeight; y++) {

				chunks[x][y] = new TerrainChunk(this, x, y);

				/* Create a model part */
				meshBuilder.begin(Usage.Position | Usage.Normal | Usage.ColorPacked, GL20.GL_TRIANGLES);

				/* using every tile's triangles */
				for (int cx = 0; cx < chunks[x][y].tiles.length; cx++) {
					for (int cy = 0; cy < chunks[x][y].tiles[0].length; cy++) {

						vi1 = chunks[x][y].tiles[cx][cy].getBottomTri().getRIVertexInfo();
						vi2 = chunks[x][y].tiles[cx][cy].getBottomTri().getATVertexInfo();
						vi3 = chunks[x][y].tiles[cx][cy].getBottomTri().getABVertexInfo();

						meshBuilder.triangle(vi1, vi2, vi3);

						vi1 = chunks[x][y].tiles[cx][cy].getTopTri().getRIVertexInfo();
						vi2 = chunks[x][y].tiles[cx][cy].getTopTri().getATVertexInfo();
						vi3 = chunks[x][y].tiles[cx][cy].getTopTri().getABVertexInfo();

						meshBuilder.triangle(vi1, vi2, vi3);

					}
				}

				Mesh mesh = meshBuilder.end();
				modelBuilder.part("chunk" + Integer.toString(x) + "." + Integer.toString(y), mesh, GL20.GL_TRIANGLES,
						new Material());
			}
		}

		/* Terrain model finished */
		terrainModel = modelBuilder.end();

	}

	public void buildHeightmap() {

		/* 1 extra vector for outer edges. */
		int width = (chunksWidth * TerrainChunk.CHUNK_SIZE) + 1;
		int height = (chunksHeight * TerrainChunk.CHUNK_SIZE) + 1;
		float depth = 0f;
		;

		if (isIsland) {
			heightMap = NoiseGenerator.GeneratePerlinNoise(NoiseGenerator.GenerateSmoothNoise(
					NoiseGenerator.GenerateRadialWhiteNoise(width, height, seed), octave), octaveCount);
		} else {
			heightMap = NoiseGenerator.GeneratePerlinNoise(NoiseGenerator.GenerateSmoothNoise(
					NoiseGenerator.GenerateWhiteNoise(width, height, borderSize, seed), octave), octaveCount);
		}

		vectors = new TerrainVector[width][height];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				depth = (float) Math.pow((1 + (heightMap[x][y]) * strength), strength);
				vectors[x][y] = new TerrainVector(x * TerrainTile.TILE_SIZE * scale, y * TerrainTile.TILE_SIZE * scale,
						depth);

				if (depth > maxDepth) {
					maxDepth = depth;
				}
			}
		}

	}

	public void refreshModel() {

		/* 1 extra vector for outer edges. */
		int width = (chunksWidth * TerrainChunk.CHUNK_SIZE) + 1;
		int height = (chunksHeight * TerrainChunk.CHUNK_SIZE) + 1;

		/* setup Builders */
		ModelBuilder modelBuilder = new ModelBuilder();
		MeshBuilder meshBuilder = new MeshBuilder();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				vectors[x][y].z = (float) Math.pow((1 + (heightMap[x][y]) * strength), strength);
			}
		}

		modelBuilder.begin();

		/* For each chunk */
		for (int x = 0; x < chunksWidth; x++) {
			for (int y = 0; y < chunksHeight; y++) {

				/* Create a model part */
				meshBuilder.begin(Usage.Position | Usage.Normal | Usage.ColorPacked, GL20.GL_TRIANGLES);

				/* using every tile's triangles */
				for (int cx = 0; cx < chunks[x][y].tiles.length; cx++) {
					for (int cy = 0; cy < chunks[x][y].tiles[0].length; cy++) {

						vi1 = chunks[x][y].tiles[cx][cy].getBottomTri().getRIVertexInfo();
						vi2 = chunks[x][y].tiles[cx][cy].getBottomTri().getATVertexInfo();
						vi3 = chunks[x][y].tiles[cx][cy].getBottomTri().getABVertexInfo();

						meshBuilder.triangle(vi1, vi2, vi3);

						vi1 = chunks[x][y].tiles[cx][cy].getTopTri().getRIVertexInfo();
						vi2 = chunks[x][y].tiles[cx][cy].getTopTri().getATVertexInfo();
						vi3 = chunks[x][y].tiles[cx][cy].getTopTri().getABVertexInfo();

						meshBuilder.triangle(vi1, vi2, vi3);

					}
				}

				Mesh mesh = meshBuilder.end();
				modelBuilder.part("chunk" + Integer.toString(x) + "." + Integer.toString(y), mesh, GL20.GL_TRIANGLES,
						new Material());
			}
		}

		/* Terrain model finished */
		terrainModel = modelBuilder.end();
	}

	/**
	 * Gets the Depth at x and y.
	 * 
	 * @param xPos
	 * @param yPos
	 * @return
	 */
	public float getDepth(Vector3 v1, Vector3 v2, Vector3 v3, float x, float z) {

		float det = (v2.z - v3.z) * (v1.x - v3.x) + (v3.x - v2.x) * (v1.z - v3.z);

		float l1 = ((v2.z - v3.z) * (x - v3.x) + (v3.x - v2.x) * (z - v3.z)) / det;
		float l2 = ((v3.z - v1.z) * (x - v3.x) + (v1.x - v3.x) * (z - v3.z)) / det;
		float l3 = 1.0f - l1 - l2;

		return l1 * v1.y + l2 * v2.y + l3 * v3.y;

	}

	public float getWidthUnits() {
		return (float) chunksWidth * TerrainTile.TILE_SIZE * TerrainChunk.CHUNK_SIZE * scale;
	}

	public boolean isIsland() {
		return isIsland;
	}

	public void setIsland(boolean isIsland) {
		this.isIsland = isIsland;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getStrength() {
		return strength;
	}

	public void setStrength(float strength) {
		this.strength = strength;
	}

	public float[][] getHeightMap() {
		return heightMap;
	}

	public void setHeightMap(float[][] heightMap) {
		this.heightMap = heightMap;
	}

	public int getOctave() {
		return octave;
	}

	public void setOctave(int octave) {
		this.octave = octave;
	}

	public int getBorderSize() {
		return borderSize;
	}

	public void setBorderSize(int borderSize) {
		this.borderSize = borderSize;
	}

	public int getChunkBorderWidth() {
		return chunkBorderWidth;
	}

	public void setChunkBorderWidth(int chunkBorderWidth) {
		this.chunkBorderWidth = chunkBorderWidth;
	}

	public int getChunksHeight() {
		return chunksHeight;
	}

	public void setChunksHeight(int chunksHeight) {
		this.chunksHeight = chunksHeight;
	}

	public int getChunksWidth() {
		return chunksWidth;
	}

	public void setChunksWidth(int chunksWidth) {
		this.chunksWidth = chunksWidth;
	}

	public int getOctaveCount() {
		return octaveCount;
	}

	public void setOctaveCount(int octaveCount) {
		this.octaveCount = octaveCount;
	}

	public int getOctaves() {
		return octaves;
	}

	public void setOctaves(int octaves) {
		this.octaves = octaves;
	}

	public Mesh getMesh() {
		return mesh;
	}

	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}

	public Model getTerrainModel() {
		return terrainModel;
	}

	public void setTerrainModel(Model terrainModel) {
		this.terrainModel = terrainModel;
	}

	public TerrainChunk[][] getChunks() {
		return chunks;
	}

	public void setChunks(TerrainChunk[][] chunks) {
		this.chunks = chunks;
	}

	public float getMaxHeight() {
		return maxDepth;
	}

	public void setMaxHeight(float maxHeight) {
		this.maxDepth = maxHeight;
	}

}
