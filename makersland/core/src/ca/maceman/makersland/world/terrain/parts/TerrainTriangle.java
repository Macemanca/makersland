package ca.maceman.makersland.world.terrain.parts;

import ca.maceman.makersland.world.utils.terrain.TerrainUtils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.math.Vector3;

public class TerrainTriangle {

	private TerrainTile parentTile;
	private VertexInfo vi;

	public TerrainVector vRA;
	public TerrainVector vAT;
	public TerrainVector vAB;
	public Vector3 normal;
	public Color colour;
	public int type;
	/*
	 * Types (short sides) : Bottom-Left = 1 BR = 2 TL = 3 TR = 4
	 */

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

		colour = new Color(.9f, .8f, .7f, 1f);

		float maxHeight = parentTile.getParentChunk().getParentTerrain().getMaxHeight();

		System.out.println(maxHeight);
		float step6 = maxHeight * 0.75f;
		float step5 = maxHeight * 0.65f;
		float step4 = maxHeight * 0.45f;
		float step3 = maxHeight * 0.35f;
		float step2 = maxHeight * 0.20f;
		float step1 = maxHeight * 0.15f;
 
		if (vRA.z >= step6 && vAT.z >= step6 && vAB.z >= step6) {
			colour = new Color(1f, 1f, 1f, 1f);
		} else if (vRA.z >= step5 && vAT.z >= step5 && vAB.z >= step5) {
			colour = new Color(.7f, .7f, .7f, 1f);
		} else if (vRA.z >= step4 && vAT.z >= step4 && vAB.z >= step4) {
			colour = new Color(.5f, .6f, .3f, 1f);
		} else if (vRA.z >= step3 && vAT.z >= step3 && vAB.z >= step3) {
			colour = new Color(.2f, .4f, .2f, 1f);
		} else if (vRA.z >= step2 && vAT.z >= step2 && vAB.z >= step2) {
			colour = new Color(.3f, .6f, .3f, 1f);
		} else if (vRA.z >= step1 && vAT.z >= step1 && vAB.z >= step1) {
			colour = new Color(.5f, .7f, .5f, 1f);
		}

		vi = new VertexInfo();

	}

	public VertexInfo getRIVertexInfo() {

		vi = new VertexInfo();
		vi.setPos(vRA.toVector3()).setNor(normal).setCol(colour).setUV(vRA.u, vRA.v);

		return vi;
	}

	public VertexInfo getATVertexInfo() {

		vi = new VertexInfo();
		vi.setPos(vAT.toVector3()).setNor(normal).setCol(colour).setUV(vAT.u, vAT.v);

		return vi;
	}

	public VertexInfo getABVertexInfo() {

		vi = new VertexInfo();

		vi.setPos(vAB.toVector3()).setNor(normal).setCol(colour).setUV(vAB.u, vAB.v);

		return vi;
	}

	public TerrainTile getParentTile() {
		return parentTile;
	}

	public void setParentTile(TerrainTile parentTile) {
		this.parentTile = parentTile;
	}

}
