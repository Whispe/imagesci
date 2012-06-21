/**
 *       Java Image Science Toolkit
 *                  --- 
 *     Multi-Object Image Segmentation
 *
 * Center for Computer-Integrated Surgical Systems and Technology &
 * Johns Hopkins Applied Physics Laboratory &
 * The Johns Hopkins University
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.  The license is available for reading at:
 * http://www.gnu.org/copyleft/lgpl.html
 *
 * @author Blake Lucas (blake@cs.jhu.edu)
 */
package org.imagesci.utility;

import data.PlaceHolder;
import edu.jhu.ece.iacl.jist.io.SurfaceReaderWriter;
import edu.jhu.ece.iacl.jist.structures.geom.EmbeddedSurface;
import edu.jhu.ece.iacl.jist.structures.geom.EmbeddedSurface.Direction;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Stack;

// TODO: Auto-generated Javadoc
/**
 * The Class SurfaceConnectedComponent.
 */
public class SurfaceConnectedComponent {

	/**
	 * Builds the neighbor vertex vertex table.
	 *
	 * @param mesh the mesh
	 * @param dir the dir
	 * @return the int[][]
	 */
	public static int[][] buildNeighborVertexVertexTable(EmbeddedSurface mesh,
			Direction dir) {

		int vertexCount = mesh.getVertexCount();
		int indexCount = mesh.getIndexCount();
		int v1, v2, v3;
		int[][] neighborTable = new int[vertexCount][0];
		ArrayList<Integer>[] tmpTable = new ArrayList[vertexCount];
		for (int i = 0; i < vertexCount; i++) {
			tmpTable[i] = new ArrayList<Integer>();
		}
		if (dir == Direction.CLOCKWISE) {
			for (int i = 0; i < indexCount; i += 3) {
				v1 = mesh.getCoordinateIndex(i);
				v2 = mesh.getCoordinateIndex(i + 1);
				v3 = mesh.getCoordinateIndex(i + 2);
				tmpTable[v1].add(v2);
				tmpTable[v1].add(v3);
				tmpTable[v2].add(v3);
				tmpTable[v2].add(v1);
				tmpTable[v3].add(v1);
				tmpTable[v3].add(v2);
			}
		} else if (dir == Direction.COUNTER_CLOCKWISE) {
			for (int i = 0; i < indexCount; i += 3) {
				v1 = mesh.getCoordinateIndex(i);
				v2 = mesh.getCoordinateIndex(i + 1);
				v3 = mesh.getCoordinateIndex(i + 2);
				tmpTable[v1].add(v3);
				tmpTable[v1].add(v2);
				tmpTable[v2].add(v1);
				tmpTable[v2].add(v3);
				tmpTable[v3].add(v2);
				tmpTable[v3].add(v1);
			}
		}
		int pivot;
		int count = 0;
		ArrayList<Integer> neighbors;
		boolean found;
		for (int i = 0; i < vertexCount; i++) {
			neighborTable[i] = new int[tmpTable[i].size() / 2];
			// System.out.println(i + " NEIGHBOR TABLE " +
			// neighborTable[i].length);
			neighbors = tmpTable[i];
			count = 0;
			if (neighbors.size() == 0)
				continue;
			neighbors.remove(0);
			neighborTable[i][count++] = pivot = neighbors.remove(0);
			while (neighbors.size() > 0) {
				found = false;
				for (int k = 0; k < neighbors.size(); k += 2) {
					if (neighbors.get(k) == pivot) {
						neighbors.remove(k);
						neighborTable[i][count++] = pivot = neighbors.remove(k);
						found = true;
						break;
					}
				}
				if (!found) {
					neighbors.remove(0);
					neighborTable[i][count++] = pivot = neighbors.remove(0);
				}
			}
		}
		return neighborTable;
	}

	/**
	 * Label components.
	 *
	 * @param surf the surf
	 * @return the int
	 */
	public static final int labelComponents(EmbeddedSurface surf) {
		int[][] neighborVertexVertexTable = buildNeighborVertexVertexTable(
				surf, EmbeddedSurface.Direction.CLOCKWISE);
		int vertCount = surf.getVertexCount();
		Stack<VertexLabel> stack = new Stack<VertexLabel>();
		VertexLabel[] labels = new VertexLabel[vertCount];
		for (int id = 0; id < vertCount; ++id) {
			labels[id] = new VertexLabel(id, -1);
		}
		int labelCount = 0;
		while (true) {
			VertexLabel first = null;
			for (int id = 0; id < vertCount; ++id) {
				if (labels[id].label == -1) {
					first = labels[id];
					break;
				}
			}
			if (first == null)
				break;
			int label = ++labelCount;
			first.label = label;
			stack.push(first);
			while (!(stack.isEmpty())) {
				VertexLabel top = (VertexLabel) stack.pop();
				int[] nbrs = neighborVertexVertexTable[top.vid];
				for (int nbr : nbrs) {
					if (labels[nbr].label == -1) {
						labels[nbr].label = label;
						stack.push(labels[nbr]);
					}
				}
			}
		}
		double[][] vertData = new double[vertCount][1];
		for (VertexLabel label : labels) {
			vertData[label.vid][0] = label.label;
		}
		int indexCount = surf.getIndexCount();
		double[][] cellData = new double[indexCount / 3][1];
		for (int i = 0; i < indexCount; i += 3) {
			cellData[i / 3][0] = vertData[surf.getCoordinateIndex(i)][0];
		}
		surf.setCellData(cellData);
		surf.setVertexData(vertData);
		return labelCount + 1;
	}

	/**
	 * The Class VertexLabel.
	 */
	protected static class VertexLabel {
		
		/** The vid. */
		public int vid;
		
		/** The label. */
		public int label;

		/**
		 * Instantiates a new vertex label.
		 *
		 * @param vid the vid
		 * @param label the label
		 */
		public VertexLabel(int vid, int label) {
			this.vid = vid;
			this.label = label;
		}
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		try {
			File f = new File(PlaceHolder.class.getResource("skeleton.vtk")
					.toURI());
			EmbeddedSurface surf = SurfaceReaderWriter.getInstance().read(f);
			SurfaceConnectedComponent.labelComponents(surf);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}