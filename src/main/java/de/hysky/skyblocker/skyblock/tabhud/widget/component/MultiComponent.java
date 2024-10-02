package de.hysky.skyblocker.skyblock.tabhud.widget.component;

import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class MultiComponent extends Component {

	private final List<Component> components;
	private int cellH;

	public MultiComponent() {
		components = new ArrayList<>();
	}

	public void add(Component component) {
		components.add(component);
		this.cellH = Math.max(component.height, cellH);
		this.width = components.stream().mapToInt(c -> c.width).sum();
		this.height = components.stream().mapToInt(c -> c.height).max().getAsInt();
	}

	public int size() {
		return components.size();
	}

	@Override
	public void render(DrawContext context, int x, int y) {
		int xPos = x;
		for (Component component : components) {
			if (component == null) continue;
			int yPos = y + ((cellH - component.height) / 2);
			component.render(context, xPos, yPos);
			xPos += component.width;
		}
	}
}
