package ca.maceman.makersland.world.terrain.parts;

import com.badlogic.gdx.graphics.Color;

public enum TerrainType {
		BEACH("Beach",new Color(.9f, .8f, .7f, 1f)),
		GRASSLAND("Grass Lands",new Color(.3f, .6f, .3f, 1f)),
		TEMPERATE("Temperate Forest",new Color(.2f, .4f, .2f, 1f)),
		BOREAL("Boreal Forest",new Color(.5f, .6f, .3f, 1f)),
		MOUNTAIN("Mountains",new Color(.7f, .7f, .7f, 1f)),
		SNOWY_PEAKS("Snowy Peaks",new Color(1f, 1f, 1f, 1f));
		
	public String name;
	public Color colour;
	
	private TerrainType(String name, Color colour) {
		this.name = name;
		this.colour = colour;
	}
	
	@Override
	public String toString(){
		return name;
	}
}
