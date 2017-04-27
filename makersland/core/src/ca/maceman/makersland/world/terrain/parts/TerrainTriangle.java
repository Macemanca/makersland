package ca.maceman.makersland.world.terrain.parts;

import ca.maceman.makersland.world.utils.terrain.TerrainUtils;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.math.Vector3;

public class TerrainTriangle {

	private TerrainTile parentTile;
	private VertexInfo vi;

	public TerrainVector vRA;
	public TerrainVector vAT;
	public TerrainVector vAB;
	public Vector3 normal;
	public TerrainType terrainType = TerrainType.BEACH;

	/**
	 * 
	 * @param vri
	 *            : right angle of tri
	 * @param vat
	 *            : top acute
	 * @param vab
	 *            : bottom acute
	 */
	public TerrainTriangle(TerrainVector var, TerrainVector vat, TerrainVector vab, TerrainTile parentTile) {

		this.vRA = var;
		this.vAT = vat;
		this.vAB = vab;
		this.parentTile = parentTile;

		normal = TerrainUtils.calcNormal(vRA.toVector3(), vAB.toVector3(), vAT.toVector3());

		float maxHeight = parentTile.getParentChunk().getParentTerrain().getMaxHeight();

		System.out.println(maxHeight);

		if (vRA.z >= maxHeight * 0.75f && vAT.z >= maxHeight * 0.75f && vAB.z >= maxHeight * 0.75f) {
			terrainType = TerrainType.SNOWY_PEAKS;
		} else if (vRA.z >= maxHeight * 0.45f && vAT.z >= maxHeight * 0.45f && vAB.z >= maxHeight * 0.45f) {
			terrainType = TerrainType.MOUNTAIN;
		} else if (vRA.z >= maxHeight * 0.25f && vAT.z >= maxHeight * 0.25f && vAB.z >= maxHeight * 0.25f) {
			terrainType = TerrainType.BOREAL;
		} else if (vRA.z >= maxHeight * 0.15f && vAT.z >= maxHeight * 0.15f && vAB.z >= maxHeight * 0.15f) {
			terrainType = TerrainType.TEMPERATE;
		} else if (vRA.z >= maxHeight * 0.05f && vAT.z >= maxHeight * 0.05f && vAB.z >= maxHeight * 0.05f) {
			terrainType = TerrainType.GRASSLAND;
		}

		vi = new VertexInfo();

	}

	public VertexInfo getRIVertexInfo() {

		vi = new VertexInfo();
		vi.setPos(vRA.toVector3()).setNor(normal).setCol(terrainType.colour).setUV(vRA.u, vRA.v);

		return vi;
	}

	public VertexInfo getATVertexInfo() {

		vi = new VertexInfo();
		vi.setPos(vAT.toVector3()).setNor(normal).setCol(terrainType.colour).setUV(vAT.u, vAT.v);

		return vi;
	}

	public VertexInfo getABVertexInfo() {

		vi = new VertexInfo();

		vi.setPos(vAB.toVector3()).setNor(normal).setCol(terrainType.colour).setUV(vAB.u, vAB.v);

		return vi;
	}

	public TerrainTile getParentTile() {
		return parentTile;
	}

	public void setParentTile(TerrainTile parentTile) {
		this.parentTile = parentTile;
	}

}
