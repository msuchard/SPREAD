package templates;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import jebl.evolution.graphs.Node;
import jebl.evolution.io.ImportException;
import jebl.evolution.io.NexusImporter;
import jebl.evolution.io.TreeImporter;
import jebl.evolution.trees.RootedTree;
import processing.core.PApplet;
import structure.Coordinates;
import utils.Utils;

@SuppressWarnings("serial")
public class ContinuousTreeToProcessing extends PApplet {

	private String coordinatesName;
	private TreeImporter importer;
	private RootedTree tree;
	private String longitudeName;
	private String latitudeName;
	private double treeHeightMax;
	private String HPD;
	private MapBackground mapBackground;

	private double minPolygonRedMapping;
	private double minPolygonGreenMapping;
	private double minPolygonBlueMapping;
	private double minPolygonOpacityMapping;

	private double maxPolygonRedMapping;
	private double maxPolygonGreenMapping;
	private double maxPolygonBlueMapping;
	private double maxPolygonOpacityMapping;

	private double minBranchRedMapping;
	private double minBranchGreenMapping;
	private double minBranchBlueMapping;
	private double minBranchOpacityMapping;

	private double maxBranchRedMapping;
	private double maxBranchGreenMapping;
	private double maxBranchBlueMapping;
	private double maxBranchOpacityMapping;

	private double branchWidth;

	// Borders of the map coordinates
	// min/max longitude
	private float minX, maxX;
	// min/max latitude
	private float minY, maxY;

	public ContinuousTreeToProcessing() {
	}// END:ContinuousTreeToProcessing

	public void setHPD(String percent) throws RuntimeException {
		HPD = percent;
	}

	public void setCoordinatesName(String name) {

		coordinatesName = name;
		// this is for coordinate attribute names
		longitudeName = (coordinatesName + 2);
		latitudeName = (coordinatesName + 1);
	}

	public void setTreePath(String path) throws IOException, ImportException {

		importer = new NexusImporter(new FileReader(path));
		tree = (RootedTree) importer.importNextTree();
		// this is for mappings
		treeHeightMax = Utils.getTreeHeightMax(tree);
	}

	public void setMinPolygonRedMapping(double min) {
		minPolygonRedMapping = min;
	}

	public void setMinPolygonGreenMapping(double min) {
		minPolygonGreenMapping = min;
	}

	public void setMinPolygonBlueMapping(double min) {
		minPolygonBlueMapping = min;
	}

	public void setMinPolygonOpacityMapping(double min) {
		minPolygonOpacityMapping = min;
	}

	public void setMaxPolygonRedMapping(double max) {
		maxPolygonRedMapping = max;
	}

	public void setMaxPolygonGreenMapping(double max) {
		maxPolygonGreenMapping = max;
	}

	public void setMaxPolygonBlueMapping(double max) {
		maxPolygonBlueMapping = max;
	}

	public void setMaxPolygonOpacityMapping(double max) {
		maxPolygonOpacityMapping = max;
	}

	public void setMinBranchRedMapping(double min) {
		minBranchRedMapping = min;
	}

	public void setMinBranchGreenMapping(double min) {
		minBranchGreenMapping = min;
	}

	public void setMinBranchBlueMapping(double min) {
		minBranchBlueMapping = min;
	}

	public void setMinBranchOpacityMapping(double min) {
		minBranchOpacityMapping = min;
	}

	public void setMaxBranchRedMapping(double max) {
		maxBranchRedMapping = max;
	}

	public void setMaxBranchGreenMapping(double max) {
		maxBranchGreenMapping = max;
	}

	public void setMaxBranchBlueMapping(double max) {
		maxBranchBlueMapping = max;
	}

	public void setMaxBranchOpacityMapping(double max) {
		maxBranchOpacityMapping = max;
	}

	public void setBranchWidth(double width) {
		branchWidth = width;
	}

	public void setup() {

		minX = -180;
		maxX = 180;

		minY = -90;
		maxY = 90;

		mapBackground = new MapBackground(this);

	}// END:setup

	public void draw() {

		smooth();
		mapBackground.drawMapBackground();
		drawPolygons();
		drawBranches();

	}// END:draw

	// ////////////////
	// ---BRANCHES---//
	// ////////////////
	private void drawBranches() {

		strokeWeight((float) branchWidth);

		for (Node node : tree.getNodes()) {
			if (!tree.isRoot(node)) {

				double longitude = Utils.getDoubleNodeAttribute(node,
						longitudeName);

				double latitude = Utils.getDoubleNodeAttribute(node,
						latitudeName);

				Node parentNode = tree.getParent(node);

				double parentLongitude = Utils.getDoubleNodeAttribute(
						parentNode, longitudeName);
				double parentLatitude = Utils.getDoubleNodeAttribute(
						parentNode, latitudeName);

				// Equirectangular projection:
				double x0 = Utils.map(parentLongitude, minX, maxX, 0, width);
				double y0 = Utils.map(parentLatitude, maxY, minY, 0, height);

				double x1 = Utils.map(longitude, minX, maxX, 0, width);
				double y1 = Utils.map(latitude, maxY, minY, 0, height);

				/**
				 * Color mapping
				 * */
				double nodeHeight = tree.getHeight(node);

				int red = (int) Utils.map(nodeHeight, 0, treeHeightMax,
						minBranchRedMapping, maxBranchRedMapping);

				int green = (int) Utils.map(nodeHeight, 0, treeHeightMax,
						minBranchGreenMapping, maxBranchGreenMapping);

				int blue = (int) Utils.map(nodeHeight, 0, treeHeightMax,
						minBranchBlueMapping, maxBranchBlueMapping);

				int alpha = (int) Utils.map(nodeHeight, 0, treeHeightMax,
						maxBranchOpacityMapping, minBranchOpacityMapping);

				stroke(red, green, blue, alpha);

				line((float) x0, (float) y0, (float) x1, (float) y1);

			}
		}// END: nodes loop
	}// END: DrawBranches

	// ////////////////
	// ---POLYGONS---//
	// ////////////////
	private void drawPolygons() {

		for (Node node : tree.getNodes()) {
			if (!tree.isRoot(node)) {
				if (!tree.isExternal(node)) {

					Integer modality = Utils.getIntegerNodeAttribute(node,
							coordinatesName + "_" + HPD + "HPD_modality");

					for (int i = 1; i <= modality; i++) {

						Object[] longitudeHPD = Utils.getArrayNodeAttribute(
								node, longitudeName + "_" + HPD + "HPD_" + i);
						Object[] latitudeHPD = Utils.getArrayNodeAttribute(
								node, latitudeName + "_" + HPD + "HPD_" + i);
						/**
						 * Color mapping
						 * */
						double nodeHeight = tree.getHeight(node);

						int red = (int) Utils.map(nodeHeight, 0, treeHeightMax,
								minPolygonRedMapping, maxPolygonRedMapping);

						int green = (int) Utils.map(nodeHeight, 0,
								treeHeightMax, minPolygonGreenMapping,
								maxPolygonGreenMapping);

						int blue = (int) Utils.map(nodeHeight, 0,
								treeHeightMax, minPolygonBlueMapping,
								maxPolygonBlueMapping);

						int alpha = (int) Utils.map(nodeHeight, 0,
								treeHeightMax, maxPolygonOpacityMapping,
								minPolygonOpacityMapping);

						stroke(red, green, blue, alpha);
						fill(red, green, blue, alpha);

						List<Coordinates> coordinates = Utils.ParsePolygons(
								longitudeHPD, latitudeHPD);

						beginShape();

						for (int row = 0; row < coordinates.size() - 1; row++) {

							double X = Utils.map(coordinates.get(row)
									.getLongitude(), minX, maxX, 0, width);
							double Y = Utils.map(coordinates.get(row)
									.getLatitude(), maxY, minY, 0, height);

							double XEND = Utils.map(coordinates.get(row + 1)
									.getLongitude(), minX, maxX, 0, width);
							double YEND = Utils.map((coordinates.get(row + 1)
									.getLatitude()), maxY, minY, 0, height);

							vertex((float) X, (float) Y);
							vertex((float) XEND, (float) YEND);

						}// END: coordinates loop

						endShape(CLOSE);

					}// END: modality loop

				}
			}
		}// END: node loop
	}// END: drawPolygons

}// END: PlotOnMap class
